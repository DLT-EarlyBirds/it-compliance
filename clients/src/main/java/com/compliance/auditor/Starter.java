package com.compliance.auditor;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.WebApplicationType.SERVLET;

/**
 * Spring Boot entry point of the auditor client application.
 */
@SpringBootApplication
public class Starter {
    /**
     * Starts our Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Starter.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setWebApplicationType(SERVLET);
        app.run(args);
    }
}