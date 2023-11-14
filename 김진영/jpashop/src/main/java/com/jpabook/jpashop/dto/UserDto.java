package com.jpabook.jpashop.dto;

import com.jpabook.jpashop.domain.Member;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String zipcode;

    public static UserDto toUserDto(Member member){
        UserDto userDto=new UserDto();
        userDto.setName(member.getName());
        userDto.setZipcode(member.getAddress().getZipcode());
        return userDto;
    }
    public static List<UserDto> toUserDtoList(List<Member> members){
        List userDtoList=new ArrayList<>();
        for (Member m: members){
            userDtoList.add(toUserDto(m));
        }
        return userDtoList;
    }
}
