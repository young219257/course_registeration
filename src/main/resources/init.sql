-- 초기화 스크립트

-- 튜터 테이블 생성
CREATE TABLE IF NOT EXISTS tutor (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- 학생 테이블 생성
CREATE TABLE IF NOT EXISTS student (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- 초기화 데이터 삽입
-- 튜터 데이터 삽입
INSERT INTO tutor (name) VALUES ('John Doe');
INSERT INTO tutor (name) VALUES ('Jane Smith');
INSERT INTO tutor (name) VALUES ('Emily Johnson');

-- 학생 데이터 삽입
INSERT INTO student (name) VALUES ('김영아');
INSERT INTO student (name) VALUES ('박서영');
INSERT INTO student (name) VALUES ('김가영');