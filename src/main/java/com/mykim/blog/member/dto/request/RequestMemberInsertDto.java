package com.mykim.blog.member.dto.request;

import com.mykim.blog.member.domain.MemberRole;
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

    private MemberRole memberRole;

    @Builder
    public RequestMemberInsertDto(String email, String password, String username, MemberRole memberRole) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.memberRole = memberRole;
    }
}
