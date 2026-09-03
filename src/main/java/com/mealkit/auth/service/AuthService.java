package com.mealkit.auth.service;

import com.mealkit.auth.dto.req.LoginReq;
import com.mealkit.auth.dto.res.LoginRes;
import com.mealkit.auth.dto.res.TokenRefreshRes;
import com.mealkit.auth.store.MemberStore;
import com.mealkit.common.exception.CommonExceptions;
import com.mealkit.common.exception.enums.ExceptionEnum;
import com.mealkit.security.JwtTokenProvider;
import com.mealkit.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 로그인·토큰 재발급.
 * 회원 조회는 {@link MemberStore} 포트만 사용한다 — 기본 RDBMS(MyBatis), 선택 인메모리.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    // 구현체: MyBatisMemberStore (!inmemory) | InMemoryMemberStore (inmemory)
    private final MemberStore memberStore;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginRes login(LoginReq req) {
        if (req == null || req.getLoginId() == null || req.getLoginId().isBlank()) {
            throw authFailed();
        }

        String loginId = req.getLoginId().trim();
        MemberPrincipal member = memberStore.findByLoginId(loginId)
                .orElseThrow(() -> {
                    log.warn("로그인 실패 — 사용자 없음 loginId={}", loginId);
                    return authFailed();
                });

        if (req.getPassword() == null || req.getPassword().isBlank()
                || !passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            log.warn("로그인 실패 — 비밀번호 불일치 loginId={}", loginId);
            throw authFailed();
        }

        TokenRefreshRes tokens = issueTokenPair(member);
        return LoginRes.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .role(member.getRole())
                .name(member.getName())
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .build();
    }

    public TokenRefreshRes refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CommonExceptions(ExceptionEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        String loginId = jwtTokenProvider.extractLoginIdFromRefreshToken(refreshToken);
        MemberPrincipal member = memberStore.findByLoginId(loginId)
                .orElseThrow(() ->
                        new CommonExceptions(ExceptionEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED));

        return issueTokenPair(member);
    }

    public long getRefreshTokenMaxAgeSeconds() {
        return Math.max(0L, jwtTokenProvider.getRefreshExpirationMs() / 1000L);
    }

    private TokenRefreshRes issueTokenPair(MemberPrincipal member) {
        return TokenRefreshRes.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(member))
                .refreshToken(jwtTokenProvider.generateRefreshToken(member))
                .build();
    }

    private CommonExceptions authFailed() {
        return new CommonExceptions(ExceptionEnum.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
    }
}
