package dev.smartconsumer.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
public class ApplicationContextListener implements ServletContextListener {

    private HikariDataSource sourceDs;  // Write 전용 (3308)
    private HikariDataSource replicaDs; // Read 전용 (3309)

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info(">>> SmartConsumer Application Initializing...");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1. Source DB (Write) 설정 - 3308 포트
            HikariConfig sourceConfig = new HikariConfig();
            sourceConfig.setJdbcUrl("jdbc:mysql://localhost:55758/card_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true");
            sourceConfig.setUsername("root"); // 실제 계정으로 변경
            sourceConfig.setPassword("1234");
            sourceConfig.setMaximumPoolSize(5); // Write는 상대적으로 적음
            sourceConfig.setPoolName("Hikari-Source-Pool");
            
            // 2. Replica DB (Read) 설정 - 3309 포트
            HikariConfig replicaConfig = new HikariConfig();
            replicaConfig.setJdbcUrl("jdbc:mysql://localhost:55759/card_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true");
            replicaConfig.setUsername("root");
            replicaConfig.setPassword("1234");
            replicaConfig.setMaximumPoolSize(15); // Read가 많으므로 넉넉하게
            replicaConfig.setPoolName("Hikari-Replica-Pool");

            sourceDs = new HikariDataSource(sourceConfig);
            replicaDs = new HikariDataSource(replicaConfig);

            // Context에 저장
            ServletContext ctx = sce.getServletContext();
            ctx.setAttribute("DS_SOURCE", sourceDs);
            ctx.setAttribute("DS_REPLICA", replicaDs);
            
            log.info(">>> DB Connection Pools (Source/Replica) Initialized Successfully.");

        } catch (Exception e) {
            log.error(">>> Critical Error: DB Init Failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (sourceDs != null) sourceDs.close();
        if (replicaDs != null) replicaDs.close();
        log.info(">>> DB Connection Pools Closed.");
    }
}