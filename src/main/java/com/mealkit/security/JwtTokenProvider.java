package com.mealkit.security;

import com.mealkit.common.exception.CommonExceptions;
import com.mealkit.common.exception.enums.ExceptionEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Access / Refresh JWT 발급·검증.
 * 스타터는 DB 조회 없이 claim 만으로 Authentication 을 구성한다.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-secret:}")
    private String refreshSecret;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshExpiration;

    private Key signingKey;
    private Key refreshSigningKey;

    @PostConstruct
    void initSigningKeys() {
        signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        String resolvedRefreshSecret = StringUtils.hasText(refreshSecret)
                ? refreshSecret
                : secret + "-refresh";
        refreshSigningKey = Keys.hmacShaKeyFor(resolvedRefreshSecret.getBytes());
    }

    public String generateAccessToken(MemberPrincipal member) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(member.getLoginId())
                .claim("role", member.getRole())
                .claim("identifyKey", String.valueOf(member.getId()))
                .claim("name", member.getName())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(MemberPrincipal member) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(member.getLoginId())
                .claim("role", member.getRole())
                .claim("identifyKey", String.valueOf(member.getId()))
                .claim("name", member.getName())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpiration))
                .signWith(refreshSigningKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractLoginIdFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshSigningKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!TOKEN_TYPE_REFRESH.equals(claims.get(TOKEN_TYPE_CLAIM, String.class))) {
                throw new CommonExceptions(ExceptionEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
            }

            String loginId = claims.getSubject();
            if (loginId == null || loginId.isBlank()) {
                throw new CommonExceptions(ExceptionEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
            }
            return loginId;
        } catch (ExpiredJwtException e) {
            throw new CommonExceptions(ExceptionEnum.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        } catch (CommonExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new CommonExceptions(ExceptionEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String loginId = claims.getSubject();
        if (loginId == null || loginId.isBlank()) {
            throw new CommonExceptions(ExceptionEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        MemberPrincipal principal = MemberPrincipal.builder()
                .id(parseLongSafe(claims.get("identifyKey", String.class)))
                .loginId(loginId)
                .role(claims.get("role", String.class) != null ? claims.get("role", String.class) : "1")
                .name(claims.get("name", String.class) != null ? claims.get("name", String.class) : loginId)
                .password("")
                .build();

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getClaims(token));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Long parseLongSafe(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public long getRefreshExpirationMs() {
        return refreshExpiration;
    }
}
