-- submission_answers 테이블에 solving_time 컬럼 추가
ALTER TABLE submission_answers ADD COLUMN solving_time INTEGER;

-- 기존 데이터에 대한 기본값 설정 (선택사항)
-- UPDATE submission_answers SET solving_time = 0 WHERE solving_time IS NULL;

-- 컬럼 설명 추가 (선택사항)
COMMENT ON COLUMN submission_answers.solving_time IS '각 문항별 푸는 시간 (초 단위)';
