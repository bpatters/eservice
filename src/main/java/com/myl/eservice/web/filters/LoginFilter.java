package com.myl.eservice.web.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myl.eservice.common.errorhandling.IExceptionFactory;
import com.myl.eservice.model.impl.ServerAuthentication;
import com.myl.eservice.model.user.IUser;
import com.myl.eservice.web.dto.user.LoginDTO;
import com.myl.eservice.web.dto.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bpatterson
 */
@Component
public class LoginFilter extends OncePerRequestFilter {
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private IExceptionFactory exceptionFactory;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().endsWith("/login")) {
            response.setContentType("application/json");

            LoginDTO loginDTO = getObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
            UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
            Authentication auth = getAuthenticationProvider().authenticate(loginToken);
            // create our own authentication object, lets us customize as necessary, including hide raw password from code
            ServerAuthentication serverAuth = new ServerAuthentication((IUser) auth.getPrincipal());
            SecurityContextHolder.getContext().setAuthentication(serverAuth);

            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            UserDTO userDTO = new UserDTO((IUser) serverAuth.getPrincipal());

            response.setStatus(HttpServletResponse.SC_OK);
            this.getObjectMapper().writeValue(response.getOutputStream(), userDTO);
            return;
        } else if (request.getRequestURI().endsWith("/logout")) {
            SecurityContextHolder.getContext().setAuthentication(null);
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        filterChain.doFilter(request, response);
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public IExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }

    public void setExceptionFactory(IExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
