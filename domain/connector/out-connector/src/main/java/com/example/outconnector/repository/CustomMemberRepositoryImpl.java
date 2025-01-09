package com.example.outconnector.repository;

import com.example.enums.SearchType;
import com.example.jpa.member.MemberEntity;
import com.example.jpa.member.QMemberEntity;
import com.example.model.member.MemberModel;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final JPAQueryFactory jpaQueryFactory;

    QMemberEntity qMemberEntity;

    public CustomMemberRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
        qMemberEntity = QMemberEntity.memberEntity;
    }

    @Override
    public Page<MemberModel> findByAllSearch(String keyword, SearchType searchType, Pageable pageable) {

        List<MemberEntity> list = getMemberEntityDto(keyword,pageable);

        long count = getCount(keyword);

        return new PageImpl<>(list
                .stream()
                .map(MemberModel::new)
                .collect(Collectors.toList()),pageable,count);
    }

    private List<MemberEntity> getMemberEntityDto(String keyword, Pageable pageable) {
        return jpaQueryFactory
                .select(qMemberEntity)
                .from(qMemberEntity)
                .where(userNameCt(keyword).or(userEmailCt(keyword)))
                .orderBy(qMemberEntity.id.desc())
                .limit(pageable.getPageNumber())
                .offset(pageable.getOffset())
                .fetch();
    }

    private Long getCount(String searchVal){
        return jpaQueryFactory
                .select(qMemberEntity.count())
                .from(qMemberEntity)
                .where(userNameCt(searchVal).or(userEmailCt(searchVal)))
                .orderBy(qMemberEntity .id.desc())
                .fetchOne();
    }

    //회원 아이디
    BooleanBuilder userIdCt(String searchVal) {
        return nullSafeBuilder(() -> qMemberEntity.userId.contains(searchVal));
    }
    //회원 이름
    BooleanBuilder userNameCt(String searchVal) {
        return nullSafeBuilder(() -> qMemberEntity.userName.contains(searchVal));
    }
    //회원 이메일
    BooleanBuilder userEmailCt(String searchVal){
        return nullSafeBuilder(()->qMemberEntity.userEmail.contains(searchVal));
    }

    //BooleanBuilder를 Safe하게 만들기 위해 만든 메소드
    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }
}
