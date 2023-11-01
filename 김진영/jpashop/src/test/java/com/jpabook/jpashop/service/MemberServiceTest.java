package com.jpabook.jpashop.service;


import com.jpabook.jpashop.domain.Member;
import com.jpabook.jpashop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class) //최근 스프링부트는 JUnit5 사용하므로 변경
@SpringBootTest
@Transactional //데이터를 변경해야하므로
public class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId= memberService.join(member);

        //then
        //em.flush();
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1= new Member();
        member1.setName("kim1");

        Member member2= new Member();
        member2.setName("kim1"); //중복회원을 넣자 -> exception을 테스트에서 발생시키자

        //when
        memberService.join(member1);


        //then
        //fail("예외가 발생해야한다"); //여기로 오면 안됨
        assertThrows(IllegalStateException.class,()->{
            memberService.join(member2); //에러가 발생해야함
        });
    }

}
