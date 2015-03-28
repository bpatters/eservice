package com.myl.eservice.managers.user.impl;

import com.myl.eservice.model.user.IUser;
import com.myl.eservice.model.user.impl.elasticsearch.ElasticUser;
import com.myl.eservice.managers.user.IUserObjectFactory;
import org.springframework.stereotype.Component;

/**
 * Created by bpatterson on 2/7/15.
 */
@Component
public class UserObjectFactory implements IUserObjectFactory {

    public IUser newUser() {
      return new ElasticUser();
    }
}
