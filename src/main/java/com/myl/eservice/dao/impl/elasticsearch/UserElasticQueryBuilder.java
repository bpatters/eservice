package com.myl.eservice.dao.impl.elasticsearch;

import com.myl.eservice.common.config.UserIndexConfig;
import com.myl.eservice.common.elasticsearch.impl.AbstractElasticQueryBuilder;
import com.myl.eservice.model.user.impl.elasticsearch.ElasticUser;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.rescore.RescoreBuilder;

import java.util.Optional;

/**
 * Created by bpatterson on 2/6/15.
 */
public class UserElasticQueryBuilder extends AbstractElasticQueryBuilder<ElasticUser> {
    UserIndexConfig config;

    public UserElasticQueryBuilder(UserIndexConfig config) {
        this.config = config;
    }

    public String getIndex() {
        return config.getIndex();
    }

    @Override
    public String getDocumentType() {
        return config.getType();
    }

    @Override
    public Class getType() {
        return config.getDocumentClass();
    }

    @Override
    public Optional<FilterBuilder> filter() {
        return Optional.empty();
    }

    @Override
    protected Optional<QueryBuilder> query() {
        return Optional.empty();
    }

    @Override
    protected Optional<RescoreBuilder.Rescorer> queryRescorer() {
        return Optional.empty();
    }

    @Override
    protected int queryRescoreWindow() {
        return getPageSize();
    }
}
