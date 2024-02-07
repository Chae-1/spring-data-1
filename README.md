트랜잭션, DB 락
==
데이터베이스에 데이터를 저장하는 가장 큰 이유는 **트랜잭션**을 지원하기 때문이다.

**트랜잭션**은 하나의 거래를 안전하게 처리하도록 보장하는 것이다. 
- 여러개의 쿼리가 하나의 트랜잭션이라면, 모든 쿼리들이 정상적으로 처리되야 한다.
- 모든 작업이 성공해서 데이터베이스에 정상 반영하는 것을 **커밋**이라 하고, 하나라도 실패해서 이전 상태로 되돌리는 것을 **롤백**이라고 한다.


## 트랜잭션 ACID
트랜잭션은 ACID라는 원자성(Atomicity), 일관성(Consistency), 격리성(Isolation), 지속성(Duration)을 보장해야한다.

- **원자성:** 트랜잭션 내에서 실행한 작업은 마치 하나의 작업인 것처럼 모두 성공하거나, 실패해야한다. All Or Nothing
- **일관성:** 모든 트랜잭션은 일관성 있는 데이터베이스 상태를 유지해야 한다.
- **격리성:** 동시에 실행되는 트랜잭션들이 서로에게 영향을 미치지 않도록 격리한다. 동시성 관련 문제로 인해, 격리 수준을 선택할 수 있다.
- **지속성:** 트랜잭션을 성공적으로 끝내면 그 결과가 항상 기록되어야 한다. 중간에 시스템에 문제가 발생해도 로그를 통해 성공한 트랜잭션 내용을 복구 해야한다.


## 트랜잭션 이해
애플리케이션에서 커넥션을 획득하면 데이터베이스 서버 내부에는 **세션**이 만들어진다. 

#### 트랜잭션 사용
여기서는 세션 1이 테이블에 데이터를 삽입하고 세션 2가 이를 조회하는 경우를 생각해보자.
- 데이터 변경 쿼리를 실행하고 그 결과를 데이터베이스에 반영하려면 `commit` 명령어를 호출하고, 이전 상태로 되돌리고 싶으면 `rollback`을 사용한다
- 커밋 이전에는 임시적으로 데이터를 저장해 트랜잭션을 수행한 세션에서만 변경된 데이터가 보이고 다른 세션에서는 보이지 않는다.
- 세션 1에서 변경 쿼리를 실행시키고 `commit`하지 않은 상태에서 세션 2에서 select 를 수행하면 데이터가 보이지 않는다.

**세션 1 신규 데이터를 추가한 후 commit**
- 커밋을 하게되면 세션 2 뿐아닌 모든 세션에서 세션 1에서 진행한 트랜잭션의 결과를 조회할 수 있게 된다.

**세션 1 신규 데이터를 추가한 후 rollback**
- 세션 1에서 임시로 저장한 데이터를 이전 상태로 되돌리고, 다른 세션들은 이전 처럼 정상적으로 조회가 가능하다.


#### 자동 커밋, 수동 커밋

데이터베이스는 **자동 커밋**모드를 기본적으로 사용하고 있다. 자동 커밋모드는 등록, 수정, 삭제 쿼리를 호출하면 그 즉시 커밋이 되는 데이터베이스 모드이다.

**수동 커밋**모드는 `commit`, `rollback` 명령어를 통해 변경사항을 저장하고 되돌릴 수 있는 데이터베이스 모드이다. 수동 커밋모드를 활성화했을 때, 트랜잭션을 시작했다고 한다.
- 트랜잭션 내 작업이 하나라도 실패하면, `rollback`, 전부 성공하면 데이터를 반영하는 `commit`을 호출해서 원자성을 지켜야한다.


## DB 락
세션1이 트랜잭션을 시작하고 수정하는 동안 커밋하지 않은 데이터를 다른 세션이 동시에 수정한다면 심각한 문제가 발생한다.
- 원자성이 깨지고, 잘못된 데이터를 수정하는 문제가 동시에 발생

이런 문제가 발생하지 않기 위해 커밋이나, 롤백 전까지는 다른 세션이 데이터에 접근할 수 없도록 막아야한다. 이를 위해 DB에서는 **락**을 제공한다.

