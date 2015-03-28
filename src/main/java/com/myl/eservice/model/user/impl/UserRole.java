package com.myl.eservice.model.user.impl;

import com.myl.eservice.model.user.IUserRole;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by bpatterson on 2/5/15.
 */
public enum UserRole implements IUserRole, GrantedAuthority{
    USER,
    ADMIN;

    public String getAuthority() {
        return this.name();
    };
}
