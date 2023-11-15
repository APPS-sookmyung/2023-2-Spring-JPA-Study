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

## 상품 등록/목록 
* 실무에서는 setter 다 날림 
* 회원 등록/목록과 비슷한 과정임 

## 상품 수정 
* `수정`이 제일 중요하다! 복잡하기 때문! 
* GET을 통해 PathVariable에서 userId를 받아와 등록되어있던 내용을 그대로 불러오기 
    ```java
        @GetMapping("/items/{itemId}/edit")
        public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
            Book item=(Book) itemService.findOne(itemId);

            BookForm form=new BookForm();
            //이런 밑의 코드들을 자동화 해주는 것 -> modelMapper
            form.setId(item.getId());
            form.setName(item.getName());
            form.setPrice(item.getPrice());
            form.setStockQuantity(item.getStockQuantity());
            form.setAuthor(item.getAuthor());
            form.setIsbn(item.getIsbn());

            model.addAttribute("form",form);
            return "items/updateItemForm";
        }
    ```
* POST를 통해 내용을 수정한다 
    ```java
        @PostMapping("/items/{itemId}/edit")
        public String updateItem(@PathVariable String itemId,@ModelAttribute("form") BookForm form){
            Book book=new Book();
            book.setId(form.getId());
            book.setName(form.getName());
            book.setPrice(form.getPrice());
            book.setStockQuantity(form.getStockQuantity());
            book.setAuthor(form.getAuthor());
            book.setIsbn(form.getIsbn());

            itemService.saveItem(book);
            return "redirect:items"; 
        }
    ```
    * 함수 파라미터에 itemId 만들 필요 없음 : form에서 자동으로 받아오기 때문이다 
    * @ModelAttirbute("form")을 선언해야함 (.html에서 "form"을 읽어오는 것)
    * 추가로 유저가 수정할 수 있는지 권한 체크하는 로직이 필요함!
        * 세션을 사용해도 되나 요즘은 잘 사용하지 않는다고.. 
* [중요] merge가 머지? 
    ```java
        public void save(Item item){
            if (item.getId() ==null){ //item은 jpa에 저장하기 전까지 id값이 없음 -> 새로 생성하는 객체이다
                em.persist(item);
            }else{
                em.merge(item); //update같은 것
            }
        }
    ```

## 변경 감지와 병합(merge) !!중요!!
* 준영속 엔티티: 영속성 컨텍스트가 더이상 관리하지 않는 엔티티 
    * itemService.saveItem(book)에서 수정을 시도하는 Book 객체라고 할 수 있음 (database에 id가 있음)
    * JPA가 관리하지 못함 
    * 준영속 엔티티를 수정하는 방법
        1. 변경 감지(dirty checking) 기능 사용
        2. 병합(merge) 사용 
1. **`변경 감지 (dirty checking)`** 기능 사용
    ```java
        @Transactional
        public void updateItem(Long itemId, Book param){
            Item findItem = itemRepository.findOne(itemId);
            findItem.setPrice(param.getPrice());
            findItem.setName(param.getName());
            findItem.setStockQuantity(param.getStockQuantity());
            //...
        }
    ```
    * findItem은 JPA가 관리하는 영속성 컨텍스트 
    * Book param 객체에서 가져온 값을 findItem에 set 하면 끝 
    * 그러면 @Transactional에 의해서 JPA가 DB를 업데이트 시킴! 
2. **`병합 (merge)`** 사용 
    * 병합은 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능 
    * 기존의 영속성 컨텍스트를 바꿔치기하는 것! 
    * 병합 동작 방식
        1. merge() 를 실행
        2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회
            * 만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고 1차 캐시에 저장
        3. 조회한 영속 엔티티(mergeMember)에 member 엔티티 값을 채워 넣음 (member 엔티티의 모든 값을 mergeMember에 밀어넣음. 이때 mergeMember의 "회원1"이라는 이름이 "회원명변경"으로 바뀜)
        4. 영속 상태인 mergeMember를 반환 
    * 주의: 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, **병합을 사용하면 모든 속성이 변경됨** 
        * 병합시 값이 없으면 null로 업데이트 할 위험도 있음 (병합은 모든 필드 교체)
* 가급적이면 변경 감지(dirty checking)기능을 사용하자 
    * 단순한 경우에는 merge() 사용해도 되는거임 
    * 실무에서는 보통 변경가능한 데이터만 노출하기 때문에, 병합을 사용하는 것이
오히려 번거롭다.
* 따라서 이런식으로 코드를 작성하면 더욱 깔끔해짐 
    ```java
        @Transactional
        public Item updateItem(Long itemId, String name, int price,int stockQuantity){
            Item findItem = itemRepository.findOne(itemId);
            findItem.setPrice(price);
            findItem.setName(name);
            findItem.setStockQuantity(stockQuantity);
            return findItem;
        }
    ```
    ```java
        @PostMapping("/items/{itemId}/edit")
        public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form){
            itemService.updateItem(itemId,form.getName(),form.getPrice(),form.getStockQuantity());
            return "redirect:/items";
        }
    ```
    * 유지 보수하기 더 편함 
    * 필요한 것만 받아서 수정이 가능하다 
    * 아니면 UpdateItemDto class를 만들어서 parameter로 UpdateItemDto를 넘겨줘도 됨 
        ```java
        @Transactional
        public Item updateItem(Long itemId, UpdateItemDto updateitemdto){
            Item findItem = itemRepository.findOne(itemId);
            findItem.setPrice(price);
            findItem.setName(name);
            findItem.setStockQuantity(stockQuantity);
            return findItem;
        }
        ```
    * id와 변경할 데이터(파라미터 or dto)를 명확하게 전달해야함 
