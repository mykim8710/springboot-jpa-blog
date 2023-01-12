package com.mykim.blog.post.service;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.mykim.blog.post.exception.NotFoundPostException;
import com.mykim.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Long createPost(RequestPostCreateDto postCreateDto) {
        // RequestPostCreateDto => Post Entity
        Post post = Post.createPost(postCreateDto);
        postRepository.save(post);
        return post.getId();
    }

    @Transactional(readOnly = true)
    public ResponsePostSelectDto selectPostById(Long postId) {
        Post findPost = postRepository.findById(postId)
                                        .orElseThrow(() -> new NotFoundPostException());

        return ResponsePostSelectDto.builder()
                                        .title(findPost.getTitle())
                                        .content(findPost.getContent())
                                        .build();


        /**
         * Controller -> Service -> Repository
         *
         * Controller
         *  > ApiController(@RestController)
         *  > ViewController(@Controller)
         *
         * Service
         *  > WebService
         *  > Service
         *
         *  Repository
         *
         */

    }








}
