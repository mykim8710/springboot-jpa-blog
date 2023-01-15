package com.mykim.blog.post.repository;

import com.mykim.blog.post.domain.QPost;
import com.mykim.blog.post.dto.request.RequestPostSelectDto;
import com.mykim.blog.post.dto.response.QResponsePostSelectDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
                                                    .where(titleLike(dto.getKeyword()).or(contentLike(dto.getKeyword())))
                                                    .offset(dto.getOffset())
                                                    .limit(dto.getSize())
                                                    .orderBy(post.id.desc())
                                                    .fetch();

        Long count = queryFactory.select(post.count())
                                    .from(post)
                                    .where(titleLike(dto.getKeyword()).or(contentLike(dto.getKeyword())))
                                    .fetchOne();

        return new PageImpl<>(responsePostSelectDtos, null, count);
    }



    private BooleanExpression titleLike(String keyword) {
        return !StringUtils.hasLength(keyword) ? null : post.title.like(keyword);
    }

    private BooleanExpression contentLike(String keyword) {
        return !StringUtils.hasLength(keyword) ? null : post.content.like(keyword);
    }

//    private BooleanExpression sortCondition(String sortCondition) {
//        // id,asc or desc
//
//        //sortCondition.split(",")
//
//        if(!StringUtils.hasLength(sortCondition)) {
//            return null;
//        }
//
//        String[] split = sortCondition.split(",");
//
//        String sort = split[0];
//        String order = split[1];
//
//        if (sort.equals("id")) {
//            NumberPath<Long> id = post.id;
//        }
//
//        if (sort.equals("title")) {
//            post.title
//        }
//
//
//    }
}
