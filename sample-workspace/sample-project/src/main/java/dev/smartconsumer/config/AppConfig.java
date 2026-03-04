package dev.smartconsumer.config;

import javax.sql.DataSource;

import dev.smartconsumer.model.dao.AnalysisDAO;
import dev.smartconsumer.model.dao.UserDAO;
import dev.smartconsumer.service.LoginManager;
import dev.smartconsumer.service.SessionManager;

public class AppConfig {
    
    private final UserDAO userDAO;
    private final AnalysisDAO analysisDAO;
    private final LoginManager loginManager;
    
    // ★ 에러 원인 해결 1: 필드 선언 추가
    private final SessionManager sessionManager; 

    public AppConfig(DataSource sourceDs, DataSource replicaDs) {
        this.userDAO = new UserDAO(sourceDs, replicaDs);
        this.analysisDAO = new AnalysisDAO(replicaDs);
        this.loginManager = new LoginManager(this.userDAO);
        
        // ★ 에러 원인 해결 2: 객체 생성(초기화) 추가
        this.sessionManager = new SessionManager(); 
    }

    public UserDAO getUserDAO() { return userDAO; }
    public AnalysisDAO getAnalysisDAO() { return analysisDAO; }
    public LoginManager getLoginManager() { return loginManager; }
    public SessionManager getSessionManager() { return sessionManager; } 
}