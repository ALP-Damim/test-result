package com.kt.damim.testresult.dto;

import java.time.LocalDateTime;

/**
 * Submission 제출 응답 DTO
 */
public record SubmitSubmissionResponse(
    Long submissionId,
    boolean success,
    String message,
    LocalDateTime submittedAt
) {}