#### 락 동작 - 삭제, 등록, 업데이트
세션 1, 세션 2가 동시에 특정한 row를 수정하는 상황이라고 가정한다면 다음과 같이 동작한다.
- 세션 1이 먼저 해당 row를 수정하기 위해 트랜잭션을 수행하면, 그 row들의 락을 획득한다.
- 세션 2가 트랜잭션을 수행하려고 보니, 락이 존재하지 않기 때문에 대기한다.
- 세션 1이 commit을 하면 락이 반환되고 동시에, 대기하고 있던 세션 2에서 lock을 획득해 트랜잭션을 수행한다.
- 세션 2에서 Lock 을 획득하지 못하면 timeout이 발생해 오류가 발생한다.


#### 락 동작 - 조회
조회를 할 때, 다른 세션에서 업데이트를 하면 안되는 경우 락을 걸고 조회를 할 수 있다.
- select for update 문을 사용하면 락을 걸고 조회를 할 수있다.
- 중요한 어플리케이션 로직이 처리되야할 경우 이를 사용한다.



#### 트랜잭션 - 코드 적용
코드로 트랜잭션을 적용하기 위해선, 수동 커밋모드로 변경시키고 비즈니스 로직을 전부 실행한 후 커밋이나 롤백을 수행해야한다.

서비스 계층으로 트랜잭션 코드가 있어 매우 복잡한 코드가 요구되는데, 스프링이 해당 문제를 해결해준다.

#### 트랜잭션 코드의 문제점
애플리케이션 구조
- 가장 단순하고 많이 사용하는 방법은 프레젠테이션 계층, 서비스 계층, 데이터 접근 계층을 나눠 개발하는 것이 가장 단순하다.

- **프레젠테이션 계층**
  - UI와 관련된 처리 기술
  - 웹 요청과 응답
  - 사용자 요청을 검증
  - 서블릿, HTTP 같은 웹기술, 스프링 MVC

- **서비스 계층**
  - 비즈니스 로직을 담당
  - 순수 자바 코드로 작성
- **데이터 접근 계층**
  - 실제 데이터베이스에 접근하는 코드
  - JDBC, JPA, Redis, File, Mongo

3가지 계층 중 가장 중요한 계층은 서비스 계층이다. 그래서 순수한 서비스 계층을 유지하는 것이 중요하다.

서비스 계층을 특정 기술에 종속적이지 않게 개발하면 다음과 같은 장점을 가진다.
- 프레젠테이션 계층, 데이터 접근 계층의 기술이 변경되도 핵심 비즈니스 코드에는 영향을 미치지 않는다.
- 핵심 로직을 테스트하고, 유지보수하기가 쉬워진다.

현재 모든 서비스 계층에서 데이터 접근 기술에 의존하고 있다.
- ex) SQLException, 트랜잭션 관련 코드

**정리**
- 트랜잭션 코드
  - 서비스 계층은 순수한 자바코드로 구성돼 있어야 하나, 그렇지 못한 문제
    - 비즈니스 로직은 순수해야 한다. 그래서 데이터 접근 계층에 JDBC 코드를 몰아둔 것이다.
    - 하지만, 트랜잭션을 수행하기 위해 서비스 계층에서 데이터 접근 기술을 사용해야한다.
  - 같은 커넥션을 유지하기 위해 커넥션을 계속 파라미터를 통해 넘겨야한다.
  - 트랜잭션 예외 처리가 반복적이다.
  
- 예외 누수
  - SQLException이 체크 예외이기 때문에 데이터 접근 계층을 호출한 서비스 계층에서 예외를 처리하거나 명시적으로 다시 밖으로 던져야한다.  
- JDBC 반복 문제
  - 유사한 코드들이 지속적으로 반복된다.
  - try, catch, finally..


#### 트랜잭션 추상화
트랜잭션 코드를 추상화해서 인터페이스로 제공하면 구현 기술이 달라져도 DI를 통해 OCP 원칙을 지킬 수 있다.

스프링에서 `PlatformTransactionManager`라는 인터페이스를 제공하고 각 기술들이 인터페이스를 구현하고 있어 가져다 쓰면 된다.

#### 트랜잭션 동기화
스프링의 트랜잭션 매니저는 2가지 역할을 한다.
- 트랜잭션 추상화
- 리소스 동기화
  - 트랜잭션을 유지하려면 커넥션을 유지해야한다. 
    - 트랜잭션 매니저는 트랜잭션 동기화 매니저를 활용해 커넥션을 유지시켜준다.

**동작 방식**
1. 트랜잭션 매니저가 데이터소스를 통해 커넥션을 만들고 트랜잭션을 시작.
2. 트랜잭션 매니저는 커넥션을 트랜잭션 동기화 매니저에 보관.
3. 리포지토리에서 동기화 매니저에 있는 커넥션을 꺼내서 사용한다.
4. 트랜잭션이 종료되면 커넥션을 커넥션 풀에 반환하거나, 커넥션을 닫는다.


