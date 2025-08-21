package com.kt.damim.testresult.dto;

/**
 * Submission 조회 요청 DTO
 */
public record GetSubmissionRequest(
    Long submissionId,
    Long examId,
    Long userId
) {}
