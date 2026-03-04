package dev.smartconsumer.common;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.smartconsumer.config.AppConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
public class ApplicationContextListener implements ServletContextListener {

    private HikariDataSource sourceDs;  // Write 전용
    private HikariDataSource replicaDs; // Read 전용

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info(">>> SmartConsumer Application Initializing...");
        ServletContext ctx = sce.getServletContext();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ==========================================
            // 1. DB Connection Pool 초기화
            // ==========================================
            // Source DB (Write) 설정
            HikariConfig sourceConfig = new HikariConfig();
            sourceConfig.setJdbcUrl("jdbc:mysql://192.168.0.206:62114/card_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true");
            sourceConfig.setUsername("root"); // 실제 계정으로 변경 필요
            sourceConfig.setPassword("1234");
            sourceConfig.setMaximumPoolSize(5); // Write는 상대적으로 적음
            sourceConfig.setPoolName("Hikari-Source-Pool");
            
            // Replica DB (Read) 설정
            HikariConfig replicaConfig = new HikariConfig();
            replicaConfig.setJdbcUrl("jdbc:mysql://192.168.0.206:62043/card_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true");
            replicaConfig.setUsername("root");
            replicaConfig.setPassword("1234");
            replicaConfig.setMaximumPoolSize(15); // Read가 많으므로 넉넉하게
            replicaConfig.setPoolName("Hikari-Replica-Pool");

            sourceDs = new HikariDataSource(sourceConfig);
            replicaDs = new HikariDataSource(replicaConfig);

            // Context에 DataSource 저장
            ctx.setAttribute("DS_SOURCE", sourceDs);
            ctx.setAttribute("DS_REPLICA", replicaDs);
            
            log.info(">>> DB Connection Pools (Source/Replica) Initialized Successfully.");

            // ==========================================
            // 2. Bean 설정 및 등록 로직 (AppConfig 연동)
            // ==========================================
            AppConfig appConfig = new AppConfig(sourceDs, replicaDs);
            
            // 생성된 Bean들을 ServletContext에 저장하여 전역에서 사용 가능하게 함
            ctx.setAttribute("userDAO", appConfig.getUserDAO());
            ctx.setAttribute("analysisDAO", appConfig.getAnalysisDAO());
            ctx.setAttribute("loginManager", appConfig.getLoginManager());
            ctx.setAttribute("sessionManager", appConfig.getSessionManager());
            
            log.info(">>> Application Beans (DAOs, Managers) Registered Successfully.");

        } catch (Exception e) {
            log.error(">>> Critical Error: Application Init Failed", e);
            throw new RuntimeException(e); // 초기화 실패 시 톰캣 구동 중단
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 자원 누수 방지를 위한 Pool 종료
        if (sourceDs != null) sourceDs.close();
        if (replicaDs != null) replicaDs.close();
        log.info(">>> DB Connection Pools Closed.");
    }
}