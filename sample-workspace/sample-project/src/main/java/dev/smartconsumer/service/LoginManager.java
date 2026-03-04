package dev.smartconsumer.service;

import javax.servlet.ServletContext;

import dev.smartconsumer.model.dao.UserDAO;
import dev.smartconsumer.model.dto.UserDTO;

public class LoginManager {

    private final UserDAO userDAO;

    // 생성자를 통한 의존성 주입
    public LoginManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * 로그인 인증 로직 (DB 확인 등 비즈니스 로직 수행)
     */
    public UserDTO authenticate(String seq, String password) {
        if (seq == null || password == null) return null;
        return userDAO.login(seq, password); // ctx 전달 제거
    }
}