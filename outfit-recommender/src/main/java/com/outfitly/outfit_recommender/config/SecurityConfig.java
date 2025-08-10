package com.outfitly.outfit_recommender.config;

import com.outfitly.outfit_recommender.jwt.JwtAuthFilter;
import com.outfitly.outfit_recommender.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {/* 생략 가능: 같은 오리진이면 굳이 필요 X */})
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1) 정적 리소스/뷰는 모두 허용
                        .requestMatchers("/", "/login", "/signup", "/wardrobe", "/recommend").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // 2) 회원가입/로그인 API는 허용
                        .requestMatchers("/api/users/login", "/api/users/signup").permitAll()

                        // 3) 나머지 API는 인증 필요
                        .requestMatchers("/api/**").authenticated()

                        // 4) 그 외는 전부 허용(원치 않으면 authenticated()로 바꿔도 됨)
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
