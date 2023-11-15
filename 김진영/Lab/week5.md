# 5주차 과제 
Section7 의 “회원 목록 조회”에서 화면에 Member 객체 그대로 조회할 수 있도록 하는데 이걸 DTO를 사용하여 “이름”과 “우편번호”만 화면에 출력 될 수 있도록 하기

1. dto package를 만들어서 UserDto라는 class를 만들고 dto객체로 바꾸는 static 메소드를 작성했습니다 
    ```java
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
    ```
2. MemberController에 @GetMapping("/members2")로 members 리스트를 UserDto형의 리스트로 보내도록 하였습니다. 
    ```java
        @GetMapping("/members2")
        public String list2(Model model){
            List<Member> members=memberService.findMembers();
            List<UserDto> membersDto=UserDto.toUserDtoList(members);
            model.addAttribute("members",membersDto);
            return "members/memberList";
        }
    ```