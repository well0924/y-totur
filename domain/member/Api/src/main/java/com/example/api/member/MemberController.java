package com.example.api.member;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @GetMapping("/test")
    public ResponseEntity<String> mdcTestController() {
        logger.info("test-controller!");
        logger.debug("test-controller!!");
        return ResponseEntity.status(HttpStatus.OK).body("mdc-test");
    }
}
