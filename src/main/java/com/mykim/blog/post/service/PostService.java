package com.mykim.blog.post.service;

import com.mykim.blog.global.pagination.CustomPaginationResponse;
import com.mykim.blog.global.result.error.ErrorCode;
import com.mykim.blog.global.result.error.exception.NotFoundException;
import com.mykim.blog.global.pagination.CustomPaginationRequest;
import com.mykim.blog.global.pagination.CustomSortingRequest;
import com.mykim.blog.member.domain.Member;
import com.mykim.blog.post.domain.Post;
import com.mykim.blog.post.domain.PostEditor;
import com.mykim.blog.post.dto.request.RequestPostCreateDto;
import com.mykim.blog.post.dto.request.RequestPostUpdateDto;
import com.mykim.blog.post.dto.response.ResponsePostListDto;
import com.mykim.blog.post.dto.response.ResponsePostSelectDto;
import com.mykim.blog.post.exception.NotPermitAccessPostException;
import com.mykim.blog.post.repository.PostQuerydslRepository;
import com.mykim.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
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


    /** ====================================================================================================== */


    @Transactional
    public Long createPostV2(RequestPostCreateDto postCreateDto, Member member) {
        Post post = Post.createPost(postCreateDto, member);
        postRepository.save(post);
        return post.getId();
    }

    @Transactional(readOnly = true)
    public ResponsePostSelectDto selectPostByIdV2(Long postId, Long memberId) {
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        checkMyPost(findPost.getMember().getId(), memberId);
        return new ResponsePostSelectDto(findPost);
    }

    @Transactional(readOnly = true)
    public ResponsePostListDto selectPostAllPaginationQuerydslV2(CustomPaginationRequest paginationRequest,
                                                                 CustomSortingRequest sortingRequest,
                                                                 String keyword,
                                                                 Long memberId) {

        PageRequest pageRequest = PageRequest.of(paginationRequest.getPage() - 1, paginationRequest.getSize(), Sort.by(sortingRequest.of()));
        Page<ResponsePostSelectDto> pagePost = postQuerydslRepository.findPostSearchPaginationV2(pageRequest, keyword, memberId);

        return ResponsePostListDto.builder()
                                    .responsePostSelectDtos(pagePost.getContent())
                                    .paginationResponse(CustomPaginationResponse.of(pagePost.getTotalElements(), pagePost.getTotalPages(), pagePost.getNumber()))
                                    .build();
    }

    @Transactional
    public void editPostByIdV2(Long postId, RequestPostUpdateDto dto, Long memberId) {
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        checkMyPost(findPost.getMember().getId(), memberId);

        // create PostEditor
        PostEditor postEditor = PostEditor.builder()
                                        .title(dto.getTitle() == null ? findPost.getTitle() : dto.getTitle())
                                        .content(dto.getContent() == null ? findPost.getContent() : dto.getContent())
                                        .build();

        findPost.editPost(postEditor);
    }

    @Transactional
    public void removePostByIdV2(Long postId, Long memberId) {
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        checkMyPost(findPost.getMember().getId(), memberId);
        postRepository.delete(findPost);
    }

    private void checkMyPost(Long postCreatorMemberId, Long authenticationMemberId) {
        if(!postCreatorMemberId.equals(authenticationMemberId)) {
            throw new NotPermitAccessPostException(ErrorCode.NOT_PERMIT_ACCESS_POST);
        }

    }
}
