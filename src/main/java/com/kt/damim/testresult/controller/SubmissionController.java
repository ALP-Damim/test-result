package com.kt.damim.testresult.controller;

import com.kt.damim.testresult.dto.*;
import com.kt.damim.testresult.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Submission 관리", description = "Submission 및 답안 관리 API")
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    
    private final SubmissionService submissionService;
    
    /**
     * Submission 생성
     */
    @Operation(summary = "Submission 생성", description = "새로운 submission을 생성합니다.")
    @PostMapping
    public SubmitSubmissionResponse createSubmission(
        @Valid @RequestBody SubmitSubmissionRequest request
    ) {
        return submissionService.submitSubmission(request.examId(), request.userId(), request);
    }
    
    /**
     * Submission 조회
     */
    @Operation(summary = "Submission 조회", description = "특정 submission의 상세 정보를 조회합니다.")
    @GetMapping("/{submissionId}")
    public GetSubmissionResponse getSubmission(
        @Parameter(description = "Submission ID") @PathVariable Long submissionId,
        @Parameter(description = "사용자 ID") @RequestParam Long userId
    ) {
        return submissionService.getSubmission(submissionId, userId);
    }
    
    /**
     * 시험별 Submission 조회
     */
    @Operation(summary = "시험별 Submission 조회", description = "특정 시험의 submission을 조회합니다.")
    @GetMapping("/exam/{examId}")
    public GetSubmissionResponse getSubmissionByExam(
        @Parameter(description = "시험 ID") @PathVariable Long examId,
        @Parameter(description = "사용자 ID") @RequestParam Long userId
    ) {
        return submissionService.getSubmissionByExam(examId, userId);
    }
    
    /**
     * Submission 답안 제출
     */
    @Operation(summary = "Submission 답안 제출", description = "특정 submission에 개별 답안을 제출합니다.")
    @PostMapping("/{submissionId}/answers")
    public SubmitAnswerResponse submitAnswer(
        @Parameter(description = "Submission ID") @PathVariable Long submissionId,
        @Parameter(description = "사용자 ID") @RequestParam Long userId,
        @Valid @RequestBody SubmitAnswerRequest request
    ) {
        return submissionService.submitSubmissionAnswer(submissionId, userId, request);
    }
    
    /**
     * Submission 답안 수정
     */
    @Operation(summary = "Submission 답안 수정", description = "특정 submission의 개별 답안을 수정합니다.")
    @PutMapping("/{submissionId}/answers/{questionId}")
    public SubmitAnswerResponse updateAnswer(
        @Parameter(description = "Submission ID") @PathVariable Long submissionId,
        @Parameter(description = "문제 ID") @PathVariable Long questionId,
        @Parameter(description = "사용자 ID") @RequestParam Long userId,
        @Valid @RequestBody SubmitAnswerRequest request
    ) {
        return submissionService.updateSubmissionAnswer(submissionId, questionId, userId, request);
    }
    
    /**
     * Submission 답안 조회
     */
    @Operation(summary = "Submission 답안 조회", description = "특정 submission의 특정 문제 답안을 조회합니다.")
    @GetMapping("/{submissionId}/answers/{questionId}")
    public GetAnswerResponse getAnswer(
        @Parameter(description = "Submission ID") @PathVariable Long submissionId,
        @Parameter(description = "문제 ID") @PathVariable Long questionId,
        @Parameter(description = "사용자 ID") @RequestParam Long userId
    ) {
        return submissionService.getAnswer(submissionId, questionId, userId);
    }
    
    /**
     * Submission 삭제
     */
    @Operation(summary = "Submission 삭제", description = "특정 submission을 삭제합니다.")
    @DeleteMapping("/{submissionId}")
    public void deleteSubmission(
        @Parameter(description = "Submission ID") @PathVariable Long submissionId,
        @Parameter(description = "사용자 ID") @RequestParam Long userId
    ) {
        submissionService.deleteSubmission(submissionId, userId);
    }
    
    /**
     * Submission 답안 삭제
     */
    @Operation(summary = "Submission 답안 삭제", description = "특정 submission의 특정 문제 답안을 삭제합니다.")
    @DeleteMapping("/{submissionId}/answers/{questionId}")
    public void deleteAnswer(
        @Parameter(description = "Submission ID") @PathVariable Long submissionId,
        @Parameter(description = "문제 ID") @PathVariable Long questionId,
        @Parameter(description = "사용자 ID") @RequestParam Long userId
    ) {
        submissionService.deleteSubmissionAnswer(submissionId, questionId, userId);
    }
}
