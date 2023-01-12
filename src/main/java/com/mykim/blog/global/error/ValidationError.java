package com.mykim.blog.global.error;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ValidationError {
    private String fieldName;
    private String errorMessage;

    @Builder
    public ValidationError(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }
}
