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
 * 商品更新请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    @Size(min = 2, max = 100, message = "商品名称长度必须在2-100个字符之间")
    private String name;

    @Size(min = 10, max = 1000, message = "商品描述长度必须在10-1000个字符之间")
    private String description;

    private String category;

    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "99999.99", message = "商品价格不能超过99999.99")
    private BigDecimal price;

    private BigDecimal originalPrice;

    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stockQuantity;

    @Min(value = 1, message = "低库存阈值必须大于0")
    private Integer lowStockThreshold;

    private String unit;

    private BigDecimal weight;

    // 营养信息
    @Min(value = 0, message = "卡路里不能为负数")
    private Integer calories;
    
    @DecimalMin(value = "0.0", message = "蛋白质不能为负数")
    private BigDecimal protein;
    
    @DecimalMin(value = "0.0", message = "碳水化合物不能为负数")
    private BigDecimal carbohydrates;
    
    @DecimalMin(value = "0.0", message = "脂肪不能为负数")
    private BigDecimal fat;
    
    @DecimalMin(value = "0.0", message = "纤维不能为负数")
    private BigDecimal fiber;
    
    @DecimalMin(value = "0.0", message = "糖分不能为负数")
    private BigDecimal sugar;
    
    @Min(value = 0, message = "钠含量不能为负数")
    private Integer sodium;
    
    @Min(value = 0, message = "胆固醇不能为负数")
    private Integer cholesterol;
    
    private String vitamins;
    private String minerals;

    // 特殊饮食信息
    private Boolean vegetarian;
    private Boolean vegan;
    private Boolean glutenFree;
    private Boolean dairyFree;
    private String allergens;

    // 商品详情
    private String ingredients;
    private String cookingMethod;
    private String storageInstructions;
    private LocalDate expiryDate;

    // 图片和标签
    private List<String> images;
    private List<String> tags;

    // 营销信息
    private Boolean onSale;
    private BigDecimal discount;
    private Boolean recommended;
    private Boolean hot;
    private Boolean newArrival;

    // 状态信息
    private String status;

    /**
     * 检查是否有更新
     */
    public boolean hasUpdates() {
        return name != null || description != null || category != null ||
               price != null || originalPrice != null || stockQuantity != null ||
               lowStockThreshold != null || unit != null || weight != null ||
               calories != null || protein != null || carbohydrates != null ||
               fat != null || fiber != null || sugar != null || sodium != null ||
               cholesterol != null || vitamins != null || minerals != null ||
               vegetarian != null || vegan != null || glutenFree != null ||
               dairyFree != null || allergens != null || ingredients != null ||
               cookingMethod != null || storageInstructions != null || expiryDate != null ||
               images != null || tags != null || onSale != null || discount != null ||
               recommended != null || hot != null || newArrival != null || status != null;
    }
    
    /**
     * 验证更新数据
     */
    public void validate() {
        if (onSale != null && onSale && discount == null) {
            throw new IllegalArgumentException("促销商品必须设置折扣");
        }
        
        if (discount != null && (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("折扣必须在0-100之间");
        }
        
        if (stockQuantity != null && stockQuantity < 0) {
            throw new IllegalArgumentException("库存数量不能为负数");
        }
        
        if (lowStockThreshold != null && lowStockThreshold < 1) {
            throw new IllegalArgumentException("低库存阈值必须大于0");
        }
    }
    
    /**
     * 获取更新字段摘要
     */
    public String getUpdateSummary() {
        List<String> updates = new java.util.ArrayList<>();
        
        if (name != null) updates.add("名称");
        if (description != null) updates.add("描述");
        if (price != null) updates.add("价格");
        if (stockQuantity != null) updates.add("库存");
        if (onSale != null) updates.add("促销状态");
        if (status != null) updates.add("状态");
        
        return String.join(", ", updates);
    }
    
    /**
     * 检查是否为价格更新
     */
    public boolean isPriceUpdate() {
        return price != null || originalPrice != null || discount != null || onSale != null;
    }
    
    /**
     * 检查是否为库存更新
     */
    public boolean isStockUpdate() {
        return stockQuantity != null || lowStockThreshold != null;
    }
    
    /**
     * 检查是否为营养信息更新
     */
    public boolean isNutritionUpdate() {
        return calories != null || protein != null || carbohydrates != null ||
               fat != null || fiber != null || sugar != null || sodium != null ||
               cholesterol != null || vitamins != null || minerals != null;
    }
    
    /**
     * 检查是否为营销信息更新
     */
    public boolean isMarketingUpdate() {
        return onSale != null || discount != null || recommended != null ||
               hot != null || newArrival != null;
    }
}