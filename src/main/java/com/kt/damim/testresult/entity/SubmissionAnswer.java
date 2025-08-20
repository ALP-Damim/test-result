package com.kt.damim.testresult.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "submission_answers")
@IdClass(SubmissionAnswerId.class)
@Getter @Setter @NoArgsConstructor
public class SubmissionAnswer {
    @Id
    @Column(name = "question_id")
    private Long questionId;

    @Id
    @Column(name = "exam_id")
    private Long examId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "answer_text")
    private String answerText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "score", nullable = false)
    private BigDecimal score = BigDecimal.ZERO;
}