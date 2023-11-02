package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //컴포넌트 스캔의 대상이 되어 자동으로 스프링 빈 등록
@Transactional //(readOnly = false)
//@AllArgsConstructor //필드의 생성자 만들기
@RequiredArgsConstructor //final인 것들만 생성자 만들기
public class MemberService {

    private final MemberRepository memberRepository; //변경할 일 X

    //@Autowired //스프링이 스프링 빈에 등록되어 있는 MemberRepository를 주입 = field injection
    //spring에서는 생성자가 1개만 있는 경우에는 @Autowired 없이도 자동으로 injection 해줌
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId(); //항상 값이 있다는 것이 보장됨
    }

    private void validateDuplicateMember(Member member) {
        //Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    public Member findOne(Long memberId){ //단권 조회
        return memberRepository.findOne(memberId);
    }
}
