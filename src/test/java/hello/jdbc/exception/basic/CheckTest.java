package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckTest {

    Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * Exception을 상속받은 예외는 체크 예외가 된다.
     */


    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }


    /**
     * Checked 예외는
     * 예외를 잡아서 처리하거나, 던지거나 필수로 체크해야함.
     */
    static class Service {
        Logger log = LoggerFactory.getLogger(this.getClass());
        Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                //예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 체크예외를 밖으로 던지는 코드
         * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예외를 메서드에 필수로 선언해야한다.
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }

    }

    static class Repository {
        // 체크 예외는 예외를 반드시 던지거나, 잡는것을 컴파일러가 체크
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
