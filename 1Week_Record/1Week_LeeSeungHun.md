## Title: [1Week] 이승훈

### 미션 요구사항 분석 & 체크리스트
- 2022-10-17 -10:00 ~ 10-19 -18:00
---
**필수 과제**

- [x]  회원 가입, 회원 정보 수정, 로그인, 로그아웃, 아이디 찾기
- [x]  글 작성, 글 수정, 글 리스트, 글 삭제

**추가 과제**

- [x]  비번 찾기
- [x]  상품 등록
- [x]  상품 수정
- [x]  상품 리스트
- [x]  상품 상세페이지


### 1주차 미션 요약
### 회원

- 로그인, 로그아웃
  - 스프링 시큐리티를 사용하여 구현
- 회원 정보 수정
  - 새 이메일과 닉네임을 입력 받아 변경
- 비밀번호 변경
  - 현재 비밀번호와 새 비밀번호, 새 비밀번호 확인을 입력 받아 변경
- 아이디 찾기
  - 가입 시 입력한 이메일 주소를 입력하여 아이디를 찾을 수 있다.
- 비밀번호 찾기
  - 로그인 시 필요한 아이디와 이메일로 비밀번호를 찾을 수 있다.
  - Gmail API를 활용하여 가입 시 메일로 임시 비밀번호를 발송한다.

### 글

- 글 작성/수정
  - 위즈윅 에디터를 활용한다.
  - 마크 다운 원문과 렌더링 결과(HTML)까지 같이 저장한다.
- 글 리스트
  - 번호, 제목, 작성자, 작성 날짜, 수정 날짜를 확인
- 글 삭제
  - 글이 삭제되면 글 리스트로 리다이렉트 한다.
  - 삭제 버튼 눌렀을 때 confirm 창으로 삭제 여부를 한 번 더 물어본 후 삭제
- 글 상세 화면
  - 번호, 제목, 작성자, 작성날짜, 수정날짜, 내용, 해시태그 확인
  - 글의 내용은 마크다운 해석이 되어야 한다.
  - 해시 태그를 클릭하면 내가 작성한 글 중 해시 태그를 포함하는 모든 글을 확인 가능

### 상품(책)

- 상품 작성/수정
  - 상품의 이름, 가격, 해시태그를 작성할 수 있다.
  - 글을 선택할 수 있다.
  - 등록을 누르면 해당 상품 상세 페이지로 이동해야 한다.
  - 내용, 가격만 수정이 가능하다.
- 상품 리스트
  - 모든 상품을 출력한다.
  - 각각 상품은 상품의 이름, 가격, 작가를 출력한다.
- 상품 삭제
  - 상품이 삭제되면 상품 리스트로 리다이렉트 한다.
  - 삭제버튼 눌렀을 때 confirm 창으로 삭제여부를 한 번 더 물어본다.
- 상품 상세 화면
  - 상품의 이름, 가격, 작가, 생성 날짜, 해시태그를 출력한다.
  - 상품 설명을 출력한다.
  - 해시 태그를 클릭하면 내가 작성한 글 중 해시 태그를 포함하는 모든 글을 확인 가능하다.

### 해시태그

- 상품 해시태그와 글 해시태그를 구분하는 중간 테이블을 따로 관리한다.

### ERD
![img.png](img.png)
---

**[접근 방법]**

체크리스트를 중심으로 각각의 기능을 구현하기 위해 어떤 생각을 했는지 정리합니다.

 
- 프론트에서의 로직을 최대한 줄이고 스프링 단에서 해당 로직을 처리한다.
- 목표했던 기능이 정상적으로 수행되는지를 우선적으로 판단하여 개발을 진행한다.
- Ui 작업의 시간을 최소화하기 위해 이전 진행했던 프로젝트의 툴을 따른다.
- 빈 충돌이 나지 않게 로직의 흐름이 단방향으로 흐를 수 있도록 유의하며 설계한다.
- 주어진 ERD를 참고하여 DB를 설계하되 필요 시 수정한다.
- 이전 강의 중 실습한 내용을 이해하고 활용해본다.

회원
- 사용자는 아이디, 패스워드, 이메일, 닉네임을 통해 회원가입을 진행할 수 있다.
- 회원 정보 수정, 패스워드 수정이 가능하다.
- 아이디를 분실한 경우 회원가입 시 입력한 이메일로 모달창을 통해 아이디를 확인할 수 있다. 
- 패스워드를 분실한 경우 회원가입시 입력한 이메일로 임시 비밀번호를 받을 수 있다.

글
- 사용자는 비로그인 상태에서는 전체 글의 리스트와 전체 상품의 리스트를 확인할 수 있다.
- 로그인 상태에서 사용자는 글을 작성할 수 있다. 
- 글은 제목, 내용, 해시태그를 입력하여 작성할 수 있다. 위즈윅 에디터를 사용하여 마크 다운 원문과 렌더링 결과(HTML)을 따로 DB에 저장한다.
- 글 상세보기에서 해당 해시태그를 클릭하면 내가 작성한 글 중 해시태그를 포함하는 글만 필터링하여 볼 수 있다.
- 글 수정과 삭제가 가능하다.

상품
- 로그인 상태에서 사용자는 상품을 등록할 수 있다.
- 상품 등록시 내가 작성한 글을 여러개 고를 수 있으며 제목, 가격, 해시태그를 달 수 있다.
- 글과 마찬가지로 상품 상세보기에서 해시태그를 입력하면 내가 작성한 해시태그를 포함한 글을 확인할 수 있다.
- 상품 수정 시에는 제목과 가격만 수정이 가능하며 상품 삭제가 가능하다.
- 상품 상세에는 상품 작성 시 추가한 글 키워드(번호, 제목)를 볼 수 있으며 클릭 시 해당 상세 글로 이동하게 된다.

해시태그
- 해시태그는 글에 포함되는 해시태그와 상품에 포함되는 해시태그가 있다.
- 해시태그와 글, 또는 해시태그와 상품은 M:N 의 관계를 가지기에 둘 사이에 중간 테이블을 하나씩 설계하였다.

**[특이사항]**
- 미션에 하나의 상품은 여러 개의 글로 구성된다는 것을 보고 상품 등록 시 내가 작성한 글을 선택할 수 있도록 selcet multiple을 활용했습니다.
- 하지만 ERD에 표기된 상품 테이블에는 글에 관련한 필드가 해시태그 키워드 뿐 이였습니다.
- 그래서 사용자와 해시태그 키워드 두가지의 조합을 활용하여 글을 찾고 상품에 적용하라는 예시로 받아드렸습니다.
- 이러한 방법으로는 상품을 등록할 때 사용자는 글에 포함되어있는 해시태그만을 상품해시태그에 입력해야한다 생각합니다.
- 글과 상품간의 M:N으로 결합하여 상품을 등록시키는 방법을 활용하면 관리가 더 복잡할 수 있지만 사용자 입장에서 편리하게 사용할 수 있을거라 생각했습니다.
- 그래서 상품과 글 사이에 ProductInterArticle 테이블을 생성하고 상품과 글이 1:N, M:1로 연결되도록 수정하고 상품 상세페이지에 상품 생성 시 추가한 글에 대한 정보를 추가하였습니다.

[Refactoring]
  - 작가와 일반 사용자를 분리하는 기능 추가
  - 기능 개발을 종료한 후 흐름이 얽히는 부분이 있는지 천천히 읽어보고 진행합니다.
  - 피어리뷰를 통해 전달받은 다양한 의견과 피드백을 조율하여 진행합니다.
