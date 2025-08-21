package com.kt.damim.testresult.dto;

import java.time.LocalDateTime;

/**
 * 답안 조회 응답 DTO
 */
public record GetAnswerResponse(
    Long submissionId,
    Long questionId,
    String studentAnswer,
    String correctAnswer,
    boolean isCorrect,
    Integer solvingTime,
    LocalDateTime answeredAt,
    String questionText,
    String explanation
) {}
