package com.example.presentation.member;

import com.example.auth.service.AuthService;
import com.example.common.dto.TokenDto;
import com.example.model.dto.LoginDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    private final long COOKIE_EXPIRATION = 1209600;

    @PostMapping("/signup")
    public ResponseEntity <TokenDto> signupAndIssueToken(@Valid @RequestBody LoginDto loginDto){

        TokenDto tokenResponse = authService.login(loginDto);

        // RT 쿠키에 저장하기.
        HttpCookie RtHttpCookie = ResponseCookie.from("refresh-token", tokenResponse.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .path("/")
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, RtHttpCookie.toString())
                // AT 저장
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String requestAccessToken) {
        authService.logout(requestAccessToken);

        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", null)
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body("log-out");
    }

    //재발급
    @PostMapping("/reissue")
    public ResponseEntity<?>reissueToken(@CookieValue(name = "refresh-token",required = false) String requestRefreshToken,
                                         @RequestHeader("Authorization") String requestAccessToken){

        TokenDto tokenResponse = authService.reissue(requestAccessToken,requestRefreshToken);

        log.info(requestRefreshToken);

        if (tokenResponse != null) { // 토큰 재발급 성공
            // RT 저장
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", tokenResponse.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .secure(true)
                    .path("/")
                    .build();
            log.info("쿠키값 저장::"+responseCookie);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    // AT 저장
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                    .body(tokenResponse);

        } else { // Refresh Token 탈취 가능성
            // Cookie 삭제 후 재로그인 유도
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            log.info("401인경우:"+responseCookie);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
    }
}
