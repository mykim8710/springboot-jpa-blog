package com.mykim.blog.post.dto.response;

import com.mykim.blog.global.pagination.CustomPaginationResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ResponsePostListDto {
    private List<ResponsePostSelectDto> responsePostSelectDtos;
    private CustomPaginationResponse paginationResponse;

    @Builder
    public ResponsePostListDto(List<ResponsePostSelectDto> responsePostSelectDtos, CustomPaginationResponse paginationResponse) {
        this.responsePostSelectDtos = responsePostSelectDtos;
        this.paginationResponse = paginationResponse;
    }
}
