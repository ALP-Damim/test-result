package com.kt.damim.testresult.service;

import com.kt.damim.testresult.dto.AIAdviceRequest;
import com.kt.damim.testresult.dto.AIAdviceResponse;

public interface AIAdviceService {
    AIAdviceResponse requestAIAdvice(Long examId, Long studentId);
}
