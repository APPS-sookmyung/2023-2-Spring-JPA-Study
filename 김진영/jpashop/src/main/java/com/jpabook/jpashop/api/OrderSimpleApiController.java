package com.jpabook.jpashop.api;

import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderStatus;
import com.jpabook.jpashop.repository.OrderRepository;
import com.jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders=orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> result=orders.stream()
                .map(o ->new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders=orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result=orders.stream()
                .map(o ->new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId= order.getId();
            name=order.getMember().getName();
            orderDate=order.getOrderDate();
            orderStatus=order.getStatus();
            address=order.getDelivery().getAddress();
        }

    }
}

