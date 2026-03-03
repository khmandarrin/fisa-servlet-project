package dev.smartconsumer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dev.smartconsumer.common.Const;
import dev.smartconsumer.model.dao.AnalysisDAO;
import dev.smartconsumer.model.dto.StatDTO;
import dev.smartconsumer.model.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final AnalysisDAO analysisDAO = new AnalysisDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // 1. 로그인 체크
        if (session == null || session.getAttribute(Const.SESSION_USER) == null) {
            log.warn("Unauthorized access to dashboard.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute(Const.SESSION_USER);
        log.info("Dashboard request for SEQ: {}", user.getSeq());

        // 2. 데이터 조회
        // A. 나의 전체 카테고리별 소비 (원형 차트용) — 한 번만 조회
        List<StatDTO> myAllStats = analysisDAO.getMyAllConsumption(getServletContext(), user.getSeq());

        // B. 나의 Top 3 소비 (myAllStats에서 추출 → DB 재조회 없음)
        List<StatDTO> myStats = myAllStats.size() > 3 ? new ArrayList<>(myAllStats.subList(0, 3)) : myAllStats;

        // C. 또래 평균 소비 (연령대 + 성별)
        List<StatDTO> peerStats = analysisDAO.getPeerStats(getServletContext(), user.getAge(), user.getSexCd());

        // 3. Request에 데이터 담기
        req.setAttribute("myStats", myStats);
        req.setAttribute("myAllStats", myAllStats);
        req.setAttribute("peerStats", peerStats);

        // 4. JSP로 포워딩
        req.getRequestDispatcher(Const.VIEW_PREFIX + "dashboard.jsp").forward(req, resp);
    }
}