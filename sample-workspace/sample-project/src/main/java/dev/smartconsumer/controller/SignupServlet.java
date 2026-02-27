package dev.smartconsumer.controller;

import dev.smartconsumer.model.dao.UserDAO;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String seq = req.getParameter("seq");
        String password = req.getParameter("password");
        String sexCd = req.getParameter("sexCd");
        String age = req.getParameter("age");

        log.info("Signup attempt: SEQ={}, SEX_CD={}, AGE={}", seq, sexCd, age);

        boolean success = userDAO.signup(getServletContext(), seq, password, sexCd, age);

        if (success) {
            log.info("Signup success → redirecting to login: {}", seq);
            resp.sendRedirect(req.getContextPath() + "/login?signupSuccess=true");
        } else {
            req.setAttribute("error", "회원가입에 실패했습니다. 이미 가입된 번호이거나 오류가 발생했습니다.");
            req.setAttribute("seq", seq);
            req.setAttribute("sexCd", sexCd);
            req.setAttribute("age", age);
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
        }
    }
}
