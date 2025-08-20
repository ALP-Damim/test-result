package com.kt.damim.testresult.service.impl;

import com.kt.damim.testresult.dto.SubmitAnswerRequest;
import com.kt.damim.testresult.dto.SubmitAnswerResponse;
import com.kt.damim.testresult.entity.Question;
import com.kt.damim.testresult.entity.Submission;
import com.kt.damim.testresult.entity.SubmissionAnswer;
import com.kt.damim.testresult.repository.QuestionRepository;
import com.kt.damim.testresult.repository.SubmissionAnswerRepository;
import com.kt.damim.testresult.repository.SubmissionRepository;
import com.kt.damim.testresult.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionServiceImpl implements SubmissionService {
    
    private final QuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    
    @Override
    public SubmitAnswerResponse submitAnswer(Long examId, Long userId ,SubmitAnswerRequest req) {
        // 문제 조회
        Question question = questionRepository.findById(req.questionId())
            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + req.questionId()));
        
        // 시험 ID 검증
        if (!question.getExam().getId().equals(examId)) {
            throw new IllegalArgumentException("시험 ID가 일치하지 않습니다");
        }
        
        // 학생 시도 기록 조회 (임시로 userId를 1L로 설정)
        Submission submission = getOrCreateSubmission(examId, userId);
        
        // 답안 저장
        SubmissionAnswer answer = new SubmissionAnswer();
        answer.setQuestionId(question.getId());
        answer.setExamId(examId);
        answer.setUserId(userId);
        answer.setAnswerText(req.answerText());
        answer.setIsCorrect(isCorrectAnswer(question, req.answerText()));
        answer.setScore(calculateScore(question, answer.getIsCorrect()));
        submissionAnswerRepository.save(answer);
        
        // 제출 기록 업데이트
        updateSubmissionScore(submission);
        
        // 다음 문제 위치 계산
        Integer nextPosition = calculateNextPosition(examId, question.getPosition());
        if (nextPosition == null) {
            return SubmitAnswerResponse.finished();
        }
        return SubmitAnswerResponse.next(nextPosition);
    }
    
    private Submission getOrCreateSubmission(Long examId, Long userId) {
        return submissionRepository.findByExamIdAndUserId(examId, userId)
            .orElseGet(() -> {
                Submission newSubmission = new Submission();
                newSubmission.setExamId(examId);
                newSubmission.setUserId(userId);
                newSubmission.setTotalScore(BigDecimal.ZERO);
                return submissionRepository.save(newSubmission);
            });
    }
    
    private boolean isCorrectAnswer(Question question, String studentAnswer) {
        if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
            return false;
        }
        
        String answerKey = question.getAnswerKey();
        if (answerKey == null) {
            return false;
        }
        
        return answerKey.trim().equalsIgnoreCase(studentAnswer.trim());
    }
    
    private BigDecimal calculateScore(Question question, boolean isCorrect) {
        if (isCorrect) {
            return question.getPoints();
        }
        return BigDecimal.ZERO;
    }
    
    private void updateSubmissionScore(Submission submission) {
        List<SubmissionAnswer> answers = submissionAnswerRepository.findByExamIdAndUserId(
            submission.getExamId(), submission.getUserId());
        
        BigDecimal totalScore = answers.stream()
            .map(SubmissionAnswer::getScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        submission.setTotalScore(totalScore);
        submissionRepository.save(submission);
    }
    
    private Integer calculateNextPosition(Long examId, int currentPosition) {
        List<Question> questions = questionRepository.findByExamIdOrderByPosition(examId);
        if (currentPosition >= questions.size()) {
            return null; // 모든 문제 완료
        }
        return currentPosition + 1;
    }
}
