package dev.smartconsumer.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.smartconsumer.model.dto.UserDTO;
import dev.smartconsumer.service.SessionManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private SessionManager sessionManager;

    @Override
    public void init() throws ServletException {
        // ServletContext에서 SessionManager Bean 가져오기
        this.sessionManager = (SessionManager) getServletContext().getAttribute("sessionManager");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 현재 로그인된 사용자 정보 가져오기 (로그 기록용)
        UserDTO user = sessionManager.getLoggedInUser(req);
        String userId = (user != null) ? user.getSeq() : "unknown";

        // 2. 매니저를 통해 세션 안전하게 무효화
        sessionManager.invalidateSession(req);
        
        log.info("User logged out: {}", userId);

        // 3. 로그인 페이지로 리다이렉트
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}