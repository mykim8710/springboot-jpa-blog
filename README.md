# Springboot + JPA 간단한 CRUD Blog

## Project outline
[Project Info]
  - Springboot 2.7.7, Java 11
  - Name(project name) : mykimblog
  - Language : Java
  - Type : Gradle(Groovy)
  - Packaging : Jar
  - TestCode : JUnit5

[Project Metadata]
  - Group: com.mykim
  - Artifact: blog
  - Package name: com.mykim.blog

[Dependencies]
  - Spring Web
  - Spring Data JPA
  - Lombok
  - H2 Database
  - Thymeleaf
  - Querydsl
  - Jwt
  - Redis
  - Spring Rest Docs
[package design]
  ```
  └── src
    ├── main
    │   ├── java
    │   │     └── com.mykim.blog
    │   │            ├── MykimblogApplication(C)
    │   │            ├── auth
    │   │            │    ├── manual
    │   │            │    │    │── annotaion
    │   │            │    │    │     │── CustomJwtAuthorization(I, @)
    │   │            │    │    │     └── CustomSessionAuthorization(I, @)
    │   │            │    │    │── api
    │   │            │    │    │     └── AuthApiController(C)
    │   │            │    │    │── argumentresolver
    │   │            │    │    │     │── JwtAuthArgumentResolver(C)
    │   │            │    │    │     └── SessionAuthArgumentResolver(C)
    │   │            │    │    │── config
    │   │            │    │    │     └── JwtConfig(C)
    │   │            │    │    │── domain
    │   │            │    │    │     └── AuthSession(C)
    │   │            │    │    │── dto
    │   │            │    │    │     │── request
    │   │            │    │    │     │      └── RequestAuthDto(C)
    │   │            │    │    │     └── response
    │   │            │    │    │            └── ResponseAuthDto(C)
    │   │            │    │    │── repository
    │   │            │    │    │     └── AuthSessionRepository(C)
    │   │            │    │    └── service
    │   │            │    │          │── JwtAuthService(C)
    │   │            │    │          └── SessionAuthService(C)
    │   │            │    └── security
    │   │            │         │── jwt
    │   │            │         │     │── JwtAuthenticationFilter(C)
    │   │            │         │     │── JwtAuthorizationFilter(C)
    │   │            │         │     │── JwtProperties(C)
    │   │            │         │     └── JwtProvider(C)
    │   │            │         │── principal
    │   │            │         │     │── PrincipalDetail(C)
    │   │            │         │     └── PrincipalDetailService(C)
    │   │            │         │── CustomAuthenticationEntryPoint(C)
    │   │            │         │── CustomAccessDeniedHandler(C)
    │   │            │         │── CustomAuthenticationFailureHandler(C)
    │   │            │         │── CustomAuthenticationSuccessHandler(C)
    │   │            │         │── CustomAuthenticationProvider(C)
    │   │            │         │── CorsConfig(C)
    │   │            │         └── SecurityConfig(C)
    │   │            ├── global
    │   │            │    ├── config
    │   │            │    │    │── querydsl
    │   │            │    │    │     └── QuerydslConfig(C)
    │   │            │    │    │── redis
    │   │            │    │    │     │── EmbeddedRedisConfig(C)
    │   │            │    │    │     └── RedisConfig(C)
    │   │            │    │    └── web
    │   │            │    │          └── WebMvcConfig(C)
    │   │            │    ├── entity
    │   │            │    │    │── BaseEntity(C)
    │   │            │    │    └── BaseTimeEntity(C)
    │   │            │    ├── init
    │   │            │    │    └── InitPostCreate(C)
    │   │            │    ├── pagination
    │   │            │    │    │── CustomPaginationRequest(C)
    │   │            │    │    │── CustomPaginationResponse(C)
    │   │            │    │    └── CustomSortingRequest(C)
    │   │            │    └── result
    │   │            │         │── error
    │   │            │         │     │── exception
    │   │            │         │     │      │── BusinessRollbackException(C)
    │   │            │         │     │      └── NotFoundException(C)
    │   │            │         │     │── ErrorCode(E)
    │   │            │         │     │── ValidationError(C)
    │   │            │         │     └── GlobalExceptionHandler(C)
    │   │            │         │── SuccessCode(E)
    │   │            │         └── CommonResult(C)
    │   │            │── member
    │   │            │    ├── api
    │   │            │    │    └── MemberApiController(C)
    │   │            │    ├── domain
    │   │            │    │    ├── Member(C)
    │   │            │    │    └── MemberRole(E)
    │   │            │    ├── dto.request
    │   │            │    │    ├── RequestMemberSignInDto(C)
    │   │            │    │    └── RequestMemberInsertDto(C)
    │   │            │    ├── exception
    │   │            │    │    ├── DuplicateMemberEmailException(C)
    │   │            │    │    ├── InvalidSignInInfoException(C)
    │   │            │    │    └── UnAuthorizedMemberException(C)
    │   │            │    ├── repository
    │   │            │    │    └── MemberRepository(I)
    │   │            │    └── service
    │   │            │         └── MemberService(C)
    │   │            │── post
    │   │            │    ├── api
    │   │            │    │    └── PostApiController(C)
    │   │            │    ├── domain
    │   │            │    │    ├── Post(C)
    │   │            │    │    ├── PostEditor(C)
    │   │            │    │    └── PostCategory(E) : not used
    │   │            │    ├── dto
    │   │            │    │    ├── request
    │   │            │    │    │      ├── RequestPostCreateDto(C)
    │   │            │    │    │      └── RequestPostUpdateDto(C)
    │   │            │    │    ├── response
    │   │            │    │    │      ├── ResponsePostListDto(C)
    │   │            │    │    │      └── ResponsePostSelectDto(C)
    │   │            │    ├── exception
    │   │            │    │    └── NotPermitAccessPostException(C)
    │   │            │    ├── repository
    │   │            │    │    ├── PostQuerydslRepository(C)
    │   │            │    │    └── PostRepository(I)
    │   │            │    └── service
    │   │            │         └── PostService(C)
    │   │            └── tip
    │   │                 ├── api
    │   │                 │    └── ExportApiController(C)
    │   │                 ├── enums
    │   │                 │    └── ExportType(E)
    │   │                 └── service
    │   │                      ├── ExportService(I)
    │   │                      ├── ExcelExportService(C)
    │   │                      ├── HwpExportService(C)
    │   │                      ├── PdfExportService(C)
    │   │                      ├── ExportServiceFinder(C)
    │   │                      └── PostService(C)
    │   │        
    │   └── resources
    │       ├── static           
    │       │     └── index.html
    │       ├── templates           
    │       ├── data.sql           
    │       └── application.yaml
    ├── test
    │   ├── java
    │   │     └── com.mykim.blog
    │   │            ├── MykimblogApplicationTests(C)
    │   │            ├── auth
    │   │            │    ├── manual
    │   │            │    │    │── api
    │   │            │    │    │     └── AuthApiControllerTest(C)
    │   │            │    │    └── service
    │   │            │    │          └── SessionAuthServiceTest(C)
    │   │            │    └── security
    │   │            │         └── SpringSecurityJwtTest(C)
    │   │            ├── member
    │   │            │    ├── api
    │   │            │    │    └── MemberApiControllerTest(C)
    │   │            │    └── service
    │   │            │         └── MemberServiceTest(C)
    │   │            ├── post
    │   │            │    ├── api
    │   │                      ├── PostApiControllerDocTest(C)
    │   │            │    │    └── PostApiControllerTest(C)
    │   │            │    └── service
    │   │            │         └── PostServiceTest(C)
    │   │            └── tip
    │   │                 └── api
    │   │                      └── ExportApiControllerTest(C)
    │   │        
    │   └── resources
    │       └── application.yaml
  ```

