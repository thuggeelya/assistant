package com.thuggeelya.assistantclient.integration.client;

import com.thuggeelya.assistantclient.integration.dto.RagRequestDto;
import com.thuggeelya.assistantclient.integration.dto.RagResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rag-client", url = "${rag.base-url}")
public interface RagClient {

    @PostMapping("/ask")
    RagResponseDto ask(@RequestBody final RagRequestDto request);

}