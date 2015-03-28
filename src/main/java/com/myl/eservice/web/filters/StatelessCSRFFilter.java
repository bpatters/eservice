package com.myl.eservice.web.filters;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bpatterson
 */
@Component
public class StatelessCSRFFilter extends OncePerRequestFilter {
    private static final String CSRF_TOKEN = "X-CSRF-TOKEN";
    private static final String X_CSRF_TOKEN = "X-CSRF-TOKEN";
    private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String csrfTokenValue = request.getHeader(X_CSRF_TOKEN);
        final Cookie[] cookies = request.getCookies();
        String csrfCookieValue = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CSRF_TOKEN)) {
                    csrfCookieValue = cookie.getValue();
                }
            }
        }
        if (csrfTokenValue == null || !csrfTokenValue.equals(csrfCookieValue)) {
            accessDeniedHandler.handle(request, response, new AccessDeniedException(
                    "Missing or non-matching CSRF-token"));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
