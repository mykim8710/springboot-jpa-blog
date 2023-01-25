package com.mykim.blog.global.pagination;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CustomPaginationResponse {
    private int page;        // 현재 페이지 번호
    private long totalCount; // 전체 게시물 수
    private int totalPage;   // 전체 페이지의 수
    private int prevPage;    // 이전 페이지 숫자
    private int nextPage;    // 다음 페이지 숫자
    private int startPage;   // 첫페이지 번호
    private int endPage;     // 마지막 페이지 번호

    private CustomPaginationResponse(long totalCount, int totalPage, int page) {
        this.page = page;
        this.totalCount = totalCount;
        this.totalPage = totalPage;

        if (totalCount == 0) {
            this.prevPage = 1;
            this.nextPage = 1;
            this.startPage = 1;
            this.endPage = 1;
            return;
        }

        init(page);
    }

    public static CustomPaginationResponse of(long totalCount, int totalPage, int page) {
        return new CustomPaginationResponse(totalCount, totalPage, (page+1));
    }

    public void init(int page) {
        int pageCount = 5;  // 페이지 개수(5) : ex) |<  <   1  2  3  4  5  >  >|

        startPage = ((page - 1) / pageCount) * pageCount + 1;
        endPage = startPage + pageCount - 1;

        if (endPage > totalPage) {
            endPage = totalPage;
        }

        prevPage = startPage - 1;
        if (prevPage < 1) {
            prevPage = 1;
        }

        nextPage = endPage + 1;
        if (nextPage > totalPage) {
            nextPage = totalPage;
        }
    }

}
