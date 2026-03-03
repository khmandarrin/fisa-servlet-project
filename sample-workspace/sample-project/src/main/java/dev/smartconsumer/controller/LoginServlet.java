package dev.smartconsumer.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dev.smartconsumer.common.Const;
import dev.smartconsumer.model.dao.UserDAO;
import dev.smartconsumer.model.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 이미 로그인 상태면 대시보드로 리다이렉트
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Const.SESSION_USER) != null) {
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

        log.info("Login attempt: SEQ={}", seq);

        UserDTO user = userDAO.login(getServletContext(), seq, password);

        if (user != null) {
            // 로그인 성공 → 세션에 사용자 정보 저장
            HttpSession session = req.getSession(true);
            session.setAttribute(Const.SESSION_USER, user);
            session.setMaxInactiveInterval(30 * 60); // 30분

            log.info("Login success → redirecting to dashboard: {}", seq);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            // 로그인 실패 → 에러 메시지와 함께 로그인 페이지로
            req.setAttribute("error", "고객번호 또는 비밀번호가 올바르지 않습니다.");
            req.setAttribute("seq", seq);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
