# Springboot + JPA 간단한 CRUD Blog + Board

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



##Entity mapping


