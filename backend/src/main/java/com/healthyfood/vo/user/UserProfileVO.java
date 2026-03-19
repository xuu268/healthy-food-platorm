package com.healthyfood.vo.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

/**
 * 用户个人资料视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 手机号 (脱敏)
     */
    private String phone;
    
    /**
     * 邮箱 (脱敏)
     */
    private String email;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 身高 (cm)
     */
    private Double height;
    
    /**
     * 体重 (kg)
     */
    private Double weight;
    
    /**
     * BMI
     */
    private Double bmi;
    
    /**
     * BMI分类
     */
    private String bmiCategory;
    
    /**
     * 基础代谢率 (BMR)
     */
    private Double bmr;
    
    /**
     * 每日热量需求
     */
    private Double dailyCalorieNeeds;
    
    /**
     * 活动水平
     */
    private String activityLevel;
    
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
     * 当前健康评分
     */
    private Double currentHealthScore;
    
    /**
     * 健康评分等级
     */
    private String healthScoreLevel;
    
    /**
     * 会员等级
     */
    private String membershipLevel;
    
    /**
     * 会员有效期
     */
    private LocalDateTime membershipExpiry;
    
    /**
     * 总积分
     */
    private Integer totalPoints;
    
    /**
     * 可用积分
     */
    private Integer availablePoints;
    
    /**
     * 总消费金额
     */
    private Double totalSpent;
    
    /**
     * 总订单数
     */
    private Integer totalOrders;
    
    /**
     * 平均订单金额
     */
    private Double averageOrderAmount;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 注册时间
     */
    private LocalDateTime registerTime;
    
    /**
     * 账户状态
     */
    private String accountStatus;
    
    /**
     * 是否已验证手机
     */
    private Boolean phoneVerified;
    
    /**
     * 是否已验证邮箱
     */
    private Boolean emailVerified;
    
    /**
     * 是否完成健康档案
     */
    private Boolean healthProfileCompleted;
    
    /**
     * 健康档案完成度
     */
    private Integer healthProfileCompletion;
    
    /**
     * 连续登录天数
     */
    private Integer consecutiveLoginDays;
    
    /**
     * 累计登录天数
     */
    private Integer totalLoginDays;
    
    /**
     * 关注商家数量
     */
    private Integer followingShopsCount;
    
    /**
     * 收藏商品数量
     */
    private Integer favoriteProductsCount;
    
    /**
     * 创作内容数量
     */
    private Integer createdContentsCount;
    
    /**
     * 获赞数量
     */
    private Integer receivedLikesCount;
    
    /**
     * 粉丝数量
     */
    private Integer followersCount;
    
    /**
     * 关注数量
     */
    private Integer followingCount;
    
    /**
     * 成就徽章
     */
    private Set<String> achievementBadges;
    
    /**
     * 推荐偏好
     */
    private RecommendationPreferencesVO recommendationPreferences;
    
    /**
     * 营养需求
     */
    private NutritionalNeedsVO nutritionalNeeds;
    
    /**
     * 统计信息
     */
    private StatisticsVO statistics;
    
    /**
     * 推荐偏好视图对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationPreferencesVO {
        private Boolean preferHealthy;          // 偏好健康食品
        private Boolean preferLowCalorie;       // 偏好低卡路里
        private Boolean preferHighProtein;      // 偏好高蛋白
        private Boolean preferLowFat;           // 偏好低脂肪
        private Boolean preferLowCarb;          // 偏好低碳水
        private Boolean preferVegetarian;       // 偏好素食
        private Boolean preferVegan;            // 偏好纯素
        private Boolean preferGlutenFree;       // 偏好无麸质
        private Boolean preferDairyFree;        // 偏好无乳制品
        private Double priceSensitivity;        // 价格敏感度 (0-1)
        private Double deliverySpeedPriority;   // 配送速度优先级 (0-1)
        private Double tasteImportance;         // 口味重要性 (0-1)
        private Double healthImportance;        // 健康重要性 (0-1)
        private Double convenienceImportance;   // 便利性重要性 (0-1)
    }
    
    /**
     * 营养需求视图对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalNeedsVO {
        private Double dailyCalories;           // 每日热量需求
        private Double proteinNeeds;            // 蛋白质需求 (g)
        private Double carbNeeds;               // 碳水化合物需求 (g)
        private Double fatNeeds;                // 脂肪需求 (g)
        private Double fiberNeeds;              // 纤维需求 (g)
        private Double sugarLimit;              // 糖分限制 (g)
        private Double sodiumLimit;             // 钠限制 (mg)
        private Double cholesterolLimit;        // 胆固醇限制 (mg)
        private Map<String, Double> vitaminNeeds;  // 维生素需求
        private Map<String, Double> mineralNeeds;  // 矿物质需求
    }
    
    /**
     * 统计信息视图对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsVO {
        private Integer thisWeekOrders;         // 本周订单数
        private Integer thisMonthOrders;        // 本月订单数
        private Double thisWeekSpent;           // 本周消费金额
        private Double thisMonthSpent;          // 本月消费金额
        private Double averageWeeklySpent;      // 平均每周消费
        private Double averageMonthlySpent;     // 平均每月消费
        private String favoriteCuisine;         // 最喜爱的菜系
        private String favoriteShop;            // 最喜爱的商家
        private String mostOrderedProduct;      // 最常点的商品
        private LocalTime peakOrderTime;        // 高峰下单时间
        private Double averageDeliveryTime;     // 平均配送时间
        private Double satisfactionRate;        // 满意度评分
        private Integer complaintCount;         // 投诉次数
        private Integer refundCount;            // 退款次数
        private Double refundRate;              // 退款率
    }
    
    /**
     * 获取脱敏手机号
     */
    public String getMaskedPhone() {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 获取脱敏邮箱
     */
    public String getMaskedEmail() {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return localPart + domain;
        }
        
        return localPart.substring(0, 2) + "***" + domain;
    }
    
    /**
     * 计算健康档案完成度
     */
    public void calculateCompletion() {
        int totalFields = 0;
        int completedFields = 0;
        
        // 基本资料
        if (nickname != null) completedFields++;
        totalFields++;
        
        if (avatar != null) completedFields++;
        totalFields++;
        
        if (gender != null) completedFields++;
        totalFields++;
        
        if (birthday != null) completedFields++;
        totalFields++;
        
        // 健康数据
        if (height != null) completedFields++;
        totalFields++;
        
        if (weight != null) completedFields++;
        totalFields++;
        
        if (activityLevel != null) completedFields++;
        totalFields++;
        
        // 偏好设置
        if (healthGoals != null && !healthGoals.isEmpty()) completedFields++;
        totalFields++;
        
        if (foodRestrictions != null && !foodRestrictions.isEmpty()) completedFields++;
        totalFields++;
        
        if (allergens != null && !allergens.isEmpty()) completedFields++;
        totalFields++;
        
        if (tastePreferences != null && !tastePreferences.isEmpty()) completedFields++;
        totalFields++;
        
        this.healthProfileCompletion = totalFields > 0 ? 
            (int) Math.round((double) completedFields / totalFields * 100) : 0;
        this.healthProfileCompleted = this.healthProfileCompletion >= 80;
    }
    
    /**
     * 获取健康建议
     */
    public String getHealthAdvice() {
        if (bmi == null) {
            return "请完善身高体重信息以获取健康建议";
        }
        
        if (bmi < 18.5) {
            return "您的体重偏轻，建议适当增加营养摄入，选择高蛋白、高热量的健康食品";
        } else if (bmi < 24) {
            return "您的体重正常，继续保持健康的饮食习惯和适量运动";
        } else if (bmi < 28) {
            return "您的体重超重，建议控制热量摄入，增加运动量，选择低卡路里、高纤维食品";
        } else {
            return "您的体重属于肥胖范围，建议咨询专业营养师，制定科学的减重计划";
        }
    }
}