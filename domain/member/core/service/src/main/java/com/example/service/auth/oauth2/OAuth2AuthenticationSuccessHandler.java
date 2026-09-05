package com.example.service.auth.oauth2;

import com.example.service.auth.jwt.JwtTokenProvider;
import com.example.service.auth.jwt.TokenDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String URI = "/auth/success";
    private static final String REDIRECT_URI = "http://localhost:3000/auth/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        TokenDto  dto = jwtTokenProvider.generateToken(authentication);
        String accessToken = dto.getAccessToken();
        String refreshToken = dto.getRefreshToken();

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JS 접근 못 하게
        refreshTokenCookie.setSecure(true);   // HTTPS 전용 (개발환경에서는 false 가능)
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);

        // refreshToken은 이미 httpOnly 쿠키로 전달되므로 URL에는 싣지 않는다.
        // accessToken은 쿼리스트링이 아닌 URL 프래그먼트(#)로 전달해,
        // 서버 액세스 로그·Referer 헤더에 남지 않도록 한다. (프론트에서 location.hash로 파싱 필요)
        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .fragment("accessToken=" + accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
