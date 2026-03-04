package dev.smartconsumer.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션 시작/종료 시 로그를 출력하는 리스너.
 * DataSource 관리는 Spring ApplicationContext (applicationContext.xml)로 이관됨.
 */
@Slf4j
@WebListener
public class ApplicationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info(">>> SmartConsumer Application Initializing...");
        log.info(">>> DataSource management delegated to Spring ApplicationContext.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info(">>> SmartConsumer Application Shutting Down...");
    }
}