-- Exam 테이블에 is_ready 컬럼 추가
ALTER TABLE exams ADD COLUMN is_ready BOOLEAN NOT NULL DEFAULT FALSE;

-- 기존 데이터가 있다면 기본값 설정
UPDATE exams SET is_ready = FALSE WHERE is_ready IS NULL;
