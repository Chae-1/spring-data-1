package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * 체크 예외를 런타임 예외로 변경
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{

    private final JdbcTemplate jdbcTemplate;

    public MemberRepositoryV5(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member values(?, ?)";
        jdbcTemplate.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        return jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }

    public void update(String memberId, int money) {
        String sql = "update member set money = ? where member_id = ?";
        jdbcTemplate.update(sql, money, memberId);
    }

    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";
        jdbcTemplate.update(sql, memberId);
    }
}
