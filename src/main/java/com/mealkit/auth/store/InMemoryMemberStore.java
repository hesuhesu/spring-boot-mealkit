package com.mealkit.auth.store;

import com.mealkit.security.MemberPrincipal;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 데모용 인메모리 회원 — {@code spring.profiles.active=inmemory} 일 때만 활성.
 * <p>
 * 기본은 RDBMS({@link MyBatisMemberStore}). DB 없이 빠르게 돌릴 때만 이 프로필을 켠다.
 * <pre>
 *   # application.yml 또는 CLI
 *   spring.profiles.active=inmemory
 *   # 또는
 *   mvn spring-boot:run -Dspring-boot.run.profiles=inmemory
 * </pre>
 * 기본 계정: admin / password , user / password
 */
@Repository
@Profile("inmemory")
@RequiredArgsConstructor
public class InMemoryMemberStore implements MemberStore {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, MemberPrincipal> members = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        members.put("admin", MemberPrincipal.builder()
                .id(1L)
                .loginId("admin")
                .password(passwordEncoder.encode("password"))
                .role("2")
                .name("관리자")
                .build());
        members.put("user", MemberPrincipal.builder()
                .id(2L)
                .loginId("user")
                .password(passwordEncoder.encode("password"))
                .role("1")
                .name("일반사용자")
                .build());
    }

    @Override
    public Optional<MemberPrincipal> findByLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(members.get(loginId.trim()));
    }
}
