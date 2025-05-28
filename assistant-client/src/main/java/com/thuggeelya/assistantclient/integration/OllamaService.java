package com.thuggeelya.assistantclient.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OllamaService {

    private final String model;
    private final WebClient webClient;

    public OllamaService(@Value("${ollama.base-url}") final String baseUrl,
                         @Value("${ollama.model}") final String model) {

        this.model = model;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "ollama", fallbackMethod = "askFallback")
    public Mono<String> askOllama(final String prompt) {

        return webClient.post()
                        .uri("/api/generate")
                        .bodyValue(
                                Map.of(
                                        "model", model,
                                        "prompt", prompt,
                                        "stream", false
                                )
                        )
                        .retrieve()
                        .bodyToMono(String.class);
    }

    public Mono<String> askFallback(final String prompt, final Throwable t) {
        return Mono.just("Сервис временно недоступен. Попробуйте позже.");
    }
}
