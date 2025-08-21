package com.kt.damim.testresult.service.impl;

import com.kt.damim.testresult.dto.StudentProgressDto;
import com.kt.damim.testresult.dto.TeacherViewDto;
import com.kt.damim.testresult.entity.Exam;
import com.kt.damim.testresult.entity.Question;
import com.kt.damim.testresult.entity.Submission;
import com.kt.damim.testresult.entity.SubmissionAnswer;
import com.kt.damim.testresult.repository.ExamRepository;
import com.kt.damim.testresult.repository.QuestionRepository;
import com.kt.damim.testresult.repository.SubmissionAnswerRepository;
import com.kt.damim.testresult.repository.SubmissionRepository;
import com.kt.damim.testresult.service.ProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressServiceImpl implements ProgressService {
    
    private final SubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    
    @Override
    public List<StudentProgressDto> getProgress(Long examId, Long since) {
        // 시험 상태 확인 - 출제 준비된 시험만 진행 상태 조회 가능
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("시험을 찾을 수 없습니다: " + examId));
        
        if (!exam.getIsReady()) {
            log.warn("출제 준비되지 않은 시험의 진행 상태 조회 시도: {}", examId);
            throw new IllegalArgumentException("출제 준비되지 않은 시험입니다.");
        }
        
        List<Submission> submissions = submissionRepository.findByExamId(examId);
        
        return submissions.stream()
            .filter(submission -> since == null || submission.getSubmittedAt().toEpochMilli() > since)
            .map(this::convertToStudentProgressDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public TeacherViewDto getStudentDetail(Long examId, Long userId) {
        // 시험 상태 확인 - 출제 준비된 시험만 상세 정보 조회 가능
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("시험을 찾을 수 없습니다: " + examId));
        
        if (!exam.getIsReady()) {
            log.warn("출제 준비되지 않은 시험의 학생 상세 정보 조회 시도: {}", examId);
            throw new IllegalArgumentException("출제 준비되지 않은 시험입니다.");
        }
        
        Submission submission = submissionRepository.findByExamIdAndUserId(examId, userId)
            .orElseThrow(() -> new IllegalArgumentException("학생 제출 기록을 찾을 수 없습니다: " + userId));
        
        List<SubmissionAnswer> answers = submissionAnswerRepository.findByExamIdAndUserId(examId, userId);
        List<TeacherViewDto.MistakeItem> mistakes = answers.stream()
            .filter(answer -> !answer.getIsCorrect())
            .map(this::convertToMistakeItem)
            .collect(Collectors.toList());
        
        return new TeacherViewDto(
            submission.getUserId(),
            0, // TODO: 현재 위치 계산 로직 필요
            0, // TODO: 답변 수 계산 로직 필요
            submission.getTotalScore(),
            mistakes
        );
    }
    
    private StudentProgressDto convertToStudentProgressDto(Submission submission) {
        return new StudentProgressDto(
            submission.getUserId(),
            0, // TODO: 현재 위치 계산 로직 필요
            0, // TODO: 답변 수 계산 로직 필요
            submission.getTotalScore(),
            submission.getSubmittedAt() == null ? null : submission.getSubmittedAt().toEpochMilli()
        );
    }
    
    private TeacherViewDto.MistakeItem convertToMistakeItem(SubmissionAnswer answer) {
        Question question = questionRepository.findById(answer.getQuestionId()).orElse(null);
        if (question == null) {
            return new TeacherViewDto.MistakeItem(0, 0L, "", answer.getAnswerText(), answer.getSolvingTime());
        }
        
        return new TeacherViewDto.MistakeItem(
            question.getPosition(),
            question.getId(),
            question.getAnswerKey(),
            answer.getAnswerText(),
            answer.getSolvingTime() // 푸는 시간 추가
        );
    }
}

