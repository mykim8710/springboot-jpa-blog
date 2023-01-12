package com.mykim.blog.test.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestTestDto {

    /**
     * Bean Validation annotation
     *
     * @NotBlank(message = "") : null, "", "...." 모두체크 => 가장강력
     * @NotEmpty(message = "") : null, "" 체크("..."는 체크안함)
     * @NotNull(message = "") : null만 체크("", "..."는 체크안함)
     * @Min(message = "") : 최소값 지정(@Size의 min과 동일)
     * @Max(message = "") : 최대값 지정(@Size의 max과 동일)
     * @Email(message = "") : 이메일 형식 체크
     * @Size(min = , max = , message = "") : 최소, 최대 사이즈를 지정
     * .....
     */

    @NotBlank(message ="제목이 없습니다.")
    private String title;

    @NotBlank(message ="내용이 없습니다.")
    private String content;

    @Builder
    public RequestTestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

