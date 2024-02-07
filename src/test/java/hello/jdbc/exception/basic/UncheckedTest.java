package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        service.callThrow();
    }

    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Logger log = LoggerFactory.getLogger(this.getClass());
        Repository repository = new Repository();

        /**
         * 예외를 잡을 수 있다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
         * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        // 체크 예외는 예외를 반드시 던지거나, 잡는것을 컴파일러가 체크
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}
