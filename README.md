# Test Result API

시험 결과 관리 시스템의 REST API입니다. 학생들의 답안 제출, 조회, 수정 및 삭제 기능을 제공합니다.

## 🚀 주요 기능

- **Submission 관리**: 시험 답안 제출 및 관리
- **Answer 관리**: 개별 문제 답안 제출, 수정, 삭제
- **진행 상태 조회**: 시험 진행 상황 모니터링
- **AI 조언**: 개인 맞춤 AI 조언 제공

## 📋 API 목록

### 1. 헤더 기반 API (X-User-Id 헤더 사용)

#### 1.1 답안 제출
```http
POST /api/exams/{examId}/answers
X-User-Id: {userId}
Content-Type: application/json

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

#### 1.2 Submission 제출 (전체 답안)
```http
POST /api/exams/{examId}/submissions
X-User-Id: {userId}
Content-Type: application/json

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
    }
  ]
}
```

**응답 예시:**
```json
{
  "submissionId": 1,
  "success": true,
  "message": "Submission이 성공적으로 제출되었습니다.",
  "submittedAt": "2024-01-15T10:30:00"
}
```

#### 1.3 Submission 조회
```http
GET /api/exams/submissions/{submissionId}
X-User-Id: {userId}
```

**응답 예시:**
```json
{
  "submissionId": 1,
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
    }
  ]
}
```

#### 1.4 답안 조회
```http
GET /api/exams/submissions/{submissionId}/answers/{questionId}
X-User-Id: {userId}
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

### 2. 평범한 REST API (쿼리 파라미터 사용)

#### 2.1 Submission 생성
```http
POST /api/submissions
Content-Type: application/json

{
  "examId": 1,
  "userId": 1,
  "answers": [
    {
      "questionId": 1,
      "answerText": "정답1",
      "solvingTime": 120
    }
  ]
}
```

#### 2.2 Submission 조회
```http
GET /api/submissions/{submissionId}?userId={userId}
```

#### 2.3 답안 제출
```http
POST /api/submissions/{submissionId}/answers?userId={userId}
Content-Type: application/json

{
  "questionId": 3,
  "answerText": "새로운 답안",
  "solvingTime": 150
}
```

#### 2.4 답안 수정
```http
PUT /api/submissions/{submissionId}/answers/{questionId}?userId={userId}
Content-Type: application/json

{
  "questionId": 1,
  "answerText": "수정된 답안",
  "solvingTime": 200
}
```

#### 2.5 답안 삭제
```http
DELETE /api/submissions/{submissionId}/answers/{questionId}?userId={userId}
```

#### 2.6 Submission 삭제
```http
DELETE /api/submissions/{submissionId}?userId={userId}
```

### 3. 진행 상태 관리 API

#### 3.1 시험 진행 상태 조회
```http
GET /api/exams/{examId}/progress
```

**응답 예시:**
```json
[
  {
    "userId": 1,
    "currentPosition": 3,
    "answered": 2,
    "totalScore": 85.0
  },
  {
    "userId": 2,
    "currentPosition": 1,
    "answered": 1,
    "totalScore": 40.0
  }
]
```

#### 3.2 학생 상세 정보 조회
```http
GET /api/exams/{examId}/students/{studentId}
```

**응답 예시:**
```json
{
  "userId": 1,
  "currentPosition": 3,
  "answered": 2,
  "totalScore": 85.0,
  "mistakes": [
    {
      "questionPosition": 2,
      "questionId": 2,
      "answerKey": "4",
      "studentAnswer": "3",
      "solvingTime": 90
    }
  ]
}
```

### 4. AI 조언 API

#### 4.1 AI 조언 요청
```http
POST /api/exams/{examId}/ai/advice
Content-Type: application/json

{
  "studentId": 1
}
```

**응답 예시:**
```json
{
  "advice": "학생님은 기본 개념은 잘 이해하고 있지만, 계산 실수가 많습니다. 문제를 다시 한 번 확인하는 습관을 기르시면 좋겠습니다.",
  "strengths": ["개념 이해력", "문제 해결 의지"],
  "weaknesses": ["계산 실수", "시간 관리"],
  "recommendations": ["문제 풀이 후 검토하기", "시간 분배 연습하기"]
}
```

## 🔧 기술 스택

- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL (H2 for testing)
- **ORM**: JPA/Hibernate
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Gradle

## 🚀 실행 방법

### 1. 환경 설정
```bash
# 데이터베이스 설정
spring.datasource.url=jdbc:postgresql://localhost:5432/testresult
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. API 문서 확인
```
http://localhost:8080/swagger-ui.html
```

## 📝 데이터베이스 스키마

### 주요 테이블

#### submissions
- submission_id (PK, Auto Increment)
- exam_id (FK)
- user_id
- submitted_at
- total_score
- feedback_status

#### submission_answers
- question_id (PK)
- exam_id (PK)
- user_id (PK)
- answer_text
- is_correct
- score
- solving_time

#### questions
- question_id (PK)
- exam_id (FK)
- body
- answer_key
- points
- position

## 🔒 보안 및 권한

- 사용자는 자신의 submission만 조회/수정 가능
- 시험 상태 검증 (is_ready = true인 시험만 답안 제출 가능)
- 중복 답안 제출 방지

## 📊 에러 처리

### 주요 에러 코드

- `400 Bad Request`: 잘못된 요청 데이터
- `404 Not Found`: 존재하지 않는 리소스
- `403 Forbidden`: 권한 없음
- `409 Conflict`: 중복 제출 시도

### 에러 응답 예시
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "시험을 찾을 수 없습니다: 999",
  "path": "/api/exams/999/answers"
}
```

## 🧪 테스트

### HTTP 파일 테스트
```bash
# test.http 파일을 사용하여 API 테스트
# IntelliJ IDEA, VS Code 등에서 지원
```

### 단위 테스트
```bash
./gradlew test
```

## 📞 지원

문제가 발생하거나 문의사항이 있으시면 이슈를 등록해 주세요.

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
