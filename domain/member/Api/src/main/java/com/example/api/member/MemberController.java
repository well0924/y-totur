package com.example.api.member;

import com.example.apimodel.member.MemberApiModel;
import com.example.enumerate.member.SearchType;
import com.example.inconnector.member.MemberInConnector;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberInConnector memberInConnector;

    @GetMapping("/test")
    public ResponseEntity<String> mdcTestController() {
        logger.info("test-controller!");
        logger.debug("test-controller!!");
        return ResponseEntity.status(HttpStatus.OK).body("mdc-test");
    }

    @GetMapping("/page")
    public ResponseEntity<Page<MemberApiModel.MemberResponse>> findAllPage(@PageableDefault Pageable pageable) {
        Page<MemberApiModel.MemberResponse> memberPageList = memberInConnector.findAll(pageable);
        logger.info("memberList::" + memberPageList);
        return ResponseEntity.status(HttpStatus.OK).body(memberPageList);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MemberApiModel.MemberResponse>> findAllMemberSearch(@RequestParam("keyword") String keyword,@RequestParam("searchType") SearchType searchType,@PageableDefault Pageable pageable) {
        Page<MemberApiModel.MemberResponse> memberSearchResult = memberInConnector.findAllMemberSearch(keyword, searchType, pageable);
        logger.info("searchResult::"+memberSearchResult);
        return ResponseEntity.status(HttpStatus.OK).body(memberSearchResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberApiModel.MemberResponse> findById(@PathVariable("id") Long id) {
        MemberApiModel.MemberResponse memberDetail = memberInConnector.findById(id);
        logger.info("memberDetail::"+memberDetail);
        return ResponseEntity.status(HttpStatus.OK).body(memberDetail);
    }

    @PostMapping("/")
    public ResponseEntity<MemberApiModel.MemberResponse> createMember(@RequestBody @Validated MemberApiModel.CreateRequest request) {
        MemberApiModel.MemberResponse createResponse = memberInConnector.createMember(request);
        logger.info("createdResult::"+createResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(createResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemberApiModel.MemberResponse> updateMember(@PathVariable("id")Long id, @RequestBody @Validated MemberApiModel.UpdateRequest request) {
        MemberApiModel.MemberResponse updateResponse = memberInConnector.updateMember(id,request);
        logger.info("updatedResponse::"+updateResponse);
        return ResponseEntity.status(HttpStatus.OK).body(updateResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("id")Long id) {
        memberInConnector.deleteMember(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
