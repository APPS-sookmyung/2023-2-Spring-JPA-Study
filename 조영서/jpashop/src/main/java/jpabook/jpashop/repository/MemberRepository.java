package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Member member){ //jpa가 저장하는 로직
        em.persist(member);
    }

    public Member findOne(Long id) { //Member를 반환, 단권 조회
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){ //리스트 조회
        return em.createQuery("select m from Member m", Member.class) //jpql이라는 것, entity 객체에 대해 query
                .getResultList();
    }

    public List<Member> findByName(String name){ //이름으로 회원 검색
        return em.createQuery("select m from Member m where m.name = :name", Member.class) //jpql이라는 것, entity 객체에 대해 query
                .setParameter("name", name)
                .getResultList();
    }

    //회원 삭제 API
    public void delete(Long id) {
        Member member = em.find(Member.class, id);
        if (member != null) {
            em.remove(member);
        } else {
            throw new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다.");
        }
    }
}
