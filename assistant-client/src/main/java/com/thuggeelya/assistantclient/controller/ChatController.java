package com.thuggeelya.assistantclient.controller;

import com.thuggeelya.assistantclient.integration.service.OllamaService;
import com.thuggeelya.assistantclient.integration.service.RAGService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ask")
public class ChatController {

    private final RAGService ragService;
    private final OllamaService ollamaService;

    @Value("${rag.enabled:true}")
    private boolean ragEnabled;

    @GetMapping
    public ResponseEntity<?> ask(@RequestParam("prompt") final String prompt) {

        if (ragEnabled) {
            return ok(ragService.handleUserInput(prompt));
        } else {
            return ok(ollamaService.askOllama(prompt));
        }
    }

}
