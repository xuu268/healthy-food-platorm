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
 * 产品成分实体类
 * 存储产品的详细成分信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "product_ingredient")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductIngredient {
    
    /**
     * 成分类型枚举
     */
    public enum IngredientType {
        MAIN,       // 主料
        ACCESSORY,  // 辅料
        ADDITIVE,   // 添加剂
        SEASONING   // 调味料
    }
    
    /**
     * 安全等级枚举
     */
    public enum SafetyLevel {
        SAFE,       // 安全
        CAUTION,    // 注意
        WARNING     // 警告
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 产品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    /**
     * 成分名称
     */
    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String ingredientName;
    
    /**
     * 成分类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ingredient_type", nullable = false, length = 20)
    private IngredientType ingredientType;
    
    /**
     * 含量
     */
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    
    /**
     * 单位
     */
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;
    
    /**
     * 安全等级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "safety_level", length = 20)
    private SafetyLevel safetyLevel = SafetyLevel.SAFE;
    
    /**
     * 过敏原标志
     */
    @Column(name = "allergen_flag")
    private Boolean allergenFlag = false;
    
    /**
     * 营养成分数据 (JSON格式)
     */
    @Column(name = "nutrition_data", columnDefinition = "json")
    private String nutritionData;
    
    /**
     * 成分来源
     */
    @Column(name = "source", length = 100)
    private String source;
    
    /**
     * 加工方式
     */
    @Column(name = "processing_method", length = 50)
    private String processingMethod;
    
    /**
     * 有机认证标志
     */
    @Column(name = "organic_certified")
    private Boolean organicCertified = false;
    
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    /**
     * 获取营养成分对象
     * @return 营养成分对象
     */
    public NutritionData getNutritionDataObject() {
        if (nutritionData == null || nutritionData.isEmpty()) {
            return new NutritionData();
        }
        try {
            // 这里需要实现JSON到对象的转换
            // 简化处理，实际项目中应使用Jackson或Gson
            return new NutritionData();
        } catch (Exception e) {
            return new NutritionData();
        }
    }
    
    /**
     * 设置营养成分对象
     * @param nutritionData 营养成分对象
     */
    public void setNutritionDataObject(NutritionData nutritionData) {
        if (nutritionData == null) {
            this.nutritionData = null;
        } else {
            // 这里需要实现对象到JSON的转换
            // 简化处理，实际项目中应使用Jackson或Gson
            this.nutritionData = "{}";
        }
    }
    
    /**
     * 营养成分数据内部类
     * 与Product实体的营养字段保持一致
     */
    @Data
    public static class NutritionData {
        private Integer calories;             // 热量 (千卡) - 与Product.calories类型一致
        private BigDecimal protein;           // 蛋白质 (克)
        private BigDecimal fat;               // 脂肪 (克)
        private BigDecimal carbohydrates;     // 碳水化合物 (克)
        private BigDecimal fiber;             // 膳食纤维 (克)
        private BigDecimal sugar;             // 糖分 (克)
        private BigDecimal sodium;            // 钠 (毫克)
        
        // 扩展字段（Product实体中没有的）
        private BigDecimal saturatedFat;      // 饱和脂肪 (克)
        private BigDecimal cholesterol;       // 胆固醇 (毫克)
        private BigDecimal vitaminA;          // 维生素A (微克)
        private BigDecimal vitaminC;          // 维生素C (毫克)
        private BigDecimal calcium;           // 钙 (毫克)
        private BigDecimal iron;              // 铁 (毫克)
        
        public NutritionData() {
            this.calories = 0;
            this.protein = BigDecimal.ZERO;
            this.fat = BigDecimal.ZERO;
            this.carbohydrates = BigDecimal.ZERO;
            this.fiber = BigDecimal.ZERO;
            this.sugar = BigDecimal.ZERO;
            this.sodium = BigDecimal.ZERO;
            this.saturatedFat = BigDecimal.ZERO;
            this.cholesterol = BigDecimal.ZERO;
            this.vitaminA = BigDecimal.ZERO;
            this.vitaminC = BigDecimal.ZERO;
            this.calcium = BigDecimal.ZERO;
            this.iron = BigDecimal.ZERO;
        }
        
        /**
         * 转换为Product.NutritionInfo格式
         */
        public Product.NutritionInfo toProductNutritionInfo() {
            Product.NutritionInfo info = new Product.NutritionInfo();
            info.setCalories(this.calories);
            info.setProtein(this.protein);
            info.setFat(this.fat);
            info.setCarbohydrates(this.carbohydrates);
            info.setFiber(this.fiber);
            info.setSugar(this.sugar);
            info.setSodium(this.sodium);
            return info;
        }
    }
    
    /**
     * 计算每100克的标准营养成分
     * @return 标准营养成分
     */
    public NutritionData calculateStandardNutrition() {
        NutritionData data = getNutritionDataObject();
        if (BigDecimal.ZERO.equals(quantity) || quantity == null) {
            return data;
        }
        
        // 计算每100克的营养成分
        BigDecimal factor = new BigDecimal("100").divide(quantity, 2, BigDecimal.ROUND_HALF_UP);
        
        NutritionData standardData = new NutritionData();
        standardData.setCalories(calculateCaloriesValue(data.getCalories(), factor));
        standardData.setProtein(calculateValue(data.getProtein(), factor));
        standardData.setFat(calculateValue(data.getFat(), factor));
        standardData.setSaturatedFat(calculateValue(data.getSaturatedFat(), factor));
        standardData.setCarbohydrates(calculateValue(data.getCarbohydrates(), factor));
        standardData.setSugar(calculateValue(data.getSugar(), factor));
        standardData.setFiber(calculateValue(data.getFiber(), factor));
        standardData.setSodium(calculateValue(data.getSodium(), factor));
        standardData.setCholesterol(calculateValue(data.getCholesterol(), factor));
        standardData.setVitaminA(calculateValue(data.getVitaminA(), factor));
        standardData.setVitaminC(calculateValue(data.getVitaminC(), factor));
        standardData.setCalcium(calculateValue(data.getCalcium(), factor));
        standardData.setIron(calculateValue(data.getIron(), factor));
        
        return standardData;
    }
    
    private BigDecimal calculateValue(BigDecimal value, BigDecimal factor) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.multiply(factor).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    private Integer calculateCaloriesValue(Integer calories, BigDecimal factor) {
        if (calories == null) {
            return 0;
        }
        return BigDecimal.valueOf(calories).multiply(factor).intValue();
    }
    
    /**
     * 计算该成分对产品总营养的贡献
     * @param productQuantity 产品总份量（克）
     * @return 营养贡献值
     */
    public Product.NutritionInfo calculateNutritionContribution(BigDecimal productQuantity) {
        if (productQuantity == null || BigDecimal.ZERO.equals(productQuantity)) {
            return new Product.NutritionInfo();
        }
        
        NutritionData data = getNutritionDataObject();
        BigDecimal proportion = quantity.divide(productQuantity, 4, BigDecimal.ROUND_HALF_UP);
        
        Product.NutritionInfo contribution = new Product.NutritionInfo();
        
        if (data.getCalories() != null) {
            contribution.setCalories((int)(data.getCalories() * proportion.doubleValue()));
        }
        
        if (data.getProtein() != null) {
            contribution.setProtein(data.getProtein().multiply(proportion).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        if (data.getFat() != null) {
            contribution.setFat(data.getFat().multiply(proportion).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        if (data.getCarbohydrates() != null) {
            contribution.setCarbohydrates(data.getCarbohydrates().multiply(proportion).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        if (data.getFiber() != null) {
            contribution.setFiber(data.getFiber().multiply(proportion).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        if (data.getSugar() != null) {
            contribution.setSugar(data.getSugar().multiply(proportion).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        if (data.getSodium() != null) {
            contribution.setSodium(data.getSodium().multiply(proportion).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        
        return contribution;
    }
    
    /**
     * 检查是否为过敏原
     * @return 是否为过敏原
     */
    public boolean isAllergen() {
        return Boolean.TRUE.equals(allergenFlag);
    }
    
    /**
     * 检查是否为有机认证
     * @return 是否为有机认证
     */
    public boolean isOrganic() {
        return Boolean.TRUE.equals(organicCertified);
    }
    
    /**
     * 获取安全等级描述
     * @return 安全等级描述
     */
    public String getSafetyLevelDescription() {
        switch (safetyLevel) {
            case SAFE:
                return "安全";
            case CAUTION:
                return "注意：建议适量食用";
            case WARNING:
                return "警告：特殊人群需谨慎";
            default:
                return "未知";
        }
    }
}