package com.kt.damim.testresult.controller;

import com.kt.damim.testresult.dto.*;
import com.kt.damim.testresult.service.ProgressService;
import com.kt.damim.testresult.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ProgressController {
    
    private final SubmissionService submissionService;
    private final ProgressService progressService;
    
    /**
     * 제출 답변 제출
     * @param examId 시험 ID
     * @param userId 사용자 ID
     * @param req 제출 답변 요청 정보
     * @return 제출 답변 응답 정보
     * 
      */ 
    @PostMapping("/{examId}/answers")
    public SubmitAnswerResponse submitAnswer(
        @PathVariable Long examId,
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody SubmitAnswerRequest req
    ) {
        return submissionService.submitAnswer(examId, userId, req); // ← 서비스에 userId 전달
    }
    /**
     * 시험 진행 상태 조회
     * @param examId 시험 ID
     * @param since 조회 시작 시간
     * @return 학생 진행 상태 목록
     */
    @GetMapping("/{examId}/progress")
    public List<StudentProgressDto> getProgress(
        @PathVariable Long examId,
        @RequestParam(required = false) Long since
    ) {
        return progressService.getProgress(examId, since);
    }
    
    /**
     * 학생 상세 정보 조회
     * @param examId 시험 ID
     * @param studentId 학생 ID
     * @return 학생 상세 정보
     */
    @GetMapping("/{examId}/students/{studentId}")
    public TeacherViewDto getStudentDetail(
        @PathVariable Long examId,
        @PathVariable Long studentId
    ) {
        return progressService.getStudentDetail(examId, studentId);
    }
}
