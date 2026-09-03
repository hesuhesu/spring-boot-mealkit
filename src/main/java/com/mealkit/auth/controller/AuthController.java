package com.mealkit.auth.controller;

import com.mealkit.auth.dto.req.LoginReq;
import com.mealkit.auth.dto.res.AccessTokenRes;
import com.mealkit.auth.dto.res.LoginRes;
import com.mealkit.auth.service.AuthService;
import com.mealkit.auth.support.AuthRefreshCookieSupport;
import com.mealkit.common.dto.DefaultRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mealkit.auth.support.AuthRefreshCookieSupport.COOKIE_NAME;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증/인가 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "accessToken은 body, refreshToken은 httpOnly 쿠키")
    @PostMapping("/login")
    public ResponseEntity<DefaultRes.ApiResponse<LoginRes>> login(
            @Valid @RequestBody LoginReq req,
            HttpServletRequest request
    ) {
        LoginRes response = authService.login(req);
        ResponseEntity<DefaultRes.ApiResponse<LoginRes>> body = DefaultRes.build(response);
        return AuthRefreshCookieSupport.attachRefreshCookie(
                body,
                response.getRefreshToken(),
                authService.getRefreshTokenMaxAgeSeconds(),
                request
        );
    }

    @Operation(summary = "액세스 토큰 재발급")
    @PostMapping("/token/refresh")
    public ResponseEntity<DefaultRes.ApiResponse<AccessTokenRes>> refreshToken(
            @CookieValue(value = COOKIE_NAME, required = false) String refreshTokenCookie,
            HttpServletRequest request
    ) {
        return AuthRefreshCookieSupport.buildTokenRefreshResponse(
                authService.refreshToken(refreshTokenCookie),
                authService.getRefreshTokenMaxAgeSeconds(),
                request
        );
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<DefaultRes.ApiResponse<Void>> logout(HttpServletRequest request) {
        return AuthRefreshCookieSupport.clearRefreshCookieResponse(request);
    }
}
