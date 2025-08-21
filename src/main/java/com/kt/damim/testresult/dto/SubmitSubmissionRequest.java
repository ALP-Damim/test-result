package com.kt.damim.testresult.dto;

import java.util.List;

/**
 * Submission 제출 요청 DTO
 */
public record SubmitSubmissionRequest(
    Long examId,
    Long userId,
    List<SubmitAnswerRequest> answers
) {}
