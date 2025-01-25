package com.example.service.auth;

import com.example.service.auth.jwt.JwtTokenProvider;
import com.example.service.auth.jwt.TokenDto;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    //로그인
    public TokenDto login(String userId, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userId,password);

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        redisService.save(authentication.getName(),tokenDto.getRefreshToken(),tokenDto.getRefreshTokenExpiredTime());
        return tokenDto;
    }

    //로그아웃
    public void logout(String userId) {
        redisService.delete(userId);
        SecurityContextHolder.clearContext();
    }

    //토큰 재발급
    public TokenDto tokenReissue(String refreshToken){
        if(jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Claims claims = jwtTokenProvider.parseClaims(refreshToken);
        String username = claims.getSubject();

        String storedToken = redisService.findByKey(username);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new RuntimeException("일치하지 않는 Refresh Token입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        TokenDto newToken = jwtTokenProvider.generateToken(authentication);
        // 5. Redis에 새로운 Refresh Token 저장
        redisService.save(username, newToken.getRefreshToken(), newToken.getRefreshTokenExpiredTime());

        return newToken;
    }
}
