package dev.smartconsumer.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.smartconsumer.model.dto.UserDTO;
import dev.smartconsumer.service.LoginManager;
import dev.smartconsumer.service.SessionManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private LoginManager loginManager;
    private SessionManager sessionManager;

    @Override
    public void init() throws ServletException {
        this.loginManager = (LoginManager) getServletContext().getAttribute("loginManager");
        this.sessionManager = (SessionManager) getServletContext().getAttribute("sessionManager");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 매니저를 통한 깔끔한 로그인 상태 체크
        if (sessionManager.getLoggedInUser(req) != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String seq = req.getParameter("seq");
        String password = req.getParameter("password");

        UserDTO user = loginManager.authenticate(seq, password);

        if (user != null) {
            // 매니저를 통한 세션 생성
            sessionManager.createSession(req, user);
            
            log.info("Login success → redirecting to dashboard: {}", seq);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "고객번호 또는 비밀번호가 올바르지 않습니다.");
            req.setAttribute("seq", seq);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}