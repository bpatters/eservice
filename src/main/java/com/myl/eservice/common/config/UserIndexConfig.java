package com.myl.eservice.common.config;

import com.myl.eservice.model.user.impl.elasticsearch.ElasticUser;

/**
 * Created by bpatterson on 3/12/15.
 */
public class UserIndexConfig  extends ElasticIndexTypeConfiguration<Class<ElasticUser>> {

    public UserIndexConfig(String index, String type, Class<ElasticUser> documentClass) {
        super(index, type, documentClass);
    }
}