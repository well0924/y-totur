package com.example.presentation.member;

import com.example.enums.SearchType;
import com.example.model.dto.MemberDto;
import com.example.model.member.MemberModel;
import com.example.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    
    //테스트용
    @GetMapping("/")
    public ResponseEntity<List<MemberModel>> memberList() {
        List<MemberModel> list = memberService.findAll();
        log.info("list::"+list);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<?> memberPaging(@PageableDefault Pageable pageable) {
        Page<MemberModel> list = memberService.findAll(pageable);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> memberSearch(@PageableDefault(sort="id",direction = Sort.Direction.DESC,size=5)Pageable pageable,
                                          @RequestParam(required = false,value = "searchType") String searchType,
                                          @RequestParam(required = false,value = "keyword")String keyword) {
        Page<MemberModel> searchResult = memberService.findByAllSearch(keyword, SearchType.toSearch(searchType), pageable);
        return new ResponseEntity<>(searchResult,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id")Long id) {
        MemberModel model = memberService.findById(id);
        return new ResponseEntity<>(model,HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> createMember(@RequestBody @Valid MemberDto.Request request) {
        MemberModel model = memberService.save(request);
        return new ResponseEntity<>(model,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable("id")Long id, @RequestBody @Valid MemberDto.Request request) {
        MemberModel model = memberService.update(id, request);
        return new ResponseEntity<>(model,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable("id")Long id) {
        memberService.deleteById(id);
        return new ResponseEntity("Delete O.K",HttpStatus.OK);
    }

    @GetMapping("/check-id")
    public ResponseEntity<?> existsByUserId(@RequestParam String userId) {
        return new ResponseEntity<>(memberService.existsByUserId(userId),HttpStatus.OK);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> existsByUserEmail(@RequestParam String userEmail) {
        return new ResponseEntity<>(memberService.existsByUserEmail(userEmail),HttpStatus.OK);
    }
}