## 요구사항 정의
[Member]
- 회원 가입
  - email(아이디), password, username, memberRole(ROLE_ADMIN, ROLE_MEMBER), createdDate, lastModifiedDate
  - email duplicate check
  - email, password validation check
- 로그인
  - email, password
  - return jwt token
- 로그아웃
  - redis 적용 
  - 로그아웃 시 redis에 해당 토큰을 저장, 인증 시 해당 토큰이 redis에 있는지 확인
  - redis 저장 시 해당토큰의 기존 만료시간을 같이 저장하여 redis에서도 그 시간이 지나면 삭제됨

[Post]
- 글 작성
- 글 조회(단건) : 내가 작성한 글만
- 글 조회(목록, 페이징, 검색(universal), 정렬) : 내가 작성한 글만
- 글 수정 : 내가 작성한 글만
- 글 삭제 : 내가 작성한 글만


## Code Convention
[package.name] => all.lower.case(전부 소문자)

[ClassName] => PascalCase(대문자로 시작)
- controller class
  - RestController : Domain + Api  + Controller
  - Controller     : Domain + View + Controller

- dto class
  - 요청용  : Request  + (...) + Dto 
  - 응답용  : Response + (...) + Dto
  - (...) : domain + 동사(용도)
  - ex) 글조회 응답용 dto : ResponsePostSelectDto, 글등록을 위한 요청용 dto : RequestPostCreateDto  

