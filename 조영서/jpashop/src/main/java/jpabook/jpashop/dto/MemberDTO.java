package jpabook.jpashop.dto;

import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** 5주차 과제 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;
    private String name;
    private String address;

    // 멤버를 MemberDTO로 변환하는 정적 메서드 추가
    public static MemberDTO from(Member member) {
        MemberDTO memberDto = new MemberDTO();
        memberDto.setId(member.getId());
        memberDto.setName(member.getName());
        memberDto.setAddress(member.getAddress().getZipcode());
        return memberDto;
    }

}
