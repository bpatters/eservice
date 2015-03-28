package com.myl.eservice.web.config;

import com.myl.eservice.web.filters.ExceptionFilter;
import com.myl.eservice.web.filters.LoginFilter;
import com.myl.eservice.web.filters.StatelessCSRFFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter  implements AuthenticationEntryPoint{

    @Autowired
    AuthenticationProvider authProvider;
    @Autowired
    StatelessCSRFFilter statelessCSRFFilter;
    @Autowired
    LoginFilter loginFilter;
    @Autowired
    ExceptionFilter exceptionFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/ws/user/**").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(this)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .csrf().disable()
                .addFilterBefore(statelessCSRFFilter, CsrfFilter.class)
                .addFilterBefore(exceptionFilter, StatelessCSRFFilter.class)
                .addFilterAfter(loginFilter,StatelessCSRFFilter.class);
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }


}
