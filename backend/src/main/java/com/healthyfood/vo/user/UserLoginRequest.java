package com.healthyfood.vo.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;

/**
 * 用户登录请求对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    
    /**
     * 登录方式
     */
    @NotNull(message = "登录方式不能为空")
    private LoginType loginType;
    
    /**
     * 手机号 (手机号登录时使用)
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 邮箱 (邮箱登录时使用)
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    /**
     * 验证码 (验证码登录时使用)
     */
    @Size(min = 6, max = 6, message = "验证码必须是6位数字")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verifyCode;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 登录来源
     */
    private String loginSource;
    
    /**
     * 记住我
     */
    private Boolean rememberMe;
    
    /**
     * 登录方式枚举
     */
    public enum LoginType {
        PHONE_PASSWORD,     // 手机号+密码
        EMAIL_PASSWORD,     // 邮箱+密码
        PHONE_VERIFY_CODE,  // 手机号+验证码
        WECHAT,             // 微信登录
        ALIPAY,             // 支付宝登录
        APPLE,              // Apple登录
        GOOGLE              // Google登录
    }
    
    /**
     * 验证登录参数
     */
    public boolean validate() {
        switch (loginType) {
            case PHONE_PASSWORD:
                return phone != null && !phone.trim().isEmpty() && 
                       password != null && !password.trim().isEmpty();
            case EMAIL_PASSWORD:
                return email != null && !email.trim().isEmpty() && 
                       password != null && !password.trim().isEmpty();
            case PHONE_VERIFY_CODE:
                return phone != null && !phone.trim().isEmpty() && 
                       verifyCode != null && !verifyCode.trim().isEmpty();
            case WECHAT:
            case ALIPAY:
            case APPLE:
            case GOOGLE:
                // 第三方登录需要其他验证逻辑
                return true;
            default:
                return false;
        }
    }
    
    /**
     * 获取登录标识
     */
    public String getLoginIdentifier() {
        switch (loginType) {
            case PHONE_PASSWORD:
            case PHONE_VERIFY_CODE:
                return phone;
            case EMAIL_PASSWORD:
                return email;
            default:
                return null;
        }
    }
    
    /**
     * 是否需要密码验证
     */
    public boolean requiresPassword() {
        return loginType == LoginType.PHONE_PASSWORD || 
               loginType == LoginType.EMAIL_PASSWORD;
    }
    
    /**
     * 是否需要验证码
     */
    public boolean requiresVerifyCode() {
        return loginType == LoginType.PHONE_VERIFY_CODE;
    }
    
    /**
     * 是否是第三方登录
     */
    public boolean isThirdPartyLogin() {
        return loginType == LoginType.WECHAT || 
               loginType == LoginType.ALIPAY || 
               loginType == LoginType.APPLE || 
               loginType == LoginType.GOOGLE;
    }
}