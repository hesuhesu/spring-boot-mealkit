# 아키텍처

ctl_backend 에서 가져온 골격과, 스타터에서 의도적으로 줄인 부분을 정리한다.

## 한눈에

> Cursor 미리보기는 SVG 임베드를 막는다. 파일: [architecture-layers.svg](assets/architecture-layers.svg)

```mermaid
flowchart TB
  C[controller]
  S[service]
  ST[MemberStore]
  J[JwtTokenProvider / Filter]
  R[DefaultRes + GlobalExceptionHandler]
  FE[vite-ts-mealkit]

  C --> S
  S --> ST
  S --> J
  C --> R
  FE -->|Bearer + cookie| C
```

## 패키지

```
com.mealkit
  MealkitApplication
  common/          DefaultRes, exception, SecurityConfig, Swagger, CORS
  security/        JWT filter · provider · principal · entry point
  auth/            login · refresh · logout · store · mapper · model
  health/          GET /api/health
  member/          GET /api/my
```

도메인이 늘면 ctl 처럼 `com.mealkit.{domain}.{controller|service|mapper|dto}` 로 확장한다.

## 응답 계약

프론트와 동일:

```json
{ "code": "0000", "message": "Success", "result": { } }
```

- 성공: HTTP 200 + `code=0000` (`DefaultRes.build`)
- 업무/인증 실패: `CommonExceptions` → `GlobalExceptionHandler` (`E100001` 등)
- 미인증 API: 401 + JSON (`JwtAuthenticationEntryPoint`)

## 보안

파일: [architecture-auth-flow.svg](assets/architecture-auth-flow.svg)

| 항목 | 내용 |
| --- | --- |
| 세션 | STATELESS |
| Access | `Authorization: Bearer` |
| Refresh | httpOnly 쿠키 `mealkit_refreshToken` |
| 화이트리스트 | `/api/auth/**`(login/refresh/logout), `/api/health`, swagger |
| 그 외 `/api/**` | authenticated |

JWT claim: `sub`(loginId), `role`, `identifyKey`, `name`, `type`(access|refresh)

## 회원 저장소 (RDBMS 기본)

```
AuthService → MemberStore
                ├─ MyBatisMemberStore (!inmemory) → MemberMapper → member 테이블
                └─ InMemoryMemberStore (inmemory)
```

| 프로필 | 구현 | 설정 |
| --- | --- | --- |
| `local` | H2 + `MyBatisMemberStore` | `application-local.yml`, `db/schema.sql` |
| `mariadb` | MariaDB + 동일 Store | `application-mariadb.yml` |
| `inmemory` | `InMemoryMemberStore` | `application-inmemory.yml` (JDBC/MyBatis exclude) |

`AuthService` 는 포트만 의존 — 구현 교체 시 서비스 수정 불필요.  
소스 주석: `MemberStore`, `MyBatisMemberStore`, `InMemoryMemberStore`, `Member.xml`.

ctl 처럼 `member` + `member_info` 조인이 필요하면 `Member.xml` / `MemberRow` 만 확장하면 된다.

## ctl 대비 생략 (의도)

| ctl | mealkit |
| --- | --- |
| MyBatis + MariaDB | MyBatis + H2(기본) / MariaDB 프로필 |
| war + 정적 SPA | jar API 서버 |
| 비밀번호 재설정·메일 | 미포함 |
| 메뉴 권한 | role claim 만 |
| 대량 도메인 | auth / health / my |

## 새 API 추가 절차

1. `dto/req`, `dto/res` 작성 (`@Valid`)
2. `service` — 실패는 `throw new CommonExceptions(ExceptionEnum.…)`
3. `controller` — `DefaultRes.build(result)`
4. 보호 API면 Security whitelist에 넣지 않음
5. `docs` / Swagger group 필요 시 `SwaggerConfig`에 패키지 추가
6. `MockMvc` 테스트 추가

## 프론트 페어

| 백엔드 | vite-ts-mealkit |
| --- | --- |
| `POST /api/auth/login` | `usePostAuthLogin` |
| `POST /api/auth/token/refresh` | axios 401 interceptor |
| `POST /api/auth/logout` | `usePostAuthLogout` |
| `GET /api/health` | `useGetHealth` |
| `GET /api/my` | 이후 RequireAuth 확장 |
