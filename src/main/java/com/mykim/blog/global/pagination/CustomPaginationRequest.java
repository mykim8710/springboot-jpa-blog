package com.mykim.blog.global.pagination;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CustomPaginationRequest {
    private int page;
    private int size;

    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 2000;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    @Builder
    public CustomPaginationRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
