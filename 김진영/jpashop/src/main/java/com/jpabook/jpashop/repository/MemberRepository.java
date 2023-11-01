package com.jpabook.jpashop.repository;

import com.jpabook.jpashop.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //스프링 빈으로 등록
@RequiredArgsConstructor
public class MemberRepository {

    /*
    @PersistenceContext
    private EntityManager em;
     */
    //@RequiredArgsConstructor 사용으로 위의 코드를 아래와 같이 변경 가능
    //(멤버 리포지토리를 생성자로 자동으로 주입) @Autowired, @PersistenceContext도 포함해서
    private final EntityManager em;

    public Long save(Member member){
        em.persist(member); //persist하는 순간 영속성 컨텍스트 객체를 올림
        return member.getId();
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); //member를 찾아서 반환해줌
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        //이름으로 찾는 것
        return em.createQuery("select m from Member m where m.name=:name",Member.class)
                .setParameter("name",name)
                .getResultList();
    }
}
