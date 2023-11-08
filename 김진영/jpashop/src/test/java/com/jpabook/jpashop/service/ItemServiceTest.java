package com.jpabook.jpashop.service;

import com.jpabook.jpashop.domain.item.Book;
import com.jpabook.jpashop.domain.item.Item;
import com.jpabook.jpashop.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) //최근 스프링부트는 JUnit5 사용하므로 변경
@SpringBootTest
@Transactional //데이터를 변경해야하므로
public class ItemServiceTest {
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 아이템_테스트() throws Exception{
        //given
        Item item=new Book();
        item.setName("아이템1");
        item.setPrice(1000);
        item.setStockQuantity(10);
        em.persist(item);

        //when
        itemService.saveItem(item);
        Long itemId=item.getId();

        //then
        assertEquals(item.getId(),itemRepository.findOne(itemId));
    }
}
