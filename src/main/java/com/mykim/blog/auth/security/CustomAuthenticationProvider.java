package com.mykim.blog.auth.security;

import com.mykim.blog.auth.security.principal.PrincipalDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PrincipalDetailService principalDetailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 전달 받은 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        String email = authenticationToken.getName();
        String password = (String)authenticationToken.getCredentials();

        // 해당 회원 Database 조회
        UserDetails userDetails = principalDetailService.loadUserByUsername(email);

        // password 확인
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("password is not matched");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * provider 동작 여부를 설정
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);    // true
    }
}
