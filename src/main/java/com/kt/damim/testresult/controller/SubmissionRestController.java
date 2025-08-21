package com.kt.damim.testresult.controller;

import com.kt.damim.testresult.dto.*;
import com.kt.damim.testresult.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submission API", description = "시험 답안 제출 및 관리 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubmissionRestController {

    private final SubmissionService submissionService;

    // ===== SUBMISSION APIs =====

    @Operation(summary = "Submission 생성", description = "새로운 시험 답안을 제출합니다.")
    @PostMapping("/submissions")
    public ResponseEntity<SubmitSubmissionResponse> createSubmission(
            @Valid @RequestBody SubmitSubmissionRequest request) {
        SubmitSubmissionResponse response = submissionService.submitSubmission(
                request.examId(), request.userId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submission 조회", description = "특정 시험의 답안을 조회합니다.")
    @GetMapping("/submissions")
    public ResponseEntity<GetSubmissionResponse> getSubmission(
            @Parameter(description = "시험 ID") @RequestParam Long examId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        GetSubmissionResponse response = submissionService.getSubmissionByExam(examId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submission 삭제", description = "특정 시험의 답안을 삭제합니다.")
    @DeleteMapping("/submissions")
    public ResponseEntity<Void> deleteSubmission(
            @Parameter(description = "시험 ID") @RequestParam Long examId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        submissionService.deleteSubmission(examId, userId);
        return ResponseEntity.noContent().build();
    }

    // ===== SUBMISSION ANSWER APIs =====

    @Operation(summary = "답안 제출", description = "특정 문제의 답안을 제출합니다.")
    @PostMapping("/submissions/answers")
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @Parameter(description = "시험 ID") @RequestParam Long examId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        SubmitAnswerResponse response = submissionService.submitAnswer(examId, userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "답안 조회", description = "특정 문제의 답안을 조회합니다.")
    @GetMapping("/submissions/answers")
    public ResponseEntity<GetAnswerResponse> getAnswer(
            @Parameter(description = "시험 ID") @RequestParam Long examId,
            @Parameter(description = "문제 ID") @RequestParam Long questionId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        GetAnswerResponse response = submissionService.getAnswer(examId, questionId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "답안 수정", description = "특정 문제의 답안을 수정합니다.")
    @PutMapping("/submissions/answers")
    public ResponseEntity<SubmitAnswerResponse> updateAnswer(
            @Parameter(description = "시험 ID") @RequestParam Long examId,
            @Parameter(description = "문제 ID") @RequestParam Long questionId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        SubmitAnswerResponse response = submissionService.updateSubmissionAnswer(examId, questionId, userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "답안 삭제", description = "특정 문제의 답안을 삭제합니다.")
    @DeleteMapping("/submissions/answers")
    public ResponseEntity<Void> deleteAnswer(
            @Parameter(description = "시험 ID") @RequestParam Long examId,
            @Parameter(description = "문제 ID") @RequestParam Long questionId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        submissionService.deleteSubmissionAnswer(examId, questionId, userId);
        return ResponseEntity.noContent().build();
    }
}
