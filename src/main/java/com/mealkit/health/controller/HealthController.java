package com.mealkit.health.controller;

import com.mealkit.common.dto.DefaultRes;
import com.mealkit.health.dto.HealthRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "헬스체크 API")
public class HealthController {

    private final Environment environment;

    @Operation(summary = "헬스체크")
    @GetMapping
    public ResponseEntity<DefaultRes.ApiResponse<HealthRes>> health(
            @RequestParam(required = false, defaultValue = "false") boolean includeDetail
    ) {
        String active = String.join(",", Arrays.asList(environment.getActiveProfiles()));
        if (active.isBlank()) {
            active = "default";
        }

        HealthRes result = HealthRes.builder()
                .status("UP")
                .service("spring-boot-mealkit")
                .profile(includeDetail ? active : null)
                .build();

        return DefaultRes.build(result);
    }
}
