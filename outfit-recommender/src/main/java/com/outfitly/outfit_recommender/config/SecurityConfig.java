package com.outfitly.outfit_recommender.config;

import com.outfitly.outfit_recommender.jwt.JwtAuthFilter;
import com.outfitly.outfit_recommender.jwt.JwtUtil;
import com.outfitly.outfit_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // JwtAuthFilter에 주입할 서비스

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // JWT 사용 시 세션/CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 동일 오리진만 쓸거면 생략 가능
                .cors(cors -> {})
                // 세션 상태 없음
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 401/403을 JSON으로 응답 (프론트 처리 편의)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"인증이 필요합니다(401)\",\"code\":\"UNAUTHORIZED\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"권한이 없습니다(403)\",\"code\":\"FORBIDDEN\"}");
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스/뷰는 모두 허용
                        .requestMatchers("/", "/login", "/signup", "/wardrobe", "/recommend", "/profile").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // 회원가입/로그인 API는 허용
                        .requestMatchers("/api/users/login", "/api/users/signup").permitAll()

                        // 나머지 API는 인증 필요
                        .requestMatchers("/api/**").authenticated()

                        // 그 외는 전부 허용(원치 않으면 authenticated()로 바꿔도 됨)
                        .anyRequest().permitAll()
                )

                // JwtAuthFilter를 UserDetailsService와 함께 등록 (UsernamePasswordAuthenticationFilter 앞)
                .addFilterBefore(new JwtAuthFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // 필요 시 CORS 허용 도메인 지정해서 사용 (다른 도메인 프론트가 붙는다면 활성화)
    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:3000", "https://your-frontend.example"));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
    */
}
