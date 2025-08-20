package com.kt.damim.testresult.service;

import com.kt.damim.testresult.dto.SubmitAnswerRequest;
import com.kt.damim.testresult.dto.SubmitAnswerResponse;


public interface SubmissionService {
    SubmitAnswerResponse submitAnswer(Long examId, Long userId, SubmitAnswerRequest req);
}
