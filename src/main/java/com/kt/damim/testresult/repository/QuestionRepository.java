package com.kt.damim.testresult.repository;

import com.kt.damim.testresult.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamIdOrderByPosition(Long examId);
}
