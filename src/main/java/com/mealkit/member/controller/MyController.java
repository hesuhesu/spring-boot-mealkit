package com.mealkit.member.controller;

import com.mealkit.common.dto.DefaultRes;
import com.mealkit.member.dto.MyRes;
import com.mealkit.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my")
@Tag(name = "내 정보 API")
public class MyController {

    @Operation(summary = "내 정보 조회")
    @GetMapping
    public ResponseEntity<DefaultRes.ApiResponse<MyRes>> me(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        MyRes result = MyRes.builder()
                .id(principal.getId())
                .loginId(principal.getLoginId())
                .role(principal.getRole())
                .name(principal.getName())
                .build();
        return DefaultRes.build(result);
    }
}
