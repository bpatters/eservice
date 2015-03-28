package com.myl.eservice.common.elasticsearch.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.myl.eservice.common.elasticsearch.IDocument;
import com.myl.eservice.common.elasticsearch.IElasticQueryBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.rescore.QueryRescorer;
import org.elasticsearch.search.rescore.RescoreBuilder;

public abstract class AbstractElasticQueryBuilder<DOCTYPE extends IDocument> implements IElasticQueryBuilder<DOCTYPE> {
    private Integer pageSize;
    private Optional<Integer> pageNumber = Optional.empty();
    private List<FilterBuilder> filters = Lists.newArrayList();


    @Override
    public final SearchRequestBuilder build(Client client) {
        checkNotNull(client, "client is required");
        checkState(pageSize != null, "pageSize must be set");

        Optional<QueryBuilder> query = this.query();
        SearchRequestBuilder builder = client.prepareSearch(this.getIndex())
                .setQuery(QueryBuilders.filteredQuery(query.orElse(QueryBuilders.matchAllQuery()), this.buildFilter()))
                .setTypes(this.getDocumentType())
                .setSize(this.getPageSize());


        Optional<RescoreBuilder.Rescorer> rescoreBuilder = queryRescorer();
        if (rescoreBuilder.isPresent()) {
            builder.addRescorer(rescoreBuilder.get(), queryRescoreWindow());
        }


        if (this.from().isPresent()) {
            builder.setFrom(this.from().get());
        }

        return builder;
    }
    abstract protected Optional<QueryBuilder> query();
    abstract protected Optional<RescoreBuilder.Rescorer> queryRescorer();
    abstract protected int queryRescoreWindow();

    /**
     * Build the composite filter which applies the permanent and optional filter properties
     */
    private FilterBuilder buildFilter() {
        Optional<FilterBuilder> filter = this.filter();

        if (filter.isPresent()) {
            return filter.get();
        } else {
            return FilterBuilders.matchAllFilter();
        }
    }

    public RangeFilterBuilder withRangeFilter(String propertyName) {
        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(propertyName);

        filters.add(rangeFilter);

        return rangeFilter;
    }

    public IElasticQueryBuilder<DOCTYPE> withFilter(FilterBuilder filter) {
        filters.add(filter);

        return this;
    }

    /**
     * Filter by property values
     *
     * @param propertyName
     * @param propertyValue
     * @return
     */
    public IElasticQueryBuilder<DOCTYPE> withPropertyFilter(String propertyName, Object propertyValue) {
        filters.add(FilterBuilders.termFilter(propertyName, propertyValue));

        return this;
    }

    /**
     * Filter by property having any one of the specified filter
     *
     * @param propertyName
     * @param propertyValue
     * @return
     */
    public IElasticQueryBuilder<DOCTYPE> withPropertyFilter(String propertyName, Object... propertyValue) {
        filters.add(FilterBuilders.termsFilter(propertyName, propertyValue));

        return this;
    }


    /**
     * Returns an integer which may be used in an Elastic Search "from" clause.  The default
     * implementation determines the "from" value from the page size and requested page number.
     */
    protected Optional<Integer> from() {
        checkState(this.getPageSize() != null, "from cannot be called until pageSize has been set");

        if (this.getPageNumber().isPresent()) {
            int from = Math.max(0, (this.getPageNumber().get() - 1) * this.getPageSize());
            return Optional.of(from);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the size of the page that is requested
     */
    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets the size of the page that is requested
     */
    @Override
    public IElasticQueryBuilder<DOCTYPE> setPageSize(final int pageSize) {
        checkArgument(pageSize > 0, "pageSize must be > 0");
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Returns an optional page number to return from the result query
     */
    @Override
    public Optional<Integer> getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets an optional page number to return from the result query
     */
    @Override
    public IElasticQueryBuilder<DOCTYPE> setPageNumber(Integer pageNumber) {
        checkArgument(pageNumber == null || pageNumber >= 1, "pageNumber, if provided must be >= 1");
        this.pageNumber = Optional.ofNullable(pageNumber);
        return this;
    }
}
