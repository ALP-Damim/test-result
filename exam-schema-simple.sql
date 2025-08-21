-- Exam 테이블에 is_ready 컬럼 추가
ALTER TABLE exams ADD COLUMN is_ready BOOLEAN NOT NULL DEFAULT FALSE;

-- 기존 데이터가 있다면 기본값 설정
UPDATE exams SET is_ready = FALSE WHERE is_ready IS NULL;

-- class_id 컬럼을 session_id로 변경
ALTER TABLE exams RENAME COLUMN class_id TO session_id;

-- submission_answers 테이블에 solving_time 컬럼 추가
ALTER TABLE submission_answers ADD COLUMN solving_time INTEGER;

-- submissions 테이블에 AI advice 관련 컬럼 추가
ALTER TABLE submissions ADD COLUMN feedback_status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE submissions ADD COLUMN feedback_requested_at TIMESTAMPTZ;
ALTER TABLE submissions ADD COLUMN feedback_retry_count INTEGER DEFAULT 0;
