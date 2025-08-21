# Submission & SubmissionAnswer API 문서

## 개요
이 API는 시험 답안 제출 및 관리를 위한 RESTful API입니다. Submission(전체 답안)과 SubmissionAnswer(개별 답안)에 대한 CRUD 작업을 지원합니다.

## 기본 정보
- **Base URL**: `http://localhost:8080/api/v1`
- **Content-Type**: `application/json`
- **인증**: 현재는 없음 (향후 JWT 토큰 추가 예정)

## API 목록

### 1. Submission APIs

#### 1.1 Submission 생성 (전체 답안 제출)
```http
POST /api/v1/submissions
Content-Type: application/json
```

**요청 예시:**
```json
{
  "examId": 1,
  "userId": 1,
  "answers": [
    {
      "questionId": 1,
      "answerText": "정답1",
      "solvingTime": 120
    },
    {
      "questionId": 2,
      "answerText": "정답2",
      "solvingTime": 180
    },
    {
      "questionId": 3,
      "answerText": "정답3",
      "solvingTime": 90
    }
  ]
}
```

**응답 예시:**
```json
{
  "submissionId": null,
  "success": true,
  "message": "Submission이 성공적으로 제출되었습니다.",
  "submittedAt": "2024-01-15T10:30:00"
}
```

#### 1.2 Submission 조회
```http
GET /api/v1/submissions?examId=1&userId=1
```

**응답 예시:**
```json
{
  "submissionId": null,
  "examId": 1,
  "userId": 1,
  "submittedAt": "2024-01-15T10:30:00",
  "totalScore": 85.0,
  "totalQuestions": 5,
  "answeredQuestions": 3,
  "answers": [
    {
      "questionId": 1,
      "questionPosition": 1,
      "questionText": "1+1은?",
      "studentAnswer": "2",
      "correctAnswer": "2",
      "isCorrect": true,
      "solvingTime": 120,
      "answeredAt": "2024-01-15T10:30:00"
    },
    {
      "questionId": 2,
      "questionPosition": 2,
      "questionText": "2+2는?",
      "studentAnswer": "4",
      "correctAnswer": "4",
      "isCorrect": true,
      "solvingTime": 180,
      "answeredAt": "2024-01-15T10:30:00"
    },
    {
      "questionId": 3,
      "questionPosition": 3,
      "questionText": "3+3은?",
      "studentAnswer": "5",
      "correctAnswer": "6",
      "isCorrect": false,
      "solvingTime": 90,
      "answeredAt": "2024-01-15T10:30:00"
    }
  ]
}
```

#### 1.3 Submission 삭제
```http
DELETE /api/v1/submissions?examId=1&userId=1
```

**응답:** 204 No Content

### 2. SubmissionAnswer APIs

#### 2.1 답안 제출 (개별 문제)
```http
POST /api/v1/submissions/answers?examId=1&userId=1
Content-Type: application/json
```

**요청 예시:**
```json
{
  "questionId": 1,
  "answerText": "정답",
  "solvingTime": 120
}
```

**응답 예시:**
```json
{
  "ok": true,
  "nextIdx": 2,
  "isFinished": false
}
```

#### 2.2 답안 조회
```http
GET /api/v1/submissions/answers?examId=1&questionId=1&userId=1
```

**응답 예시:**
```json
{
  "submissionId": 1,
  "questionId": 1,
  "studentAnswer": "2",
  "correctAnswer": "2",
  "isCorrect": true,
  "solvingTime": 120,
  "answeredAt": "2024-01-15T10:30:00",
  "questionText": "1+1은?",
  "explanation": null
}
```

#### 2.3 답안 수정
```http
PUT /api/v1/submissions/answers?examId=1&questionId=1&userId=1
Content-Type: application/json
```

**요청 예시:**
```json
{
  "questionId": 1,
  "answerText": "수정된 답안",
  "solvingTime": 150
}
```

**응답 예시:**
```json
{
  "ok": true,
  "nextIdx": 2,
  "isFinished": false
}
```

#### 2.4 답안 삭제
```http
DELETE /api/v1/submissions/answers?examId=1&questionId=1&userId=1
```

**응답:** 204 No Content

## 데이터 모델

### SubmitSubmissionRequest
```json
{
  "examId": "Long - 시험 ID",
  "userId": "Long - 사용자 ID",
  "answers": [
    {
      "questionId": "Long - 문제 ID",
      "answerText": "String - 답안 내용",
      "solvingTime": "Integer - 푸는 시간(초)"
    }
  ]
}
```

