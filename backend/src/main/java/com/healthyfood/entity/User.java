package com.healthyfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "user")
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    /**
     * 手机号
     */
    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;

    /**
     * 头像
     */
    @Column(length = 500)
    private String avatar;

    /**
     * 昵称
     */
    @Column(length = 50)
    private String nickname;

    /**
     * 性别 0-未知 1-男 2-女
     */
    @Column(columnDefinition = "tinyint default 0")
    private Integer gender = 0;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 身高(cm)
     */
    @Column(precision = 5, scale = 2)
    private Double height;

    /**
     * 体重(kg)
     */
    @Column(precision = 5, scale = 2)
    private Double weight;

    /**
     * 目标体重(kg)
     */
    @Column(name = "target_weight", precision = 5, scale = 2)
    private Double targetWeight;

    /**
     * 活动水平 1-久坐 2-轻度 3-中度 4-重度
     */
    @Column(name = "activity_level", columnDefinition = "tinyint default 2")
    private Integer activityLevel = 2;

    /**
     * 健康目标
     */
    @Column(name = "health_goal", length = 50)
    private String healthGoal;

    /**
     * 过敏原
     */
    @Column(columnDefinition = "text")
    private String allergies;

    /**
     * 积分
     */
    @Column(columnDefinition = "int default 0")
    private Integer points = 0;

    /**
     * 用户等级
     */
    @Column(columnDefinition = "int default 1")
    private Integer level = 1;

    /**
     * 状态 0-禁用 1-正常
     */
    @Column(columnDefinition = "tinyint default 1")
    private Integer status = 1;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @TableLogic
    @Column(columnDefinition = "tinyint default 0")
    private Integer deleted = 0;

    /**
     * 计算每日热量需求
     */
    public Double calculateDailyCalorieNeeds() {
        if (weight == null || height == null || birthday == null) {
            return 2000.0; // 默认值
        }

        // 计算基础代谢率(BMR) - Mifflin-St Jeor公式
        double bmr;
        int age = LocalDate.now().getYear() - birthday.getYear();
        
        if (gender == 1) { // 男性
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else if (gender == 2) { // 女性
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        } else { // 未知性别
            bmr = 10 * weight + 6.25 * height - 5 * age - 78;
        }

        // 根据活动水平调整
        double activityMultiplier;
        switch (activityLevel) {
            case 1: // 久坐
                activityMultiplier = 1.2;
                break;
            case 2: // 轻度
                activityMultiplier = 1.375;
                break;
            case 3: // 中度
                activityMultiplier = 1.55;
                break;
            case 4: // 重度
                activityMultiplier = 1.725;
                break;
            default:
                activityMultiplier = 1.375;
        }

        double dailyCalories = bmr * activityMultiplier;

        // 根据健康目标调整
        if (healthGoal != null) {
            switch (healthGoal) {
                case "weight_loss":
                    dailyCalories *= 0.8; // 减重，减少20%热量
                    break;
                case "weight_gain":
                    dailyCalories *= 1.2; // 增重，增加20%热量
                    break;
                case "muscle_gain":
                    dailyCalories *= 1.1; // 增肌，增加10%热量
                    break;
            }
        }

        return Math.round(dailyCalories * 100.0) / 100.0;
    }

    /**
     * 计算BMI
     */
    public Double calculateBMI() {
        if (weight == null || height == null || height == 0) {
            return null;
        }
        double heightInMeters = height / 100.0;
        return Math.round(weight / (heightInMeters * heightInMeters) * 100.0) / 100.0;
    }

    /**
     * 获取BMI分类
     */
    public String getBMICategory() {
        Double bmi = calculateBMI();
        if (bmi == null) {
            return "未知";
        }
        
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi < 24) {
            return "正常";
        } else if (bmi < 28) {
            return "超重";
        } else {
            return "肥胖";
        }
    }

    /**
     * 检查用户是否活跃
     */
    public boolean isActive() {
        return status != null && status == 1;
    }

    /**
     * 检查用户是否是VIP
     */
    public boolean isVip() {
        return level != null && level >= 3;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return nickname != null ? nickname : username;
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