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
import java.util.List;

/**
 * 内容实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "content")
@TableName("content")
public class Content implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容类型常量
     */
    public static final String TYPE_ARTICLE = "article";     // 文章
    public static final String TYPE_VIDEO = "video";         // 视频
    public static final String TYPE_IMAGE = "image";         // 图片

    /**
     * 平台常量
     */
    public static final String PLATFORM_DOUYIN = "douyin";   // 抖音
    public static final String PLATFORM_XIAOHONGSHU = "xiaohongshu"; // 小红书
    public static final String PLATFORM_WECHAT = "wechat";   // 微信
    public static final String PLATFORM_WEIBO = "weibo";     // 微博
    public static final String PLATFORM_BILIBILI = "bilibili"; // B站

    /**
     * 状态常量
     */
    public static final int STATUS_DRAFT = 0;        // 草稿
    public static final int STATUS_PUBLISHED = 1;    // 已发布
    public static final int STATUS_REVIEWING = 2;    // 审核中
    public static final int STATUS_REJECTED = 3;     // 审核失败

    /**
     * 内容ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创作者ID
     */
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    /**
     * 关联商家ID
     */
    @Column(name = "shop_id")
    private Long shopId;

    /**
     * 标题
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 描述
     */
    @Column(columnDefinition = "text")
    private String description;

    /**
     * 内容类型 article/video/image
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * 媒体文件URL
     */
    @Column(name = "media_urls", columnDefinition = "json")
    private String mediaUrls;

    /**
     * 发布平台
     */
    @Column(nullable = false, length = 50)
    private String platform;

    /**
     * 平台内容ID
     */
    @Column(name = "platform_content_id", length = 100)
    private String platformContentId;

    /**
     * 平台链接
     */
    @Column(name = "platform_url", length = 500)
    private String platformUrl;

    /**
     * 标签
     */
    @Column(length = 500)
    private String tags;

    /**
     * 是否AI生成
     */
    @Column(name = "ai_generated", columnDefinition = "tinyint default 0")
    private Integer aiGenerated = 0;

    /**
     * AI提示词
     */
    @Column(name = "ai_prompt", columnDefinition = "text")
    private String aiPrompt;

    /**
     * 质量评分
     */
    @Column(name = "quality_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal qualityScore = BigDecimal.ZERO;

    /**
     * 互动评分
     */
    @Column(name = "engagement_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal engagementScore = BigDecimal.ZERO;

    /**
     * 转化评分
     */
    @Column(name = "conversion_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal conversionScore = BigDecimal.ZERO;

    /**
     * 综合评分
     */
    @Column(name = "total_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal totalScore = BigDecimal.ZERO;

    /**
     * 浏览数
     */
    @Column(name = "view_count", columnDefinition = "int default 0")
    private Integer viewCount = 0;

    /**
     * 点赞数
     */
    @Column(name = "like_count", columnDefinition = "int default 0")
    private Integer likeCount = 0;

    /**
     * 评论数
     */
    @Column(name = "comment_count", columnDefinition = "int default 0")
    private Integer commentCount = 0;

    /**
     * 分享数
     */
    @Column(name = "share_count", columnDefinition = "int default 0")
    private Integer shareCount = 0;

    /**
     * 点击量
     */
    @Column(name = "click_count", columnDefinition = "int default 0")
    private Integer clickCount = 0;

    /**
     * 转化量
     */
    @Column(name = "conversion_count", columnDefinition = "int default 0")
    private Integer conversionCount = 0;

    /**
     * 收益
     */
    @Column(precision = 10, scale = 2, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal earnings = BigDecimal.ZERO;

    /**
     * 状态 0-草稿 1-已发布 2-审核中 3-审核失败
     */
    @Column(columnDefinition = "tinyint default 0")
    private Integer status = STATUS_DRAFT;

    /**
     * 发布时间
     */
    @Column(name = "published_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    /**
     * 计划发布时间
     */
    @Column(name = "scheduled_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledAt;

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
     * 创作者信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private Creator creator;

    /**
     * 商家信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private Shop shop;

    /**
     * 检查内容是否已发布
     */
    public boolean isPublished() {
        return status != null && status == STATUS_PUBLISHED;
    }

    /**
     * 检查内容是否在审核中
     */
    public boolean isUnderReview() {
        return status != null && status == STATUS_REVIEWING;
    }

    /**
     * 检查内容是否被拒绝
     */
    public boolean isRejected() {
        return status != null && status == STATUS_REJECTED;
    }

    /**
     * 获取内容类型描述
     */
    public String getTypeDescription() {
        switch (type) {
            case TYPE_ARTICLE:
                return "文章";
            case TYPE_VIDEO:
                return "视频";
            case TYPE_IMAGE:
                return "图片";
            default:
                return "内容";
        }
    }

    /**
     * 获取平台描述
     */
    public String getPlatformDescription() {
        switch (platform) {
            case PLATFORM_DOUYIN:
                return "抖音";
            case PLATFORM_XIAOHONGSHU:
                return "小红书";
            case PLATFORM_WECHAT:
                return "微信";
            case PLATFORM_WEIBO:
                return "微博";
            case PLATFORM_BILIBILI:
                return "B站";
            default:
                return platform;
        }
    }

    /**
     * 获取平台图标
     */
    public String getPlatformIcon() {
        switch (platform) {
            case PLATFORM_DOUYIN:
                return "🎵";
            case PLATFORM_XIAOHONGSHU:
                return "📕";
            case PLATFORM_WECHAT:
                return "💬";
            case PLATFORM_WEIBO:
                return "🐦";
            case PLATFORM_BILIBILI:
                return "📺";
            default:
                return "📱";
        }
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case STATUS_DRAFT:
                return "草稿";
            case STATUS_PUBLISHED:
                return "已发布";
            case STATUS_REVIEWING:
                return "审核中";
            case STATUS_REJECTED:
                return "审核失败";
            default:
                return "未知";
        }
    }

    /**
     * 计算互动率
     */
    public BigDecimal calculateEngagementRate() {
        if (viewCount == null || viewCount == 0) {
            return BigDecimal.ZERO;
        }
        
        int totalInteractions = (likeCount != null ? likeCount : 0) +
                               (commentCount != null ? commentCount : 0) +
                               (shareCount != null ? shareCount : 0);
        
        double rate = (double) totalInteractions / viewCount * 100;
        return BigDecimal.valueOf(Math.round(rate * 100.0) / 100.0);
    }

    /**
     * 计算转化率
     */
    public BigDecimal calculateConversionRate() {
        if (clickCount == null || clickCount == 0) {
            return BigDecimal.ZERO;
        }
        
        double rate = (double) (conversionCount != null ? conversionCount : 0) / clickCount * 100;
        return BigDecimal.valueOf(Math.round(rate * 100.0) / 100.0);
    }

    /**
     * 更新综合评分
     */
    public void updateTotalScore() {
        if (qualityScore == null || engagementScore == null || conversionScore == null) {
            totalScore = BigDecimal.ZERO;
            return;
        }
        
        // 加权计算：质量40%，互动30%，转化30%
        double total = qualityScore.doubleValue() * 0.4 
                     + engagementScore.doubleValue() * 0.3
                     + conversionScore.doubleValue() * 0.3;
        
        totalScore = BigDecimal.valueOf(Math.round(total * 100.0) / 100.0);
    }

    /**
     * 获取内容等级
     */
    public String getContentLevel() {
        if (totalScore == null) {
            return "普通";
        }
        
        double score = totalScore.doubleValue();
        if (score >= 4.5) {
            return "爆款";
        } else if (score >= 4.0) {
            return "优质";
        } else if (score >= 3.0) {
            return "良好";
        } else {
            return "普通";
        }
    }

    /**
     * 检查内容是否热门
     */
    public boolean isHotContent() {
        if (viewCount == null) {
            return false;
        }
        
        // 根据平台设定不同的热门标准
        int hotThreshold;
        switch (platform) {
            case PLATFORM_DOUYIN:
                hotThreshold = 10000; // 抖音1万浏览
                break;
            case PLATFORM_XIAOHONGSHU:
                hotThreshold = 5000;  // 小红书5千浏览
                break;
            case PLATFORM_WECHAT:
                hotThreshold = 3000;  // 微信3千浏览
                break;
            default:
                hotThreshold = 1000;  // 其他平台1千浏览
        }
        
        return viewCount >= hotThreshold;
    }

    /**
     * 获取内容简略信息
     */
    public String getBriefInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPlatformIcon()).append(" ");
        sb.append(title);
        
        if (isPublished() && publishedAt != null) {
            sb.append(" (").append(publishedAt.toLocalDate()).append(")");
        }
        
        return sb.toString();
    }

    /**
     * 获取标签数组
     */
    public String[] getTagArray() {
        if (tags == null || tags.isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }

    /**
     * 添加标签
     */
    public void addTag(String tag) {
        if (tags == null || tags.isEmpty()) {
            tags = tag;
        } else {
            tags += "," + tag;
        }
    }

    /**
     * 检查是否包含标签
     */
    public boolean hasTag(String tag) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }
        
        String[] tagArray = getTagArray();
        for (String t : tagArray) {
            if (t.trim().equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 增加浏览数
     */
    public void incrementViewCount() {
        if (viewCount == null) {
            viewCount = 1;
        } else {
            viewCount++;
        }
        updateEngagementScore();
    }

    /**
     * 增加点赞数
     */
    public void incrementLikeCount() {
        if (likeCount == null) {
            likeCount = 1;
        } else {
            likeCount++;
        }
        updateEngagementScore();
    }

    /**
     * 增加评论数
     */
    public void incrementCommentCount() {
        if (commentCount == null) {
            commentCount = 1;
        } else {
            commentCount++;
        }
        updateEngagementScore();
    }

    /**
     * 增加分享数
     */
    public void incrementShareCount() {
        if (shareCount == null) {
            shareCount = 1;
        } else {
            shareCount++;
        }
        updateEngagementScore();
    }

    /**
     * 增加点击量
     */
    public void incrementClickCount() {
        if (clickCount == null) {
            clickCount = 1;
        } else {
            clickCount++;
        }
        updateConversionScore();
    }

    /**
     * 增加转化量
     */
    public void incrementConversionCount() {
        if (conversionCount == null) {
            conversionCount = 1;
        } else {
            conversionCount++;
        }
        updateConversionScore();
    }

    /**
     * 更新互动评分
     */
    private void updateEngagementScore() {
        BigDecimal engagementRate = calculateEngagementRate();
        double rate = engagementRate.doubleValue();
        
        // 将互动率转换为5分制评分
        double score;
        if (rate >= 10) {
            score = 5.0;
        } else if (rate >= 5) {
            score = 4.0 + (rate - 5) / 5;
        } else if (rate >= 2) {
            score = 3.0 + (rate - 2) / 3;
        } else if (rate >= 0.5) {
            score = 2.0 + (rate - 0.5) / 1.5;
        } else {
            score = Math.max(1.0, rate * 2);
        }
        
        engagementScore = BigDecimal.valueOf(Math.min(Math.round(score * 100.0) / 100.0, 5.0));
        updateTotalScore();
    }

    /**
     * 更新转化评分
     */
    private void updateConversionScore() {
        BigDecimal conversionRate = calculateConversionRate();
        double rate = conversionRate.doubleValue();
        
        // 将转化率转换为5分制评分
        double score;
        if (rate >= 20) {
            score = 5.0;
        } else if (rate >= 10) {
            score = 4.0 + (rate - 10) / 10;
        } else if (rate >= 5) {
            score = 3.0 + (rate - 5) / 5;
        } else if (rate >= 2) {
            score = 2.0 + (rate - 2) / 3;
        } else {
            score = Math.max(1.0, rate * 0.5);
        }
        
        conversionScore = BigDecimal.valueOf(Math.min(Math.round(score * 100.0) / 100.0, 5.0));
        updateTotalScore();
    }

    /**
     * 设置质量评分
     */
    public void setQualityScore(BigDecimal score) {
        this.qualityScore = score != null ? 
                BigDecimal.valueOf(Math.min(Math.max(score.doubleValue(), 0.0), 5.0)) : 
                BigDecimal.ZERO;
        updateTotalScore();
    }

    /**
     * 检查内容是否已计划发布
     */
    public boolean isScheduled() {
        return scheduledAt != null && !isPublished();
    }

    /**
     * 检查是否到了计划发布时间
     */
    public boolean isScheduledTimeReached() {
        return isScheduled() && LocalDateTime.now().isAfter(scheduledAt);
    }

    /**
     * 获取内容统计数据摘要
     */
    public String getStatsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("👁️ ").append(viewCount != null ? viewCount : 0);
        sb.append(" ❤️ ").append(likeCount != null ? likeCount : 0);
        sb.append(" 💬 ").append(commentCount != null ? commentCount : 0);
        sb.append(" 🔗 ").append(shareCount != null ? shareCount : 0);
        
        if (clickCount != null && clickCount > 0) {
            sb.append(" 👆 ").append(clickCount);
        }
        
        if (conversionCount != null && conversionCount > 0) {
            sb.append(" 💰 ").append(conversionCount);
        }
        
        return sb.toString();
    }

    /**
     * 计算内容收益
     */
    public BigDecimal calculateEarnings(BigDecimal baseAmount) {
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 基于内容评分计算收益系数
        double earningsMultiplier = 1.0;
        if (totalScore != null) {
            earningsMultiplier = totalScore.doubleValue() / 5.0;
        }
        
        // 基于转化率调整
        BigDecimal conversionRate = calculateConversionRate();
        if (conversionRate.compareTo(BigDecimal.valueOf(10)) >= 0) {
            earningsMultiplier *= 1.5; // 高转化率奖励
        }
        
        BigDecimal calculatedEarnings = baseAmount.multiply(BigDecimal.valueOf(earningsMultiplier))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        
        this.earnings = calculatedEarnings;
        return calculatedEarnings;
    }

    /**
     * 检查内容是否关联商家
     */
    public boolean hasShop() {
        return shopId != null;
    }

    /**
     * 获取内容完整信息
     */
    public String getFullInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPlatformIcon()).append(" ");
        sb.append(title).append("\n");
        
        if (description != null && description.length() > 100) {
            sb.append(description.substring(0, 100)).append("...\n");
        } else if (description != null) {
            sb.append(description).append("\n");
        }
        
        sb.append("📊 ").append(getStatsSummary()).append("\n");
        sb.append("⭐ 评分: ").append(totalScore != null ? totalScore : "0.00");
        sb.append(" (").append(getContentLevel()).append(")");
        
        if (earnings != null && earnings.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" | 💰 收益: ¥").append(earnings);
        }
        
        return sb.toString();
    }

    /**
     * 验证内容数据
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        if (title == null || title.trim().isEmpty()) {
            result.addError("标题不能为空");
        } else if (title.length() > 200) {
            result.addError("标题不能超过200字符");
        }
        
        if (type == null || type.trim().isEmpty()) {
            result.addError("内容类型不能为空");
        } else if (!type.equals(TYPE_ARTICLE) && !type.equals(TYPE_VIDEO) && !type.equals(TYPE_IMAGE)) {
            result.addError("不支持的内容类型");
        }
        
        if (platform == null || platform.trim().isEmpty()) {
            result.addError("发布平台不能为空");
        }
        
        if (creatorId == null) {
            result.addError("创作者ID不能为空");
        }
        
        return result;
    }

    /**
     * 验证结果类
     */
    @Data
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();
        
        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }
        
        public String getErrorMessage() {
            return errors.toString();
        }
    }

    /**
     * 发布内容
     */
    public boolean publish() {
        if (isPublished()) {
            return false; // 已经发布
        }
        
        if (isRejected()) {
            return false; // 被拒绝的内容不能直接发布
        }
        
        status = STATUS_PUBLISHED;
        publishedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 提交审核
     */
    public boolean submitForReview() {
        if (isPublished() || isUnderReview()) {
            return false;
        }
        
        status = STATUS_REVIEWING;
        return true;
    }

    /**
     * 审核通过
     */
    public boolean approve() {
        if (!isUnderReview()) {
            return false;
        }
        
        status = STATUS_PUBLISHED;
        publishedAt = LocalDateTime.now();
        return true;
    }

    /**
     * 审核拒绝
     */
    public boolean reject() {
        if (!isUnderReview()) {
            return false;
        }
        
        status = STATUS_REJECTED;
        return true;
    }

    /**
     * 保存为草稿
     */
    public boolean saveAsDraft() {
        if (isPublished()) {
            return false; // 已发布的内容不能保存为草稿
        }
        
        status = STATUS_DRAFT;
        return true;
    }

    /**
     * 检查内容是否可编辑
     */
    public boolean isEditable() {
        return !isPublished() && !isUnderReview();
    }

    /**
     * 检查内容是否可删除
     */
    public boolean isDeletable() {
        return !isPublished() || 
               (publishedAt != null && 
                publishedAt.isBefore(LocalDateTime.now().minusDays(1)));
    }

    /**
     * 获取内容年龄（发布后的天数）
     */
    public Long getAgeInDays() {
        if (publishedAt == null) {
            return null;
        }
        
        return java.time.Duration.between(publishedAt, LocalDateTime.now()).toDays();
    }

    /**
     * 检查内容是否新鲜（7天内发布）
     */
    public boolean isFresh() {
        Long age = getAgeInDays();
        return age != null && age <= 7;
    }

    /**
     * 获取内容热度值
     */
    public double calculateHeatValue() {
        double heat = 0.0;
        
        // 浏览数权重
        if (viewCount != null) {
            heat += Math.log10(viewCount + 1) * 0.3;
        }
        
        // 互动数权重
        int interactions = (likeCount != null ? likeCount : 0) +
                          (commentCount != null ? commentCount : 0) * 2 +
                          (shareCount != null ? shareCount : 0) * 3;
        heat += Math.log10(interactions + 1) * 0.4;
        
        // 时间衰减
        Long age = getAgeInDays();
        if (age != null) {
            double timeDecay = Math.exp(-age / 7.0); // 半衰期7天
            heat *= timeDecay;
        }
        
        // 评分加成
        if (totalScore != null) {
            heat *= (1.0 + totalScore.doubleValue() / 10.0);
        }
        
        return Math.round(heat * 100.0) / 100.0;
    }

    /**
     * 检查是否是AI生成内容
     */
    public boolean isAiGenerated() {
        return aiGenerated != null && aiGenerated == 1;
    }

    /**
     * 获取AI提示词摘要
     */
    public String getAiPromptSummary() {
        if (aiPrompt == null || aiPrompt.isEmpty()) {
            return "无AI提示词";
        }
        
        if (aiPrompt.length() > 50) {
            return aiPrompt.substring(0, 50) + "...";
        }
        
        return aiPrompt;
    }
}