package jpabook.jpashop;

//Entity를 찾아주는, DAO와 유사

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository //컴포넌트 스캔의 대상, Spring Bean에 자동 등록
public class MemberRepository {

    @PersistenceContext
    EntityManager em;

    //저장하는 코드
    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
