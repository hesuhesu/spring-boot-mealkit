package com.mealkit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealkit.common.dto.DefaultRes;
import com.mealkit.common.exception.CommonExceptions;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (CommonExceptions e) {
            SecurityContextHolder.clearContext();
            writeErrorResponse(response, e);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, CommonExceptions e) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        DefaultRes.ApiResponse<Object> body = DefaultRes.ApiResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .result(e.getData())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
