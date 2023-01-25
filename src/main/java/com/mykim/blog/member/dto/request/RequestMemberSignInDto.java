package com.mykim.blog.member.dto.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class RequestMemberSignInDto {
    private String email;
    private String password;

    @Builder
    public RequestMemberSignInDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
