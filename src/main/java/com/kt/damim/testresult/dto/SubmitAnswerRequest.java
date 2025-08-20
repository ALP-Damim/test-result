package com.kt.damim.testresult.dto;

import jakarta.validation.constraints.NotNull;


public record SubmitAnswerRequest(
    @NotNull Long questionId,
    String answerText
) {}
