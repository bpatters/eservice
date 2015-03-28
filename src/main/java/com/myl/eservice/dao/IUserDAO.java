package com.myl.eservice.dao;

import com.myl.eservice.model.user.IUser;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by bpatterson on 2/5/15.
 */
public interface IUserDAO extends UserDetailsService {
    IUser findUser(String id);
    IUser findUserByEmail(String email);
    IUser createUser(IUser user);
    IUser updateUser(IUser user);
}
