package com.example.security.config;

import com.example.logging.MDC.MDCFilter;
import com.example.service.auth.jwt.JwtAccessDeniedHandler;
import com.example.service.auth.jwt.JwtAuthenticationEntryPoint;
import com.example.service.auth.jwt.JwtAuthenticationFilter;
import com.example.service.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration)throws Exception{
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .httpFirewall(defaultFireWell())
                .ignoring().requestMatchers("/images/**", "/js/**","/font/**", "/webfonts/**","/istatic/**",
                        "/main/**", "/webjars/**", "/dist/**", "/plugins/**", "/css/**","/favicon.ico","/h2-console/**","/css/**",
                        "/vendor/**","/scss/**","/**");
    }

    //chain filter
    @Bean
    public SecurityFilterChain cofigure(HttpSecurity http)throws Exception{

        http
                .cors(httpSecurityCorsConfigurer
                        -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.disable())
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new MDCFilter(), JwtAuthenticationFilter.class)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //인증 전부 permitall로 해놓음.... 추후에 모듈이 만들어지면 분기 설정.
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry
                        -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers("/api/auth/*").permitAll()
                        .requestMatchers("/api/member/*").permitAll()
                        .requestMatchers("/api/category/*").hasRole("ADMIN")
                        .requestMatchers("/api/attach/*").hasAnyRole("ADMIN","USER")
                        .requestMatchers("/api/schedule/*").hasAnyRole("ADMIN","USER")
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("*")); // 사용할 CRUD 메소드 등록
        configuration.setAllowedHeaders(Arrays.asList("*")); // 사용할 Header 등록
        configuration.setExposedHeaders(Arrays.asList("authorization", "refreshToken"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public HttpFirewall defaultFireWell(){
        return new DefaultHttpFirewall();
    }
}
