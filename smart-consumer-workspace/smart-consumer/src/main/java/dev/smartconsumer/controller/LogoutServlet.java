package dev.smartconsumer.controller;

import dev.smartconsumer.common.Const;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String userId = "unknown";
            Object user = session.getAttribute(Const.SESSION_USER);
            if (user != null) {
                userId = user.toString();
            }
            session.invalidate();
            log.info("User logged out: {}", userId);
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
