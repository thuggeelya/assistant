package com.thuggeelya.assistantclient.integration.service;

import com.thuggeelya.assistantclient.integration.client.RagClient;
import com.thuggeelya.assistantclient.integration.dto.RagRequestDto;
import com.thuggeelya.assistantclient.integration.dto.RagResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class RAGService {

    private final RagClient ragClient;

    @TimeLimiter(name = "rag")
    @CircuitBreaker(name = "rag", fallbackMethod = "fallbackAnswer")
    public CompletableFuture<String> handleUserInput(final String question) {

        return CompletableFuture.supplyAsync(() -> {

            final RagRequestDto request = new RagRequestDto(question);
            final RagResponseDto response = ragClient.ask(request);

            log.debug("Generated prompt:\n{}", response.getPrompt());

            return response.getResponse();
        });
    }

    public CompletableFuture<String> fallbackAnswer(final String question, final Throwable t) {

        log.debug("RAGService error: {}", t.getMessage(), t);

        return CompletableFuture.failedFuture(
                new RuntimeException("Ассистент не смог обработать ваш вопрос, попробуйте позже.")
        );
    }

}
