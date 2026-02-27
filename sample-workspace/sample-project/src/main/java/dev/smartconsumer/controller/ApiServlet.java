package dev.smartconsumer.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet("/api/stats")
public class ApiServlet extends HttpServlet {

    private final AnalysisDAO analysisDAO = new AnalysisDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Const.SESSION_USER) == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        UserDTO user = (UserDTO) session.getAttribute(Const.SESSION_USER);

        List<StatDTO> myAllStats = analysisDAO.getMyAllConsumption(getServletContext(), user.getSeq());
        List<StatDTO> myStats = myAllStats.size() > 3 ? new ArrayList<>(myAllStats.subList(0, 3)) : myAllStats;
        List<StatDTO> peerStats = analysisDAO.getPeerStats(getServletContext(), user.getAge(), user.getSexCd());

        // 수동 JSON 빌드
        StringBuilder json = new StringBuilder();
        json.append("{");

        // User info
        json.append("\"user\":{");
        json.append("\"seq\":\"").append(escapeJson(user.getSeq())).append("\",");
        json.append("\"age\":\"").append(escapeJson(user.getAge())).append("\",");
        json.append("\"sexCd\":\"").append(escapeJson(user.getSexCd())).append("\"");
        json.append("},");

        // My Top 3
        json.append("\"myTop3\":").append(toJsonArray(myStats)).append(",");

        // My All Stats
        json.append("\"myAll\":").append(toJsonArray(myAllStats)).append(",");

        // Peer Stats
        json.append("\"peerStats\":").append(toJsonArray(peerStats));

        json.append("}");

        PrintWriter out = resp.getWriter();
        out.write(json.toString());
        out.flush();
    }

    private String toJsonArray(List<StatDTO> stats) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < stats.size(); i++) {
            StatDTO s = stats.get(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"categoryName\":\"").append(escapeJson(s.getCategoryName())).append("\",");
            sb.append("\"totalAmount\":").append(s.getTotalAmount()).append(",");
            sb.append("\"percentage\":").append(s.getPercentage());
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
    }
}
