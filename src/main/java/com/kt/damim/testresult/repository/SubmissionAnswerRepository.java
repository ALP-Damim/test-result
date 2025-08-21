package com.kt.damim.testresult.repository;

import com.kt.damim.testresult.entity.SubmissionAnswer;
import com.kt.damim.testresult.entity.SubmissionAnswerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, SubmissionAnswerId> {
    List<SubmissionAnswer> findByExamIdAndUserId(Long examId, Long userId);
    
    Optional<SubmissionAnswer> findByQuestionIdAndExamIdAndUserId(Long questionId, Long examId, Long userId);
    
    boolean existsByQuestionIdAndExamIdAndUserId(Long questionId, Long examId, Long userId);
}
