package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void 상픔주문() {
        //given
        Member member = createMember();
        Item book = createBook("시골 JPA", 10000, 10); //이름, 가격, 재고

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals( OrderStatus.ORDER, getOrder.getStatus()); //주문 상태가 맞는지
        assertEquals(1, getOrder.getOrderItems().size()); //주문한 상품 종류 수가 1개가 맞는지
        assertEquals(10000 * orderCount, getOrder.getTotalPrice()); //주문한 상품
        assertEquals(8, book.getStockQuantity()); //주문 수량만큼 재고가 줄어야한다.
    }

    @Test
    void 상품주문_재고수량추가() {
        Member member = createMember();
        Item book = createBook("시골 JPA", 10000, 10); //이름, 가격, 재고
        int orderCount = 12;
        Assertions.assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderCount));
    }


    @Test()
    void 주문취소() {
        //given
        Member member = createMember();
        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        
        //when
        orderService.cancelOrder(orderId);
        
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals( OrderStatus.CANCEL, getOrder.getStatus()); //주문 상태가 취소인지
        assertEquals(10, book.getStockQuantity()); //주문을 취소했으므로 재고가 돌아와야함
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

    private Member createMember(){
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);
        return member;
    }

}