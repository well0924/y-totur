package com.example.auth.jwt;

import com.example.auth.model.CustomUserDetails;
import com.example.auth.service.CustomUserDetailService;
import com.example.common.dto.TokenDto;
import com.example.common.redis.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Log4j2
@Component
public class JwtTokenProvider implements InitializingBean {

    private final CustomUserDetailService userDetailsService;
    private final RedisService redisService;
    private static final String AUTHORITIES_KEY = "role";
    private static final String USERID_KEY = "userId";
    private final String secretKey;
    private static Key signingKey;
    private final Long accessTokenValidityInMilliseconds;
    private final Long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            CustomUserDetailService userDetailsService,
            RedisService redisService,
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") Long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") Long refreshTokenValidityInMilliseconds) {
        this.userDetailsService = userDetailsService;
        this.redisService = redisService;
        this.secretKey = secretKey;
        // seconds -> milliseconds
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        signingKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    // JWT 토큰 생성
    @Transactional
    public TokenDto createToken(String username, String authorities){
        Long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
                .setSubject("access-token")
                .claim(USERID_KEY, username)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
                .setSubject("refresh-token")
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        return new TokenDto(accessToken, refreshToken);
    }

    // 토큰에서 회원 정보 추출
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) { // Access Token
            return e.getClaims();
        }
    }

    //인증값 가져오기.
    public Authentication getAuthentication(String token) {
        String username = getClaims(token).get(USERID_KEY).toString();
        CustomUserDetails userDetailsImpl = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetailsImpl, "", userDetailsImpl.getAuthorities());
    }

    //기간 확인하기.
    public long getTokenExpirationTime(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateRefreshToken(String refreshToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        } catch (NullPointerException e){
            log.error("JWT Token is empty.");
        }
        return false;
    }

    //filter에 사용
    public boolean validateAccessToken(String accessToken) {
        try {
            if (redisService.getValues(accessToken) != null // NPE 방지
                    && redisService.getValues(accessToken).equals("logout")) { // 로그아웃 했을 경우
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch(ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
