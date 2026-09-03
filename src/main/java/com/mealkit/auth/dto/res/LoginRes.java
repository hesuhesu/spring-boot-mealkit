package com.mealkit.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRes {

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "로그인 ID")
    private String loginId;

    @Schema(description = "권한 코드")
    private String role;

    @Schema(description = "회원 PK")
    private Long id;

    @Schema(description = "표시 이름")
    private String name;

    /** httpOnly 쿠키로만 전달 */
    @JsonIgnore
    private String refreshToken;
}
