package com.mykim.blog.member.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class RequestMemberInsertDto {
    @NotBlank(message = "계정을 입력해주세요.")
    @Email(message = "이메일형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호을 입력해주세요.")
    private String password;

    private String username;

    @Builder
    public RequestMemberInsertDto(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
