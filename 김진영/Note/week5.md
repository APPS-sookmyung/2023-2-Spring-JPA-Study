# 웹 계층 개발 

## 홈 화면과 레이아웃 
* resources/static 안에 있는 것들은 정적으로 계속 제공됨 
* thymeleaf, bootstrap 사용 
* include 방식으로 fragments의 .html 내용들을 가져옴 

## 회원 등록 
* 이름을 필수로 받고 싶을때 `@NotEmpty(message="")` 어노테이션 사용
    * 스프링부트 2.3 부터는 직접 build.gradle에 'org.springframework.boot:spring-boot-starter-validation' 추가하고 refresh해야함 
* PostMapping
    ```java
        @PostMapping("/members/new")
        public String create(@Valid MemberForm form, BindingResult result){
            if (result.hasErrors()){
                return "members/createMemberForm"; 
            }

            Address address= new Address(form.getCity(),form.getStreet(),form.getZipcode());

            Member member= new Member();
            member.setName(form.getName());
            member.setAddress(address);

            memberService.join(member);
            return "redirect:/";

        }
    ```
    * `@Valid`는 MemberForm.java에서 사용했었던 @NotEmpty 적용을 위해 필요 
    * `BindingResult`는 에러가 발생하면 form에서 오류가 발생하면 오류가 담겨서 코드가 실행됨 

## 회원 목록 조회 
* getmapping
    ```java
        @GetMapping("/members")
        public String list(Model model){
            List<Member> members=memberService.findMembers();
            model.addAttribute("members",members);
            return "members/memberList";
        }
    ```
    * 요구사항이 너무 단순할 때는 엔티티 그대로 폼에 사용해도 되지만
    * 실무에서는 요구사항이 단순하지 않음 -> 엔티티가 지저분해짐 -> 유지 보수가 어려워짐 
    * 엔티티를 최대한 순수하게 유지해야함! -> 그래야 유지보수하기 편함 
    * 엔티티에 비즈니스 로직은 있으나, 화면에 대한 로직은 없어야..
    * `Dto`를 통해 필요한 것들만 가져올 수 있도록 하자 
    * api 만들때는 엔티티 넘기면 안됨
        * 만약에 엔티티에 password 필드가 있으면 노출될 수 있음 
        * api 스택이 변화됨 