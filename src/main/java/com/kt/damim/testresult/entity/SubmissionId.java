package com.kt.damim.testresult.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Submission 엔티티의 복합 키 클래스
 */
public class SubmissionId implements Serializable {
    private Long examId;
    private Long userId;

    public SubmissionId() {}

    public SubmissionId(Long examId, Long userId) {
        this.examId = examId;
        this.userId = userId;
    }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionId that = (SubmissionId) o;
        return Objects.equals(examId, that.examId) && Objects.equals(userId, that.userId);
    }
    @Override public int hashCode() { return Objects.hash(examId, userId); }
}
