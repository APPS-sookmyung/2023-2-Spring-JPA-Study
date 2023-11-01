package com.jpabook.jpashop.repository;

import com.jpabook.jpashop.domain.item.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        if (item.getId() ==null){ //item은 jpa에 저장하기 전까지 id값이 없음 -> 새로 생성하는 객체이다
            em.persist(item);
        }else{
            em.merge(item); //update같은 것
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i",Item.class)
                .getResultList();
    }


}
