package dev.smartconsumer.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.smartconsumer.model.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 데이터 접근 객체.
 * Spring 빈으로 등록되며, 생성자 주입을 통해 DataSource를 전달받음.
 */
@Slf4j
@Repository
public class UserDAO {

    private final DataSource sourceDs;  // Write 전용
    private final DataSource replicaDs; // Read 전용

    // 생성자 주입: @Qualifier로 두 개의 DataSource를 구분
    public UserDAO(@Qualifier("sourceDataSource") DataSource sourceDs,
                   @Qualifier("replicaDataSource") DataSource replicaDs) {
        this.sourceDs = sourceDs;
        this.replicaDs = replicaDs;
        log.info(">>> UserDAO initialized with Source/Replica DataSources");
    }

    /**
     * 회원가입 - Source DB (Write)
     * SEQ, PASSWORD, SEX_CD, AGE를 직접 받아서 USER_INFO에 INSERT
     */
    public boolean signup(String seq, String password, String sexCd, String age) {
        seq = seq.trim().toUpperCase();

        String insertSql = "INSERT INTO USER_INFO (SEQ, PASSWORD, SEX_CD, AGE) VALUES (?, ?, ?, ?)";

        try (Connection con = sourceDs.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {

            ps.setString(1, seq);
            ps.setString(2, password);
            ps.setString(3, sexCd);
            ps.setString(4, age);

            int result = ps.executeUpdate();
            log.info("User signup success: {} (SEX_CD={}, AGE={})", seq, sexCd, age);
            return result > 0;

        } catch (Exception e) {
            log.error("Signup failed for SEQ: {}", seq, e);
            return false;
        }
    }

    /**
     * 로그인 - Replica DB (Read)
     */
    public UserDTO login(String seq, String password) {
        seq = seq.trim().toUpperCase();
        String sql = "SELECT TRIM(SEQ) AS SEQ, PASSWORD, TRIM(SEX_CD) AS SEX_CD, TRIM(AGE) AS AGE FROM USER_INFO WHERE SEQ = ?";

        try (Connection con = replicaDs.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, seq);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("PASSWORD");
                    if (storedPassword.equals(password)) {
                        log.info("Login success: {}", seq);
                        return UserDTO.builder()
                                .seq(rs.getString("SEQ"))
                                .sexCd(rs.getString("SEX_CD"))
                                .age(rs.getString("AGE"))
                                .build();
                    } else {
                        log.warn("Login failed - wrong password: {}", seq);
                    }
                } else {
                    log.warn("Login failed - user not found: {}", seq);
                }
            }
        } catch (Exception e) {
            log.error("Login error for SEQ: {}", seq, e);
        }
        return null;
    }

    /**
     * 고객번호로 조회 - Replica DB (Read)
     */
    public UserDTO findBySeq(String seq) {
        seq = seq.trim().toUpperCase();
        String sql = "SELECT TRIM(SEQ) AS SEQ, TRIM(SEX_CD) AS SEX_CD, TRIM(AGE) AS AGE FROM USER_INFO WHERE SEQ = ?";

        try (Connection con = replicaDs.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, seq);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UserDTO.builder()
                            .seq(rs.getString("SEQ"))
                            .sexCd(rs.getString("SEX_CD"))
                            .age(rs.getString("AGE"))
                            .build();
                }
            }
        } catch (Exception e) {
            log.error("FindBySeq error: {}", seq, e);
        }
        return null;
    }
}
