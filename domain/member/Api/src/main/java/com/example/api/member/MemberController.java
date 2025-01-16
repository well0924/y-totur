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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/")
    public ResponseEntity<List<MemberApiModel.Response>> findAll() {
        List<MemberApiModel.Response> memberList = memberInConnector.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(memberList);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<MemberApiModel.Response>> findAllPage(@PageableDefault Pageable pageable) {
        Page<MemberApiModel.Response> memberPageList = memberInConnector.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(memberPageList);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MemberApiModel.Response>> findAllMemberSearch(@RequestParam("keyword") String keyword,@RequestParam("searchType") String searchType,@PageableDefault Pageable pageable) {
        Page<MemberApiModel.Response> memberSearchResult = memberInConnector.findAllMemberSearch(keyword, SearchType.toSearch(searchType),pageable);
        return ResponseEntity.status(HttpStatus.OK).body(memberSearchResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberApiModel.Response> findById(@PathVariable("id") Long id) {
        MemberApiModel.Response memberDetail = memberInConnector.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(memberDetail);
    }

    @PostMapping("/")
    public ResponseEntity<MemberApiModel.Response> createMember(@RequestBody MemberApiModel.Request request) {
        MemberApiModel.Response createResponse = memberInConnector.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemberApiModel.Response> updateMember(@PathVariable("id")Long id, @RequestBody MemberApiModel.Request request) {
        MemberApiModel.Response updateResponse = memberInConnector.updateMember(id,request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("id")Long id) {
        memberInConnector.deleteMember(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
