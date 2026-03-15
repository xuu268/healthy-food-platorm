package com.healthyfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 创作者实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "creator")
@TableName("creator")
public class Creator implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创作者等级常量
     */
    public static final String LEVEL_BRONZE = "bronze";      // 青铜
    public static final String LEVEL_SILVER = "silver";      // 白银
    public static final String LEVEL_GOLD = "gold";          // 黄金
    public static final String LEVEL_DIAMOND = "diamond";    // 钻石

    /**
     * 认证状态常量
     */
    public static final int VERIFICATION_PENDING = 0;    // 未认证
    public static final int VERIFICATION_APPROVED = 1;   // 已认证

    /**
     * 状态常量
     */
    public static final int STATUS_DISABLED = 0;         // 禁用
    public static final int STATUS_ACTIVE = 1;           // 正常

    /**
     * 创作者ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 创作者昵称
     */
    @Column(nullable = false, length = 50)
    private String nickname;

    /**
     * 头像
     */
    @Column(length = 500)
    private String avatar;

    /**
     * 个人简介
     */
    @Column(columnDefinition = "text")
    private String bio;

    /**
     * 擅长领域
     */
    @Column(length = 500)
    private String specialties;

    /**
     * 等级 bronze/silver/gold/diamond
     */
    @Column(length = 20, columnDefinition = "varchar(20) default 'bronze'")
    private String level = LEVEL_BRONZE;

    /**
     * 积分
     */
    @Column(columnDefinition = "int default 0")
    private Integer points = 0;

    /**
     * 总收益
     */
    @Column(name = "total_earnings", precision = 10, scale = 2, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    /**
     * 可用余额
     */
    @Column(name = "available_balance", precision = 10, scale = 2, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal availableBalance = BigDecimal.ZERO;

    /**
     * 内容数量
     */
    @Column(name = "content_count", columnDefinition = "int default 0")
    private Integer contentCount = 0;

    /**
     * 平均评分
     */
    @Column(name = "avg_rating", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal avgRating = BigDecimal.ZERO;

    /**
     * 粉丝数
     */
    @Column(name = "follower_count", columnDefinition = "int default 0")
    private Integer followerCount = 0;

    /**
     * 平台账号
     */
    @Column(name = "platform_accounts", columnDefinition = "json")
    private String platformAccounts;

    /**
     * 认证状态 0-未认证 1-已认证
     */
    @Column(name = "verification_status", columnDefinition = "tinyint default 0")
    private Integer verificationStatus = VERIFICATION_PENDING;

    /**
     * 状态 0-禁用 1-正常
     */
    @Column(columnDefinition = "tinyint default 1")
    private Integer status = STATUS_ACTIVE;

    /**
     * 最后活跃时间
     */
    @Column(name = "last_active_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActiveTime;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @TableLogic
    @Column(columnDefinition = "tinyint default 0")
    private Integer deleted = 0;

    /**
     * 用户信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private User user;

    /**
     * 检查创作者是否活跃
     */
    public boolean isActive() {
        return status != null && status == STATUS_ACTIVE;
    }

    /**
     * 检查创作者是否已认证
     */
    public boolean isVerified() {
        return verificationStatus != null && verificationStatus == VERIFICATION_APPROVED;
    }

    /**
     * 获取创作者等级描述
     */
    public String getLevelDescription() {
        switch (level) {
            case LEVEL_BRONZE:
                return "青铜创作者";
            case LEVEL_SILVER:
                return "白银创作者";
            case LEVEL_GOLD:
                return "黄金创作者";
            case LEVEL_DIAMOND:
                return "钻石创作者";
            default:
                return "创作者";
        }
    }

    /**
     * 获取等级图标
     */
    public String getLevelIcon() {
        switch (level) {
            case LEVEL_BRONZE:
                return "🥉";
            case LEVEL_SILVER:
                return "🥈";
            case LEVEL_GOLD:
                return "🥇";
            case LEVEL_DIAMOND:
                return "💎";
            default:
                return "👤";
        }
    }

    /**
     * 计算创作者评分
     */
    public BigDecimal calculateRating() {
        // 基于多个因素计算综合评分
        double rating = 0.0;
        int factorCount = 0;
        
        // 内容质量评分
        if (avgRating != null && avgRating.compareTo(BigDecimal.ZERO) > 0) {
            rating += avgRating.doubleValue() * 0.4;
            factorCount++;
        }
        
        // 粉丝数量评分
        if (followerCount != null && followerCount > 0) {
            double followerScore = Math.min(followerCount / 1000.0, 5.0); // 每1000粉丝1分，最高5分
            rating += followerScore * 0.3;
            factorCount++;
        }
        
        // 内容数量评分
        if (contentCount != null && contentCount > 0) {
            double contentScore = Math.min(contentCount / 50.0, 5.0); // 每50个内容1分，最高5分
            rating += contentScore * 0.2;
            factorCount++;
        }
        
        // 认证状态加分
        if (isVerified()) {
            rating += 1.0;
            factorCount++;
        }
        
        // 活跃度加分
        if (lastActiveTime != null) {
            long daysSinceActive = java.time.Duration.between(lastActiveTime, LocalDateTime.now()).toDays();
            if (daysSinceActive <= 7) {
                rating += 0.5; // 一周内活跃
            }
        }
        
        if (factorCount == 0) {
            return BigDecimal.ZERO;
        }
        
        double finalRating = Math.min(rating / factorCount, 5.0);
        return BigDecimal.valueOf(Math.round(finalRating * 100.0) / 100.0);
    }

    /**
     * 检查是否需要升级
     */
    public UpgradeInfo checkUpgrade() {
        UpgradeInfo info = new UpgradeInfo();
        info.setCurrentLevel(level);
        info.setCurrentPoints(points);
        
        int requiredPoints = getRequiredPointsForNextLevel();
        if (requiredPoints > 0) {
            info.setCanUpgrade(points >= requiredPoints);
            info.setRequiredPoints(requiredPoints);
            info.setNextLevel(getNextLevel());
            info.setPointsNeeded(Math.max(0, requiredPoints - points));
        } else {
            info.setCanUpgrade(false);
            info.setNextLevel(level); // 已经是最高级
        }
        
        return info;
    }

    /**
     * 获取升级所需积分
     */
    private int getRequiredPointsForNextLevel() {
        switch (level) {
            case LEVEL_BRONZE:
                return 1000; // 青铜→白银需要1000积分
            case LEVEL_SILVER:
                return 5000; // 白银→黄金需要5000积分
            case LEVEL_GOLD:
                return 20000; // 黄金→钻石需要20000积分
            case LEVEL_DIAMOND:
                return -1; // 钻石是最高级
            default:
                return 1000;
        }
    }

    /**
     * 获取下一等级
     */
    private String getNextLevel() {
        switch (level) {
            case LEVEL_BRONZE:
                return LEVEL_SILVER;
            case LEVEL_SILVER:
                return LEVEL_GOLD;
            case LEVEL_GOLD:
                return LEVEL_DIAMOND;
            default:
                return level;
        }
    }

    /**
     * 获取分成比例
     */
    public BigDecimal getCommissionRate() {
        switch (level) {
            case LEVEL_BRONZE:
                return new BigDecimal("0.15"); // 15%
            case LEVEL_SILVER:
                return new BigDecimal("0.18"); // 18%
            case LEVEL_GOLD:
                return new BigDecimal("0.20"); // 20%
            case LEVEL_DIAMOND:
                return new BigDecimal("0.25"); // 25%
            default:
                return new BigDecimal("0.15");
        }
    }

    /**
     * 计算单次推广收益
     */
    public BigDecimal calculateEarnings(BigDecimal promotionAmount) {
        if (promotionAmount == null || promotionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal commissionRate = getCommissionRate();
        return promotionAmount.multiply(commissionRate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 增加收益
     */
    public void addEarnings(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            totalEarnings = totalEarnings.add(amount);
            availableBalance = availableBalance.add(amount);
        }
    }

    /**
     * 提现
     */
    public boolean withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        if (availableBalance.compareTo(amount) >= 0) {
            availableBalance = availableBalance.subtract(amount);
            return true;
        }
        
        return false;
    }

    /**
     * 增加积分
     */
    public void addPoints(int pointsToAdd) {
        if (pointsToAdd > 0) {
            points += pointsToAdd;
            checkAutoUpgrade();
        }
    }

    /**
     * 减少积分
     */
    public boolean deductPoints(int pointsToDeduct) {
        if (pointsToDeduct > 0 && points >= pointsToDeduct) {
            points -= pointsToDeduct;
            return true;
        }
        return false;
    }

    /**
     * 自动检查并升级
     */
    private void checkAutoUpgrade() {
        UpgradeInfo upgradeInfo = checkUpgrade();
        if (upgradeInfo.isCanUpgrade()) {
            level = upgradeInfo.getNextLevel();
        }
    }

    /**
     * 获取创作者完整信息
     */
    public String getFullInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLevelIcon()).append(" ").append(nickname);
        sb.append(" (").append(getLevelDescription()).append(")");
        
        if (isVerified()) {
            sb.append(" ✅");
        }
        
        if (avgRating != null && avgRating.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" ⭐").append(avgRating);
        }
        
        if (followerCount != null && followerCount > 0) {
            sb.append(" 👥").append(followerCount);
        }
        
        return sb.toString();
    }

    /**
     * 获取擅长领域数组
     */
    public String[] getSpecialtiesArray() {
        if (specialties == null || specialties.isEmpty()) {
            return new String[0];
        }
        return specialties.split(",");
    }

    /**
     * 检查是否擅长特定领域
     */
    public boolean hasSpecialty(String specialty) {
        if (specialties == null || specialties.isEmpty()) {
            return false;
        }
        
        String[] specialtyArray = getSpecialtiesArray();
        for (String s : specialtyArray) {
            if (s.trim().equalsIgnoreCase(specialty)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加擅长领域
     */
    public void addSpecialty(String specialty) {
        if (specialties == null || specialties.isEmpty()) {
            specialties = specialty;
        } else {
            specialties += "," + specialty;
        }
    }

    /**
     * 检查创作者是否在线（最近15分钟内活跃）
     */
    public boolean isOnline() {
        if (lastActiveTime == null) {
            return false;
        }
        
        long minutesSinceActive = java.time.Duration.between(lastActiveTime, LocalDateTime.now()).toMinutes();
        return minutesSinceActive <= 15;
    }

    /**
     * 获取在线状态描述
     */
    public String getOnlineStatus() {
        if (isOnline()) {
            return "在线";
        } else if (lastActiveTime != null) {
            long hoursSinceActive = java.time.Duration.between(lastActiveTime, LocalDateTime.now()).toHours();
            if (hoursSinceActive < 24) {
                return hoursSinceActive + "小时前在线";
            } else {
                return "近期不在线";
            }
        } else {
            return "从未活跃";
        }
    }

    /**
     * 升级信息类
     */
    @Data
    public static class UpgradeInfo {
        private String currentLevel;
        private String nextLevel;
        private int currentPoints;
        private int requiredPoints;
        private int pointsNeeded;
        private boolean canUpgrade;
        
        public String getUpgradeDescription() {
            if (!canUpgrade) {
                return "暂不符合升级条件";
            }
            
            return String.format("可从%s升级到%s，需要%d积分（已有%d积分，还需%d积分）",
                    getLevelDescription(currentLevel),
                    getLevelDescription(nextLevel),
                    requiredPoints,
                    currentPoints,
                    pointsNeeded);
        }
        
        private String getLevelDescription(String level) {
            switch (level) {
                case LEVEL_BRONZE:
                    return "青铜";
                case LEVEL_SILVER:
                    return "白银";
                case LEVEL_GOLD:
                    return "黄金";
                case LEVEL_DIAMOND:
                    return "钻石";
                default:
                    return level;
            }
        }
    }

    /**
     * 更新活跃时间
     */
    public void updateActiveTime() {
        lastActiveTime = LocalDateTime.now();
    }

    /**
     * 增加内容数量
     */
    public void incrementContentCount() {
        if (contentCount == null) {
            contentCount = 1;
        } else {
            contentCount++;
        }
    }

    /**
     * 增加粉丝数量
     */
    public void incrementFollowerCount() {
        if (followerCount == null) {
            followerCount = 1;
        } else {
            followerCount++;
        }
    }

    /**
     * 减少粉丝数量
     */
    public void decrementFollowerCount() {
        if (followerCount != null && followerCount > 0) {
            followerCount--;
        }
    }

    /**
     * 更新平均评分
     */
    public void updateAverageRating(BigDecimal newRating) {
        if (newRating == null) {
            return;
        }
        
        if (avgRating == null || avgRating.compareTo(BigDecimal.ZERO) == 0) {
            avgRating = newRating;
        } else {
            // 简化计算，实际应该存储所有评分
            avgRating = avgRating.add(newRating).divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}