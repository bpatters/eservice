package com.myl.eservice.model.user;

import com.myl.eservice.common.elasticsearch.IDocument;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by bpatterson on 2/5/15.
 */
public interface IUser extends IDocument {
    public class Properties {
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String ROLES = "roles";
    }

    public IUser setEmail(String email);
    public String getEmail();

    public IUser setPassword(String password);
    public String getPassword();

    public IUser setRoles(Set<IUserRole> roles);
    public Set<IUserRole> getRoles();

    public UserDetails getUserDetails();
}
