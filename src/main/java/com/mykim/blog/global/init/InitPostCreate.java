package com.mykim.blog.global.init;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.usertype.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile("local")
@RequiredArgsConstructor
@Component
public class InitPostCreate {
    private final InitPostCreateService initPostCreateService;

    @PostConstruct
    public void init() {
        initPostCreateService.init();
    }

    @Service
    static class InitPostCreateService {
        @Autowired
        PostRepository postRepository;

        @Transactional
        public void init() {
            List<Post> createdPosts = IntStream.range(1, 31)
                                                .mapToObj(i -> Post.builder()
                                                        .title("title_" +i)
                                                        .content("content_" +i)
                                                        .build()
                                                ).collect(Collectors.toList());
            postRepository.saveAll(createdPosts);
        }
    }

}
