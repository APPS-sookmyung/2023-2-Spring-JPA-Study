package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;
    @PersistenceContext
    EntityManager em;
    
    @Test
    public void 아이템_저장() throws Exception {
        //given
        Item item = new Item();
        item.setName("책1");
        item.setPrice(10000);
        item.setStockQuantity(10);
        em.persist(item);


        //when
        Long saveId = itemService.saveItem(item);
        //then
        assertEquals(item, itemRepository.findOne(saveId));
    }
}