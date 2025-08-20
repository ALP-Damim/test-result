package com.kt.damim.testresult.dto;

import com.kt.damim.testresult.entity.Submission;

import java.math.BigDecimal;


/**
 * 교사 모니터 테이블 행
 */
public record StudentProgressDto(
    Long userId,
    int currentPosition,
    int answered,
    BigDecimal totalScore,
    Long updatedAt
) {
    public static StudentProgressDto of(Submission s) {
        return new StudentProgressDto(
            s.getUserId(),
            0, // TODO: 현재 위치 계산 로직 필요
            0, // TODO: 답변 수 계산 로직 필요
            s.getTotalScore(),
            s.getSubmittedAt() == null ? null : s.getSubmittedAt().toEpochMilli()
        );
    }
}
