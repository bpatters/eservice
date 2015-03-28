package com.myl.eservice.cli.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myl.eservice.model.impl.ServerAuthentication;
import com.myl.eservice.model.user.IUser;
import com.myl.eservice.model.user.impl.elasticsearch.SystemUser;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by bpatterson on 3/4/15.
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = {
        "com.myl.eservice.common",
        "com.myl.eservice.model",
        "com.myl.eservice.dao",
        "com.myl.eservice.managers",
        "com.myl.eservice.cli.base"})
public class BaseCLI implements CommandLineRunner {
    private Logger LOGGER = Log.getLogger(BaseCLI.class);
    @Autowired
    private ObjectMapper objectMapper;
    private IUser systemUser = new SystemUser();

    public void run(String... args) throws JsonProcessingException {
        // setup this CLI to run as the system user
        SecurityContextHolder.getContext().setAuthentication(new ServerAuthentication(systemUser));
        // implement your CLI code here

        System.exit(0);
    }

    public static void main(String[] args) throws Throwable {
        SpringApplication app = new SpringApplication(BaseCLI.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);
    }
}
