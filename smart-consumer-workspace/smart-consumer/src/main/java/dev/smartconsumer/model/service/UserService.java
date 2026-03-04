package dev.smartconsumer.model.service;

import org.springframework.stereotype.Service;

import dev.smartconsumer.model.dao.UserDAO;
import dev.smartconsumer.model.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 관련 비즈니스 로직을 담당하는 서비스 계층.
 * Spring 컨테이너에 의해 빈으로 관리되며, UserDAO를 생성자 주입받음.
 */
@Slf4j
@Service
public class UserService {

    private final UserDAO userDAO;

    // 생성자 주입 (단일 생성자이므로 @Autowired 생략 가능)
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
        log.info(">>> UserService initialized with UserDAO");
    }

    /**
     * 회원가입 처리
     */
    public boolean signup(String seq, String password, String sexCd, String age) {
        return userDAO.signup(seq, password, sexCd, age);
    }

    /**
     * 로그인 처리
     */
    public UserDTO login(String seq, String password) {
        return userDAO.login(seq, password);
    }

    /**
     * 고객번호로 사용자 조회
     */
    public UserDTO findBySeq(String seq) {
        return userDAO.findBySeq(seq);
    }
}
