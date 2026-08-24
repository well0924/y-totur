package com.example.member;

import com.example.events.enums.AggregateType;
import com.example.events.enums.EventType;
import com.example.events.kafka.MemberSignUpKafkaEvent;
import com.example.events.outbox.OutboxEventService;
import com.example.interfaces.member.MemberRepositoryPort;
import com.example.model.member.MemberModel;
import com.example.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberUnitTest {

    @Mock
    private MemberRepositoryPort memberRepositoryPort;

    @Mock
    private OutboxEventService outboxEventService;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("전체 회원 조회 성공")
    void findAll_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        MemberModel member = MemberModel.builder().id(1L).userId("testUser").build();
        Page<MemberModel> memberPage = new PageImpl<>(List.of(member), pageable, 1);

        given(memberRepositoryPort.findAll(pageable)).willReturn(memberPage);

        // when
        Page<MemberModel> result = memberService.findAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("testUser");
        verify(memberRepositoryPort).findAll(pageable);
    }

    @Test
    @DisplayName("단건 회원 조회 성공")
    void findById_success() {
        // given
        Long memberId = 1L;
        MemberModel member = MemberModel.builder().id(memberId).userId("testUser").build();

        given(memberRepositoryPort.findById(memberId)).willReturn(member);

        // when
        MemberModel result = memberService.findById(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(memberId);
        verify(memberRepositoryPort).findById(memberId);
    }

    @Test
    @DisplayName("회원 생성 및 Outbox 이벤트 저장 성공")
    void createMember_success() {
        // given
        MemberModel requestModel = MemberModel.builder()
                .userId("testUser")
                .password("password123")
                .userEmail("test@example.com")
                .userPhone("+821012345678")
                .build();

        MemberModel savedModel = MemberModel.builder()
                .id(1L)
                .userId("testUser")
                .userEmail("test@example.com")
                .build();

        given(memberRepositoryPort.createMember(any(MemberModel.class))).willReturn(savedModel);

        // when
        MemberModel result = memberService.createMember(requestModel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        // Repository 저장 호출 검증
        verify(memberRepositoryPort).createMember(any(MemberModel.class));

        // Outbox 이벤트 저장 호출 검증 (회원가입 환영 이벤트)
        verify(outboxEventService).saveEvent(
                any(MemberSignUpKafkaEvent.class),
                eq(AggregateType.MEMBER.name()),
                eq("1"),
                eq(EventType.SIGNED_UP_WELCOME.name())
        );
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMember_success() {
        // given
        Long memberId = 1L;
        MemberModel requestModel = MemberModel.builder()
                .userId("testUser")
                .userName("수정된이름")
                .build();

        given(memberRepositoryPort.updateMember(eq(memberId), any(MemberModel.class)))
                .willReturn(requestModel);

        // when
        MemberModel result = memberService.updateMember(memberId, requestModel);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("수정된이름");
        verify(memberRepositoryPort).updateMember(memberId, requestModel);
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void deleteMember_success() {
        // given
        Long memberId = 1L;

        // when
        memberService.deleteMember(memberId);

        // then
        verify(memberRepositoryPort).deleteMember(memberId);
    }
}
