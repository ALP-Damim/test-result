// src/main/java/com/kt/damim/testresult/entity/SubmissionAnswerId.java
package com.kt.damim.testresult.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * SubmissionAnswer 엔티티의 복합 키 클래스
 */
public class SubmissionAnswerId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long questionId;
    private Long examId;
    private Long userId;

    public SubmissionAnswerId() {}

    public SubmissionAnswerId(Long questionId, Long examId, Long userId) {
        this.questionId = questionId;
        this.examId = examId;
        this.userId = userId;
    }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionAnswerId that = (SubmissionAnswerId) o;
        return Objects.equals(questionId, that.questionId) && 
               Objects.equals(examId, that.examId) && 
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, examId, userId);
    }
}
