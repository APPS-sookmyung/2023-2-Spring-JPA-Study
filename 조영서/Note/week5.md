# 5주차 스터디

## **홈 화면과 레이아웃**

- jpashop/src/main/java/jpabook/jpashop/controller/HomeController.java 생성

```
package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping("/")
    public String home(){
        log.info("home controller");//home controller에 대한 log가 출력된다return "home";//home.html로 찾아가 타임리프 파일을 찾는다
    }

}
```

- jpashop/src/main/resources/templates/home.html 생성

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header"> //fragments의 header로 바꿔치기
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader" />
    <div class="jumbotron">
        <h1>HELLO SHOP</h1>
        <p class="lead">회원 기능</p>
        <p>
            <a class="btn btn-lg btn-secondary" href="/members/new">회원 가입</a>
            <a class="btn btn-lg btn-secondary" href="/members">회원 목록</a>
        </p>
        <p class="lead">상품 기능</p>
        <p>
            <a class="btn btn-lg btn-dark" href="/items/new">상품 등록</a>
            <a class="btn btn-lg btn-dark" href="/items">상품 목록</a>
        </p>
        <p class="lead">주문 기능</p>
        <p>
            <a class="btn btn-lg btn-info" href="/order">상품 주문</a>
            <a class="btn btn-lg btn-info" href="/orders">주문 내역</a>
        </p>
    </div>
    <div th:replace="fragments/footer :: footer" />
</div><!-- /container --></body>
</html>
```

- jpashop/src/main/resources/templates/fragments/bodyHeader.html 생성

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div class="header" th:fragment="bodyHeader">
  <ul class="nav nav-pills pull-right">
    <li><a href="/">Home</a></li>
  </ul>
  <a href="/"><h3 class="text-muted">HELLO SHOP</h3></a>
</div>
```

- jpashop/src/main/resources/templates/fragments/footer.html

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div class="footer" th:fragment="footer">
  <p>&copy; Hello Shop V2</p>
</div>
```

- jpashop/src/main/resources/templates/fragments/header.html

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="header">
<!-- Required meta tags --><meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrinkto-fit=no">
<!-- Bootstrap CSS --><link rel="stylesheet" href="/css/bootstrap.min.css" integrity="sha384-
ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
        crossorigin="anonymous">
<!-- Custom styles for this template --><link href="/css/jumbotron-narrow.css" rel="stylesheet">
  <title>Hello, world!</title>
</head>
```

- bootstrap 설치

