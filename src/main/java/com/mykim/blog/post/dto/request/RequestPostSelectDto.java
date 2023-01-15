package com.mykim.blog.post.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class RequestPostSelectDto {
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 10;

    private String keyword;
    private String sortCondition;   // desc, asc

    public long getOffset() {
        return (long) (page - 1) * size;
    }

}
