package com.healthyfood.vo.shop;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 商家登录请求VO
 */
@Data
public class ShopLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32个字符之间")
    private String password;

    // 登录类型：PASSWORD, SMS, WECHAT
    private String loginType = "PASSWORD";

    // 验证码（短信登录时使用）
    @Size(min = 4, max = 6, message = "验证码长度必须在4-6位")
    private String verificationCode;

    // 设备信息
    private String deviceId;
    private String deviceType; // WEB, IOS, ANDROID
    private String deviceModel;
    private String appVersion;

    // IP地址
    private String ipAddress;

    // 地理位置
    private Double latitude;
    private Double longitude;

    // 记住我
    private Boolean rememberMe = false;

    // 验证方法
    
    /**
     * 检查是否为短信登录
     */
    public boolean isSmsLogin() {
        return "SMS".equals(loginType);
    }
    
    /**
     * 检查是否需要验证码
     */
    public boolean requiresVerificationCode() {
        return isSmsLogin();
    }
    
    /**
     * 验证登录参数
     */
    public void validate() {
        if (isSmsLogin() && (verificationCode == null || verificationCode.trim().isEmpty())) {
            throw new IllegalArgumentException("短信登录需要验证码");
        }
        
        if (!isSmsLogin() && (password == null || password.trim().isEmpty())) {
            throw new IllegalArgumentException("密码登录需要密码");
        }
    }
    
    /**
     * 获取登录摘要
     */
    public String getLoginSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("商家登录: ").append(username);
        
        if (isSmsLogin()) {
            summary.append(" (短信登录)");
        } else {
            summary.append(" (密码登录)");
        }
        
        if (deviceType != null) {
            summary.append(" 设备: ").append(deviceType);
        }
        
        return summary.toString();
    }
    
    /**
     * 获取设备信息摘要
     */
    public String getDeviceSummary() {
        StringBuilder device = new StringBuilder();
        
        if (deviceType != null) {
            device.append(deviceType);
        }
        
        if (deviceModel != null) {
            if (device.length() > 0) device.append(" ");
            device.append(deviceModel);
        }
        
        if (appVersion != null) {
            if (device.length() > 0) device.append(" ");
            device.append("v").append(appVersion);
        }
        
        return device.length() > 0 ? device.toString() : "未知设备";
    }
    
    /**
     * 获取地理位置信息
     */
    public String getLocationInfo() {
        if (latitude != null && longitude != null) {
            return String.format("(%.6f, %.6f)", latitude, longitude);
        }
        return "未知位置";
    }
    
    /**
     * 检查是否记住登录状态
     */
    public boolean shouldRemember() {
        return rememberMe != null && rememberMe;
    }
    
    /**
     * 获取登录安全级别
     */
    public String getSecurityLevel() {
        if (isSmsLogin()) {
            return "HIGH"; // 短信验证码登录安全性较高
        }
        
        // 密码登录，检查是否有设备信息和地理位置
        int securityScore = 0;
        if (deviceId != null) securityScore++;
        if (ipAddress != null) securityScore++;
        if (latitude != null && longitude != null) securityScore++;
        
        if (securityScore >= 3) return "HIGH";
        else if (securityScore >= 1) return "MEDIUM";
        else return "LOW";
    }
    
    /**
     * 清理敏感信息
     */
    public void sanitize() {
        this.password = null;
        this.verificationCode = null;
        this.deviceId = null;
        this.ipAddress = null;
    }
    
    /**
     * 复制对象（不包含敏感信息）
     */
    public ShopLoginRequest copyWithoutSensitiveInfo() {
        ShopLoginRequest copy = new ShopLoginRequest();
        copy.setUsername(this.username);
        copy.setLoginType(this.loginType);
        copy.setDeviceType(this.deviceType);
        copy.setDeviceModel(this.deviceModel);
        copy.setAppVersion(this.appVersion);
        copy.setLatitude(this.latitude);
        copy.setLongitude(this.longitude);
        copy.setRememberMe(this.rememberMe);
        return copy;
    }
}