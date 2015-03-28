package com.myl.eservice.web;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by bpatters on 1/17/15.
 */


@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = {
        "com.myl.eservice.common",
        "com.myl.eservice.model",
        "com.myl.eservice.dao",
        "com.myl.eservice.managers",
        "com.myl.eservice.web"})
public class WebApplication {

    public static void main(String[] args) throws Throwable {
        SpringApplication app = new SpringApplication(WebApplication.class);
        ConfigurableApplicationContext ctx = app.run(args);
    }

}
