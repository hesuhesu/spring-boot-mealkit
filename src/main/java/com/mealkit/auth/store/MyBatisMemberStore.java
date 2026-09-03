package com.mealkit.auth.store;

import com.mealkit.auth.mapper.MemberMapper;
import com.mealkit.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RDBMS(MyBatis) 회원 저장소 — 기본 구현 ({@code local} / {@code mariadb}).
 * <p>
 * 인메모리로 되돌리려면 {@code spring.profiles.active=inmemory} 를 사용한다.
 * ({@link InMemoryMemberStore} 참고)
 */
@Repository
@Profile("!inmemory")
@RequiredArgsConstructor
public class MyBatisMemberStore implements MemberStore {

    private final MemberMapper memberMapper;

    @Override
    public Optional<MemberPrincipal> findByLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            return Optional.empty();
        }
        return memberMapper.findByLoginId(loginId.trim())
                .map(row -> MemberPrincipal.builder()
                        .id(row.getId())
                        .loginId(row.getLoginId())
                        .password(row.getPassword())
                        .role(row.getRole())
                        .name(row.getName())
                        .build());
    }
}
