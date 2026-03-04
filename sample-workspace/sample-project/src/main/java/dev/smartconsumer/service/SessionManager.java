package dev.smartconsumer.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import dev.smartconsumer.common.Const;
import dev.smartconsumer.model.dto.UserDTO;

public class SessionManager {

    /**
     * 로그인 성공 시: 세션 생성 및 사용자 정보 저장
     */
    public void createSession(HttpServletRequest req, UserDTO user) {
        HttpSession session = req.getSession(true);
        session.setAttribute(Const.SESSION_USER, user);
        session.setMaxInactiveInterval(30 * 60); // 30분 타임아웃 설정
    }

    /**
     * 현재 로그인한 사용자 객체 반환 (비로그인 상태면 null)
     */
    public UserDTO getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (UserDTO) session.getAttribute(Const.SESSION_USER);
        }
        return null;
    }

    /**
     * 로그아웃 시: 세션 무효화
     */
    public void invalidateSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}