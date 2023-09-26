package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    @PersistenceContext
    EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId(); // 커맨드와 쿼리 분리해라. 아이디 정도만 조회
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
