package com.mealkit.auth.store;

import com.mealkit.security.MemberPrincipal;

import java.util.Optional;

/**
 * 회원 조회 포트.
 * <ul>
 *   <li>기본: {@link MyBatisMemberStore} (H2 local / MariaDB mariadb 프로필)</li>
 *   <li>옵션: {@link InMemoryMemberStore} ({@code inmemory} 프로필)</li>
 * </ul>
 * {@link com.mealkit.auth.service.AuthService} 는 이 인터페이스만 의존한다 — 구현 교체 시 서비스 수정 불필요.
 */
public interface MemberStore {
    Optional<MemberPrincipal> findByLoginId(String loginId);
}
