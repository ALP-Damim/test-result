package com.kt.damim.testresult.dto;

import java.math.BigDecimal;
import java.util.List;


/**
 * 특정 학생 상세 화면 (틀린 문항 목록 포함)
 */
public record TeacherViewDto(
    Long userId,
    int currentPosition,
    int answered,
    BigDecimal totalScore,
    List<MistakeItem> mistakes
) {
    public record MistakeItem(
        int questionPosition,
        Long questionId,
        String answerKey,
        String studentAnswer
    ) {}
}
