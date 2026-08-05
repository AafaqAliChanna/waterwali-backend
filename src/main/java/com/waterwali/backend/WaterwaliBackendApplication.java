package com.waterwali.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This is the entry point of the whole backend.
// Running this file starts an embedded web server (Tomcat) on port 8080.
@SpringBootApplication
public class WaterwaliBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(WaterwaliBackendApplication.class, args);
    }
}
