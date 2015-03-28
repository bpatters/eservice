package com.myl.eservice.dao.impl.elasticsearch;

import com.myl.eservice.common.config.UserIndexConfig;
import com.myl.eservice.model.user.IUser;
import com.myl.eservice.common.elasticsearch.impl.AbstractElasticsearchDAO;
import com.myl.eservice.dao.IUserDAO;
import org.elasticsearch.index.query.FilterBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by bpatterson on 2/5/15.
 */
@Component
public class ElasticUserDAO extends AbstractElasticsearchDAO implements IUserDAO {

    @Autowired
    private UserIndexConfig config;

    @Override
    public IUser findUser(String id) {
        return this.findDocument(id, config.getIndex(), config.getType(), config.getDocumentClass());
    }

    @Override
    public IUser updateUser(IUser user) {
        return this.saveDocument(config.getIndex(), config.getType(), user);
    }

    @Override
    public IUser createUser(IUser user) {
        return this.createDocument(config.getIndex(), config.getType(), user);
    }

    @Override
    public IUser findUserByEmail(String email) throws UsernameNotFoundException {
        return findDocument(FilterBuilders.termFilter(IUser.Properties.EMAIL, email), config.getIndex(), config.getType(), config.getDocumentClass());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(email)) {
            throw new UsernameNotFoundException(email);
        }

        // load the user by email, there should only be one
        IUser user = this.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        return user.getUserDetails();
    }
}
