package com.thuggeelya.assistantconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class AssistantConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistantConfigServerApplication.class, args);
    }

}
