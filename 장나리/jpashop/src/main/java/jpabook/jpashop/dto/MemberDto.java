package jpabook.jpashop.dto;

import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {
    @NotEmpty
    private String name;

    private String zipcode;

    public static MemberDto createMember(Member member){
        MemberDto memberDto = new MemberDto();
        memberDto.setName(member.getName());
        memberDto.setZipcode(member.getAddress().getZipcode());
        return memberDto;
    }

    public static List<MemberDto> createMemberList(List<Member> members){
        List<MemberDto> memberList = new ArrayList<>();
        for (Member m: members){
            memberList.add(createMember(m));
        }
        return memberList;
    }
}