package com.mykim.blog.post.service;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.mykim.blog.post.exception.NotFoundPostException;
import com.mykim.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

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
                                        .id(findPost.getId())
                                        .title(findPost.getTitle())
                                        .content(findPost.getContent())
                                        .build();
    }

    @Transactional(readOnly = true)
    public List<ResponsePostSelectDto> selectPostAll() {
        return postRepository.findAll()
                                .stream()
                                .map(post -> new ResponsePostSelectDto(post))
                                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostSelectDto> selectPostAllPagination(int page, int size) {
        PageRequest of = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        return postRepository.findAll(of)
                .stream()
                .map(post -> new ResponsePostSelectDto(post))
                .collect(Collectors.toList());
    }

}
