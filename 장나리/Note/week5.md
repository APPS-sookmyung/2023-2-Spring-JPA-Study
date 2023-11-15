# 섹션 7 웹 계층 개발

## 1.  홈 화면과 레이아웃

- homeController 생성
    - 로거는 @Slf4j
    
    ```java
    @Controller
    @Slf4j
    public class HomeController {
    
        @RequestMapping("/")
        public String home(){
            log.info("home controller");
            return "home";
        }
    }
    ```
    
- thymeleaf 페이지에서 layout 검색
    
    [Thymeleaf Page Layouts - Thymeleaf](https://www.thymeleaf.org/doc/articles/layouts.html)
    
    - include style, hierarchical style …
    - include는 중복이 많이 생김
- include style로 html 작성
- spring-boot-devtools
    - 개발할때 restartedMain으로 뜸
    - 기본적으로 파일들에 대해 캐싱 안함.
    - 리컴파일하면 반영이 돼서 변형된걸 바로 확인 가능


- Bootstrap 사이트에서 css, js 파일들 다운로드 → resources>static에 복붙

[Bootstrap](https://getbootstrap.com/)

## 2. 회원 등록

- MemberForm 생성
    
    ```java
    @Getter @Setter
    public class MemberForm {
        @NotEmpty(message = "회원 이름은 필수 입니다")
        private String name;
    
        private String city;
        private String street;
        private String zipcode;
    }
    ```
    
    - @NotEmpty
        - 필수로 입력 받을 부분
        - `implementation 'org.springframework.boot:spring-boot-starter-validation’`
        - javax.validation
- MemberController 생성 - Get 매핑
    
    ```java
    @Controller
    @RequiredArgsConstructor
    public class MemberController {
        private final MemberService memberService;
    
        @GetMapping("/members/new")
        public String createForm(Model model){
            model.addAttribute("memberForm",new MemberForm());
            return "members/createMemberForm";
        }
    ```
    
    - createMemberForm.html 생성
    - @GetMapping: Get 방식으로 왔을 때
- MemberController 생성 - Post 매핑
    
    ```java
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){
    
        if(result.hasErrors()){
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
    
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
    
        memberService.join(member);
    
        return "redirect:/";
    }
    ```
    
    - @Valid : 필수로 받아야 하는 것
    - BindingResult : Spring이 제공해줌. 오류가 있을 때 튕기지 않고 오류가 담겨서 코드가 실행됨
- Thymeleaf 사이트 - **thymeleaf+Spring 찾아보기**
- 실무에서는 단순한 form 화면이 거의 없음
- 실무에서는 거의 엔티티를 그대로 파리미터로 받아서 하기에는 차이가 너무 많음
- form으로 받고 controller에서 한번 정제하는 걸 추천

## 3. 회원 목록 조회

- MemberController 생성 - 멤버 리스트
    
    ```java
    @GetMapping("/members")
        public String list(Model model){
            model.addAttribute("members", memberService.findMembers());
            return "members/memberList";
        }
    ```
    
- thymeleaf에서의 ? : null이면 더이상 진행 안해
- form으로 받지 않으면 엔티티가 화면 기능때문에 지저분해짐 → 엔티티를 최대한 순수하게 유지 → form으로 따로 받자 → 유지보수성 높아짐

## 4. 상품 등록

- BookForm 생성
    
    ```java
    @Getter @Setter
    public class BookForm {
        private Long id;
        private String name;
        private int price;
        private int stockQuantity;
        private String author;
        private String isbn;
    }
    ```
    
- ItemController 생성 - Get 매핑
    
    ```java
    @Controller
    @RequiredArgsConstructor
    public class ItemController {
        private final ItemService itemService;
    
        @GetMapping(value = "/items/new")
        public String createForm(Model model) {
            model.addAttribute("form", new BookForm());
            return "items/createItemForm";
        }
            @PostMapping(value = "/items/new")
        public String create(BookForm form) {
            Book book = new Book();
            book.setName(form.getName());
            book.setPrice(form.getPrice());
            book.setStockQuantity(form.getStockQuantity());
            book.setAuthor(form.getAuthor());
            book.setIsbn(form.getIsbn());
            itemService.saveItem(book);
            return "redirect:/items";
        }
    }
    ```
    
    - setter는 제거하는게 좋음

## 5. 상품 목록

- ItemController
    
    ```java
    @GetMapping("/items")
        public String list(Model model){
            List<Item> items = itemService.findItems();
            model.addAttribute("items",items);
            return "items/itemList";
        }
    ```
    

## 6. 상품 수정

- ItemController
    
    ```java
    @GetMapping("items/{itemId}/edit")
        public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
            Book item = (Book) itemService.findOne(itemId);
    
            BookForm form = new BookForm();
            form.setId(item.getId());
            form.setName(item.getName());
            form.setPrice(form.getPrice());
            form.setStockQuantity(item.getStockQuantity());
            form.setAuthor(item.getAuthor());
            form.setIsbn(item.getIsbn());
    
            model.addAttribute("form",form);
            return "items/updateItemForm";
        }
    
    @PostMapping("items/{itemId}/edit")
        public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){
            itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
            return "redirect:/items";
        }
    ```
    
    - updateItemForm : Book entity를 보내는 것이 아니라 form을 보냄
    - PathVariable : 아이디값을 조작해서 넘길수도 있음 → 보안상 취약점에 많이 걸림 → 유저가 아이디에 권한이 있는지 확인하는 로직 필요

## 7. 변경 감지와 병합(merge)

- 어떻게 데이터를 수정하는 게 올바른 방법인지
- 굉장히 중요!!!
- 준영속 엔티티
    - 영속성 컨텍스트가 더는 관리하지 않는 엔티티
    - 여기서는 itemService.update(book)에서 수정을 시도하는 Book 객체
    - Book 객체는 이미 DB 에 한번 저장되어서 식별자가 존재
    - 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준 영속 엔티티로 볼 수 있음
    - JPA가 관리하지 않아 값을 바꿔치지 해도 DB 업데이트가 일어나지 않음
    - 어떻게 데이터 변경 ?
    
- 변경 감지 기능 사용 - dirty checking
    - save 안해도 됨
    - 이상태로 끝나면 @Transactional로 인해 트랜잭션이 커밋됨
    - JPA가 flush 날림 → 어떤게 바뀌었는지 검사
    - 바뀐것을 업데이트 쿼리 날림
    
    ```java
    public class ItemService {
        private final ItemRepository itemRepository;
            ...
        @Transactional
        public void updateItem(Long itemId, Book param){
            Item findItem = itemRepository.findOne(itemId);
            findItem.setPrice(param.getPrice());
            findItem.setName(param.getName());
            findItem.setStockQuantity(param.getStockQuantity());
            //itemRepository.save(findItem);
        }
    }
    ```
    
- 병합(merge) 사용
    - 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능
    - `em.merge()`
    
    1. merge()를실행한다.
    2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다.
    3.  만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고, 1차 캐시에 저장한다.
    4. 조회한 영속 엔티티( mergeMember )에 member 엔티티의 값을 채워 넣는다. (member 엔티티의 모든 값을 mergeMember에 밀어 넣는다. 이때 mergeMember의 “회원1”이라는 이름이 “회원명변경”으로 바뀐다.)
    5. 영속 상태인 mergeMember를 반환한다.
    
    ```java
    @Transactional
    void update(Item itemParam) { //itemParam: 파리미터로 넘어온 준영속 상태의 엔티티 
            Item mergeItem = em.merge(itemParam);
    }
    ```
    
    - mergeItem : 영속성 컨텍스트에서 관리되는 객체
    - itemParam : 파라미터로 넘어온 객체, 영속성 컨텍스트에서 관리되지는 않음
    
    주의 : 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경됨. 병합시 값이 없으면 null 로 업데이트 될수도!! (병합은 모든 필드 교체)
    
    ```java
    Book book = new Book();
    book.setId(form.getId());
    book.setName(form.getName());
    //book.setPrice(form.getPrice());
    book.price = null;
    book.setStockQuantity(form.getStockQuantity());
    book.setAuthor(form.getAuthor());
    book.setIsbn(form.getIsbn());
    ```
    
- 가장 좋은 해결 방법
    - 항상 변경 감지 사용!!
    - 컨트롤러에서 어설프게 엔티티 생성 x
    - 이렇게 하면 유지보수성 좋음
    
    ```java
    //어설프게 만들지 말고
    @PostMapping("items/{itemId}/edit")
        public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){
            Book book = new Book();
            book.setId(form.getId());
            book.setName(form.getName());
            book.setPrice(form.getPrice());
            book.setStockQuantity(form.getStockQuantity());
            book.setAuthor(form.getAuthor());
            book.setIsbn(form.getIsbn());
    
            itemService.saveItem(book);
            return "redirect:/items";
        }
    
    //itemService.java
    @Transactional
        public void updateItem(Long itemId, String name, int price, int stockQuantity){
            Item findItem = itemRepository.findOne(itemId);
            findItem.setPrice(price);
            findItem.setName(name);
            findItem.setStockQuantity(stockQuantity);
    //        itemRepository.save(findItem);
        }
    
    // 이렇게 하자
    @PostMapping("items/{itemId}/edit")
        public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){
            itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
            return "redirect:/items";
        }
    ```
