package com.healthyfood.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 成分分析结果实体类
 * 存储AI对产品成分的分析结果
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ingredient_analysis")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 产品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    /**
     * 安全评分 (0-1)
     */
    @Column(name = "safety_score", precision = 3, scale = 2)
    private BigDecimal safetyScore;
    
    /**
     * 营养评分 (0-1)
     */
    @Column(name = "nutrition_score", precision = 3, scale = 2)
    private BigDecimal nutritionScore;
    
    /**
     * 健康评分 (0-1)
     */
    @Column(name = "health_score", precision = 3, scale = 2)
    private BigDecimal healthScore;
    
    /**
     * 综合评分 (0-1)
     */
    @Column(name = "overall_score", precision = 3, scale = 2)
    private BigDecimal overallScore;
    
    /**
     * 过敏原摘要 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "allergen_summary", columnDefinition = "json")
    private AllergenSummary allergenSummary;
    
    /**
     * 营养摘要 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "nutrition_summary", columnDefinition = "json")
    private NutritionSummary nutritionSummary;
    
    /**
     * 健康警告 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "health_warnings", columnDefinition = "json")
    private List<HealthWarning> healthWarnings;
    
    /**
     * 适合人群 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "suitable_for", columnDefinition = "json")
    private List<String> suitableFor;
    
    /**
     * 不适合人群 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "not_suitable_for", columnDefinition = "json")
    private List<String> notSuitableFor;
    
    /**
     * AI分析原始数据 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "ai_analysis_json", columnDefinition = "json")
    private AiAnalysisData aiAnalysisJson;
    
    /**
     * 分析版本
     */
    @Column(name = "analysis_version", length = 20)
    private String analysisVersion = "1.0";
    
    /**
     * 分析时间
     */
    @Column(name = "analyzed_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime analyzedAt;
    
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
     * 关联的产品实体
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    /**
     * 计算综合评分
     */
    public void calculateOverallScore() {
        if (safetyScore == null || nutritionScore == null || healthScore == null) {
            overallScore = null;
            return;
        }
        
        // 权重分配：安全40%，营养35%，健康25%
        BigDecimal weightedScore = safetyScore.multiply(new BigDecimal("0.4"))
                .add(nutritionScore.multiply(new BigDecimal("0.35")))
                .add(healthScore.multiply(new BigDecimal("0.25")));
        
        overallScore = weightedScore.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 获取评分等级
     * @param score 评分
     * @return 等级描述
     */
    private String getScoreLevel(BigDecimal score) {
        if (score == null) {
            return "未评估";
        }
        
        if (score.compareTo(new BigDecimal("0.9")) >= 0) {
            return "优秀";
        } else if (score.compareTo(new BigDecimal("0.7")) >= 0) {
            return "良好";
        } else if (score.compareTo(new BigDecimal("0.5")) >= 0) {
            return "一般";
        } else {
            return "较差";
        }
    }
    
    /**
     * 获取安全等级
     * @return 安全等级
     */
    public String getSafetyLevel() {
        return getScoreLevel(safetyScore);
    }
    
    /**
     * 获取营养等级
     * @return 营养等级
     */
    public String getNutritionLevel() {
        return getScoreLevel(nutritionScore);
    }
    
    /**
     * 获取健康等级
     * @return 健康等级
     */
    public String getHealthLevel() {
        return getScoreLevel(healthScore);
    }
    
    /**
     * 获取综合等级
     * @return 综合等级
     */
    public String getOverallLevel() {
        return getScoreLevel(overallScore);
    }
    
    /**
     * 获取评分颜色
     * @param score 评分
     * @return 颜色代码
     */
    private String getScoreColor(BigDecimal score) {
        if (score == null) {
            return "#757575";  // 灰色
        }
        
        if (score.compareTo(new BigDecimal("0.9")) >= 0) {
            return "#4CAF50";  // 绿色
        } else if (score.compareTo(new BigDecimal("0.7")) >= 0) {
            return "#FF9800";  // 橙色
        } else if (score.compareTo(new BigDecimal("0.5")) >= 0) {
            return "#FFC107";  // 黄色
        } else {
            return "#F44336";  // 红色
        }
    }
    
    /**
     * 获取安全评分颜色
     * @return 颜色代码
     */
    public String getSafetyColor() {
        return getScoreColor(safetyScore);
    }
    
    /**
     * 获取营养评分颜色
     * @return 颜色代码
     */
    public String getNutritionColor() {
        return getScoreColor(nutritionScore);
    }
    
    /**
     * 获取健康评分颜色
     * @return 颜色代码
     */
    public String getHealthColor() {
        return getScoreColor(healthScore);
    }
    
    /**
     * 获取综合评分颜色
     * @return 颜色代码
     */
    public String getOverallColor() {
        return getScoreColor(overallScore);
    }
    
    /**
     * 检查是否有过敏原
     * @return 是否有过敏原
     */
    public boolean hasAllergens() {
        return allergenSummary != null && allergenSummary.hasAllergens();
    }
    
    /**
     * 检查是否有健康警告
     * @return 是否有健康警告
     */
    public boolean hasHealthWarnings() {
        return healthWarnings != null && !healthWarnings.isEmpty();
    }
    
    /**
     * 获取主要健康警告
     * @return 主要健康警告
     */
    public String getMainHealthWarning() {
        if (!hasHealthWarnings()) {
            return null;
        }
        
        // 返回第一个警告
        return healthWarnings.get(0).getDescription();
    }
    
    /**
     * 检查是否适合特定人群
     * @param group 人群名称
     * @return 是否适合
     */
    public boolean isSuitableFor(String group) {
        return suitableFor != null && suitableFor.contains(group);
    }
    
    /**
     * 检查是否不适合特定人群
     * @param group 人群名称
     * @return 是否不适合
     */
    public boolean isNotSuitableFor(String group) {
        return notSuitableFor != null && notSuitableFor.contains(group);
    }
    
    /**
     * 获取简化的分析信息
     * @return 简化信息
     */
    public SimpleAnalysisInfo toSimpleInfo() {
        SimpleAnalysisInfo info = new SimpleAnalysisInfo();
        info.setId(id);
        info.setProductId(productId);
        info.setSafetyScore(safetyScore);
        info.setNutritionScore(nutritionScore);
        info.setHealthScore(healthScore);
        info.setOverallScore(overallScore);
        info.setSafetyLevel(getSafetyLevel());
        info.setNutritionLevel(getNutritionLevel());
        info.setHealthLevel(getHealthLevel());
        info.setOverallLevel(getOverallLevel());
        info.setSafetyColor(getSafetyColor());
        info.setNutritionColor(getNutritionColor());
        info.setHealthColor(getHealthColor());
        info.setOverallColor(getOverallColor());
        info.setHasAllergens(hasAllergens());
        info.setHasHealthWarnings(hasHealthWarnings());
        info.setMainHealthWarning(getMainHealthWarning());
        info.setAnalysisVersion(analysisVersion);
        info.setAnalyzedAt(analyzedAt);
        
        return info;
    }
    
    /**
     * 简化分析信息类
     */
    @Data
    public static class SimpleAnalysisInfo {
        private Long id;
        private Long productId;
        private BigDecimal safetyScore;
        private BigDecimal nutritionScore;
        private BigDecimal healthScore;
        private BigDecimal overallScore;
        private String safetyLevel;
        private String nutritionLevel;
        private String healthLevel;
        private String overallLevel;
        private String safetyColor;
        private String nutritionColor;
        private String healthColor;
        private String overallColor;
        private Boolean hasAllergens;
        private Boolean hasHealthWarnings;
        private String mainHealthWarning;
        private String analysisVersion;
        private LocalDateTime analyzedAt;
        
        public SimpleAnalysisInfo() {
            // 默认构造函数
        }
    }
    
    /**
     * 过敏原摘要类
     */
    @Data
    public static class AllergenSummary {
        private List<String> commonAllergens;      // 常见过敏原
        private List<String> potentialAllergens;   // 潜在过敏原
        private String crossReactivity;            // 交叉反应
        private String severity;                   // 严重程度
        
        public boolean hasAllergens() {
            return (commonAllergens != null && !commonAllergens.isEmpty()) ||
                   (potentialAllergens != null && !potentialAllergens.isEmpty());
        }
    }
    
    /**
     * 营养摘要类
     */
    @Data
    public static class NutritionSummary {
        private BigDecimal totalCalories;          // 总热量
        private BigDecimal proteinPercentage;      // 蛋白质比例
        private BigDecimal fatPercentage;          // 脂肪比例
        private BigDecimal carbPercentage;         // 碳水比例
        private BigDecimal fiberContent;           // 纤维含量
        private BigDecimal sugarContent;           // 糖分含量
        private BigDecimal sodiumContent;          // 钠含量
        private String balanceAssessment;          // 均衡评估
        private List<String> keyNutrients;         // 关键营养素
    }
    
    /**
     * 健康警告类
     */
    @Data
    public static class HealthWarning {
        private String type;                       // 警告类型
        private String description;                // 描述
        private String severity;                   // 严重程度
        private String recommendation;             // 建议
    }
    
    /**
     * AI分析数据类
     */
    @Data
    public static class AiAnalysisData {
        private String modelVersion;               // 模型版本
        private LocalDateTime analysisTime;        // 分析时间
        private List<String> extractedKeywords;    // 提取的关键词
        private String reasoning;                  // 推理过程
        private BigDecimal confidence;             // 置信度
        private Object rawData;                    // 原始数据
    }
    
    /**
     * 验证分析结果是否有效
     * @return 是否有效
     */
    public boolean isValid() {
        return productId != null && productId > 0 &&
               analyzedAt != null &&
               (safetyScore == null || (safetyScore.compareTo(BigDecimal.ZERO) >= 0 && safetyScore.compareTo(BigDecimal.ONE) <= 0)) &&
               (nutritionScore == null || (nutritionScore.compareTo(BigDecimal.ZERO) >= 0 && nutritionScore.compareTo(BigDecimal.ONE) <= 0)) &&
               (healthScore == null || (healthScore.compareTo(BigDecimal.ZERO) >= 0 && healthScore.compareTo(BigDecimal.ONE) <= 0)) &&
               (overallScore == null || (overallScore.compareTo(BigDecimal.ZERO) >= 0 && overallScore.compareTo(BigDecimal.ONE) <= 0));
    }
}