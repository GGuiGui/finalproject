# TACBAETICS

![TACBAETICS](https://user-images.githubusercontent.com/113872320/207004883-f000f66a-6340-48ca-a242-664b8d4dafc5.png)

## 프로젝트 기획
### 프로젝트 설명
> 💡 대용량의 택배데이터를 활용, 택배를 전략적(Tactics)으로 검색, 업데이트, 모니터링 할 수 있는 배송 관리 시스템 **TACBAETICS** 입니다.

### 팀 노션
TACBAETIC 서비스의 상세 내용 및 개발일지 👀

👉🏻 [**TACBAETICS 팀 노션**](https://amusing-child-e0e.notion.site/TACBAETICS-216e27a2a7ca466faa8ac98434645d60)

### 구현기능 
1. 회원관련
    - 회원가입
    - 로그인
    - 로그아웃
2. 검색
    - 어드민 (물류센터 관리자) 
        - 관리자 메인화면 (택배기사 배송현황, 지역별 배송현황 카운팅)
        - 지역별 배송담당자 배정표
        - 운송장 번호로 검색
        - 지역별, 고객명, 날짜, 배송담당자, 배송완료일 등 필터를 통한 상세 조건 검색
    - 택배기사
        - 택배기사 메인화면 (자신의 할당 받은 택배 내역 조회)
        - 배송중과 배송완료 상태별 조회 및 카운팅
        - 수령인 명으로 조건 검색
3. 업데이트 
    - 어드민
        - 지역별 배송담당자 일괄 업데이트 (Bulkupdate)
        - 일부 조건의 택배를 배송지연 상태로 업데이트
        - 운송장번호를 입력하여 직접 배송담당자에게 할당
        - 택배 정보 상세 수정 기능
        - 전날 배송완료가 안된 택배 24:00 이후 자동 배송예정일 변경
    - 택배기사
        - 배송완료처리, 배송완료취소 처리
	- 배송완료시 배송완료일 업데이트

### 활용한 데이터
- 500만 건의 택배 데이터
<details>
<summary>📚 데이터 생성 방식</summary>
<div markdown="1">
<br/>

- 가상 관할 구역인 서울시 특정구의 우편번호를 우체국 DB로 우편번호 범위 확인 → 자바 랜덤 함수로 해당 범위의 우편번호 랜덤 생성

- 우체국 DB의 우편번호 색인을 이용하여 상세주소 생성

- 자바 코드를 사용하여 랜덤 고객명 생성

- 구글 스프레드 시트에 해당 데이터 입력

- 실제 사용은 1개의 물류센터 단위에서 이루어질 것이기 때문에, 구로구 & 강북구를 위의 과정으로 생성 (10만개)

- 하지만, 전국 단위의 데이터가 있을것이라 가정하기 때문에 나머지 데이터 수량은 프로시저를 통해 490만개의 더미 데이터 생성해서 사용 (우체국 DB의 우편번호 색인을 이용하는 방법이 수동작업이 필요, csv import 또한 많은 시간 비용 소모)	

</div>
</details>

### 설계 고려사항
<details>
<summary>📌 효율적인 배송을 위한 기능적 측면</summary>
<div markdown="1">
<br/>
	
1. 관리자

- 효율적인 배송 관리 업무를 위한 배송 현황 모니터링 및 데이터 조회
- 배송 관리 업무의 확장성을 고려 (테이블 및 데이터에 활용 가능 지표 추가로 확장 가능)
- 전략적인 배송을 위해서는 성능 뿐만아니라 기능 추가로 배송 효율성을 높일 수 있음에 포커스
	
2. 택배기사
	
- 효율적인 배송을 위한 배송완료 및 완료 취소 기능
- 택배기사의 주 궁금중 중 하나인 정산을 위한 배송완료건수 카운팅 및 과거 배송완료 이력 조회
- 택배기사의 니즈에 맞춰 편의성을 제공하도록 포커스	

</div>
</details>

<details>
<summary>📌 데이터 기준</summary>
<div markdown="1">
<br/>
 1. 택배 데이터 수 : 500만개
 
 - 2021년 택배 물동량 36억건
    - [2021년 택배물동량 36억개, 가격 인상도 145원에 달해](https://www.klnews.co.kr/news/articleView.html?idxno=304003)
    - [택배시장, 대한통운 압도적 1위](https://brunch.co.kr/@logibridge/247)
    
 - 2021년 국내 하루 택배물동량 
 	- 하루평균 약 천만건 
	- 그 중 가장 큰 점유율을 가지고 있는 CJ대한통운 점유율 약 41.9%, 
	- 따라서 대략 5,000,000 건의 물량을 커버할 수 있도록 테스트 및 설계

</div>
</details>

<details>
<summary>📌 Latency, Throughput 목표</summary>
<div markdown="1">
<br/>

 1. Latency 목표값 설정  
 
  ```
 📢 택배 업무의 실시간성으로 인해 택배기사는 0.5초이내 최대한 빠른 응답을 바랄것이며, 관리자도 단순조회는 1초이내, 소량 업데이트는 10초이내, 대량업데이트는 1분이내를 원할 것으로 예측한다.
  ```
  
   * 택배기사의 경우 : 0.5초 이내
   * 관리자의 경우 : 1초 이내
   * 복잡한 트랜잭션이 필요한 경우 : 10초이내
   * 관리자의 대량 업데이트 경우 : 60초이내
	
 2. Throughput 목표값 설정
 
  ```
 📢 뉴스 자료(2021년 기준)를 통한 예측으로, DAU(Daily Active User, 하루 순수 이용자) 추이는 평균 약 2800 명이다.
  ```
  
   * DAU : 2800 (단위 : 명)
   * 안전계수 : 3
   * 1일 평균 접속 수에 대한 최대 피크 때 배율 : 2배<br/><br/>
   * 1명당 평균 접속 수 : 1회<br/>
   &nbsp; ⇒ 2,800(명) * 1(회) / 86,400(초) * 3(안전계수) * 2(1일 평균 접속 수에 대한 최대 피크 때 배율) = 약 185 rps

  	
</div>
</details>

<details>
<summary>📌 동시성 제어 기준</summary>
<br/>

 ```
 📢 1개의 물류센터 당 약 25,000건 시나리오, 따라서 25,000건당 관리자1명, 택배기사 65명의 사용자 예측
 ```
 
 1. 1초당 최대 동시 접속자 수 : 2800명 (5,000,000건 기준)
	
 2. 시간 당 처리량 : 가용성이 보장되는 범위의 최대치
 
 	* 앞선 Latency의 내용을 참고하여 택배기사는 가능한 빠른 응답을 원하고 있음
 
<div markdown="1">
</div>
</details> 

## ⭐️ 프로젝트 설명 및 주요 기능, 개선 사항
> 대용량의 택배데이터를 활용, 택배를 전략적(Tactics)으로 검색, 업데이트, 모니터링 할 수 있는 배송 관리 시스템

### ⭐️  Covering Index & QueryDSL을 활용한 빠른 택배 현황 및 상세 정보 검색

> * 기존 JPA 적용시 복잡한 조합의 택배 데이터 상세조회의 한계성
> * Covering Index 적용, QueryDSL을 이용한 쿼리 최적화
> * 지역별 택배 현황 카운팅 기능으로 택배 배송율 모니터링 기능
> * 동일한 환경(500만개, 반복횟수1500) 에서 테스트시 이전 대비 관리자 **7560%**, 택배기사 **289%** 향상
> * 평균적으로 0.5sec 이내 Latency 목표치 충족

### ⭐️  데이터베이스 모델링 & QueryDSL을 활용한 빠른 벌크 업데이트

> * 기존 테이블 연관관계 미사용으로 대량 업데이트 시 더티체킹 식으로 일괄 수정 → 속도 및 비용 비효율
> * 지역별 택배기사 배정 테이블 및 지역정보 테이블 & QueryDSL의 Bulkupdate 사용
> * 동일한 환경(500만개, 반복횟수1500) 에서 테스트시 이전 대비 관리자 **569%** 향상
> * 모든 업데이트(할당)이 평균 0.2sec 이내, Latency 목표치를 초과 충족

### ⭐️  지역별 배송담당자 추천 알고리즘 (구현중)
> * 관리자의 지역별 배송담당자 배정 업무의 비용 절감 및 배송효율성을 위한 알고리즘
> * 지역정보에 난이도, 유저정보에 희망물량 적용 및 알고리즘에 사용
> * 관리자에게 해당 데이터를 기반으로 지역별 배송담당자 추천 리스트 제공

### ⭐️  Github Actions + Docker를 활용한 CI/CD
> * 배포 자동화를 통해 효율적인 협업 및 작업 환경 구축
> * GitHub Actions 편의성 및 접근성이 좋다고 판단, 의견 수렴 후 선택
> * VM 서버 상태와 상관없이 항상 일관된 OS 상태 보장을 위해 도커의 필요성 인식
> * EC2 서버에서 별도의 환경세팅 없이 도커 설치 후 이미지를 받아와 실행시키기 때문에, **배포 자동화**의 편의성이 높아짐

### ⭐️  Nginx를 활용한 무중단 배포
> * 새로운 배포가 완료되기 전 까지 기존에 배포되었던 서비스가 중단, 무중단 배포의 필요성 인식
> * v1 버전을 배포하고 그 다음 버전인 v2를 master에 머지할때, 이상이 있다면 v1으로 대체시킬수도 있는 장점

## 기술스택
- Languge - Java11
- Framework - Spring, Springboot
- Build Tool - Gradle
- Database - MySQL, Aws RDS
- Server - Aws EC2
- CI/CD - Github Actions, Docker, Nginx
- QueryDSL

## 아키텍처
![tacbaeticsArch](https://user-images.githubusercontent.com/113872320/207789448-894dd686-6654-454a-9095-10bcd336e973.png)

## 트러블슈팅
<details>
<summary>🧨 성능 테스트</summary>
<div markdown="1">

- **테스트 계기**
    - 순수하게 API의 속도가 어느정도 인지, Treancation이 얼마나 나오는지에 대한 테스트 
    
- **요약(관리자)**
    - 조회
    	- API중 최대 ResponseTime은 778ms
    	- Transtation은 2~3
    - 수정
    	- API중 최대 ResponseTime은 3200ms
    	- Transtation은 4~5
	
👇🏻 **자세한 내용은?** 👇🏻
[성능 테스트] ([https://grove-ash-fa3.notion.site/4c48f075b7b1471084805aa1b031b305])

</div>
</details>
<details>
[부하 테스트 및 개선]
<summary>🏪 관리자용 검색 및 업데이트 성능 개선</summary>
<div markdown="1">

- **필요성**
    - 택배 데이터가 500만개 및 이에따라 동시사용자가 증가하면서 **응답시간이 증가**
    - 조회에서는 택배기사별 route당 갯수, 수정에서는 임시할당에 시간이 많이 소모되어 나머지 부분에서도 문제가 발생하는 것으로 판단된다.
    - 그러므로 2개의 api를 개선후 결과 확인 필요

  ⇒ 관리자의 입장에서 응답시간이 길다고 이탈하지는 않겠지만, 업무의 효율성이 떨어지게됨

  ⇒ 목표 : 페이지 로딩 시간 **1초 이내**

### 문제

- 조회에서는 택배기사별 route당 갯수, 수정에서는 임시할당에 시간이 많이 소모되어 나머지 부분에서도 문제가 발생하는 것으로 판단된다.
- 그러므로 2개의 api를 개선후 결과 확인 필요

### 해결 시도

### 조회 기능 개선

- 택배기사 route count
1. 택배기사에 대한 조회와 이에 따른 count의 쿼리가 따로 분리되어 있음
2. for문안 if문과 리스트에 추가시키는 부분에 문제가 생기는 것으로 판단
    
    (프론트 처리하기 편하게 만든 결과값을 결국 프론트단에서 처리해야 불필요한 코드 제거됨)
    

⇒ 추가1 : for문에서 쿼리가 많이 나간다는 것 ⇒ user당 조회 count 쿼리로 해결해야됨

⇒ 추가2 : 한 컨트롤러당 하나의 역할을 해야 하는데 기능을 세분화 시키지 않았음

(Controller의 각 기능별 분리 필요 == 택배기사 테이블 조회(할당별), route count 조회로 나누기)

### 수정 기능 개선

- 지역별 배송 담당자 배정을 통한 전체 할당기능
1. for문이 사용되면서 쿼리가 10번 나가는 것은 개선방법이 없을것으로 판단, update 쿼리에서 사용되는 서브쿼리를 따로 조회 쿼리를 사용하는 방법으로 진행함
	
분석 : 임시할당의 Throughput 5.1kb/sec 에서 5.4/sec로 약 5%상승 서브쿼리 제거후 임시할당의 평균과 최소, 최대가 줄어들어 조금더 안정적이 됨.

**그러나 아직 오류가 크게 줄어들지 않았다.**

1. hashmap을 사용 username을 종류별로 줄여서 update where 조건을 eq대신 in으로 대체
    
    (전체적인 update 쿼리문 줄이는 방법)
   
분석 : 임시할당의 Throughput 4.2kb/sec 에서 6.1kb/sec로 **약 31%** 상승, username 중복이 없는 경우는 적용전 결과와 차이가 없었지만 중복이 있는 경우는 성능이 개선됨

👇🏻 **자세한 내용은?** 👇🏻
[관리자용 성능 테스트 및 개선] ([https://grove-ash-fa3.notion.site/ADMIN-9d092e84eb854ecb9675ed057f6a730e])
	
</div>
</details>

<details>
<summary>💬 로깅</summary>
<div markdown="1">

- **로깅 기능의 필요성 및 목표**
    - 애플리케이션 최적화를 위해서 **로직이 작동하는 시간**을 기록 및 측정
    - 스프링에서 메서드의 호출속도를 실시간으로 파악
    - 기존에 작성된 로직에 영향을 끼치거나 로직의 변경이 있으면 안된다.
- **문제점**
    - 로그가 필요한 곳에 일일이 로그 로직을 작성해야 한다.
    - 중복된 로그 로직 때문에 유지보수 및 업데이트 비용이 발생한다.
- **문제 해결**
    - 로그 기능을 공통의 관심사라고 판단 **AOP**를 사용하여 일관성 있는 로직을 구현

</div>
</details>

## 프로젝트 관리
<details>
<summary>지속적인 배포(CD)</summary>
<div markdown="1">

   * 지속적인 배포의 필요성
     * 기능이 추가될 때마다 배포해야하는 불편함이 있어 배포 자동화의 필요성 인식
   * 대안
   
     |Jenkins|Github Actions|
     |------|------|
     |무료|일정 사용량 이상 시 유료|
     |작업 또는 작업이 동기화되어 제품을 시장에 배포하는데 더 많은 시간이 소요|클라우드가 있으므로, 별도 설치 필요 없음|
     |계정 및 트리거를 기반으로하며 Github 이벤트를 준수하지 않는 빌드를 중심으로 함|모든 Github 이벤트에 대한 작업을 제공하고 다양한 언어와 프레임워크를 지원|
     |전 세계 많은 사람들이 이용해 문서가 다양|젠킨스에 비해 문서가 없음|
     |캐싱 메커니즘을 지원하기 위해 플러그인 사용 가능|캐싱이 필요한 경우 자체 캐싱 메커니즘을 작성해야함|
     
   * 선택
     * GitHub Actions 편의성 및 접근성이 좋다고 판단, 의견 수렴 후 선택.
	
</div>
</details>

<details>
<summary>무중단 배포</summary>
<div markdown="1">

   * 무중단 배포의 필요성
     * 새로운 배포가 완료되기 전 까지 기존에 배포되었던 프로세스가 종료 (서비스가 중단), 무중단 배포의 필요성 인식
   * 대안
     * AWS 블루그린 
     * Nginx
     
   * 선택
     * 가장 저렴한 Nginx로 선택 및 사용.
	
   * Github Actions & Docker 와 연동하여 Merge 시 무중단 배포 구현
</div>
</details>


<details>
<summary>Git</summary>
<div markdown="1">
<br/>

   * Git Commit 메시지 컨벤션의 필요성
     * commit된 코드가 어떤 내용을 작성 했는 지 파악하려면 commit을 확인해야 한다.
     * 프로젝트 진행 중에는 수 많은 코드가 commit되기 때문에 일일이 내용을 확인하기 힘들기 때문에 
메시지 컨벤션을 통해서 제목이나 description을 통해서 commit의 정보를 전달한다.
   * Git Commit 메시지 컨벤션 전략
   
   ```
   Feat : 내가 작업한 기능 구현 완료
   Fix : 버그 수정 및 기능 수정완료
   Build : 빌드 수정 완료
   Chore : 자잘한 수정 완료
   Ci : Ci 설정 수정완료
   Docs : 문서 수정에 대한 커밋
   Style : 코드 스타일 혹은 포맷 등에 관한 커밋
   Refactor : 코드 리팩토링에 대한 커밋
   Test : 테스트 코드 수정에 대한 커밋
   ```
   
 👇🏻더 자세한 내용이 알고싶다면?👇🏻<br/>
    &nbsp; 🚥 &nbsp; [Git](https://www.notion.so/Git-3d521c25cdc14f82b8892075a813288a)
</div>
</details>


## 설계
<details>
<summary>📘 DB 설계</summary>
<div markdown="1">
<br/>
	
![tacbaeticsERD](https://user-images.githubusercontent.com/113872320/207815616-a32bd7f4-6bef-42f9-8bc1-3b1b43d9117e.png)
	
1. 계정 (어드민, 택배기사)
2. 택배
    - 운송장 번호
    - 상세주소
    - 운송장 등록 일자
    - 택배 배송예정일
    - 수령인
    - 배송상태
    - 배송담당자
    - 좌표
    - 배송완료일
3. 지역정보
    - 라우트 (지역을 1차적으로 나눈 세부지역)
    - 서브라우트 (라우트를 다시 나눈 세부지역)
4. 지역별 택배담당자 배정 데이터
	
</div>
</details>

<details>
<summary>📝 API 설계</summary>
<div markdown="1">
<br/>
	
- [API 명세서](https://www.notion.so/API-f34d6a71a69846749050d51fd0f44bcf)
	
</div>
</details>

## 팀원

|이름|포지션|분담|@ Email|Github|
|------|------|------|------|------|
|권순한|BackEnd|프로젝트 매니징<br/> 시나리오 설계<br/>데이터 생성<br/>업데이트 기능|soonable@gmail.com|https://github.com/soonhankwon|
|이재선|BackEnd|검색(쿼리 최적화) <br/>택배기사용 조회기능<br/>택배기사용 업데이트<br/>부하 테스트|jason1208@naver.com|https://github.com/sun1203|
|최규범|BackEnd|회원가입<br/>로그인<br/>관리자용 조회기능<br/>관리자용 업데이트<br/>부하 테스트|rbqjachl95@google.com|https://github.com/GGuiGui|
