package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {
    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection connection = dataSource.getConnection();
        // 시작
        try {
            // 트랜잭션 시작
            connection.setAutoCommit(false);
            // 비즈니스 로직
            bussLogic(connection, fromId, toId, money);
            // 비즈니스 로직
            connection.commit();   // 끝 -> 커밋
        } catch (Exception e) {
            // 예외 상황이 발생하면 롤백처리를 한다.
            connection.rollback();
            throw new IllegalStateException(e);
        } finally {
            releaseConnection(connection);
        }
    }

    private void bussLogic(Connection connection, String fromId, String toId, int money) {
        Member fromMember = memberRepository.findById(connection, fromId);
        Member toMember = memberRepository.findById(connection, toId);
        log.info("toMember = {}", toMember.getMemberId());
        memberRepository.update(connection, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(connection, toId, toMember.getMoney() + money);
    }

    private void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                // 쓰레드 풀에 돌려 줄때, autocommit mode가 true인 상태로 돌려놔야 한다.
                connection.setAutoCommit(true);
                connection.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
