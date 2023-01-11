package com.mykim.blog.api;

import com.mykim.blog.dto.RequestTestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestApiController {

    /**
     * Http Method
     *
     * get, post, put, patch, delete, options, head, trace, connect ...
     */

    // get
    @GetMapping("/api/test")
    public String get() {
        return "Hello World";
    }

    // post
    // application/x-www-form-urlencoded 형태
    @PostMapping("/api/test")
    public String post(@RequestParam String title, @RequestParam String content) {
        log.info("title={}, content={}", title, content);
        return "Hello World";
    }

    // application/json 형태
    @PostMapping("/api/test/json")
    public String post(@RequestBody RequestTestDto dto) {
        log.info("RequestTestDto={}", dto);
        return "Hello World";
    }
}
