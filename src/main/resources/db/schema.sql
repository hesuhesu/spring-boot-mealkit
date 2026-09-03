-- 회원 테이블 (스타터 최소 스키마)
-- H2(MODE=MySQL) · MariaDB 공통으로 쓸 수 있게 AUTO_INCREMENT 사용
-- 운영에서 member / member_info 분리(ctl)로 확장할 때는 이 DDL을 교체하고 MemberMapper XML만 맞추면 된다.
CREATE TABLE IF NOT EXISTS member (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    login_id    VARCHAR(64)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(32)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_member_login_id UNIQUE (login_id)
);