### SubmitAnswerRequest
```json
{
  "questionId": "Long - 문제 ID",
  "answerText": "String - 답안 내용",
  "solvingTime": "Integer - 푸는 시간(초)"
}
```

### SubmitSubmissionResponse
```json
{
  "submissionId": "Long - 제출 ID (복합키 사용으로 null)",
  "success": "Boolean - 성공 여부",
  "message": "String - 응답 메시지",
  "submittedAt": "LocalDateTime - 제출 시간"
}
```

### GetSubmissionResponse
```json
{
  "submissionId": "Long - 제출 ID (복합키 사용으로 null)",
  "examId": "Long - 시험 ID",
  "userId": "Long - 사용자 ID",
  "submittedAt": "LocalDateTime - 제출 시간",
  "totalScore": "BigDecimal - 총점",
  "totalQuestions": "Integer - 전체 문제 수",
  "answeredQuestions": "Integer - 답안 제출한 문제 수",
  "answers": [
    {
      "questionId": "Long - 문제 ID",
      "questionPosition": "Integer - 문제 순서",
      "questionText": "String - 문제 내용",
      "studentAnswer": "String - 학생 답안",
      "correctAnswer": "String - 정답",
      "isCorrect": "Boolean - 정답 여부",
      "solvingTime": "Integer - 푸는 시간",
      "answeredAt": "LocalDateTime - 답안 제출 시간"
    }
  ]
}
```

### GetAnswerResponse
```json
{
  "submissionId": "Long - 제출 ID",
  "questionId": "Long - 문제 ID",
  "studentAnswer": "String - 학생 답안",
  "correctAnswer": "String - 정답",
  "isCorrect": "Boolean - 정답 여부",
  "solvingTime": "Integer - 푸는 시간",
  "answeredAt": "LocalDateTime - 답안 제출 시간",
  "questionText": "String - 문제 내용",
  "explanation": "String - 해설 (현재 null)"
}
```

### SubmitAnswerResponse
```json
{
  "ok": "Boolean - 성공 여부",
  "nextIdx": "Integer - 다음 문제 번호 (null이면 완료)",
  "isFinished": "Boolean - 시험 완료 여부"
}
```

## HTTP 상태 코드

- **200 OK**: 요청 성공
- **201 Created**: 리소스 생성 성공
- **204 No Content**: 삭제 성공
- **400 Bad Request**: 잘못된 요청 데이터
- **404 Not Found**: 리소스를 찾을 수 없음
- **409 Conflict**: 중복 제출 시도
- **500 Internal Server Error**: 서버 오류

## 에러 응답 예시

### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "시험을 찾을 수 없습니다: 999",
  "path": "/api/v1/submissions"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Submission을 찾을 수 없습니다: 1",
  "path": "/api/v1/submissions"
}
```

### 409 Conflict
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "이미 제출된 답안입니다. 수정하려면 PUT 메서드를 사용하세요.",
  "path": "/api/v1/submissions/answers"
}
```

## 사용 예시

### 1. 시험 답안 전체 제출
```bash
curl -X POST http://localhost:8080/api/v1/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "examId": 1,
    "userId": 1,
    "answers": [
      {"questionId": 1, "answerText": "2", "solvingTime": 120},
      {"questionId": 2, "answerText": "4", "solvingTime": 180}
    ]
  }'
```

### 2. 개별 답안 제출
```bash
curl -X POST "http://localhost:8080/api/v1/submissions/answers?examId=1&userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "questionId": 3,
    "answerText": "6",
    "solvingTime": 90
  }'
```

### 3. 답안 조회
```bash
curl -X GET "http://localhost:8080/api/v1/submissions?examId=1&userId=1"
```

### 4. 답안 수정
```bash
curl -X PUT "http://localhost:8080/api/v1/submissions/answers?examId=1&questionId=1&userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "questionId": 1,
    "answerText": "수정된 답안",
    "solvingTime": 150
  }'
```

### 5. 답안 삭제
```bash
curl -X DELETE "http://localhost:8080/api/v1/submissions/answers?examId=1&questionId=1&userId=1"
```

## 주의사항

1. **복합키 사용**: Submission은 exam_id와 user_id의 복합키를 사용합니다.
2. **중복 제출 방지**: 같은 문제에 대해 중복 제출 시 409 Conflict 에러가 발생합니다.
3. **시험 상태 검증**: 출제 준비되지 않은 시험(is_ready=false)에는 답안 제출이 불가능합니다.
4. **권한 검증**: 현재는 단순한 userId 검증만 수행합니다.

## Swagger UI

API 문서는 다음 URL에서 확인할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```
