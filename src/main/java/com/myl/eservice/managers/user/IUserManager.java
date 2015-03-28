package com.myl.eservice.managers.user;

import com.myl.eservice.model.user.IUser;

/**
 * Created by bpatterson on 2/5/15.
 */
public interface IUserManager {
    IUser findUser(String id);
    IUser createUser(IUser user);
    IUser updateUser(IUser user);
    IUser deleteUser(String id);
}
