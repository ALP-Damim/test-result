package com.kt.damim.testresult.dto;

import jakarta.validation.constraints.NotNull;

public record SubmitAnswerRequest(
    @NotNull Long questionId,
    String answerText,
    Integer solvingTime // 초 단위로 푸는 시간
) {}
