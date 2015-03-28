package com.myl.eservice.web.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myl.eservice.common.errorhandling.IExceptionFactory;
import com.myl.eservice.common.errorhandling.impl.ValidationException;
import com.myl.eservice.common.errorhandling.impl.ErrorMessage;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bpatterson on 2/21/15.
 */
@Component
public class ExceptionFilter extends OncePerRequestFilter {
    private Logger LOGGER = Log.getLogger(ExceptionFilter.class);

    @Autowired
    private IExceptionFactory exceptionFactory;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ValidationException) {
                LOGGER.info(ex.getCause().getMessage());
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                response.setContentType("application/json");
                this.getObjectMapper().writeValue(response.getOutputStream(),
                        new ErrorMessage(HttpServletResponse.SC_NOT_ACCEPTABLE, ex.getCause().getMessage()));

            } else if (ex instanceof BadCredentialsException) {
                LOGGER.info(ex.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                this.getObjectMapper().writeValue(response.getOutputStream(),
                        new ErrorMessage(HttpServletResponse.SC_FORBIDDEN, "Invalid username or password."));
            } else {
                LOGGER.warn(ex);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                this.getObjectMapper().writeValue(response.getOutputStream(),
                        this.getExceptionFactory().getErrorMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error.unknown"));
            }
        }
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
