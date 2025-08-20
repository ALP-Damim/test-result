package com.kt.damim.testresult.dto;

import com.kt.damim.testresult.entity.Question;
import com.kt.damim.testresult.entity.QuestionType;

import java.math.BigDecimal;
import java.util.List;


/**
 * 한 페이지 = 한 문항 조회 DTO
 */
public record QuestionDto(
    Long id,
    int position,
    QuestionType qtype,
    String body,
    List<String> choices,
    BigDecimal points
) {
    public static QuestionDto of(Question q, List<String> choices) {
        return new QuestionDto(q.getId(), q.getPosition(), q.getQtype(), q.getBody(), choices, q.getPoints());
    }
}
