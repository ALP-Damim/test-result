package com.kt.damim.testresult.service;

import com.kt.damim.testresult.dto.*;

public interface SubmissionService {
    SubmitAnswerResponse submitAnswer(Long examId, Long userId, SubmitAnswerRequest req);
    
    SubmitSubmissionResponse submitSubmission(Long examId, Long userId, SubmitSubmissionRequest request);
    
    GetSubmissionResponse getSubmission(Long submissionId, Long userId);
    
    GetSubmissionResponse getSubmissionByExam(Long examId, Long userId);
    
    GetAnswerResponse getAnswer(Long submissionId, Long questionId, Long userId);
    
    SubmitAnswerResponse submitSubmissionAnswer(Long submissionId, Long userId, SubmitAnswerRequest request);
    
    SubmitAnswerResponse updateSubmissionAnswer(Long submissionId, Long questionId, Long userId, SubmitAnswerRequest request);
    
    void deleteSubmission(Long submissionId, Long userId);
    
    void deleteSubmissionAnswer(Long submissionId, Long questionId, Long userId);
}
