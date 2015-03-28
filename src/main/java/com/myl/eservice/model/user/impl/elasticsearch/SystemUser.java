package com.myl.eservice.model.user.impl.elasticsearch;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
import com.google.common.collect.ImmutableSet;
import com.myl.eservice.model.user.impl.UserRole;

/**
 * Created by bpatterson on 3/12/15.
 */
// don't allow this user to be saved to the database
@JsonDeserialize(as = NullifyingDeserializer.class)
public class SystemUser extends ElasticUser {

    public SystemUser() {
        this.setEmail("SYSTEM");
        this.setPassword("");
        this.setId("SYSTEM");
        this.setRoles(ImmutableSet.of(UserRole.ADMIN));
    }
}
