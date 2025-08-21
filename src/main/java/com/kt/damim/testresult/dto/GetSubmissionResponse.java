package com.kt.damim.testresult.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Submission 조회 응답 DTO
 */
public record GetSubmissionResponse(
    Long submissionId,
    Long examId,
    Long userId,
    LocalDateTime submittedAt,
    BigDecimal totalScore,
    int totalQuestions,
    int answeredQuestions,
    List<SubmissionAnswerDto> answers
) {
    public record SubmissionAnswerDto(
        Long questionId,
        int questionPosition,
        String questionText,
        String studentAnswer,
        String correctAnswer,
        boolean isCorrect,
        Integer solvingTime,
        LocalDateTime answeredAt
    ) {}
}
