package com.example.logging.MDC;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class MDCFilter implements Filter {

    private static final String REQUEST_ID_KEY = "requestId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // UUID 생성하여 requestId 설정
        try {
            // UUID 생성하여 requestId 설정
            String requestId = UUID.randomUUID().toString();

            MDC.put(REQUEST_ID_KEY, requestId);

            filterChain.doFilter(servletRequest, servletResponse); // 다음 필터 또는 요청 처리
        } finally {
            MDC.clear(); // ThreadLocal에 저장된 MDC 데이터 정리
        }
    }
}
