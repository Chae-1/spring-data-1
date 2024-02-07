package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

public class ExTransferV1Test {
//    Repository repository;
//    Service service;
//
//    Logger log = LoggerFactory.getLogger(this.getClass());
//
//    @Test
//    void saveDuplicateMember() {
//        service.create("memberA");
//        service.create("memberA");
//    }
//
//    @BeforeEach
//    void init() {
//        DataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSWORD);
//        repository = new Repository(dataSource);
//        service = new Service(repository);
//    }
//
//    @Slf4j
//    @RequiredArgsConstructor
//    static class Service {
//        private final Repository repository;
//
//        public void create(String memberId) {
//            try {
//                repository.save(new Member(memberId, 0));
//                log.info("saveId={}", memberId);
//            } catch (MyDuplicateKeyException e) {
//                log.info("키 중복, 복구 시도");
//                String retryId = generateNewId(memberId);
//                repository.save(new Member(retryId, 0));
//            }
//        }
//
//        private String generateNewId(String memberId) {
//            return memberId + new Random().nextInt();
//        }
//    }
//
//
//    @RequiredArgsConstructor
//    static class Repository {
//
//        private final DataSource dataSource;
//
//        public Member save(Member member) {
//            String sql = "insert into member(member_id, money) values(?, ?)";
//
//            Connection con = null;
//            PreparedStatement pstmt = null;
//
//            try {
//                con = dataSource.getConnection();
//                pstmt = con.prepareStatement(sql);
//                pstmt.setString(1, member.getMemberId());
//                pstmt.setInt(2, member.getMoney());
//                return member;
//            } catch (SQLException e) {
//                if (e.getErrorCode() == 23505) {
//                    throw new MyDuplicateKeyException(e);
//                }
//                throw new MyDbException(e);
//            } finally {
//                JdbcUtils.closeStatement(pstmt);
//                JdbcUtils.closeConnection(con);
//            }
//        }
//    }
}
