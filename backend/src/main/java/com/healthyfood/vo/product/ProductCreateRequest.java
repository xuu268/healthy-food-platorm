package com.healthyfood.vo.product;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 商品创建请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "商品名称不能为空")
    @Size(min = 2, max = 100, message = "商品名称长度必须在2-100个字符之间")
    private String name;

    @NotBlank(message = "商品描述不能为空")
    @Size(min = 10, max = 1000, message = "商品描述长度必须在10-1000个字符之间")
    private String description;

    @NotNull(message = "商品分类不能为空")
    private String category;

    @NotNull(message = "商家ID不能为空")
    private Long shopId;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "99999.99", message = "商品价格不能超过99999.99")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stockQuantity;

    @Min(value = 1, message = "低库存阈值必须大于0")
    private Integer lowStockThreshold = 10;

    private String unit;

    private BigDecimal weight;

    // 营养信息
    private Integer calories;        // 卡路里 (千卡)
    private BigDecimal protein;      // 蛋白质 (克)
    private BigDecimal carbohydrates; // 碳水化合物 (克)
    private BigDecimal fat;          // 脂肪 (克)
    private BigDecimal fiber;        // 纤维 (克)
    private BigDecimal sugar;        // 糖分 (克)
    private Integer sodium;          // 钠 (毫克)
    private Integer cholesterol;     // 胆固醇 (毫克)
    private String vitamins;         // 维生素
    private String minerals;         // 矿物质

    // 特殊饮食信息
    private Boolean vegetarian = false;
    private Boolean vegan = false;
    private Boolean glutenFree = false;
    private Boolean dairyFree = false;
    private String allergens;        // 过敏原

    // 商品详情
    private String ingredients;      // 成分
    private String cookingMethod;    // 烹饪方法
    private String storageInstructions; // 存储说明
    private LocalDate expiryDate;    // 保质期

    // 图片和标签
    private List<String> images;
    private List<String> tags;

    // 营销信息
    private Boolean onSale = false;
    private BigDecimal discount;     // 折扣百分比 (0-100)
    private Boolean recommended = false;
    private Boolean hot = false;
    private Boolean newArrival = false;

    // 验证方法
    public void validate() {
        if (onSale && discount == null) {
            throw new IllegalArgumentException("促销商品必须设置折扣");
        }
        
        if (discount != null && (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("折扣必须在0-100之间");
        }
        
        if (originalPrice == null) {
            originalPrice = price;
        }
        
        if (lowStockThreshold == null || lowStockThreshold < 1) {
            lowStockThreshold = 10;
        }
    }
    
    /**
     * 计算折扣价格
     */
    public BigDecimal calculateDiscountPrice() {
        if (onSale && discount != null) {
            return price.multiply(BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100"))));
        }
        return price;
    }
    
    /**
     * 检查是否为健康食品
     */
    public boolean isHealthyFood() {
        // 简单的健康食品判断逻辑
        boolean healthy = true;
        
        if (calories != null && calories > 500) {
            healthy = false;
        }
        
        if (sugar != null && sugar.compareTo(new BigDecimal("20")) > 0) {
            healthy = false;
        }
        
        if (sodium != null && sodium > 500) {
            healthy = false;
        }
        
        return healthy;
    }
    
    /**
     * 获取营养摘要
     */
    public String getNutritionSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (calories != null) {
            summary.append("热量: ").append(calories).append("千卡");
        }
        
        if (protein != null) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("蛋白质: ").append(protein).append("g");
        }
        
        if (carbohydrates != null) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("碳水: ").append(carbohydrates).append("g");
        }
        
        if (fat != null) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("脂肪: ").append(fat).append("g");
        }
        
        return summary.toString();
    }
    
    /**
     * 获取特殊饮食标签
     */
    public List<String> getDietaryTags() {
        List<String> tags = new java.util.ArrayList<>();
        
        if (Boolean.TRUE.equals(vegetarian)) tags.add("素食");
        if (Boolean.TRUE.equals(vegan)) tags.add("纯素");
        if (Boolean.TRUE.equals(glutenFree)) tags.add("无麸质");
        if (Boolean.TRUE.equals(dairyFree)) tags.add("无乳制品");
        
        return tags;
    }
}