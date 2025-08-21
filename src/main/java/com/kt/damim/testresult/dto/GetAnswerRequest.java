package com.kt.damim.testresult.dto;

/**
 * 답안 조회 요청 DTO
 */
public record GetAnswerRequest(
    Long submissionId,
    Long questionId
) {}
