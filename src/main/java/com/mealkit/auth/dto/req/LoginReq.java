package com.mealkit.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginReq {

    @NotBlank(message = "아이디는 필수입니다.")
    @Schema(description = "아이디", example = "admin")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "패스워드", example = "password")
    private String password;
}
