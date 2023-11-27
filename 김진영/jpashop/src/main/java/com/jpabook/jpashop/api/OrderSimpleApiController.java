package com.jpabook.jpashop.api;

import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.repository.OrderRepository;
import com.jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Order
 * Order -> Member (ManyToOne 관계)
 * Order -> Delivery (OneToOne 관계)
 * 연관이 걸리게 할 것임
 * ToOne(ManyToOne, OneToOne)관계에서 연관을 어떻게 걸리게할 것인지
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all=orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order: all){
            order.getMember().getName();
            //order.getMember()까지는 proxy 객체
            //.getName()을 하면 실제 name을 가져와야하므로 lazy 강제 초기화됨

            order.getDelivery().getAddress();
        }
        return all;
    }
}

