package com.mykim.blog.member.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class RequestMemberInsertDto {
    @NotBlank(message = "계정을 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호을 입력해주세요.")
    private String password;

    @Builder
    public RequestMemberInsertDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
