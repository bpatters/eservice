package com.myl.eservice.common.elasticsearch.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.myl.eservice.common.elasticsearch.IDocument;
import com.myl.eservice.common.elasticsearch.IElasticQueryBuilder;
import com.myl.eservice.common.errorhandling.impl.SearchServiceException;
import com.myl.eservice.model.IPage;
import com.myl.eservice.model.impl.Page;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractElasticsearchDAO {

    @Autowired
    private Client client;
    @Autowired
    private ObjectMapper objectMapper;
    private LoadingCache<Class, ObjectReader> objectReaderCache;
    private LoadingCache<Class, ObjectWriter> objectWriterCache;

    @PostConstruct
    public void setUp() {
        this.objectWriterCache = CacheBuilder.newBuilder().build(
                new CacheLoader<Class, ObjectWriter>() {
                    @Override
                    public ObjectWriter load(Class key) throws Exception {
                        return AbstractElasticsearchDAO.this.objectMapper.writerWithType(key);
                    }
                });

        this.objectReaderCache = CacheBuilder.newBuilder().build(
                new CacheLoader<Class, ObjectReader>() {
                    @Override
                    public ObjectReader load(Class key) throws Exception {
                        return AbstractElasticsearchDAO.this.objectMapper.reader(key);
                    }
                });
    }

    protected <T extends IDocument, TT> void scrollDocuments(Consumer<List<TT>> documentProcessor, int pageSize, String index, String documentType, Class<T> javaType) {
        SearchResponse response = this.getClient().prepareSearch(index).setTypes(documentType).setQuery(QueryBuilders.matchAllQuery()).setSize(pageSize).setScroll(TimeValue.timeValueMinutes(5)).get();

        SearchHit[] hits = response.getHits().getHits();
        while (hits.length != 0) {
            ImmutableList.Builder<T> results = ImmutableList.builder();

            hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                results.add(this.getDocumentFromSearchHit(hit, javaType));
            }
            documentProcessor.accept((List<TT>)results.build());
            response = client.prepareSearchScroll(response.getScrollId()).setScroll(TimeValue.timeValueMinutes(5)).execute().actionGet();
        }
    }

    /**
     * Find a single document.
     *
     * @param id           The unique ID of the document you wish to find.
     * @param index        The index in which the document exists in Elasticsearch.
     * @param documentType The document type in Elasticsearch for this document.
     * @param javaType     The concrete java type the document represents.
     */
    final protected <T extends IDocument> T findDocument(String id, String index, String documentType, Class<T> javaType) {
        GetResponse response = this.getClient().prepareGet()
                .setIndex(index)
                .setType(documentType)
                .setId(id)
                .execute()
                .actionGet();

        if (response.isExists()) {
            return this.getDocumentFromSearchResponse(response, javaType);
        } else {
            return null;
        }
    }

    final protected <T extends IDocument> T findDocument(FilterBuilder filter, String index, String documentType, Class<T> javaType) {
        SearchResponse response = client.prepareSearch(index)
                .setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter))
                .setTypes(documentType)
                .setSize(2)
                .get();

        if (response.getHits().getTotalHits() > 1) {
            throw new SearchServiceException("Unexpected multiple hits for query.");
        } else if (response.getHits().getTotalHits() == 1) {
            return (this.getDocumentFromSearchHit(response.getHits().getAt(0), javaType));
        } else {
            return null;
        }
    }

    final protected <T> IPage<T> findUniqueTerms(FilterBuilder filter, AggregationBuilder aggregation, String index, String documentType, Class<T> javaType) {
        SearchResponse response = client.prepareSearch(index)
                .setTypes(documentType)
                .setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter))
                .addAggregation(aggregation)
                .setSize(0)
                .get();

        Terms results = response.getAggregations().get(aggregation.getName());


        IPage page = Page.<T>of(Iterables.transform(results.getBuckets(), new Function<Terms.Bucket, T>() {
            @Override
            public T apply(Terms.Bucket input) {
                return (T) input.getKey();
            }
        }));
        page.setPageSize(page.size());
        page.setPageNumber(1);
        page.setTotalElements(page.size());

        return page;

    }

    /**
     * Find a set of documents.
     *
     * @param ids          The unique IDs of the documents you wish to find.
     * @param index        The index in which the document exists in Elasticsearch.
     * @param documentType The document type in Elasticsearch for this document.
     * @param javaType     The concrete java type the document represents.
     */
    final protected <T extends IDocument> IPage<T> findDocuments(Set<String> ids, String index, String documentType, Class<T> javaType) {
        MultiGetResponse response = this.getClient().prepareMultiGet().add(index, documentType, ids).execute().actionGet();

        List<T> results = Lists.newArrayListWithCapacity(ids.size());
        for (MultiGetItemResponse item : response.getResponses()) {
            if (item.isFailed()) {
                throw new SearchServiceException("Unexpected failure while searching for documents.");
            }
            results.add(this.getDocumentFromSearchResponse(item.getResponse(), javaType));
        }

        IPage page = Page.of(results);
        page.setPageSize(results.size());
        page.setPageNumber(1);
        page.setTotalElements(results.size());

        return page;
    }

    /**
     * Find a page of documents of the same type.
     *
     * @param query A query object which represents the parameters you want to use to find the documents
     * @return A list of results
     */
    final protected <T extends IDocument> IPage<T> findDocuments(IElasticQueryBuilder<T> query) {
        SearchResponse response = query.build(this.getClient()).execute().actionGet();

        List<T> results = Lists.newLinkedList();
        for (SearchHit hit : response.getHits()) {
            results.add(this.getDocumentFromSearchHit(hit, query.getType()));
        }

        IPage page = Page.of(results);
        page.setPageSize(query.getPageSize());
        page.setPageNumber(query.getPageNumber().orElse(1));
        page.setTotalElements(response.getHits().getTotalHits());

        return page;
    }

    public Set<String> findAllDocumentIds() {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        SearchResponse response = this.getClient().prepareSearch().setQuery(QueryBuilders.matchAllQuery()).addField("_id").setSize(Integer.MAX_VALUE).get();

        for (SearchHit hit : response.getHits()) {
            builder.add(hit.getId());
        }

        return builder.build();
    }

    final protected void saveDocuments(String index, String documentType, List<? extends IDocument> documents) {
        if (documents.size() == 0) {
            return;
        }

        BulkRequestBuilder bulkRequestBuilder = this.getClient().prepareBulk();

        for (IDocument document : documents) {
            this.updateDocumentDatesTracking(document);
            ObjectWriter writer = getObjectWriter(document.getClass());
            IndexRequestBuilder request = this.getClient().prepareIndex()
                    .setIndex(index)
                    .setId(document.getId())
                    .setType(documentType)
                    .setOpType(IndexRequest.OpType.INDEX)
                    .setSource(this.getSourceFromDocument(writer, document));

            bulkRequestBuilder.add(request);
        }

        try {
            bulkRequestBuilder.setRefresh(true);
            BulkResponse response = bulkRequestBuilder.execute().actionGet();
            if (response.hasFailures()) {
                throw new SearchServiceException(String.format("Unexpected failure bulk saving documents %s", response.buildFailureMessage()));
            }
        } catch (VersionConflictEngineException vc) {
            throw new ConcurrentModificationException(vc);
        } catch (ElasticsearchException ex) {
            throw new SearchServiceException("Unexpected failure while indexing a document.", ex);
        }
    }

    /**
     * Bulk update a set of documents using partial documents.
     *
     * @param index            document index
     * @param documentType     Type of the document
     * @param filterProvider   Serialization filterProvider to use in document serialization to only output fields we wish to update.
     * @param documentIds      documents to update.
     * @param documentTemplate the document template to apply changes from
     */
    final protected void updateDocuments(String index, String documentType, FilterProvider filterProvider, Set<String> documentIds, IDocument documentTemplate) {
        if (documentIds.size() == 0) {
            return;
        }

        BulkRequestBuilder bulkRequestBuilder = this.getClient().prepareBulk();
        ObjectWriter writer = getObjectWriter(documentTemplate.getClass()).with(filterProvider);
        this.updateDocumentDatesTracking(documentTemplate);

        for (String documentId : documentIds) {
            UpdateRequestBuilder request = this.getClient().prepareUpdate()
                    .setIndex(index)
                    .setId(documentId)
                    .setType(documentType)
                    .setDoc(this.getSourceFromDocument(writer, documentTemplate));

            bulkRequestBuilder.add(request);
        }

        try {
            bulkRequestBuilder.setRefresh(true);
            BulkResponse response = bulkRequestBuilder.execute().actionGet();
            if (response.hasFailures()) {
                throw new SearchServiceException(String.format("Unexpected failure bulk updating documents %s", response.buildFailureMessage()));
            }
        } catch (VersionConflictEngineException vc) {
            throw new ConcurrentModificationException(vc);
        } catch (ElasticsearchException ex) {
            throw new SearchServiceException("Unexpected failure while indexing a document.", ex);
        }
    }

    /**
     * Save a single document to Elasticsearch, performing an "upsert".
     */
    final protected <T extends IDocument> T createDocument(String index, String documentType, T document) {
        if (StringUtils.isEmpty(document.getId())) {
            document.setId(this.generateId());
        }
        this.updateDocumentDatesTracking(document);

        IndexRequestBuilder request = this.getClient().prepareIndex()
                .setIndex(index)
                .setType(documentType)
                .setId(document.getId())
                .setOpType(IndexRequest.OpType.CREATE)
                .setSource(this.getSourceFromDocument(document))
                .setRefresh(true);

        if (document.getVersion().isPresent()) {
            request.setVersion(document.getVersion().get());
        }

        try {
            IndexResponse response = request.execute().actionGet();
            document.setVersion(response.getVersion());
        } catch (VersionConflictEngineException vc) {
            throw new ConcurrentModificationException(vc);
        } catch (ElasticsearchException ex) {
            throw new SearchServiceException("Unexpected failure while indexing a document.", ex);
        }

        return document;
    }

    /**
     * Save a single document to Elasticsearch, performing an "upsert".
     */
    final protected <T extends IDocument> T saveDocument(String index, String documentType, T document) {
        if (StringUtils.isEmpty(document.getId())) {
            document.setId(this.generateId());
        }
        this.updateDocumentDatesTracking(document);

        IndexRequestBuilder request = this.getClient().prepareIndex()
                .setIndex(index)
                .setType(documentType)
                .setId(document.getId())
                .setOpType(IndexRequest.OpType.INDEX)
                .setSource(this.getSourceFromDocument(document))
                .setRefresh(true);

        if (document.getVersion().isPresent()) {
            request.setVersion(document.getVersion().get());
        }

        try {
            IndexResponse response = request.execute().actionGet();
            document.setVersion(response.getVersion());
        } catch (VersionConflictEngineException vc) {
            throw new ConcurrentModificationException(vc);
        } catch (ElasticsearchException ex) {
            throw new SearchServiceException("Unexpected failure while indexing a document.", ex);
        }

        return document;
    }

    private <T extends IDocument> void updateDocumentDatesTracking(T document) {
        DateTime currentTime = DateTime.now();

        // only update if necessary
        if (document.getCreatedDate() == null) {
            document.setCreatedDate(currentTime.getMillis());
        }
        document.setLastModifiedDate(currentTime.getMillis());
    }

    private <T extends IDocument> T getDocumentFromSearchHit(SearchHit searchHit, Class<T> type) {
        return this.getDocumentFromSource(searchHit.getId(), searchHit.getVersion(), searchHit.getSourceRef(), type);
    }

    private <T extends IDocument> T getDocumentFromSearchResponse(GetResponse response, Class<T> type) {
        return this.getDocumentFromSource(response.getId(), response.getVersion(), response.getSourceAsBytesRef(), type);
    }

    private <T extends IDocument> T getDocumentFromSource(String id, long version, BytesReference reference, Class<T> type) {
        ObjectReader reader = this.getObjectReader(type);
        try (InputStream stream = reference.streamInput()) {
            T value = reader.readValue(stream);
            value.setId(id);
            value.setVersion(version);
            return value;
        } catch (IOException ex) {
            throw new SearchServiceException(String.format("Unable to deserialize object as %s:\n%s",
                    type.getSimpleName(), reference.toUtf8()), ex);
        }
    }

    private byte[] getSourceFromDocument(ObjectWriter writer, IDocument document) {
        try {
            return writer.writeValueAsBytes(document);
        } catch (IOException ex) {
            throw new SearchServiceException("Unexpected exception while attempting to serialize document", ex);
        }
    }

    @VisibleForTesting
    byte[] getSourceFromDocument(IDocument document) {
        try {
            return this.getObjectWriter(document.getClass()).writeValueAsBytes(document);
        } catch (IOException ex) {
            throw new SearchServiceException("Unexpected exception while attempting to serialize document", ex);
        }
    }

    /**
     * Generate a new random UUID for a document
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private ObjectReader getObjectReader(Class type) {
        return this.getObjectReaderCache().getUnchecked(type);
    }

    private LoadingCache<Class, ObjectReader> getObjectReaderCache() {
        return objectReaderCache;
    }

    public void setObjectReaderCache(LoadingCache<Class, ObjectReader> objectReaderCache) {
        this.objectReaderCache = objectReaderCache;
    }

    private ObjectWriter getObjectWriter(Class type) {
        return this.getObjectWriterCache().getUnchecked(type);
    }

    public LoadingCache<Class, ObjectWriter> getObjectWriterCache() {
        return objectWriterCache;
    }

    public void setObjectWriterCache(LoadingCache<Class, ObjectWriter> objectWriterCache) {
        this.objectWriterCache = objectWriterCache;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;


    }
}
