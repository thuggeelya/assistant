package com.thuggeelya.assistantclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableFeignClients
@SpringBootApplication
public class AssistantClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssistantClientApplication.class, args);
    }

}
