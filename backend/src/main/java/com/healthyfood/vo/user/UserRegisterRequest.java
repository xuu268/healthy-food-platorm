package com.healthyfood.vo.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * 用户注册请求对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20位之间")
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 性别
     */
    private Gender gender;
    
    /**
     * 生日
     */
    @Past(message = "生日必须是过去的时间")
    private LocalDate birthday;
    
    /**
     * 身高 (cm)
     */
    @Min(value = 50, message = "身高不能小于50cm")
    @Max(value = 250, message = "身高不能大于250cm")
    private Double height;
    
    /**
     * 体重 (kg)
     */
    @Min(value = 20, message = "体重不能小于20kg")
    @Max(value = 300, message = "体重不能大于300kg")
    private Double weight;
    
    /**
     * 活动水平
     */
    private ActivityLevel activityLevel;
    
    /**
     * 健康目标
     */
    private Set<String> healthGoals;
    
    /**
     * 饮食限制
     */
    private Set<String> foodRestrictions;
    
    /**
     * 过敏原
     */
    private Set<String> allergens;
    
    /**
     * 口味偏好
     */
    private Set<String> tastePreferences;
    
    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位数字")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verifyCode;
    
    /**
     * 邀请码 (可选)
     */
    private String inviteCode;
    
    /**
     * 注册来源
     */
    private String registerSource;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 性别枚举
     */
    public enum Gender {
        MALE,      // 男
        FEMALE,    // 女
        UNKNOWN    // 未知
    }
    
    /**
     * 活动水平枚举
     */
    public enum ActivityLevel {
        SEDENTARY,     // 久坐 (很少或没有运动)
        LIGHT,         // 轻度活动 (每周1-3天轻度运动)
        MODERATE,      // 中度活动 (每周3-5天中等强度运动)
        ACTIVE,        // 活跃 (每周6-7天中等强度运动)
        VERY_ACTIVE    // 非常活跃 (每天高强度运动或体力劳动)
    }
    
    /**
     * 验证密码是否一致
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
    
    /**
     * 验证必填的健康数据
     */
    public boolean hasRequiredHealthData() {
        return height != null && weight != null && activityLevel != null;
    }
    
    /**
     * 获取年龄
     */
    public Integer getAge() {
        if (birthday == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthday.getYear();
    }
}