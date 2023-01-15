package com.mykim.blog.post.repository;

import com.mykim.blog.post.dto.request.RequestPostSelectDto;
import com.mykim.blog.post.dto.response.QResponsePostSelectDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.mykim.blog.post.domain.QPost.post;

@RequiredArgsConstructor
@Repository
public class PostQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public Page<ResponsePostSelectDto> findPostSearchPagination(RequestPostSelectDto dto) {
        List<ResponsePostSelectDto> responsePostSelectDtos = queryFactory.select(new QResponsePostSelectDto(post.id, post.title, post.content))
                                                    .from(post)
                                                    .where(createUniversalSearchCondition(dto.getKeyword()))
                                                    .offset(dto.getOffset())
                                                    .limit(dto.getSize())
                                                    .orderBy(post.id.desc())
                                                    .fetch();

        Long count = queryFactory.select(post.count())
                                    .from(post)
                                    .where(createUniversalSearchCondition(dto.getKeyword()))
                                    .fetchOne();

        return new PageImpl<>(responsePostSelectDtos, PageRequest.of(1,5), count);
    }



    private BooleanExpression titleLike(String keyword) {
        return !StringUtils.hasLength(keyword) ? null : post.title.contains(keyword); // like : keyword, contains : %keyword%
    }

    private BooleanExpression contentLike(String keyword) {
        return !StringUtils.hasLength(keyword) ? null : post.content.contains(keyword);
    }

    private BooleanExpression createUniversalSearchCondition(String keyword) {
        return !StringUtils.hasLength(keyword) ? null : titleLike(keyword).or(contentLike(keyword));
    }


}
