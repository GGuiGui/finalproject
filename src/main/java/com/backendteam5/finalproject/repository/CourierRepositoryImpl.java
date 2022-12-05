package com.backendteam5.finalproject.repository;

import com.backendteam5.finalproject.dto.CourierDto;
import com.backendteam5.finalproject.dto.QCourierDto;
import com.backendteam5.finalproject.entity.Account;
import com.backendteam5.finalproject.repository.custom.CustomCourierRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.backendteam5.finalproject.entity.QCourier.courier;
import static com.backendteam5.finalproject.entity.QDeliveryAssignment.deliveryAssignment;

public class CourierRepositoryImpl implements CustomCourierRepository {

    private final JPAQueryFactory queryFactory;
    public CourierRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /*
    * 1. username == GUROADMIN and state false and delivery.account == account
     */

    // 배송 상태별 조회
    @Override
    public List<CourierDto> searchByUsernameAndState(Account account, String state, String username) {
        return queryFactory
                .select(getCourierConstructor())
                .from(courier)
                .join(courier.deliveryAssignment, deliveryAssignment)
                .where(usernameEq(account), stateEq(state), stateUsernameEq(username))
                .orderBy(courier.arrivalDate.desc())
                .fetch();
    }

    // 배송상태별 택배 개수
    @Override
    public Long countUsernameAndState(Account account, String state, String username) {
        return queryFactory
                .select(courier.count())
                .from(courier)
                .join(courier.deliveryAssignment, deliveryAssignment)
                .where(usernameEq(account), stateEq(state), stateUsernameEq(username))
                .fetchOne();
    }
    // 1. 딜리버리 account 랑 지금 택배기사 비교
    // 2. 배송상태 비교
    // 3. 딜리버리맨이 구로어드민 or 현재 택배기사이름
    // 문제점 : 건 바이 건 으로 할때 할당 받은 택배기사가 조회할때 안보임.



    // 수령인 이름으로 택배 조회
    @Override
    public List<CourierDto> searchCustomer(String customer) {
        return queryFactory
                .select(getCourierConstructor())
                .from(courier)
                .where(customerEq(customer))
                .fetch();
    }

    private static QCourierDto getCourierConstructor() {
        return new QCourierDto(
                courier.id,
                courier.address,
                courier.state,
                courier.customer,
                courier.arrivalDate,
                courier.registerDate,
                courier.username,
                courier.xPos,
                courier.yPos,
                deliveryAssignment
        );
    }

    private BooleanExpression stateUsernameEq(String username) {
        return courier.username.eq(username);
    }

    private BooleanExpression customerEq(String customer) {
        return courier.customer.eq(customer);
    }

    private BooleanExpression usernameEq(Account account) {
        return deliveryAssignment.account.eq(account);
    }

    private BooleanExpression stateEq(String state) {
        return courier.state.eq(state);
    }
}
