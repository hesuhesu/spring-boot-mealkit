package com.mealkit.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyRes {

    @Schema(description = "회원 PK")
    private Long id;

    @Schema(description = "로그인 ID")
    private String loginId;

    @Schema(description = "권한")
    private String role;

    @Schema(description = "표시 이름")
    private String name;
}
