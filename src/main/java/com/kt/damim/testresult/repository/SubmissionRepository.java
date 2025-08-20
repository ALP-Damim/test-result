package com.kt.damim.testresult.repository;

import com.kt.damim.testresult.entity.Submission;
import com.kt.damim.testresult.entity.SubmissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SubmissionRepository extends JpaRepository<Submission, SubmissionId> {
    List<Submission> findByExamId(Long examId);
    Optional<Submission> findByExamIdAndUserId(Long examId, Long userId);
}
