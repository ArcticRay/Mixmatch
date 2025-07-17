package com.example.arcticray.mixmatch.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorPageConfig {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return factory -> {
            // all 404s / 400s / 500s to /error
            factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error"));
            factory.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error"));
            factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error"));
        };
    }
}
