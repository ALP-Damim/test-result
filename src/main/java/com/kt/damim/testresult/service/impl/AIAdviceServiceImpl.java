package com.kt.damim.testresult.service.impl;

import com.kt.damim.testresult.dto.AIAdviceRequest;
import com.kt.damim.testresult.dto.AIAdviceResponse;
import com.kt.damim.testresult.entity.FeedbackStatus;
import com.kt.damim.testresult.entity.Submission;
import com.kt.damim.testresult.repository.SubmissionRepository;
import com.kt.damim.testresult.service.AIAdviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AIAdviceServiceImpl implements AIAdviceService {
    
    private final SubmissionRepository submissionRepository;
    private final RestTemplate restTemplate;
    
    @Override
    public AIAdviceResponse requestAIAdvice(Long examId, Long studentId) {
        // Submission 조회 및 상태 체크
        Submission submission = submissionRepository.findByExamIdAndUserId(examId, studentId)
            .orElseThrow(() -> new IllegalArgumentException("학생 제출 기록을 찾을 수 없습니다: " + studentId));
        
        // 이미 완료된 경우
        if (submission.getFeedbackStatus() == FeedbackStatus.COMPLETED) {
            log.info("AI advice 이미 완료됨: examId={}, studentId={}", examId, studentId);
            return new AIAdviceResponse("OK", false);
        }
        
        // 이미 처리 중인 경우
        if (submission.getFeedbackStatus() == FeedbackStatus.PROCESSING) {
            log.info("AI advice 이미 처리 중: examId={}, studentId={}", examId, studentId);
            return new AIAdviceResponse("OK", false);
        }
        
        // AI advice 요청
        try {
            // 상태를 PROCESSING으로 변경
            submission.setFeedbackStatus(FeedbackStatus.PROCESSING);
            submission.setFeedbackRequestedAt(Instant.now());
            submissionRepository.save(submission);
            
            // 외부 AI API 호출
            String aiUrl = "http://aiURL/api/exams/" + examId + "/ai/advice";
            AIAdviceRequest request = new AIAdviceRequest(studentId);
            
            AIAdviceResponse aiResponse = restTemplate.postForObject(aiUrl, request, AIAdviceResponse.class);
            
            if (aiResponse != null && "OK".equals(aiResponse.status()) && aiResponse.stored()) {
                // AI advice 성공적으로 저장됨
                submission.setFeedbackStatus(FeedbackStatus.COMPLETED);
                submission.setFeedback("AI advice가 성공적으로 생성되었습니다.");
                log.info("AI advice 성공: examId={}, studentId={}", examId, studentId);
            } else {
                // AI advice 저장 실패
                submission.setFeedbackStatus(FeedbackStatus.FAILED);
                submission.setFeedbackRetryCount(submission.getFeedbackRetryCount() + 1);
                log.warn("AI advice 저장 실패: examId={}, studentId={}", examId, studentId);
            }
            
            submissionRepository.save(submission);
            return new AIAdviceResponse("OK", true);
            
        } catch (Exception e) {
            // 에러 발생 시 상태를 FAILED로 변경
            submission.setFeedbackStatus(FeedbackStatus.FAILED);
            submission.setFeedbackRetryCount(submission.getFeedbackRetryCount() + 1);
            submissionRepository.save(submission);
            
            log.error("AI advice 요청 중 오류 발생: examId={}, studentId={}", examId, studentId, e);
            throw new RuntimeException("AI advice 요청 처리 중 오류가 발생했습니다.", e);
        }
    }
}