#### 트랜잭션 템플릿

#### 트랜잭션 AOP


## 자바 예외
- `Throwable`: 최상위 예외, 구현체로는 `Exception`, `Error`가 존재한다.
- `Error`: 메모리 부족이나, 애플리케이션에서 복구 불가능한 시스템 예외이다.
  - `Error`예외는 복구가 불가능하기 때문에, try~catch 구문으로 예외처리하면 안된다.
- `Exception`: 체크 예외, 실질적인 최상위 예외. 단, `RuntimeException`을 제외한다.
- `RuntimeException`: 런타임 예외

### 예외 규칙
- 예외를 잡아서 처리하거나 던져야한다.
- 예외를 잡거나 던질 때, 지정한 예외뿐 아니라 그 자식도 처리한다.
- main() 쓰레드의 경우 시스템이 종료된다.

#### 체크 예외
- 체크 예외는 반드시 처리하거나, 던진다는 것을 명시하지 않으면 컴파일 오류가 발생한다. 예외를 누락하지 않도록 문제를 잡아준다.
- 모든 체크 예외를 반드시 잡거나 던지도록 처리해야 하기 때문에 번거롭다.

#### 언체크 예외
- 언체크 예외는 명시하지 않아도 컴파일 오류가 발생하지 않는다.
- 예외처리를 누락할 수 있다는 단점이 있지만, 예외코드가 다른 계층으로 전파되지 않는다는 장점이 존재한다.


#### 체크 예외 활용

**원칙**
- 기본적으로 런타임 예외를 사용한다.
- 체크 예외는 비즈니스 로직상 의도적으로 던지는 예외에만 사용한다.

**문제점**
- 체크 예외는 대부분 SQLException, ConnectionException 등 애플리케이션에서 해결할 수 없는 시스템 예외이다.
  - 이런 시스템 예외는 애플리케이션 자체에서 해결할 수 없기 때문에 계속 예외를 던진다.
  - 이런 시스템 예외는 `ControllerAdvice`, 스프링 인터셉터, 서블릿 필터 등을 통해 공통으로 처리해야한다.
- 이런 해결할 수 없는 예외에 대한 종속성이 생긴다.
  - 시스템 구현기술이 달라졌을 때, 해당 기술예외로 인해 코드를 수정해야한다는 단점이 존재한다.

이런 문제점들로 인해, 런타임 예외를 활용한다.


#### 언체크 예외 활용
- 체크 예외를 언체크 예외로 변경해서 예외를 던질 수 있다.
- 끝까지 예외를 던져 공통으로 처리하면 된다.
- 예외 코드가 전파되지 않아 기술이 변경되도 다른 계층에서 영향을 받지 않는다.

해결할 수 없는 예외의 경우 런타임 예외로 던지는 것이 좋지만 놓칠수 있기 때문에 문서화하는 것이 중요하다.


#### 예외 포함
예외를 전환할 때, 기존 예외를 반드시 포함시켜야한다.
- catch로 잡은 예외를 RuntimeException에 포함시켜 던질 수 있다.
- 포함시키지 않는다면, 근원적인 원인에 대한 로그가 남지 않아 문제의 원인을 알 수 없다.

반드시, 예외를 전환할 때, 기존 예외를 포함시켜야한다.


## 스프링 예외 처리
서비스 코드를 순수하게 유지하기 위해서, 체크 예외자체도 런타임 예외로 전환해서 던지면 된다.

특정 예외를 다른 예외로 처리하고 싶은 경우가 존재할 수 있다.
- SQLException 내부에는 DB에서 발생한 오류코드를 조회할 수 있다.
- 해당 오류코드를 이용해, 특정 예외를 변환해서 처리할 수 있다.

너무나도 많은 오류코드들이 존재해, 스프링에서는 이들을 추상화해놨다.
- 'DataAccessException'이라는 예외가 존재하고, 자식 예외로는 `NonTransientDataAccessException`, `TransientDataAccessException`이 존재한다.
- `NonTransient`는 일시적이지 않다는 의미로 같은 SQL을 실행하면 반복적으로 실패한다는 것이다.
- `Transient`는 일시적이라는 의미로 SQL을 다시 실행하면 성공할 수 있다는 것이다.
- 이런 모든 예외를 기억할 수 없기에 스프링에서는 데이터 관련 예외가 발생했을 때 자동으로 변환해주는 변환기를 제공해준다.
- `SQLErrorCodeSQLExceptionTranslator`를 변환기로 제공한다.




