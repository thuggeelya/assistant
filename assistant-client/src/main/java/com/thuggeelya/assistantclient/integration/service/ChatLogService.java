package com.thuggeelya.assistantclient.integration.service;

import com.thuggeelya.assistantclient.dao.ChatLogRepository;
import com.thuggeelya.assistantclient.dao.entity.ChatLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatLogService {

    private final ChatLogRepository repository;

    @Async
    public void save(final String question, final String prompt, final String response, final String source) {

        final ChatLog chatLog = new ChatLog();

        chatLog.setQuestion(question);
        chatLog.setPrompt(prompt);
        chatLog.setResponse(response);
        chatLog.setSource(source);

        repository.save(chatLog);

        log.debug("Log saved: {}", chatLog.getId());
    }
}
