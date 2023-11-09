package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

//JUnit4
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional //롤백을 위해
public class ItemServiceTest {

    @Autowired
    ItemService ItemService;
    @Autowired
    ItemRepository ItemRepository;

    @Test
    public void 상품_등록() throws Exception{
        //given
        Item item = new Item();
        //when
        ItemService.saveItem(item);
        //then
        assertEquals(item, ItemRepository.findOne(item.getId()));
    }

    @Test
    public void 상품_목록_조회() throws Exception{
        //given
        List<Item> itemList = new ArrayList<>();
        //when
        List<Item> result = ItemService.findItems();
        //then
        assertEquals(itemList, result);
    }
    @Test
    public void 상품_조회() throws Exception{
        //given
        Item item = new Item();
        ItemService.saveItem(item);
        //when
        Item result = ItemService.findOne(item.getId());
        //then
        assertEquals(item,result);
    }
}