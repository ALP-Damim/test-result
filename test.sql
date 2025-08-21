-- src/test/resources/test.sql
-- 깨끗이 초기화
TRUNCATE TABLE submission_answers, submissions, questions, exams RESTART IDENTITY CASCADE;
-- DROP TABLE IF EXISTS exams CASCADE;
--
-- CREATE TABLE exams (
--                        exam_id     BIGSERIAL PRIMARY KEY,
--                        session_id    BIGINT,
--                        name        VARCHAR(255) NOT NULL,
--                        difficulty  VARCHAR(255),
--                        is_ready    BOOLEAN NOT NULL DEFAULT FALSE,
--                        created_by  BIGINT,
--                        created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
-- );

-- 시험 1개 (is_ready = true로 설정하여 출제 준비 완료 상태)
INSERT INTO exams (exam_id, session_id, name, difficulty, is_ready, created_by, created_at)
VALUES (1001, 501, 'Networking Basics Quiz', 'EASY', true, 8001, NOW());

-- 🔸 qtype 값은 DB 제약에 맞게 사용
--   - 옵션 A(자주 쓰는 패턴): 'MCQ' / 'SHORT'
--   - 옵션 B(만약 제약이 다르면): 'MULTIPLE_CHOICE' / 'SHORT_ANSWER'
--   A로 넣고 에러 나면 B로 교체해서 실행

-- [옵션 A]
INSERT INTO questions (question_id, exam_id, qtype, body, choices, answer_key, points, position)
VALUES
    (2001, 1001, 'MCQ',   'Which protocol is connection-oriented?', '["TCP","UDP","QUIC"]'::jsonb, 'TCP', 5.0, 1),
    (2002, 1001, 'SHORT', 'How many layers are in the TCP/IP model?', NULL, '4', 5.0, 2);

-- -- [옵션 B] 필요 시 위 A를 주석 처리하고 아래로 대체
-- INSERT INTO questions (question_id, exam_id, qtype, body, choices, answer_key, points, position)
-- VALUES
--   (2001, 1001, 'MULTIPLE_CHOICE', 'Which protocol is connection-oriented?', '["TCP","UDP","QUIC"]'::jsonb, 'TCP', 5.0, 1),
--   (2002, 1001, 'SHORT_ANSWER',    'How many layers are in the TCP/IP model?', NULL, '4', 5.0, 2);

-- 제출 헤더(학생 9001/9002)만 생성. 답안은 9001 = 없음(프론트가 처음 제출)
INSERT INTO submissions (exam_id, user_id, submitted_at, total_score, feedback)
VALUES
    (1001, 9001, NOW() - INTERVAL '10 minutes', 0.0, NULL)
ON CONFLICT (exam_id, user_id) DO NOTHING;

INSERT INTO submissions (exam_id, user_id, submitted_at, total_score, feedback)
VALUES
    (1001, 9002, NOW() - INTERVAL '8 minutes', 0.0, NULL)
ON CONFLICT (exam_id, user_id) DO NOTHING;

-- 통계 확인용으로 다른 학생(9002)의 일부 오답만 1개 넣음 (PK 충돌 방지)
INSERT INTO submission_answers (question_id, exam_id, user_id, answer_text, is_correct, score)
VALUES (2001, 1001, 9002, 'UDP', FALSE, 0.0)
ON CONFLICT (question_id, exam_id, user_id) DO NOTHING;
