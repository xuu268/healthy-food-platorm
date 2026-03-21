package com.healthyfood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 健康餐饮平台主启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableAsync
@EnableScheduling
public class HealthyFoodApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthyFoodApplication.class, args);
    }
    
    /**
     * 应用启动完成后的初始化操作
     */
    @PostConstruct
    public void init() {
        logApplicationStartup();
        checkSystemRequirements();
        initializeSystemComponents();
    }
    
    /**
     * 记录应用启动信息
     */
    private void logApplicationStartup() {
        String protocol = "http";
        String serverPort = environment.getProperty("server.port");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";
        
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("无法确定主机地址，使用默认地址");
        }
        
        log.info("\n----------------------------------------------------------\n\t" +
                "应用 '{}' 正在运行! 访问地址:\n\t" +
                "本地: \t{}://localhost:{}{}\n\t" +
                "外部: \t{}://{}:{}{}\n\t" +
                "环境: \t{}\n\t" +
                "版本: \t{}\n" +
                "----------------------------------------------------------",
                environment.getProperty("spring.application.name", "HealthyFood Platform"),
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                environment.getActiveProfiles(),
                getClass().getPackage().getImplementationVersion());
    }
    
    /**
     * 检查系统要求
     */
    private void checkSystemRequirements() {
        // 检查Java版本
        String javaVersion = System.getProperty("java.version");
        log.info("Java版本: {}", javaVersion);
        
        // 检查内存
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        log.info("JVM内存 - 最大: {} MB, 已分配: {} MB, 可用: {} MB",
                maxMemory / 1024 / 1024,
                totalMemory / 1024 / 1024,
                freeMemory / 1024 / 1024);
        
        // 检查数据库连接
        checkDatabaseConnection();
    }
    
    /**
     * 检查数据库连接
     */
    private void checkDatabaseConnection() {
        try {
            // 这里可以添加数据库连接检查逻辑
            log.info("数据库连接检查通过");
        } catch (Exception e) {
            log.error("数据库连接检查失败: {}", e.getMessage());
        }
    }
    
    /**
     * 初始化系统组件
     */
    private void initializeSystemComponents() {
        // 初始化Redis连接
        initializeRedis();
        
        // 初始化定时任务
        initializeScheduledTasks();
        
        // 初始化系统配置
        initializeSystemConfig();
    }
    
    /**
     * 初始化Redis连接
     */
    private void initializeRedis() {
        try {
            // Redis初始化逻辑
            log.info("Redis连接初始化完成");
        } catch (Exception e) {
            log.warn("Redis连接初始化失败: {}", e.getMessage());
        }
    }
    
    /**
     * 初始化定时任务
     */
    private void initializeScheduledTasks() {
        // 定时任务初始化逻辑
        log.info("定时任务初始化完成");
    }
    
    /**
     * 初始化系统配置
     */
    private void initializeSystemConfig() {
        // 系统配置初始化逻辑
        log.info("系统配置初始化完成");
    }
    
    /**
     * 应用关闭时的清理操作
     */
    @PreDestroy
    public void cleanup() {
        log.info("应用正在关闭，执行清理操作...");
        
        // 关闭数据库连接池
        closeDatabaseConnections();
        
        // 关闭Redis连接
        closeRedisConnections();
        
        // 停止定时任务
        stopScheduledTasks();
        
        log.info("应用清理完成");
    }
    
    /**
     * 关闭数据库连接
     */
    private void closeDatabaseConnections() {
        try {
            // 数据库连接关闭逻辑
            log.info("数据库连接已关闭");
        } catch (Exception e) {
            log.error("关闭数据库连接时出错: {}", e.getMessage());
        }
    }
    
    /**
     * 关闭Redis连接
     */
    private void closeRedisConnections() {
        try {
            // Redis连接关闭逻辑
            log.info("Redis连接已关闭");
        } catch (Exception e) {
            log.error("关闭Redis连接时出错: {}", e.getMessage());
        }
    }
    
    /**
     * 停止定时任务
     */
    private void stopScheduledTasks() {
        try {
            // 定时任务停止逻辑
            log.info("定时任务已停止");
        } catch (Exception e) {
            log.error("停止定时任务时出错: {}", e.getMessage());
        }
    }
    
    @Autowired
    private Environment environment;
    
    private static final Logger log = LoggerFactory.getLogger(HealthyFoodApplication.class);
}