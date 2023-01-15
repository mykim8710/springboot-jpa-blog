package com.mykim.blog.post.service;

import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.request.RequestPostSelectDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.mykim.blog.post.exception.NotFoundPostException;
import com.mykim.blog.post.repository.PostQuerydslRepository;
import com.mykim.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostQuerydslRepository postQuerydslRepository;

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
    public Page<ResponsePostSelectDto> selectPostAllPagination(Pageable pageable) {
        return postRepository.findAll(pageable).map(post -> new ResponsePostSelectDto(post));
    }


    @Transactional(readOnly = true)
    public Page<ResponsePostSelectDto> selectPostAllPaginationQuerydsl(RequestPostSelectDto dto) {

        Integer page = dto.getPage();
        Integer size = dto.getSize();   // == limit
        long offset = dto.getOffset();
        String keyword = dto.getKeyword();
        //String sortCondition = dto.getSortCondition();



        //PageRequest request = PageRequest.of();






        return postQuerydslRepository.findPostSearchPagination(dto);

    }

}
