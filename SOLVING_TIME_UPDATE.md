# Solving Time 필드 추가 변경사항

## 개요
`submission_answers` 테이블에 각 문항별 푸는 시간을 저장할 수 있는 `solving_time` 필드를 추가했습니다.

## 변경된 파일들

### 1. 엔티티 (Entity)
- `SubmissionAnswer.java`: `solvingTime` 필드 추가 (Integer 타입, 초 단위)

### 2. DTO 클래스들
- `SubmitAnswerRequest.java`: `solvingTime` 필드 추가
- `TeacherViewDto.java`: `MistakeItem`에 `solvingTime` 필드 추가

### 3. 서비스 클래스들
- `SubmissionServiceImpl.java`: 답안 제출 시 `solvingTime` 저장 로직 추가
- `ProgressServiceImpl.java`: 교사용 상세 조회 시 `solvingTime` 정보 포함

### 4. 테스트 파일
- `test.http`: `solvingTime` 필드를 포함한 테스트 요청으로 업데이트

### 5. 데이터베이스 스키마
- `add_solving_time_column.sql`: `solving_time` 컬럼 추가 SQL 스크립트

## 사용 방법

### 프론트엔드에서 답안 제출 시
```json
{
  "questionId": 2001,
  "answerText": "TCP",
  "solvingTime": 45
}
```

### 교사용 상세 조회 시
`TeacherViewDto.MistakeItem`에 `solvingTime` 정보가 포함되어 반환됩니다.

## 데이터베이스 업데이트
다음 SQL 스크립트를 실행하여 데이터베이스에 컬럼을 추가하세요:

```sql
ALTER TABLE submission_answers ADD COLUMN solving_time INTEGER;
```

## 주의사항
- `solvingTime`은 초 단위로 저장됩니다
- 기존 데이터의 경우 `solvingTime`은 `null`로 설정됩니다
- 프론트엔드에서 각 문항별 푸는 시간을 계산하여 전송해야 합니다
