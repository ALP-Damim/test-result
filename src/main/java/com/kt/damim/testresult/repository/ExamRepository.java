package com.kt.damim.testresult.repository;

import com.kt.damim.testresult.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    // 기본 CRUD 메서드는 JpaRepository에서 제공
}
