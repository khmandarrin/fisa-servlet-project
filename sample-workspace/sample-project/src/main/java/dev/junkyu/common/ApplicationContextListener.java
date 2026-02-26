package dev.junkyu.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ApplicationContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextListener.class);
    private HikariDataSource ds;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info(">>> Application Context Initialized... DB Connection Pool Setting...");
        
        ServletContext ctx = sce.getServletContext();

        try {
            HikariConfig config = new HikariConfig();
            
            // [중요] MySQL 연결 정보 (Source/Write DB 기준)
            config.setJdbcUrl("jdbc:mysql://localhost:3306/card_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8");
            config.setUsername("root"); // 아까 만든 계정
            config.setPassword("1234");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // 커넥션 풀 옵션 (튜닝)
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setPoolName("Junkyu-Hikari-Pool");

            ds = new HikariDataSource(config);
            
            // 모든 서블릿에서 쓸 수 있게 저장
            ctx.setAttribute("DATA_SOURCE", ds);
            
            log.info(">>> DB Connection Pool Created Successfully!");
            
        } catch (Exception e) {
            log.error(">>> DB Connection Pool Creation Failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (ds != null && !ds.isClosed()) {
            ds.close();
            log.info(">>> DB Connection Pool Closed.");
        }
    }
}