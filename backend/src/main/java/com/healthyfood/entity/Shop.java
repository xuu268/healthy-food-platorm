package com.healthyfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "shop")
@TableName("shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商家ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商家名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 商家描述
     */
    @Column(columnDefinition = "text")
    private String description;

    /**
     * 商家Logo
     */
    @Column(length = 500)
    private String logo;

    /**
     * 封面图
     */
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    /**
     * 商家类型
     */
    @Column(length = 50)
    private String type;

    /**
     * 分类
     */
    @Column(length = 50)
    private String category;

    /**
     * 地址
     */
    @Column(nullable = false, length = 500)
    private String address;

    /**
     * 纬度
     */
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    /**
     * 经度
     */
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    /**
     * 联系电话
     */
    @Column(nullable = false, length = 20)
    private String phone;

    /**
     * 营业时间
     */
    @Column(name = "business_hours", length = 200)
    private String businessHours;

    /**
     * 人均价格
     */
    @Column(name = "avg_price", precision = 10, scale = 2)
    private BigDecimal avgPrice;

    /**
     * 健康评分
     */
    @Column(name = "health_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal healthScore = BigDecimal.ZERO;

    /**
     * 人气评分
     */
    @Column(name = "popularity_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal popularityScore = BigDecimal.ZERO;

    /**
     * 服务评分
     */
    @Column(name = "service_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal serviceScore = BigDecimal.ZERO;

    /**
     * 综合评分
     */
    @Column(name = "total_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal totalScore = BigDecimal.ZERO;

    /**
     * 状态 0-关闭 1-营业 2-休息
     */
    @Column(columnDefinition = "tinyint default 1")
    private Integer status = 1;

    /**
     * 是否认证
     */
    @Column(name = "is_verified", columnDefinition = "tinyint default 0")
    private Integer isVerified = 0;

    /**
     * 认证时间
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * 店主用户ID
     */
    @Column(name = "owner_id")
    private Long ownerId;

    /**
     * 订阅计划
     */
    @Column(name = "subscription_plan", length = 50, columnDefinition = "varchar(50) default 'free'")
    private String subscriptionPlan = "free";

    /**
     * 订阅到期时间
     */
    @Column(name = "subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

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
     * 检查商家是否营业中
     */
    public boolean isOpen() {
        return status != null && status == 1;
    }

    /**
     * 检查商家是否已认证
     */
    public boolean isVerified() {
        return isVerified != null && isVerified == 1;
    }

    /**
     * 检查订阅是否有效
     */
    public boolean isSubscriptionValid() {
        if ("free".equals(subscriptionPlan)) {
            return true;
        }
        if (subscriptionExpiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(subscriptionExpiresAt);
    }

    /**
     * 获取商家等级
     */
    public String getShopLevel() {
        if (totalScore == null) {
            return "普通";
        }
        
        double score = totalScore.doubleValue();
        if (score >= 4.5) {
            return "金牌";
        } else if (score >= 4.0) {
            return "银牌";
        } else if (score >= 3.5) {
            return "铜牌";
        } else {
            return "普通";
        }
    }

    /**
     * 获取健康等级
     */
    public String getHealthLevel() {
        if (healthScore == null) {
            return "未知";
        }
        
        double score = healthScore.doubleValue();
        if (score >= 4.5) {
            return "非常健康";
        } else if (score >= 4.0) {
            return "健康";
        } else if (score >= 3.0) {
            return "一般";
        } else {
            return "需改进";
        }
    }

    /**
     * 计算距离（简化版，实际需要Haversine公式）
     */
    public Double calculateDistance(BigDecimal userLat, BigDecimal userLng) {
        if (latitude == null || longitude == null || userLat == null || userLng == null) {
            return null;
        }
        
        // 简化距离计算（实际应该使用Haversine公式）
        double latDiff = latitude.doubleValue() - userLat.doubleValue();
        double lngDiff = longitude.doubleValue() - userLng.doubleValue();
        double distance = Math.sqrt(latDiff * latDiff + lngDiff * lngDiff) * 111.0; // 粗略转换为公里
        
        return Math.round(distance * 100.0) / 100.0;
    }

    /**
     * 获取距离描述
     */
    public String getDistanceDescription(BigDecimal userLat, BigDecimal userLng) {
        Double distance = calculateDistance(userLat, userLng);
        if (distance == null) {
            return "距离未知";
        }
        
        if (distance < 1) {
            return "小于1公里";
        } else if (distance < 3) {
            return String.format("%.1f公里", distance);
        } else if (distance < 10) {
            return String.format("%.0f公里", distance);
        } else {
            return "较远";
        }
    }

    /**
     * 检查是否在营业时间内（简化版）
     */
    public boolean isWithinBusinessHours() {
        if (businessHours == null || businessHours.isEmpty()) {
            return true; // 如果没有设置营业时间，默认营业
        }
        
        // 简化处理，实际需要解析营业时间字符串
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        
        // 假设营业时间格式为 "10:00-22:00"
        try {
            String[] parts = businessHours.split("-");
            if (parts.length == 2) {
                int openHour = Integer.parseInt(parts[0].split(":")[0]);
                int closeHour = Integer.parseInt(parts[1].split(":")[0]);
                return hour >= openHour && hour < closeHour;
            }
        } catch (Exception e) {
            // 解析失败，返回true
        }
        
        return true;
    }

    /**
     * 获取商家完整信息
     */
    public String getFullInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (avgPrice != null) {
            sb.append(" | 人均¥").append(avgPrice);
        }
        if (address != null) {
            sb.append(" | ").append(address);
        }
        if (businessHours != null) {
            sb.append(" | ").append(businessHours);
        }
        return sb.toString();
    }

    /**
     * 更新综合评分
     */
    public void updateTotalScore() {
        if (healthScore == null || popularityScore == null || serviceScore == null) {
            totalScore = BigDecimal.ZERO;
            return;
        }
        
        // 加权计算：健康40%，人气30%，服务30%
        double total = healthScore.doubleValue() * 0.4 
                     + popularityScore.doubleValue() * 0.3
                     + serviceScore.doubleValue() * 0.3;
        
        totalScore = BigDecimal.valueOf(Math.round(total * 100.0) / 100.0);
    }
}