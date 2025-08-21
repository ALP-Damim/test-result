package com.kt.damim.testresult.service.impl;

import com.kt.damim.testresult.dto.*;
import com.kt.damim.testresult.entity.Exam;
import com.kt.damim.testresult.entity.Question;
import com.kt.damim.testresult.entity.Submission;
import com.kt.damim.testresult.entity.SubmissionAnswer;
import com.kt.damim.testresult.repository.ExamRepository;
import com.kt.damim.testresult.repository.QuestionRepository;
import com.kt.damim.testresult.repository.SubmissionAnswerRepository;
import com.kt.damim.testresult.repository.SubmissionRepository;
import com.kt.damim.testresult.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionServiceImpl implements SubmissionService {
    
    private final QuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final ExamRepository examRepository;
    
    @Override
    public SubmitAnswerResponse submitAnswer(Long examId, Long userId ,SubmitAnswerRequest req) {
        // 시험 상태 확인 - 출제 준비된 시험만 답안 제출 가능
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("시험을 찾을 수 없습니다: " + examId));
        
        if (!exam.getIsReady()) {
            log.warn("출제 준비되지 않은 시험에 답안 제출 시도: {}", examId);
            throw new IllegalArgumentException("출제 준비되지 않은 시험입니다.");
        }
        
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
        answer.setSolvingTime(req.solvingTime()); // 푸는 시간 설정
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
    
    @Override
    public SubmitSubmissionResponse submitSubmission(Long examId, Long userId, SubmitSubmissionRequest request) {
        // 시험 상태 확인
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("시험을 찾을 수 없습니다: " + examId));
        
        if (!exam.getIsReady()) {
            log.warn("출제 준비되지 않은 시험에 submission 제출 시도: {}", examId);
            throw new IllegalArgumentException("출제 준비되지 않은 시험입니다.");
        }
        
        // 기존 submission 조회 또는 생성
        Submission submission = getOrCreateSubmission(examId, userId);
        
        // 각 답안 처리
        for (SubmitAnswerRequest answerReq : request.answers()) {
            Question question = questionRepository.findById(answerReq.questionId())
                .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + answerReq.questionId()));
            
            // 시험 ID 검증
            if (!question.getExam().getId().equals(examId)) {
                throw new IllegalArgumentException("시험 ID가 일치하지 않습니다");
            }
            
            // 기존 답안이 있는지 확인
            boolean answerExists = submissionAnswerRepository.existsByQuestionIdAndExamIdAndUserId(
                answerReq.questionId(), examId, userId);
            
            if (!answerExists) {
                // 새 답안 저장
                SubmissionAnswer answer = new SubmissionAnswer();
                answer.setQuestionId(question.getId());
                answer.setExamId(examId);
                answer.setUserId(userId);
                answer.setAnswerText(answerReq.answerText());
                answer.setIsCorrect(isCorrectAnswer(question, answerReq.answerText()));
                answer.setScore(calculateScore(question, answer.getIsCorrect()));
                answer.setSolvingTime(answerReq.solvingTime());
                submissionAnswerRepository.save(answer);
            }
        }
        
        // 총점 업데이트
        updateSubmissionScore(submission);
        
        return new SubmitSubmissionResponse(
            null, // 복합키를 사용하므로 ID가 없음
            true,
            "Submission이 성공적으로 제출되었습니다.",
            LocalDateTime.ofInstant(submission.getSubmittedAt(), ZoneId.systemDefault())
        );
    }
    
    @Override
    public GetSubmissionResponse getSubmission(Long submissionId, Long userId) {
        // submissionId는 examId로 간주
        Submission submission = submissionRepository.findByExamIdAndUserId(submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Submission을 찾을 수 없습니다: " + submissionId));
        
        return buildGetSubmissionResponse(submission);
    }
    
    @Override
    public GetSubmissionResponse getSubmissionByExam(Long examId, Long userId) {
        Submission submission = submissionRepository.findByExamIdAndUserId(examId, userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 시험의 submission을 찾을 수 없습니다."));
        
        return buildGetSubmissionResponse(submission);
    }
    
    @Override
    public GetAnswerResponse getAnswer(Long submissionId, Long questionId, Long userId) {
        // submissionId는 examId로 간주
        Submission submission = submissionRepository.findByExamIdAndUserId(submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Submission을 찾을 수 없습니다: " + submissionId));
        
        // 답안 조회
        SubmissionAnswer answer = submissionAnswerRepository.findByQuestionIdAndExamIdAndUserId(questionId, submission.getExamId(), userId)
            .orElseThrow(() -> new IllegalArgumentException("답안을 찾을 수 없습니다."));
        
        // 문제 정보 조회
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + questionId));
        
        return new GetAnswerResponse(
            submissionId,
            questionId,
            answer.getAnswerText(),
            question.getAnswerKey(),
            answer.getIsCorrect(),
            answer.getSolvingTime(),
            LocalDateTime.ofInstant(submission.getSubmittedAt(), ZoneId.systemDefault()), // SubmissionAnswer에는 answeredAt이 없으므로 submission의 submittedAt 사용
            question.getBody(), // Question의 필드명은 body
            null // Question에는 explanation 필드가 없음
        );
    }
    
    private GetSubmissionResponse buildGetSubmissionResponse(Submission submission) {
        List<SubmissionAnswer> answers = submissionAnswerRepository.findByExamIdAndUserId(
            submission.getExamId(), submission.getUserId());
        
        List<GetSubmissionResponse.SubmissionAnswerDto> answerDtos = answers.stream()
            .map(answer -> {
                Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + answer.getQuestionId()));
                
                return new GetSubmissionResponse.SubmissionAnswerDto(
                    answer.getQuestionId(),
                    question.getPosition(),
                    question.getBody(), // Question의 필드명은 body
                    answer.getAnswerText(),
                    question.getAnswerKey(),
                    answer.getIsCorrect(),
                    answer.getSolvingTime(),
                    LocalDateTime.ofInstant(submission.getSubmittedAt(), ZoneId.systemDefault()) // SubmissionAnswer에는 answeredAt이 없으므로 submission의 submittedAt 사용
                );
            })
            .toList();
        
        List<Question> totalQuestions = questionRepository.findByExamIdOrderByPosition(submission.getExamId());
        
        return new GetSubmissionResponse(
            null, // 복합키를 사용하므로 ID가 없음
            submission.getExamId(),
            submission.getUserId(),
            LocalDateTime.ofInstant(submission.getSubmittedAt(), ZoneId.systemDefault()),
            submission.getTotalScore(),
            totalQuestions.size(),
            answers.size(),
            answerDtos
        );
    }
    
    @Override
    public SubmitAnswerResponse submitSubmissionAnswer(Long submissionId, Long userId, SubmitAnswerRequest request) {
        // submissionId는 examId로 간주
        Submission submission = submissionRepository.findByExamIdAndUserId(submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Submission을 찾을 수 없습니다: " + submissionId));
        
        // 문제 조회
        Question question = questionRepository.findById(request.questionId())
            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + request.questionId()));
        
        // 시험 ID 검증
        if (!question.getExam().getId().equals(submissionId)) {
            throw new IllegalArgumentException("시험 ID가 일치하지 않습니다");
        }
        
        // 기존 답안이 있는지 확인
        boolean answerExists = submissionAnswerRepository.existsByQuestionIdAndExamIdAndUserId(
            request.questionId(), submissionId, userId);
        
        if (answerExists) {
            throw new IllegalArgumentException("이미 제출된 답안입니다. 수정하려면 PUT 메서드를 사용하세요.");
        }
        
        // 새 답안 저장
        SubmissionAnswer answer = new SubmissionAnswer();
        answer.setQuestionId(question.getId());
        answer.setExamId(submissionId);
        answer.setUserId(userId);
        answer.setAnswerText(request.answerText());
        answer.setIsCorrect(isCorrectAnswer(question, request.answerText()));
        answer.setScore(calculateScore(question, answer.getIsCorrect()));
        answer.setSolvingTime(request.solvingTime());
        submissionAnswerRepository.save(answer);
        
        // 총점 업데이트
        updateSubmissionScore(submission);
        
        // 다음 문제 위치 계산
        Integer nextPosition = calculateNextPosition(submissionId, question.getPosition());
        if (nextPosition == null) {
            return SubmitAnswerResponse.finished();
        }
        return SubmitAnswerResponse.next(nextPosition);
    }
    
    @Override
    public SubmitAnswerResponse updateSubmissionAnswer(Long submissionId, Long questionId, Long userId, SubmitAnswerRequest request) {
        // submissionId는 examId로 간주
        Submission submission = submissionRepository.findByExamIdAndUserId(submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Submission을 찾을 수 없습니다: " + submissionId));
        
        // 문제 조회
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("문제를 찾을 수 없습니다: " + questionId));
        
        // 시험 ID 검증
        if (!question.getExam().getId().equals(submissionId)) {
            throw new IllegalArgumentException("시험 ID가 일치하지 않습니다");
        }
        
        // 기존 답안 조회
        SubmissionAnswer existingAnswer = submissionAnswerRepository.findByQuestionIdAndExamIdAndUserId(questionId, submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("수정할 답안을 찾을 수 없습니다."));
        
        // 답안 업데이트
        existingAnswer.setAnswerText(request.answerText());
        existingAnswer.setIsCorrect(isCorrectAnswer(question, request.answerText()));
        existingAnswer.setScore(calculateScore(question, existingAnswer.getIsCorrect()));
        existingAnswer.setSolvingTime(request.solvingTime());
        submissionAnswerRepository.save(existingAnswer);
        
        // 총점 업데이트
        updateSubmissionScore(submission);
        
        // 다음 문제 위치 계산
        Integer nextPosition = calculateNextPosition(submissionId, question.getPosition());
        if (nextPosition == null) {
            return SubmitAnswerResponse.finished();
        }
        return SubmitAnswerResponse.next(nextPosition);
    }
    
    @Override
    public void deleteSubmission(Long submissionId, Long userId) {
        // submissionId는 examId로 간주
        Submission submission = submissionRepository.findByExamIdAndUserId(submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("삭제할 submission을 찾을 수 없습니다: " + submissionId));
        
        // 관련된 모든 답안 삭제
        List<SubmissionAnswer> answers = submissionAnswerRepository.findByExamIdAndUserId(submissionId, userId);
        submissionAnswerRepository.deleteAll(answers);
        
        // submission 삭제
        submissionRepository.delete(submission);
        
        log.info("Submission 삭제 완료: examId={}, userId={}", submissionId, userId);
    }
    
    @Override
    public void deleteSubmissionAnswer(Long submissionId, Long questionId, Long userId) {
        // submissionId는 examId로 간주
        Submission submission = submissionRepository.findByExamIdAndUserId(submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Submission을 찾을 수 없습니다: " + submissionId));
        
        // 답안 조회
        SubmissionAnswer answer = submissionAnswerRepository.findByQuestionIdAndExamIdAndUserId(questionId, submissionId, userId)
            .orElseThrow(() -> new IllegalArgumentException("삭제할 답안을 찾을 수 없습니다."));
        
        // 답안 삭제
        submissionAnswerRepository.delete(answer);
        
        // 총점 재계산
        updateSubmissionScore(submission);
        
        log.info("Submission Answer 삭제 완료: examId={}, questionId={}, userId={}", submissionId, questionId, userId);
    }
}