[Download · Bootstrap (getbootstrap.com)](https://getbootstrap.com/docs/4.3/getting-started/download/)


- bootstrap의 css, js 파일을 spring project의 static 폴더 안으로 복사


- jpashop/src/main/resources/static/css/jumbtron-arrow.css 생성

```
/* Space out content a bit */body {
 padding-top: 20px;
 padding-bottom: 20px;
}
/* Everything but the jumbotron gets side spacing for mobile first views */.header,
.marketing,
.footer {
 padding-left: 15px;
 padding-right: 15px;
}
/* Custom page header */.header {
 border-bottom: 1px solid #e5e5e5;
}
/* Make the masthead heading the same height as the navigation */.header h3 {
 margin-top: 0;
 margin-bottom: 0;
 line-height: 40px;
 padding-bottom: 19px;
}
/* Custom page footer */.footer {
 padding-top: 19px;
 color: #777;
 border-top: 1px solid #e5e5e5;
}
/* Customize container */
@media (min-width: 768px) {
 .container {
 max-width: 730px;
 }
}
.container-narrow > hr {
 margin: 30px 0;
}
/* Main marketing message and sign up button */.jumbotron {
 text-align: center;
 border-bottom: 1px solid #e5e5e5;
}
.jumbotron .btn {
 font-size: 21px;
 padding: 14px 24px;
}
/* Supporting marketing content */.marketing {
 margin: 40px 0;
}
.marketing p + h4 {
 margin-top: 28px;
}
/* Responsive: Portrait tablets and up */
@media screen and (min-width: 768px) {
/* Remove the padding we set earlier */.header,
 .marketing,
 .footer {
 padding-left: 0;
 padding-right: 0;
 }
/* Space out the masthead */.header {
 margin-bottom: 30px;
 }
/* Remove the bottom border on the jumbotron for visual effect */.jumbotron {
 border-bottom: 0;
 }
}
```

## 

## **회원 등록**

- jpashop/src/main/java/jpabook/jpashop/controller/MemberForm.java 생성
    - 폼 객체 생성

```
package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {
    @NotEmpty(message = "회원 이름은 필수입니다")//필수값private String name;

//선택값private String city;
    private String street;
    private String zipcode;

}
```

- jpashop/src/main/java/jpabook/jpashop/controller/MemberForm.java 생성
    - 회원 등록하는 controller 생성

```
package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());//Controller에서 View로 넘어갈 때 싣는 데이터return "members/createMemberForm";//반환 HTML
    }

    @PostMapping(value = "/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(),
                form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
        memberService.join(member);
        return "redirect:/";
    }
}
```

- 폼 객체를 사용하여 화면 계층과 서비스 계층을 명확하게 분리한다.
    - 회원 등록에 필요한 정보를 담은 폼 객체를 만들고 그 폼 객체에서 정제하여 필요한 데이터만 넘겨준다.
- jpashop/src/main/resources/templates/members/createMemberForm.html 생성
    - 회원 등록 폼 화면 디자인

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<style>
    .fieldError {
    border-color: #bd2130;
    }
</style>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <form role="form" action="/members/new" th:object="${memberForm}"
          method="post">
        <div class="form-group">
            <label th:for="name">이름</label>
            <input type="text" th:field="*{name}" class="form-control"
                   placeholder="이름을 입력하세요"
                   th:class="${#fields.hasErrors('name')}? 'form-control
fieldError' : 'form-control'">
            <p th:if="${#fields.hasErrors('name')}"
               th:errors="*{name}">Incorrect date</p>
        </div>
        <div class="form-group">
            <label th:for="city">도시</label>
            <input type="text" th:field="*{city}" class="form-control"
                   placeholder="도시를 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="street">거리</label>
            <input type="text" th:field="*{street}" class="form-control"
                   placeholder="거리를 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="zipcode">우편번호</label>
            <input type="text" th:field="*{zipcode}" class="form-control"
                   placeholder="우편번호를 입력하세요">
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <br/>
    <div th:replace="fragments/footer :: footer" />
</div><!-- /container --></body>
</html>
```

## **회원 목록 조회**

- MemberController.java에 추가

```
    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
```

- jpashop/src/main/resources/templates/members/memberList.html 생성

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
 <div th:replace="fragments/bodyHeader :: bodyHeader" />
 <div>
 <table class="table table-striped">
 <thead>
 <tr>
 <th>#</th>
 <th>이름</th>
 <th>도시</th>
 <th>주소</th>
 <th>우편번호</th>
 </tr>
 </thead>
 <tbody>
 <tr th:each="member : ${members}">
 <td th:text="${member.id}"></td>
 <td th:text="${member.name}"></td>
 <td th:text="${member.address?.city}"></td>
 <td th:text="${member.address?.street}"></td>
 <td th:text="${member.address?.zipcode}"></td>
 </tr>
 </tbody>
 </table>
 </div>
 <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

- 요구 사항이 단순할 때에는 Member form 없이 Member Entity를 사용해도 된다.
    - 그러나 실무에서는 보통 요구 사항이 복잡하기 때문에 form 객체를 많이 사용한다.
- JPA 사용할 때 조심해야 할 점
    - Entity를 순수하게 유지해야 한다.
    - 그래야 유지보수가 쉽다.
- 실무에서는 Entity는 핵심 비즈니스 로직만 가지고 화면을 위한 로직은 가지면 안된다.
    - 화면에 맞는 API 혹은 form은 DTO를 이용한다.
- API 생성 시에는 Entity를 web으로 반환하도록 만들면 절대 안된다.
    - 필드를 하나 추가하게 되면 해당 필드가 노출도 되고 API 스펙이 노출된다.
- 위 코드에서는 Member 객체를 그대로 조회하였지만 DTO를 이용하여 필요한 정보만 조회하는 것을 추천한다.

## **상품 등록**

- jpashop/src/main/java/jpabook/jpashop/controller/BookForm.java 생성
    - 상품 등록 폼 생성

```
package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

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

- jpashop/src/main/java/jpabook/jpashop/controller/ItemController.java 생성

```
package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }
}
```

- jpashop/src/main/resources/templates/items/createItemForm.html 생성

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <form th:action="@{/items/new}" th:object="${form}" method="post">
        <div class="form-group">
            <label th:for="name">상품명</label>
            <input type="text" th:field="*{name}" class="form-control"
                   placeholder="이름을 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="price">가격</label>
            <input type="number" th:field="*{price}" class="form-control"
                   placeholder="가격을 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="stockQuantity">수량</label>
            <input type="number" th:field="*{stockQuantity}" class="formcontrol" placeholder="수량을 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="author">저자</label>
            <input type="text" th:field="*{author}" class="form-control"
                   placeholder="저자를 입력하세요">
        </div>
        <div class="form-group">
            <label th:for="isbn">ISBN</label>
            <input type="text" th:field="*{isbn}" class="form-control"
                   placeholder="ISBN을 입력하세요">
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
    <br/>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

## **상품 목록**

- ItemController.java 추가

```
@GetMapping(value = "/items")
 public String list(Model model) {
 List<Item> items = itemService.findItems();
 model.addAttribute("items", items);
 return "items/itemList";
 }
```

- jpashop/src/main/resources/templates/items/itemList.html 생성

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>상품명</th>
                <th>가격</th>
                <th>재고수량</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr th:each="item : ${items}">
                <td th:text="${item.id}"></td>
                <td th:text="${item.name}"></td>
                <td th:text="${item.price}"></td>
                <td th:text="${item.stockQuantity}"></td>
                <td>
                    <a href="#" th:href="@{/items/{id}/edit (id=${item.id})}"
                       class="btn btn-primary" role="button">수정</a>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div th:replace="fragments/footer :: footer"/>
</div><!-- /container --></body>
</html>
```

## **상품 수정**

- ItemController.java 수정
    - updateItemForm() : item을 하나 findOne 하여 수정할 상품을 조회한다 → 조회 겨로가를 모델 객체에 담아 View에 전달한다.

```
/**
 * 상품 수정 폼
 */@GetMapping(value = "/items/{itemId}/edit")
 public String updateItemForm(@PathVariable("itemId") Long itemId, Model
model) {
 Book item = (Book) itemService.findOne(itemId);
 BookForm form = new BookForm();
 form.setId(item.getId());
 form.setName(item.getName());
 form.setPrice(item.getPrice());
 form.setStockQuantity(item.getStockQuantity());
 form.setAuthor(item.getAuthor());
 form.setIsbn(item.getIsbn());
 model.addAttribute("form", form);
 return "items/updateItemForm";
 }
/**
 * 상품 수정
 */@PostMapping(value = "/items/{itemId}/edit")
 public String updateItem(@ModelAttribute("form") BookForm form) {
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
```

- jpashop/src/main/resources/templates/items/updateItemForm.html 생성

```
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header" />
<body>
<div class="container">
 <div th:replace="fragments/bodyHeader :: bodyHeader"/>
 <form th:object="${form}" method="post">
 <!-- id -->
 <input type="hidden" th:field="*{id}" />
 <div class="form-group">
 <label th:for="name">상품명</label>
 <input type="text" th:field="*{name}" class="form-control"
placeholder="이름을 입력하세요" />
 </div>
 <div class="form-group">
 <label th:for="price">가격</label>
 <input type="number" th:field="*{price}" class="form-control"
placeholder="가격을 입력하세요" />
 </div>
 <div class="form-group">
 <label th:for="stockQuantity">수량</label>
 <input type="number" th:field="*{stockQuantity}" class="form-
control" placeholder="수량을 입력하세요" />
 </div>
 <div class="form-group">
 <label th:for="author">저자</label>
 <input type="text" th:field="*{author}" class="form-control"
placeholder="저자를 입력하세요" />
 </div>
 <div class="form-group">
 <label th:for="isbn">ISBN</label>
 <input type="text" th:field="*{isbn}" class="form-control"
placeholder="ISBN을 입력하세요" />
 </div>
 <button type="submit" class="btn btn-primary">Submit</button>
 </form>
 <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

## **변경 감지와 병합**

- JPA 동작 원리
    - Handler만 Entity의 값을 변경하면 JPA가 transaction commit 시점에 변경된 부분을 찾아 DB 업데이트를 날린 후 transaction commit이 된다. 이 플러시를 할 때 Dirty Checking이 일어난다
    - 문제점 :
- 준영속 엔티티
    - JPA 영속성 컨텍스트가 더이상 관리하지 않는 엔티티
    - DB에 들어갔다 온 상태로 식별자가 정확하게 DB에 있는 엔티티
    - new로 생성하긴 했지만 DB에서 저장되고 불러온 엔티티
    - itemController.java에서 book에 해당
    - 임의로 만들어낸 엔티티여도 기존 식별자를 가지고 있으면 준영속 엔티티라고 볼 수 있다.
    - 문제점 : JPA가 관리를 안하기 때문에 변경 감지가 안 일어난다. (무슨 변경이 일어나는지 JPA가 다 보고 있지 않다.) 값이 변경되어도 DB에 반영이 되지 않는다.
- 준영속 엔티티를 수정하는 방법
    - 변경 감지 기능 이용 (dirty checking)
    - merge 사용
- 변경 감지 기능을 이용하는 방법
    - 영속성 컨텍스트에서 엔티티를 다시 조회하고 데이터를 수정한다
    - Transaction Commit 시점에 Dirty Checking이 동작하여 DB에 UPDATE SQL 실행
    - itemService.java에 추가

```
    @Transactional
    public void updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
    }
```

- merge 이용
    - 준영속 상태의 엔티티를 영속 상태의 엔티티로 변환한다.
    - DB에서, 엔티티 영속성컨텍스트에서 아이템 아이디로 찾아낸다.
    - merge의 파라미터에 있는 값을 이후에 찾아온 값들로 바꿔치기 한다.
    - 이후 transaction commit 될 때 반영된다.
- merge 동작 방식
    1. merge()를 실행한다.
    2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다.
        1. 만약 1차 캐시에 엔티티가 없으면 DB에서 엔티티를 조회하고 1차 캐시에 저장한다.
    3. 조회한 영속 엔티티(mergeMember)에 member 엔티티의 값을 채워넣는다.
        1. member 엔티티의 모든 값을 mergeMember에 밀어 넣는다. 이때 mergeMember의 "회원1"이라는 이름이 "회원명변경"으로 바뀐다.
    4. 영속 상태인 mergeMember를 반환한다.


- 아래 코드와 같은 흐름이다.

```
    @Transactional
    public Item updateItem(Long itemId, Book param){
        Item findItem = itemRepository.findOne (itemId);
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
        return findItem;
    }
```

- 즉, 준영속 엔티티의 식별자 값으로 영속 엔티티를 조회하고, 영속 엔티티의 값을 준영속 엔티티의 값으로 모두 교체(병합)한다. 이후 transaction commit 시점에 변경 감지 기능이 동작하여 DB에 UPDATE SQL이 실행된다.
- merge로 반환된 것이 영속성 컨텍스트에서 관리되는 객체가 된다.
    - 이후에 쓸 때는 이를 사용해야 한다.
- merge에서 주의할 점
    - 변강 감지 기능을 사용하면 원하는 속성만 선택하여 변경할 수 있지만
    - merge를 사용하면 모든 속성이 변경된다. 특히 값이 없다면 null로 교체될 수 있다.
    - 따라서 업데이트 해야 하는 필드만큼 set을 다 하여야 한다.
- 최대한 merge를 안 쓰는 것이 좋다. = setter을 최대한 쓰지 말자.
    - 의미있는 메소드를 사용해야지, 무작정 다 set을 사용하면 안된다.
    - 그래야 변경 지점이 다 entity로 가게 된다.
    - 실무에서는 업데이트 기능이 매우 제한적이다. 보통 변경 가능한 데이터만 노출하기 때문에 merge를 사용하는 것이 오히려 번거롭다.
- 따라서 가장 좋은 방법은 entity를 변경할 때 변경 감지를 사용하는 것이다.
    - Controller에서 어설프게 Entity를 생성하지 말기
    - transaction이 있는 Service 계층에 식별자와 변경할 데이터를 명확하게 전달하기
    - transaction이 있는 Service 계층에서 영속 상태의 Entity를 조회하고, Entity의 데이터를 직접 변경하기
    - Transaction Commit 시점에 변경 감지가 실행된다.
- 그렇게 하여 만든 코드
    - ItemService.java 에 추가

```
    @Transactional
    public void updateItem(Long itemId, int price, String name, int stockQuantity){
        Item item = itemRepository.findOne(itemId);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }
```

- ItemController.java 수정

```
    @PostMapping(value = "/items/{itemId}/edit")
    public String updateItem (@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
/* Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());*/
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }
```

- 이렇게 하면 Entity를 parameter로 안 쓰고 정확하게 내가 필요한 데이터만 받는다. → 유지보수성이 좋다.
    - 만약 업데이트 할 것이 많다면 DTO를 따로 만들어 ItemService를 수정할 수 있다.
