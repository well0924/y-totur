package com.example.outconnector.member;

import com.example.enumerate.member.SearchType;
import com.example.rdbrepository.member.custom.MemberRepositoryCustom;
import com.example.rdbrepository.member.Member;
import com.example.rdbrepository.member.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMember qMember;


    public MemberRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
        this.qMember = QMember.member;
    }

    @Override
    public Page<Member> searchAll(String keyword, SearchType searchType, Pageable pageable) {
        JPQLQuery<Member> memberSearchList = jpaQueryFactory
                .select(qMember)
                .from(qMember)
                .where(userIdCt(keyword).or(userEmailCt(keyword).or(userNameCt(keyword))))
                .orderBy(qMember.id.desc());

        JPQLQuery<Member> memberSearchMiddleQuery  = switch (searchType) {
            case MEMBER_NAME -> memberSearchList.where(userNameCt(keyword));
            case EMAIL -> memberSearchList.where(userEmailCt(keyword));
            case USERID -> memberSearchList.where(userIdCt(keyword));
            default -> throw new RuntimeException("검색 결과가 없습니다.");
        };

        return PageableExecutionUtils
                .getPage(memberSearchMiddleQuery
                        .orderBy(getAllOrderSpecifiers(pageable
                                .getSort())
                                .toArray(OrderSpecifier[]::new))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize()).fetch(), pageable, memberSearchMiddleQuery::fetchCount);
    }

    //회원 아이디
    BooleanBuilder userIdCt(String keyword) {
        return nullSafeBuilder(() -> QMember.member.userId.contains(keyword));
    }
    //회원 이메일
    BooleanBuilder userEmailCt(String keyword) {
        return nullSafeBuilder(() -> QMember.member.userEmail.contains(keyword));
    }
    //회원 이름
    BooleanBuilder userNameCt(String keyword) {
        return nullSafeBuilder(() -> QMember.member.userName.contains(keyword));
    }

    private List<OrderSpecifier> getAllOrderSpecifiers(Sort sort) {
        List<OrderSpecifier>orders =  new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            PathBuilder<Member> orderByExpression =  new PathBuilder<>(Member.class,"member");
            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }

    // null 타입 체크
    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }
}
