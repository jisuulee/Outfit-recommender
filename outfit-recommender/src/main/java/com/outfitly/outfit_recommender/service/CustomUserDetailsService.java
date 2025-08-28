package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user '" + username + "' not found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        // 비밀번호는 여기서는 안 쓰이지만 커스텀 인가/재인증 시 사용될 수 있음
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities
        );
    }
}
