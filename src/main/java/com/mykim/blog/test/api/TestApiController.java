package com.mykim.blog.test.api;

import com.mykim.blog.test.dto.RequestTestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class TestApiController {

    /**
     * Http Method
     *
     * get, post, put, patch, delete, options, head, trace, connect ...
     */

    // get
    @GetMapping("/api/test")
    public String getApi() {
        return "Hello World";
    }

    // post
    // application/x-www-form-urlencoded 형태
    @PostMapping("/api/test/form")
    public String postFormApi(@RequestParam String title, @RequestParam String content) {
        log.info("title={}, content={}", title, content);
        return "Hello World";
    }

    // application/json 형태
    @PostMapping("/api/test/json")
    public String postJsonApi(@RequestBody RequestTestDto dto) {
        log.info("RequestTestDto={}", dto);
        return "Hello World";
    }

    // application/json 형태
    // request data validation
    @PostMapping("/api/test/validation")
    public ResponseEntity<Map<String, String>> postJsonRequestDataValidationApi(@RequestBody @Valid RequestTestDto dto, BindingResult result) {
        log.info("RequestTestDto={}", dto);

        /** 이러한 데이터 검증 에러처리 매번 한다면??
         * 1. 매 매서드 마다 아래의 검증로직이 필요
         *  > 누락가능성
         *  > 검증부분에서 버그가 발생할 여지가 있다.
         *  > 지겹다(개발자스럽지 못하다)
         * 2. 응답값에 Map X, 응답용 클래스 정의
         * 3. 한 필드가 아니라 여러필드에서 에러처리일때 번거로움
         * 4. 세번이상의 반복작업은 피하자....
         */

        if(result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String, String> errors= new HashMap<>();

            for (FieldError fieldError : fieldErrors) {
                String fieldName = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();

                System.out.println("field name = " + fieldName);
                System.out.println("error message = " + errorMessage);

                errors.put(fieldName, errorMessage);
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errors);
        }

        return ResponseEntity.ok().body(Map.of());
    }


    // application/json 형태
    // request data validation
    @PostMapping("/api/test/validation/global-exception")
    public ResponseEntity<Map<String, String>> postJsonRequestDataValidationGlobalExceptionApi(@RequestBody @Valid RequestTestDto dto) {
        log.info("RequestTestDto={}", dto);
        return ResponseEntity.ok().body(Map.of());
    }


}