[functionName] => camelCase(소문자로 시작)
- default
  - 동사 + domain +@
  - 동사
    - 등록(C) : insert, create, save ....
    - 조회(R) : get, find, select....
    - 수정(U) : update, change....
    - 삭제(D) : delete, remove....
    - .....
  - 조회의 경우 어떤 것에 대해 조회를 하는지 명시해준다.
    - ex) id로 조회 : selectPostById

- in api controller
  - 동사 + domain +@ + Api => ex) 글등록 api : createPostApi()

[variableName] => camelCase(소문자로 시작)


## Test Code Rule
- default form
  - given : ~ 이 주어지고
  - when  : ~ 이것을 실행했을때
  - then  : ~ 결과가 이것이 나와야 된다.

- Controller Test Code : 전체 로직 테스트
  - @DisplayName : [성공 or 실패]  api("/api/...") + 요청방식(GET, POST..) + 요청 시 + 원하는 결과 또는 내용(~가 된다.)
    - ex) @DisplayName("[성공] /api/v1/posts POST 요청 시 글등록이 된다.")
  - functionName : 해당 method name +@ + Test
    - void createPostApiSuccessTest()
    
- Service Test Code
  -  @DisplayName : [성공 or 실패] Service class name, method name +실행하면 +원하는 결과 또는 내용(~가 된다.)
    - ex) @DisplayName("[성공] PostService, createPost() 실행하면 글이 등록된다.")
  - functionName : 해당 method name +Success or Fail + Test
    - void createPostSuccessTest()


## Rest API naming Rule
- /api/v1,2,3... /domain + s/+@
- GET    : 조회
- POST   : 등록 및 수정 및 삭제..... 
- PATCH  : 수정
- DELETE : 삭제


## Response json form
- Error, Success 모두 동일한 class로 사용
```
public class CommonResult<T> {
    private int status;     // http Status(2XX, 3XX, 4XX....)
    private String code; 	// 지정 code
    private String message; // 메세지
    private T data; 
    .....
}

{
    "status": ,
    "code": "",
    "message": "",
    "data": {...} or [...] or null
}
```

## Entity mapping
Post - Member => N : 1
```
@ManyToOne
@JoinColumn(name = "MEMBER_ID")
private Member member;
```
- 다대일 단방향 매핑
- Post가 memberId를 fk로 갖는다.
- fetch : LAZY(default : EAGER) 
- Post조회 시, Member 정보도 같이 조회되야 하지만 LAZY로 설정한다면 N+1 문제 발생 => 페치조인 또는 querydsl 조인사용

