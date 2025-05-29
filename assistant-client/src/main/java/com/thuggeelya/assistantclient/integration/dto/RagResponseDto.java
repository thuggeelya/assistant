package com.thuggeelya.assistantclient.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RagResponseDto {

    private String response;
    private String prompt;
}