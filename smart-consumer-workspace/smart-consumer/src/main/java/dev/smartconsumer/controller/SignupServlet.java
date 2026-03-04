package dev.smartconsumer.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import dev.smartconsumer.model.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        // Spring ApplicationContext에서 빈을 가져옴
        WebApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        this.userService = ctx.getBean(UserService.class);
        log.info(">>> SignupServlet initialized with Spring UserService bean");
    }

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

        boolean success = userService.signup(seq, password, sexCd, age);

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
