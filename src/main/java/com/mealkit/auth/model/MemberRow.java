package com.mealkit.auth.model;

import lombok.Getter;
import lombok.Setter;

/**
 * member 테이블 조회 행.
 * Security 계층({@link com.mealkit.security.MemberPrincipal})과 분리해 두고,
 * {@link com.mealkit.auth.store.MyBatisMemberStore}에서 변환한다.
 */
@Getter
@Setter
public class MemberRow {
    private Long id;
    private String loginId;
    private String password;
    private String role;
    private String name;
}
