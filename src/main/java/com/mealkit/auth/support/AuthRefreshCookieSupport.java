package com.mealkit.auth.support;

import com.mealkit.auth.dto.res.AccessTokenRes;
import com.mealkit.auth.dto.res.TokenRefreshRes;
import com.mealkit.common.dto.DefaultRes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

/** refreshToken httpOnly 쿠키 발급·삭제 — vite-ts-mealkit withCredentials 연동 */
public final class AuthRefreshCookieSupport {

    public static final String COOKIE_NAME = "mealkit_refreshToken";

    private AuthRefreshCookieSupport() {
    }

    public static ResponseEntity<DefaultRes.ApiResponse<AccessTokenRes>> buildTokenRefreshResponse(
            TokenRefreshRes tokenPair,
            long refreshMaxAgeSeconds,
            HttpServletRequest request
    ) {
        boolean secure = isHttpsRequest(request);
        String sameSite = secure ? "None" : "Lax";

        ResponseCookie refreshCookie = createRefreshCookie(
                tokenPair.getRefreshToken(),
                refreshMaxAgeSeconds,
                secure,
                sameSite
        );

        DefaultRes.ApiResponse<AccessTokenRes> body = DefaultRes.ApiResponse.<AccessTokenRes>builder()
                .code("0000")
                .message("토큰이 재발급되었습니다.")
                .result(AccessTokenRes.builder()
                        .accessToken(tokenPair.getAccessToken())
                        .build())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(body);
    }

    public static <T> ResponseEntity<DefaultRes.ApiResponse<T>> attachRefreshCookie(
            ResponseEntity<DefaultRes.ApiResponse<T>> response,
            String refreshToken,
            long refreshMaxAgeSeconds,
            HttpServletRequest request
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return response;
        }

        boolean secure = isHttpsRequest(request);
        String sameSite = secure ? "None" : "Lax";
        ResponseCookie refreshCookie = createRefreshCookie(
                refreshToken,
                refreshMaxAgeSeconds,
                secure,
                sameSite
        );

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response.getBody());
    }

    public static ResponseEntity<DefaultRes.ApiResponse<Void>> clearRefreshCookieResponse(
            HttpServletRequest request
    ) {
        boolean secure = isHttpsRequest(request);
        String sameSite = secure ? "None" : "Lax";
        ResponseCookie cleared = clearRefreshCookie(secure, sameSite);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .body(DefaultRes.ApiResponse.<Void>builder()
                        .code("0000")
                        .message("로그아웃되었습니다.")
                        .build());
    }

    private static ResponseCookie createRefreshCookie(
            String token,
            long maxAgeSeconds,
            boolean secure,
            String sameSite
    ) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(maxAgeSeconds)
                .build();
    }

    private static ResponseCookie clearRefreshCookie(boolean secure, String sameSite) {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(0)
                .build();
    }

    private static boolean isHttpsRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return forwardedProto != null && "https".equalsIgnoreCase(forwardedProto);
    }
}
