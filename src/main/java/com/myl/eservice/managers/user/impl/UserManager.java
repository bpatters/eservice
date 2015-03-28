package com.myl.eservice.managers.user.impl;

import com.google.common.collect.ImmutableSet;
import com.myl.eservice.common.errorhandling.IExceptionFactory;
import com.myl.eservice.model.user.IUser;
import com.myl.eservice.model.impl.DocumentStatus;
import com.myl.eservice.model.user.impl.UserRole;
import com.myl.eservice.dao.IUserDAO;
import com.myl.eservice.managers.user.IUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by bpatterson on 2/5/15.
 */
@Component
public class UserManager implements IUserManager {
    @Autowired
    private
    IUserDAO userDAO;
    @Autowired
    private
    IExceptionFactory exceptionFactory;

    @Override
    public IUser findUser(String userId) {
        return getUserDAO().findUser(userId);
    }

    @Override
    public IUser createUser(IUser user) {
        IUser existingUser = getUserDAO().findUserByEmail(user.getEmail());
        if (existingUser != null) {
            this.getExceptionFactory().throwValidation("user.already.exists", user.getEmail());
        }
        if (!StringUtils.isEmpty(user.getId())) {
            this.getExceptionFactory().throwValidation("user.create.id.exists", user.getId());
        }
        user.setRoles(ImmutableSet.of(UserRole.USER));

        this.validateUser(user);
        return getUserDAO().createUser(user);
    }

    @Override
    public IUser updateUser(IUser user) {
        this.validateUser(user);

        return getUserDAO().updateUser(user);
    }

    @Override
    public IUser deleteUser(String id) {
        IUser user = getUserDAO().findUser(id);
        if (user == null) {
            this.getExceptionFactory().throwValidation("user.notFound");
        }

        user.setStatus(DocumentStatus.DELETED);

        return getUserDAO().updateUser(user);
    }


    public void validateUser(IUser user) {
        if (user.getRoles().size() == 0) {
            this.getExceptionFactory().throwValidation("user.roles.empty", user.getEmail());
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            this.getExceptionFactory().throwValidation("user.email.blank");
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            this.getExceptionFactory().throwValidation("user.password.blank");
        }

        if (!user.getEmail().matches(".*@.*")) {
            this.getExceptionFactory().throwValidation("user.email.invalid", user.getEmail());
        }
    }

    public IUserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public IExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }

    public void setExceptionFactory(IExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }
}
