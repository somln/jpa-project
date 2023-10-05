package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 주문
 * 주문 내역 조회
 * 주문 취소
 * 상품을 주문하고 취소할 경우 재고가 줄거나 늘어야함.
 * 주문 상태별로 검색 조회가 가능해야함
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        //주문을 하려면 멤버 아이디와 아이템 아이디와 주문 수량이 필요함

        //엔터티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        /**
         * CascadeType.ALL 을 설정했기 때문에 Order 하나만 DB에 저장해도 Delivery, OrderItem까지 같이 저장됨
         * Delivery나 OrderItem 모두 Order에서만 매핑되기 때문에 가능한 기능
         * 만약 다른 곳에서도 매핑되면 사용하면 CascadeType.ALL 사용 x
         */
        return order.getId();
    }


    /**
     * 주문 취소
     */
    @Transactional
    public Long cancelOrder(Long orderId){
        //주문 엔터티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        return order.getId();
    }

    public List<Order> findOrder(OrderSearch orderSearch) {
        List<Order> orders = orderRepository.findAllByCriteria(orderSearch);
        return orders;
    }

    //검색

}
