package com.thuggeelya.assistantclient.controller;

import com.thuggeelya.assistantclient.integration.OllamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ask")
public class ChatController {

    private final OllamaService ollamaService;

    @GetMapping
    public Mono<String> ask(@RequestParam("prompt") final String prompt) {
        return ollamaService.askOllama(prompt);
    }

}
