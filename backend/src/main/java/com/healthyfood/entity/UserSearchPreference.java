package com.healthyfood.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消费者搜索偏好实体类
 * 存储用户的搜索偏好设置
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "user_search_preference")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSearchPreference {
    
    /**
     * 偏好类型枚举
     */
    public enum PreferenceType {
        REQUIRE,  // 必须包含
        PREFER,   // 偏好
        AVOID     // 避免
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 关键词ID
     */
    @Column(name = "keyword_id", nullable = false)
    private Long keywordId;
    
    /**
     * 偏好类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "preference_type", nullable = false, length = 20)
    private PreferenceType preferenceType;
    
    /**
     * 优先级
     */
    @Column(name = "priority")
    private Integer priority = 1;
    
    /**
     * 自定义权重
     */
    @Column(name = "custom_weight", precision = 3, scale = 2)
    private BigDecimal customWeight;
    
    /**
     * 最后使用时间
     */
    @Column(name = "last_used_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUsedAt;
    
    /**
     * 使用次数
     */
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * 关联的用户实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    /**
     * 关联的关键词实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", insertable = false, updatable = false)
    private IngredientKeyword keyword;
    
    /**
     * 获取有效权重
     * @return 有效权重
     */
    public BigDecimal getEffectiveWeight() {
        if (customWeight != null) {
            return customWeight;
        }
        
        // 根据偏好类型返回默认权重
        switch (preferenceType) {
            case REQUIRE:
                return new BigDecimal("1.0");
            case PREFER:
                return new BigDecimal("0.7");
            case AVOID:
                return new BigDecimal("-0.5");
            default:
                return new BigDecimal("0.5");
        }
    }
    
    /**
     * 增加使用次数
     */
    public void incrementUsageCount() {
        if (usageCount == null) {
            usageCount = 0;
        }
        usageCount++;
        lastUsedAt = LocalDateTime.now();
    }
    
    /**
     * 获取偏好类型描述
     * @return 偏好类型描述
     */
    public String getPreferenceTypeDescription() {
        switch (preferenceType) {
            case REQUIRE:
                return "必须包含";
            case PREFER:
                return "偏好";
            case AVOID:
                return "避免";
            default:
                return "未知";
        }
    }
    
    /**
     * 获取偏好类型图标
     * @return 图标名称
     */
    public String getPreferenceTypeIcon() {
        switch (preferenceType) {
            case REQUIRE:
                return "check-circle";
            case PREFER:
                return "star";
            case AVOID:
                return "x-circle";
            default:
                return "help-circle";
        }
    }
    
    /**
     * 获取偏好类型颜色
     * @return 颜色代码
     */
    public String getPreferenceTypeColor() {
        switch (preferenceType) {
            case REQUIRE:
                return "#4CAF50";  // 绿色
            case PREFER:
                return "#FF9800";  // 橙色
            case AVOID:
                return "#F44336";  // 红色
            default:
                return "#757575";  // 灰色
        }
    }
    
    /**
     * 检查是否为必须包含类型
     * @return 是否为必须包含
     */
    public boolean isRequireType() {
        return preferenceType == PreferenceType.REQUIRE;
    }
    
    /**
     * 检查是否为偏好类型
     * @return 是否为偏好
     */
    public boolean isPreferType() {
        return preferenceType == PreferenceType.PREFER;
    }
    
    /**
     * 检查是否为避免类型
     * @return 是否为避免
     */
    public boolean isAvoidType() {
        return preferenceType == PreferenceType.AVOID;
    }
    
    /**
     * 获取使用频率等级
     * @return 使用频率等级 (1-5)
     */
    public int getUsageFrequencyLevel() {
        if (usageCount == null || usageCount == 0) {
            return 1;
        }
        
        if (usageCount >= 50) {
            return 5;  // 非常高
        } else if (usageCount >= 20) {
            return 4;  // 高
        } else if (usageCount >= 10) {
            return 3;  // 中等
        } else if (usageCount >= 5) {
            return 2;  // 低
        } else {
            return 1;  // 非常低
        }
    }
    
    /**
     * 获取使用频率描述
     * @return 使用频率描述
     */
    public String getUsageFrequencyDescription() {
        int level = getUsageFrequencyLevel();
        switch (level) {
            case 5:
                return "非常高";
            case 4:
                return "高";
            case 3:
                return "中等";
            case 2:
                return "低";
            case 1:
                return "非常低";
            default:
                return "未知";
        }
    }
    
    /**
     * 检查是否最近使用过（7天内）
     * @return 是否最近使用过
     */
    public boolean isRecentlyUsed() {
        if (lastUsedAt == null) {
            return false;
        }
        return lastUsedAt.isAfter(LocalDateTime.now().minusDays(7));
    }
    
    /**
     * 获取简化的偏好信息
     * @return 简化信息
     */
    public SimplePreferenceInfo toSimpleInfo() {
        SimplePreferenceInfo info = new SimplePreferenceInfo();
        info.setId(id);
        info.setUserId(userId);
        info.setKeywordId(keywordId);
        info.setPreferenceType(preferenceType);
        info.setPriority(priority);
        info.setEffectiveWeight(getEffectiveWeight());
        info.setUsageCount(usageCount);
        info.setLastUsedAt(lastUsedAt);
        info.setPreferenceTypeDescription(getPreferenceTypeDescription());
        info.setPreferenceTypeIcon(getPreferenceTypeIcon());
        info.setPreferenceTypeColor(getPreferenceTypeColor());
        info.setUsageFrequencyDescription(getUsageFrequencyDescription());
        info.setRecentlyUsed(isRecentlyUsed());
        
        if (keyword != null) {
            info.setKeyword(keyword.getKeyword());
            info.setKeywordCategory(keyword.getCategory());
            info.setKeywordDescription(keyword.getDescription());
        }
        
        return info;
    }
    
    /**
     * 简化偏好信息类
     */
    @Data
    public static class SimplePreferenceInfo {
        private Long id;
        private Long userId;
        private Long keywordId;
        private String keyword;
        private IngredientKeyword.KeywordCategory keywordCategory;
        private String keywordDescription;
        private PreferenceType preferenceType;
        private Integer priority;
        private BigDecimal effectiveWeight;
        private Integer usageCount;
        private LocalDateTime lastUsedAt;
        private String preferenceTypeDescription;
        private String preferenceTypeIcon;
        private String preferenceTypeColor;
        private String usageFrequencyDescription;
        private Boolean recentlyUsed;
        
        public SimplePreferenceInfo() {
            // 默认构造函数
        }
    }
    
    /**
     * 验证偏好设置是否有效
     * @return 是否有效
     */
    public boolean isValid() {
        return userId != null && userId > 0 && 
               keywordId != null && keywordId > 0 &&
               preferenceType != null &&
               (priority == null || priority >= 1) &&
               (customWeight == null || 
                (customWeight.compareTo(BigDecimal.ZERO) >= 0 && 
                 customWeight.compareTo(BigDecimal.ONE) <= 0));
    }
    
    /**
     * 根据健康档案自动调整偏好
     * @param healthProfile 健康档案
     */
    public void adjustByHealthProfile(User.HealthProfile healthProfile) {
        if (healthProfile == null || keyword == null) {
            return;
        }
        
        // 根据健康档案调整偏好权重
        // 这里可以实现具体的调整逻辑
        // 例如：糖尿病患者自动增加"低糖"偏好的权重
        
        // 暂时留空，后续根据具体需求实现
    }
}