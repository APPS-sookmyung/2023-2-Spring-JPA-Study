## **요구사항 분석**

<HELLO SHOP>

1. 회원 기능 : 회원 가입, 회원 목록
2. 상품 기능 : 상품 등록, 상품 목록
3. 주문 기능 : 상품 주문, 주문 내역

<기능 목록>

- 회원 기능
    - 회원 등록
    - 회원 조회
- 상품 기능
    - 상품 등록
    - 상품 수정
    - 상품 조회
- 주문 기능
    - 상품 주문
    - 주문 내역 조회
    - 주문 취소
- 기타 요구사항
    - 상품은 재고 관리가 필요하다.
    - 상품의 종류는 도서, 음반, 영화가 있다.
    - 상품을 카테고리로 구분할 수 있다.
    - 상품 주문시 배송 정보를 입력할 수 있다.

## **도메인 모델과 테이블 설계**

![https://blog.kakaocdn.net/dn/d5TbBq/btswbzcYdUA/odKSwHwYqGSxNg8ohIkgi1/img.png](https://blog.kakaocdn.net/dn/d5TbBq/btswbzcYdUA/odKSwHwYqGSxNg8ohIkgi1/img.png)

1. 도메인 모델 설계

- 회원 : 주문 = 1 : N(*)
- 주문 : 상품 = N : N → 보통 사용하지 않는다 → 1 : N or N : 1 로 변경
- 주문 : 배송 = 1 : 1
- 상품 : 카테고리 = N : N

2. 엔티티 설계

(1) 회원 엔티티

![https://blog.kakaocdn.net/dn/bCA5yq/btswBEdaBA6/vdYDDDa3zPF0JsECjRNlgk/img.png](https://blog.kakaocdn.net/dn/bCA5yq/btswBEdaBA6/vdYDDDa3zPF0JsECjRNlgk/img.png)

- Order와 Delivery는 양방향 관계가 맞다
- 실제로는 회원이 주문을 참조하지는 않고, 주문이 회원을 참조하는 것이다

(2) 회원 테이블 분석

![https://blog.kakaocdn.net/dn/oTAxZ/btsv7qVQPtd/mQYUKsUbISjjKiCdgMOCv0/img.png](https://blog.kakaocdn.net/dn/oTAxZ/btsv7qVQPtd/mQYUKsUbISjjKiCdgMOCv0/img.png)

- 1:N 관계에서는 N에 무조건 외래키가 존재한다
- 외래키가 있는 곳을 연관관계의 주인으로 정하는 것이 좋다
- 연관관계의 주인은 외래키를 누가 관리하냐의 문제이다 (우위나 중요성을 따지는 문제 X)

## **엔티티 클래스 개발**

- 몇 대 몇 관계는 Annotaion으로 지정한다
- 연관관계의 주인은 값이 변경됐을 때 바꿀 FK로 지정한다
- FK와 가까운 것을 연관관계의 주인으로 한다
- InheritanceType 종류

![https://blog.kakaocdn.net/dn/byxT1F/btswUUHcM3p/oG78KlnFqKTjJVKkRyvkH0/img.png](https://blog.kakaocdn.net/dn/byxT1F/btswUUHcM3p/oG78KlnFqKTjJVKkRyvkH0/img.png)

- @Enumerated 사용 시, EnumType.ORDINAL은 숫자이므로 순서에 밀리면 DB 장애가 난다
    - 따라서 EnumType.STRING으로 사용해야 순서에 밀리지 않는다
- 실무에서는 Entity의 Data는 조회할 일이 많으므로 Getter은 항상 열어두는 것이 편리하다
    - Setter은 Entity가 왜 변경되는지 추적하기 어려워지기 때문에 막 열어두면 안된다

## **Entity 설계 시 주의점**

- Entity에는 가급적 Setter 사용 X
- 모든 연관관계는 자연로딩으로 설정
- 컬렉션은 필드에서 초기