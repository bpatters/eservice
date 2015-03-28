package com.myl.eservice.common.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.aggregations.bucket.filters.Filters;

import java.util.Optional;

/**
 * Created by bpatterson on 2/6/15.
 */
public interface IElasticQueryBuilder<DOCTYPE extends IDocument> {


    String getIndex();

    String getDocumentType();

    Class<DOCTYPE> getType();

    /**
     * Returns an initialized {@link org.elasticsearch.action.search.SearchRequestBuilder} instance which may
     *  be used to execute a search against ElasticSearch.
     */
    SearchRequestBuilder build(Client client);

    /**
     * Filters you wish to filter by
     * @return
     */
    public abstract Optional<FilterBuilder> filter();

    /**
     * Specifiy a custom filter
     * @return
     */
    public IElasticQueryBuilder<DOCTYPE> withFilter(FilterBuilder filter);

    /**
     * Allows you to add and configure a range filter for the specified propertyName
     * @param propertyName
     * @return
     */
    RangeFilterBuilder withRangeFilter(String propertyName);

    /**
     * Filter by property values
     * @param propertyName
     * @param propertyValue
     * @return
     */
    IElasticQueryBuilder<DOCTYPE> withPropertyFilter(String propertyName, Object propertyValue);

    /**
     * Returns the size of the page that is requested
     */
    public Integer getPageSize();

    /**
     * Sets the size of the page that is requested
     */
    public IElasticQueryBuilder<DOCTYPE> setPageSize(final int pageSize);

    /**
     * Returns an optional page number to return from the result query
     */
    public Optional<Integer> getPageNumber();

    /**
     * Sets an optional page number to return from the result query
     */
    public IElasticQueryBuilder<DOCTYPE> setPageNumber(Integer pageNumber);
}
