package dev.junkyu.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test/db")
public class HikariHealthCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 1. 현재 응답하는 WAS의 포트 확인 (Nginx 부하분산 확인용)
        int serverPort = req.getLocalPort();
        out.println("=== WAS Server Info ===");
        out.println("Response from Port: " + serverPort); // 8080 또는 8090 출력됨
        out.println("=======================\n");

        // 2. DB 커넥션 가져오기
        ServletContext ctx = getServletContext();
        DataSource ds = (DataSource) ctx.getAttribute("DATA_SOURCE");

        if (ds == null) {
            resp.setStatus(500);
            out.println("FAIL: DataSource is NULL. Check ApplicationContextListener.");
            return;
        }

        // 3. 쿼리 실행 (Health Check)
        String sql = "SELECT 1"; 
        // 실제 테이블 테스트시: "SELECT count(*) FROM card_info" 등으로 변경 가능

        long start = System.currentTimeMillis();
        
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if(rs.next()) {
                int result = rs.getInt(1);
                long elapsed = System.currentTimeMillis() - start;
                
                out.println("[DB Connection Status: OK]");
                out.println("Query Result: " + result);
                out.println("Latency: " + elapsed + "ms");
                out.println("Connection Class: " + con.getClass().getName());
            }

        } catch (Exception e) {
            resp.setStatus(500);
            e.printStackTrace(out);
        }
    }
}