package com.kt.damim.testresult.controller;

import com.kt.damim.testresult.dto.*;
import com.kt.damim.testresult.service.ProgressService;
import com.kt.damim.testresult.service.SubmissionService;
import com.kt.damim.testresult.service.AIAdviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "시험 진행 관리", description = "시험 진행 상태 및 AI 조언 관련 API")
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ProgressController {
    
    private final SubmissionService submissionService;
    private final ProgressService progressService;
    private final AIAdviceService aiAdviceService;
    
    /**
     * 제출 답변 제출
     */
    @Operation(summary = "답안 제출", description = "학생이 특정 문제에 대한 답안을 제출합니다.")
    @PostMapping("/{examId}/answers")
    public SubmitAnswerResponse submitAnswer(
        @Parameter(description = "시험 ID") @PathVariable Long examId,
        @Parameter(description = "사용자 ID") @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody SubmitAnswerRequest req
    ) {
        return submissionService.submitAnswer(examId, userId, req);
    }
    
    /**
     * 시험 진행 상태 조회
     */
    @Operation(summary = "진행 상태 조회", description = "시험의 전체 학생 진행 상태를 조회합니다.")
    @GetMapping("/{examId}/progress")
    public List<StudentProgressDto> getProgress(
        @Parameter(description = "시험 ID") @PathVariable Long examId,
        @Parameter(description = "조회 시작 시간 (선택사항)") @RequestParam(required = false) Long since
    ) {
        return progressService.getProgress(examId, since);
    }
    
    /**
     * 학생 상세 정보 조회
     */
    @Operation(summary = "학생 상세 정보", description = "특정 학생의 시험 진행 상세 정보를 조회합니다.")
    @GetMapping("/{examId}/students/{studentId}")
    public TeacherViewDto getStudentDetail(
        @Parameter(description = "시험 ID") @PathVariable Long examId,
        @Parameter(description = "학생 ID") @PathVariable Long studentId
    ) {
        return progressService.getStudentDetail(examId, studentId);
    }
    
    /**
     * AI Advice 요청
     */
    @Operation(summary = "AI 조언 요청", description = "특정 학생에 대한 AI 개인 맞춤 조언을 요청합니다.")
    @PostMapping("/{examId}/ai/advice")
    public AIAdviceResponse requestAIAdvice(
        @Parameter(description = "시험 ID") @PathVariable Long examId,
        @Parameter(description = "AI 조언 요청 정보") @RequestBody AIAdviceRequest request
    ) {
        return aiAdviceService.requestAIAdvice(examId, request.studentId());
    }
}
