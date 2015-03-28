package com.myl.eservice.web.controller;

import com.myl.eservice.common.errorhandling.IExceptionFactory;
import com.myl.eservice.model.user.IUser;
import com.myl.eservice.managers.user.IUserManager;
import com.myl.eservice.managers.user.IUserObjectFactory;
import com.myl.eservice.web.dto.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bpatterson on 2/5/15.
 */
@RestController
@RequestMapping(value = "/ws/user", consumes = "application/json", produces = "application/json")
public class UserController {
    @Autowired
    private
    IUserManager userManager;
    @Autowired
    private IUserObjectFactory userObjectFactory;
    @Autowired
    private
    PasswordEncoder passwordEncoder;
    @Autowired
    private
    IExceptionFactory exceptionFactory;
    @Autowired
    private
    AuthenticationProvider authenticationProvider;

    @RequestMapping(method = RequestMethod.GET)
    public UserDTO getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof IUser) {
            return new UserDTO((IUser) auth.getPrincipal());
        } else {
            return null;
        }
    }
    @RequestMapping(method = RequestMethod.POST)
    public UserDTO createUser(@RequestBody UserDTO dto) {
        IUser newUser = this.getUserObjectFactory().newUser();

        dto.copyToModel(newUser);
        newUser.setPassword(this.getPasswordEncoder().encode(dto.getPassword()));
        return new UserDTO(getUserManager().createUser(newUser));
    }

    @RequestMapping(value = "/logout")
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public IUserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    public IUserObjectFactory getUserObjectFactory() {
        return userObjectFactory;
    }

    public void setUserObjectFactory(IUserObjectFactory userObjectFactory) {
        this.userObjectFactory = userObjectFactory;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public IExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }

    public void setExceptionFactory(IExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }
}
