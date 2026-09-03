# 테스트 가이드

Spring Boot 스타터의 회귀는 **JUnit 5 + MockMvc** 로 고정한다.  
프론트(`vite-ts-mealkit`)의 Playwright와 짝을 이룬다 — 백엔드는 API 계약·보안, 프론트는 브라우저 경로.

## 준비

```bash
# JDK 17+
java -version

mvn -v
```

또는 프로젝트 루트의 Maven Wrapper:

```bash
./mvnw test
./mvnw spring-boot:run
```

## 실행

```bash
mvn test
mvn -Dtest=AuthFlowTest test
mvn -Dtest=HealthControllerTest test
```

## 무엇을 커버하나

| 클래스 | 시나리오 |
| --- | --- |
| `HealthControllerTest` | `/api/health` 공개, `code=0000`, `status=UP` |
| `AuthFlowTest` | 로그인 성공·실패, refresh 쿠키, `/api/my` 인가 |

## 데모 계정

| loginId | password | role |
| --- | --- | --- |
| `admin` | `password` | `2` |
| `user` | `password` | `1` |

기본 테스트는 `local`(H2) 프로필 — `MemberDataInitializer` 가 시드.  
인메모리만 검증하려면 `-Dspring.profiles.active=inmemory`.

## 새 테스트 추가

1. `@SpringBootTest` + `@AutoConfigureMockMvc`
2. 인증 필요 API는 로그인 → `Authorization: Bearer …` 헤더
3. 응답은 HTTP 상태와 `$.code` 를 함께 검증 (`0000` / `E100001` …)
4. 쿠키는 `MvcResult.getResponse().getHeader("Set-Cookie")` 확인

## 수동 스모크

```bash
mvn spring-boot:run
```

- Swagger: http://localhost:8080/swagger-ui.html
- Health: `curl http://localhost:8080/api/health`
- Login:

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"loginId\":\"admin\",\"password\":\"password\"}"
```

## CI 스케치

```yaml
- uses: actions/setup-java@v4
  with:
    java-version: '17'
    distribution: 'temurin'
- run: mvn -B test
```

## 프론트 연동

`vite-ts-mealkit` 의 `VITE_SERVER_URL=http://localhost:8080`  
CORS에 `http://localhost:5173`, `5177` 이 들어 있다 (`mealkit.cors.allowed-origins`).
