-- tutor 테이블 생성
CREATE TABLE IF NOT EXISTS tutor (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- student 테이블 생성
CREATE TABLE IF NOT EXISTS student (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- time_slot 테이블 생성
CREATE TABLE IF NOT EXISTS time_slot (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         start_time TIMESTAMP(6) NOT NULL,
    end_time TIMESTAMP(6) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    tutor_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tutor FOREIGN KEY (tutor_id) REFERENCES tutor(id) ON DELETE CASCADE
    );

-- 초기화 데이터 삽입

-- tutor 데이터 삽입 (테이블에 데이터가 없을 경우에만)
INSERT INTO tutor (name)
SELECT 'John Doe' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tutor WHERE name = 'John Doe');

INSERT INTO tutor (name)
SELECT 'Jane Smith' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM tutor WHERE name = 'Jane Smith');

-- student 데이터 삽입
INSERT INTO student (name)
SELECT '김영아' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM student WHERE name = '김영아');

INSERT INTO student (name)
SELECT '박서영' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM student WHERE name = '박서영');


-- time_slot 데이터 삽입 (중복 방지)
INSERT INTO time_slot (start_time, end_time, is_available, tutor_id)
SELECT '2024-10-21 09:30:00', '2024-10-21 10:00:00', TRUE, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM time_slot
    WHERE tutor_id = 1 AND start_time = '2024-10-21 09:30:00' AND end_time = '2024-10-21 10:00:00'
);
INSERT INTO time_slot (start_time, end_time, is_available, tutor_id)
SELECT '2024-10-21 10:00:00', '2024-10-21 10:30:00', TRUE, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM time_slot
    WHERE tutor_id = 1 AND start_time = '2024-10-21 10:00:00' AND end_time = '2024-10-21 10:30:00'
);
INSERT INTO time_slot (start_time, end_time, is_available, tutor_id)
SELECT '2024-10-21 09:30:00', '2024-10-21 10:00:00', TRUE, 2
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM time_slot
    WHERE tutor_id = 2 AND start_time = '2024-10-21 09:30:00' AND end_time = '2024-10-21 10:00:00'
);

INSERT INTO time_slot (start_time, end_time, is_available, tutor_id)
SELECT '2024-10-22 11:00:00', '2024-10-22 11:30:00', FALSE, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM time_slot
    WHERE tutor_id = 1 AND start_time = '2024-10-22 11:00:00' AND end_time = '2024-10-22 11:30:00'
);

INSERT INTO time_slot (start_time, end_time, is_available, tutor_id)
SELECT '2024-10-23 11:00:00', '2024-10-23 12:00:00', TRUE, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM time_slot
    WHERE tutor_id = 1 AND start_time = '2024-10-23 11:00:00' AND end_time = '2024-10-23 12:00:00'
);
INSERT INTO time_slot (start_time, end_time, is_available, tutor_id)
SELECT '2024-10-24 11:00:00', '2024-10-24 12:00:00', TRUE, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM time_slot
    WHERE tutor_id = 1 AND start_time = '2024-10-24 11:00:00' AND end_time = '2024-10-24 12:00:00'
);
INSERT INTO time_slot (start_time, end_time, is_available, tutor_id)
SELECT '2024-10-25 11:00:00', '2024-10-25 12:00:00', FALSE, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM time_slot
    WHERE tutor_id = 1 AND start_time = '2024-10-25 11:00:00' AND end_time = '2024-10-25 12:00:00'
);
