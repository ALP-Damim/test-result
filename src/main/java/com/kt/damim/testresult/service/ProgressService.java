package com.kt.damim.testresult.service;

import com.kt.damim.testresult.dto.StudentProgressDto;
import com.kt.damim.testresult.dto.TeacherViewDto;

import java.util.List;

public interface ProgressService {
    List<StudentProgressDto> getProgress(Long examId, Long since);
    TeacherViewDto getStudentDetail(Long examId, Long userId);
}
