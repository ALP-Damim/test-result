package com.kt.damim.testresult.entity;

public enum FeedbackStatus {
    PENDING,      // AI advice 요청 대기
    PROCESSING,   // AI advice 요청 중
    COMPLETED,    // AI advice 완료
    FAILED        // AI advice 실패
}
