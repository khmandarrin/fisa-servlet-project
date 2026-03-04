package dev.smartconsumer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.smartconsumer.common.Const;
import dev.smartconsumer.model.dao.AnalysisDAO;
import dev.smartconsumer.model.dto.StatDTO;
import dev.smartconsumer.model.dto.UserDTO;
import dev.smartconsumer.service.SessionManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private AnalysisDAO analysisDAO;
    private SessionManager sessionManager;

    @Override
    public void init() throws ServletException {
        this.analysisDAO = (AnalysisDAO) getServletContext().getAttribute("analysisDAO");
        this.sessionManager = (SessionManager) getServletContext().getAttribute("sessionManager");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. SessionManager를 활용한 깔끔한 로그인 체크
        UserDTO user = sessionManager.getLoggedInUser(req);

        if (user == null) {
            log.warn("Unauthorized access to dashboard.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        log.info("Dashboard request for SEQ: {}", user.getSeq());

        // 2. 날아갔던 데이터 조회 로직 복구
        List<StatDTO> myAllStats = analysisDAO.getMyAllConsumption(user.getSeq());
        List<StatDTO> myStats = myAllStats.size() > 3 ? new ArrayList<>(myAllStats.subList(0, 3)) : myAllStats;
        List<StatDTO> peerStats = analysisDAO.getPeerStats(user.getAge(), user.getSexCd());

        // 3. Request에 데이터 담기
        req.setAttribute("myStats", myStats);
        req.setAttribute("myAllStats", myAllStats);
        req.setAttribute("peerStats", peerStats);

        // 4. JSP로 포워딩
        req.getRequestDispatcher(Const.VIEW_PREFIX + "dashboard.jsp").forward(req, resp);
    }
}