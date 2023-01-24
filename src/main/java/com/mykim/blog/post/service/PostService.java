package com.mykim.blog.post.service;

import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.global.result.error.exception.NotFoundException;
import com.mykim.blog.global.pagination.CustomPaginationRequest;
import com.mykim.blog.global.pagination.CustomSortingRequest;
import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.domain.PostEditor;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.request.RequestPostUpdateDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
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
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        return new ResponsePostSelectDto(findPost);
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
    public Page<ResponsePostSelectDto> selectPostAllPaginationQuerydsl(CustomPaginationRequest paginationRequest, CustomSortingRequest sortingRequest, String keyword) {
        PageRequest pageRequest = PageRequest.of(paginationRequest.getPage() - 1, paginationRequest.getSize(), Sort.by(sortingRequest.of()));
        return postQuerydslRepository.findPostSearchPagination(pageRequest, keyword);
    }

    @Transactional
    public void editPostById(Long postId, RequestPostUpdateDto dto) {
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));

        // create PostEditor
        PostEditor postEditor = PostEditor.builder()
                                            .title(dto.getTitle() == null ? findPost.getTitle() : dto.getTitle())
                                            .content(dto.getContent() == null ? findPost.getContent() : dto.getContent())
                                            .build();
        findPost.editPost(postEditor);
    }

    @Transactional
    public void removePostById(Long postId) {
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        postRepository.delete(findPost);
    }


}
