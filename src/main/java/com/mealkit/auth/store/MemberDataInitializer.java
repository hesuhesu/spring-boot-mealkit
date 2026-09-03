package com.mealkit.auth.store;

import com.mealkit.auth.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 데모 계정 시드 (admin/user · password).
 * BCrypt는 실행 시 인코딩하므로 data.sql에 해시 하드코딩을 두지 않는다.
 * 운영 DB에는 이 컴포넌트를 끄거나({@code @Profile}) 시드 정책을 분리한다.
 */
@Component
@Profile("!inmemory")
@RequiredArgsConstructor
@Slf4j
public class MemberDataInitializer implements ApplicationRunner {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (memberMapper.countAll() > 0) {
            return;
        }
        String encoded = passwordEncoder.encode("password");
        memberMapper.insert("admin", encoded, "2", "관리자");
        memberMapper.insert("user", encoded, "1", "일반사용자");
        log.info("데모 회원 시드 완료 — admin/user (password)");
    }
}
