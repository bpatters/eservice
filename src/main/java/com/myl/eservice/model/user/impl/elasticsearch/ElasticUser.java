package com.myl.eservice.model.user.impl.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableSet;
import com.myl.eservice.common.elasticsearch.impl.AbstractElasticDocument;
import com.myl.eservice.model.user.IUser;
import com.myl.eservice.model.user.IUserRole;
import com.myl.eservice.model.user.impl.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * Created by bpatterson on 2/5/15.
 */
public class ElasticUser extends AbstractElasticDocument implements IUser, UserDetails {

    @JsonProperty(IUser.Properties.EMAIL)
    String email;
    @JsonProperty(IUser.Properties.PASSWORD)
    String password;
    @JsonDeserialize(contentAs = UserRole.class)
    @JsonProperty(IUser.Properties.ROLES)
    Set<IUserRole> roles = ImmutableSet.of();

    @Override
    public IUser setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public IUser setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Set<IUserRole> getRoles() {
        return this.roles;
    }

    @Override
    public IUser setRoles(Set<IUserRole> roles) {
        this.roles = roles;
        return this;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ElasticUser.this.getRoles();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    @Override
    public UserDetails getUserDetails() {
        return this;
    }


}
