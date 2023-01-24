package com.mykim.blog.auth.security.principal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.security.jwt.domain.User;
import spring.security.jwt.repository.UserRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("PrincipalDetailService 동작");
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("없는 사용자입니다."));
        return new PrincipalDetail(user);
    }
}
