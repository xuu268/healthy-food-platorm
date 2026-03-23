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
 * 成分知识库实体类
 * 存储成分的科学知识和健康信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ingredient_knowledge")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientKnowledge {
    
    /**
     * 来源可靠性枚举
     */
    public enum SourceReliability {
        HIGH,    // 高
        MEDIUM,  // 中
        LOW      // 低
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 成分名称
     */
    @Column(name = "ingredient_name", nullable = false, unique = true, length = 100)
    private String ingredientName;
    
    /**
     * 学名
     */
    @Column(name = "scientific_name", length = 200)
    private String scientificName;
    
    /**
     * 分类
     */
    @Column(name = "category", length = 50)
    private String category;
    
    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    /**
     * 营养成分 (每100g) (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "nutrition_facts", columnDefinition = "json")
    private NutritionFacts nutritionFacts;
    
    /**
     * 健康益处 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "health_benefits", columnDefinition = "json")
    private List<HealthBenefit> healthBenefits;
    
    /**
     * 潜在风险 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "potential_risks", columnDefinition = "json")
    private List<PotentialRisk> potentialRisks;
    
    /**
     * 过敏原信息 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "allergen_info", columnDefinition = "json")
    private AllergenInfo allergenInfo;
    
    /**
     * 储存条件
     */
    @Column(name = "storage_conditions", columnDefinition = "text")
    private String storageConditions;
    
    /**
     * 加工方式 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "processing_methods", columnDefinition = "json")
    private List<ProcessingMethod> processingMethods;
    
    /**
     * 可持续性评分 (0-1)
     */
    @Column(name = "sustainability_score", precision = 3, scale = 2)
    private BigDecimal sustainabilityScore;
    
    /**
     * 来源可靠性
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source_reliability", length = 20)
    private SourceReliability sourceReliability = SourceReliability.MEDIUM;
    
    /**
     * 参考文献 (JSON格式)
     */
    @Type(type = "json")
    @Column(name = "references", columnDefinition = "json")
    private List<Reference> references;
    
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
     * 获取来源可靠性描述
     * @return 可靠性描述
     */
    public String getSourceReliabilityDescription() {
        switch (sourceReliability) {
            case HIGH:
                return "高可靠性（权威机构研究）";
            case MEDIUM:
                return "中等可靠性（多个研究支持）";
            case LOW:
                return "低可靠性（初步研究或传闻）";
            default:
                return "未知";
        }
    }
    
    /**
     * 获取可持续性等级
     * @return 可持续性等级
     */
    public String getSustainabilityLevel() {
        if (sustainabilityScore == null) {
            return "未评估";
        }
        
        if (sustainabilityScore.compareTo(new BigDecimal("0.8")) >= 0) {
            return "非常高";
        } else if (sustainabilityScore.compareTo(new BigDecimal("0.6")) >= 0) {
            return "高";
        } else if (sustainabilityScore.compareTo(new BigDecimal("0.4")) >= 0) {
            return "中等";
        } else if (sustainabilityScore.compareTo(new BigDecimal("0.2")) >= 0) {
            return "低";
        } else {
            return "非常低";
        }
    }
    
    /**
     * 获取可持续性颜色
     * @return 颜色代码
     */
    public String getSustainabilityColor() {
        String level = getSustainabilityLevel();
        switch (level) {
            case "非常高":
                return "#4CAF50";  // 绿色
            case "高":
                return "#8BC34A";  // 浅绿
            case "中等":
                return "#FFC107";  // 黄色
            case "低":
                return "#FF9800";  // 橙色
            case "非常低":
                return "#F44336";  // 红色
            default:
                return "#757575";  // 灰色
        }
    }
    
    /**
     * 检查是否为常见过敏原
     * @return 是否为常见过敏原
     */
    public boolean isCommonAllergen() {
        return allergenInfo != null && allergenInfo.isCommonAllergen();
    }
    
    /**
     * 获取主要健康益处
     * @return 主要健康益处
     */
    public String getMainHealthBenefit() {
        if (healthBenefits == null || healthBenefits.isEmpty()) {
            return null;
        }
        
        // 返回第一个健康益处
        return healthBenefits.get(0).getDescription();
    }
    
    /**
     * 获取主要潜在风险
     * @return 主要潜在风险
     */
    public String getMainPotentialRisk() {
        if (potentialRisks == null || potentialRisks.isEmpty()) {
            return null;
        }
        
        // 返回第一个潜在风险
        return potentialRisks.get(0).getDescription();
    }
    
    /**
     * 获取推荐加工方式
     * @return 推荐加工方式
     */
    public String getRecommendedProcessingMethod() {
        if (processingMethods == null || processingMethods.isEmpty()) {
            return null;
        }
        
        // 查找推荐等级最高的加工方式
        return processingMethods.stream()
                .filter(method -> "RECOMMENDED".equals(method.getRecommendationLevel()))
                .findFirst()
                .map(ProcessingMethod::getName)
                .orElse(null);
    }
    
    /**
     * 检查是否为高蛋白成分
     * @return 是否为高蛋白
     */
    public boolean isHighProtein() {
        if (nutritionFacts == null || nutritionFacts.getProtein() == null) {
            return false;
        }
        return nutritionFacts.getProtein().compareTo(new BigDecimal("20")) >= 0;  // 每100g蛋白质≥20g
    }
    
    /**
     * 检查是否为低碳水成分
     * @return 是否为低碳水
     */
    public boolean isLowCarb() {
        if (nutritionFacts == null || nutritionFacts.getCarbohydrates() == null) {
            return false;
        }
        return nutritionFacts.getCarbohydrates().compareTo(new BigDecimal("10")) <= 0;  // 每100g碳水≤10g
    }
    
    /**
     * 检查是否为低脂肪成分
     * @return 是否为低脂肪
     */
    public boolean isLowFat() {
        if (nutritionFacts == null || nutritionFacts.getFat() == null) {
            return false;
        }
        return nutritionFacts.getFat().compareTo(new BigDecimal("5")) <= 0;  // 每100g脂肪≤5g
    }
    
    /**
     * 检查是否为高纤维成分
     * @return 是否为高纤维
     */
    public boolean isHighFiber() {
        if (nutritionFacts == null || nutritionFacts.getFiber() == null) {
            return false;
        }
        return nutritionFacts.getFiber().compareTo(new BigDecimal("5")) >= 0;  // 每100g纤维≥5g
    }
    
    /**
     * 获取简化的知识信息
     * @return 简化信息
     */
    public SimpleKnowledgeInfo toSimpleInfo() {
        SimpleKnowledgeInfo info = new SimpleKnowledgeInfo();
        info.setId(id);
        info.setIngredientName(ingredientName);
        info.setScientificName(scientificName);
        info.setCategory(category);
        info.setDescription(description);
        info.setSustainabilityScore(sustainabilityScore);
        info.setSustainabilityLevel(getSustainabilityLevel());
        info.setSustainabilityColor(getSustainabilityColor());
        info.setSourceReliability(sourceReliability);
        info.setSourceReliabilityDescription(getSourceReliabilityDescription());
        info.setCommonAllergen(isCommonAllergen());
        info.setMainHealthBenefit(getMainHealthBenefit());
        info.setMainPotentialRisk(getMainPotentialRisk());
        info.setRecommendedProcessingMethod(getRecommendedProcessingMethod());
        info.setHighProtein(isHighProtein());
        info.setLowCarb(isLowCarb());
        info.setLowFat(isLowFat());
        info.setHighFiber(isHighFiber());
        
        return info;
    }
    
    /**
     * 简化知识信息类
     */
    @Data
    public static class SimpleKnowledgeInfo {
        private Long id;
        private String ingredientName;
        private String scientificName;
        private String category;
        private String description;
        private BigDecimal sustainabilityScore;
        private String sustainabilityLevel;
        private String sustainabilityColor;
        private SourceReliability sourceReliability;
        private String sourceReliabilityDescription;
        private Boolean commonAllergen;
        private String mainHealthBenefit;
        private String mainPotentialRisk;
        private String recommendedProcessingMethod;
        private Boolean highProtein;
        private Boolean lowCarb;
        private Boolean lowFat;
        private Boolean highFiber;
        
        public SimpleKnowledgeInfo() {
            // 默认构造函数
        }
    }
    
    /**
     * 营养成分类
     */
    @Data
    public static class NutritionFacts {
        private BigDecimal calories;          // 热量 (千卡)
        private BigDecimal protein;           // 蛋白质 (g)
        private BigDecimal fat;               // 脂肪 (g)
        private BigDecimal saturatedFat;      // 饱和脂肪 (g)
        private BigDecimal carbohydrates;     // 碳水化合物 (g)
        private BigDecimal sugar;             // 糖分 (g)
        private BigDecimal fiber;             // 膳食纤维 (g)
        private BigDecimal sodium;            // 钠 (mg)
        private BigDecimal cholesterol;       // 胆固醇 (mg)
        private BigDecimal vitaminA;          // 维生素A (μg)
        private BigDecimal vitaminC;          // 维生素C (mg)
        private BigDecimal calcium;           // 钙 (mg)
        private BigDecimal iron;              // 铁 (mg)
        private BigDecimal potassium;         // 钾 (mg)
        private BigDecimal magnesium;         // 镁 (mg)
        private BigDecimal zinc;              // 锌 (mg)
    }
    
    /**
     * 健康益处类
     */
    @Data
    public static class HealthBenefit {
        private String type;                  // 益处类型
        private String description;           // 描述
        private String mechanism;             // 作用机制
        private String evidenceLevel;         // 证据等级
        private List<String> supportingStudies; // 支持研究
    }
    
    /**
     * 潜在风险类
     */
    @Data
    public static class PotentialRisk {
        private String type;                  // 风险类型
        private String description;           // 描述
        private String severity;              // 严重程度
        private String population;            // 风险人群
        private String prevention;            // 预防措施
    }
    
    /**
     * 过敏原信息类
     */
    @Data
    public static class AllergenInfo {
        private Boolean commonAllergen;       // 是否为常见过敏原
        private String allergenType;          // 过敏原类型
        private List<String> crossReactivity; // 交叉反应
        private String severity;              // 严重程度
        private String diagnosis;             // 诊断方法
        private String management;            // 管理方法
        
        public boolean isCommonAllergen() {
            return Boolean.TRUE.equals(commonAllergen);
        }
    }
    
    /**
     * 加工方式类
     */
    @Data
    public static class ProcessingMethod {
        private String name;                  // 加工方式名称
        private String description;           // 描述
        private String effect;                // 对成分的影响
        private String recommendationLevel;   // 推荐等级
        private String nutritionalImpact;     // 营养影响
    }
    
    /**
     * 参考文献类
     */
    @Data
    public static class Reference {
        private String title;                 // 标题
        private String authors;               // 作者
        private String journal;               // 期刊
        private Integer year;                 // 年份
        private String doi;                   // DOI
        private String url;                   // 链接
        private String type;                  // 文献类型
    }
    
    /**
     * 验证知识信息是否有效
     * @return 是否有效
     */
    public boolean isValid() {
        return ingredientName != null && !ingredientName.trim().isEmpty() &&
               (sustainabilityScore == null || 
                (sustainabilityScore.compareTo(BigDecimal.ZERO) >= 0 && 
                 sustainabilityScore.compareTo(BigDecimal.ONE) <= 0));
    }
}