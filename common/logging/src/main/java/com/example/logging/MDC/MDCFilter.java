package com.example.logging.MDC;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCFilter implements Filter {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USERNAME_KEY = "username";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // UUID 생성하여 requestId 설정
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                MDC.put(USERNAME_KEY, ((UserDetails) principal).getUsername());
            } else {
                MDC.put(USERNAME_KEY, principal.toString());
            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse); // 다음 필터 또는 요청 처리
        } finally {
            MDC.clear(); // ThreadLocal에 저장된 MDC 데이터 정리
        }
    }
}
