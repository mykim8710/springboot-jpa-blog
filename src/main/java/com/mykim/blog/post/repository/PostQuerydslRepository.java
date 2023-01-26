package com.mykim.blog.post.repository;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.response.QResponsePostSelectDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.mykim.blog.member.domain.QMember.member;
import static com.mykim.blog.post.domain.QPost.post;

@RequiredArgsConstructor
@Repository
public class PostQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public Page<ResponsePostSelectDto> findPostSearchPaginationV2(Pageable pageable, String keyword, Long memberId) {
        List<ResponsePostSelectDto> responsePostSelectDtos = queryFactory.select(new QResponsePostSelectDto(post.id, post.title, post.content, post.createdDate, post.lastModifiedDate, member.username))
                                        .from(post)
                                        .join(post.member, member)
                                        .where(member.id.eq(memberId), createUniversalSearchCondition(keyword))
                                        .offset(pageable.getOffset())
                                        .limit(pageable.getPageSize())
                                        .orderBy(getOrderSpecifier(pageable.getSort())
                                                .stream()
                                                .toArray(OrderSpecifier[]::new)
                                        )
                                        .fetch();

        Long count = queryFactory.select(post.count())
                                    .from(post)
                                    .join(post.member, member)
                                    .where(member.id.eq(memberId), createUniversalSearchCondition(keyword))
                                    .fetchOne();

        return new PageImpl<>(responsePostSelectDtos, pageable, count);
    }


    public Page<ResponsePostSelectDto> findPostSearchPagination(Pageable pageable, String keyword) {
        List<ResponsePostSelectDto> responsePostSelectDtos = queryFactory.select(new QResponsePostSelectDto(post.id, post.title, post.content, post.createdDate, post.lastModifiedDate, member.username))
                                                    .from(post).join(post.member, member)
                                                    .where(createUniversalSearchCondition(keyword))
                                                    .offset(pageable.getOffset())
                                                    .limit(pageable.getPageSize())
                                                    .orderBy(getOrderSpecifier(pageable.getSort())
                                                                    .stream()
                                                                    .toArray(OrderSpecifier[]::new)
                                                    )

                                                    .fetch();

        Long count = queryFactory.select(post.count())
                                    .from(post)
                                    .where(createUniversalSearchCondition(keyword))
                                    .fetchOne();

        return new PageImpl<>(responsePostSelectDtos, pageable, count);
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

    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach (order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String orderProperty = order.getProperty();
            PathBuilder orderByExpression = new PathBuilder(Post.class, "post");
            orders.add(new OrderSpecifier(direction, orderByExpression.get(orderProperty)));
        });

        return orders;
    }

}
