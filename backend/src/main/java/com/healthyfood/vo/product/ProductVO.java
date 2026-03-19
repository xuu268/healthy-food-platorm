package com.healthyfood.vo.product;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品视图对象VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {

    private Long productId;
    private String name;
    private String description;
    private String category;
    
    private Long shopId;
    private String shopName;
    private String shopLogo;
    
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean onSale;
    private BigDecimal discount;
    private BigDecimal discountPrice;
    
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private String unit;
    private BigDecimal weight;
    
    // 营养信息
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbohydrates;
    private BigDecimal fat;
    private BigDecimal fiber;
    private BigDecimal sugar;
    private Integer sodium;
    private Integer cholesterol;
    private String vitamins;
    private String minerals;
    
    // 健康评分
    private Double healthScore;
    
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
    private LocalDateTime expiryDate;
    
    // 图片和标签
    private List<String> images;
    private List<String> tags;
    
    // 营销信息
    private Boolean recommended;
    private Boolean hot;
    private Boolean newArrival;
    
    // 统计信息
    private Double averageRating;
    private Integer totalReviews;
    private Integer salesCount;
    private Integer viewCount;
    private Integer favoriteCount;
    
    // 状态信息
    private String status;
    
    // 时间信息
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;
    
    /**
     * 获取商品状态标签
     */
    public String getStatusLabel() {
        if ("ACTIVE".equals(status)) {
            if (Boolean.TRUE.equals(onSale)) return "促销中";
            if (stockQuantity != null && stockQuantity <= lowStockThreshold) return "库存紧张";
            if (stockQuantity != null && stockQuantity == 0) return "已售罄";
            return "在售";
        } else if ("INACTIVE".equals(status)) {
            return "已下架";
        } else if ("OUT_OF_STOCK".equals(status)) {
            return "缺货";
        } else if ("DISCONTINUED".equals(status)) {
            return "已停售";
        }
        return status;
    }
    
    /**
     * 获取商品状态颜色
     */
    public String getStatusColor() {
        if ("ACTIVE".equals(status)) {
            if (Boolean.TRUE.equals(onSale)) return "orange";
            if (stockQuantity != null && stockQuantity <= lowStockThreshold) return "red";
            if (stockQuantity != null && stockQuantity == 0) return "gray";
            return "green";
        } else if ("INACTIVE".equals(status)) {
            return "gray";
        } else if ("OUT_OF_STOCK".equals(status)) {
            return "red";
        } else if ("DISCONTINUED".equals(status)) {
            return "black";
        }
        return "default";
    }
    
    /**
     * 检查是否为促销商品
     */
    public boolean isOnSale() {
        return Boolean.TRUE.equals(onSale) && discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 计算折扣价格
     */
    public BigDecimal getDiscountPrice() {
        if (isOnSale() && price != null && discount != null) {
            return price.multiply(BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100"))));
        }
        return price;
    }
    
    /**
     * 计算节省金额
     */
    public BigDecimal getSaveAmount() {
        if (isOnSale() && price != null && originalPrice != null) {
            return originalPrice.subtract(getDiscountPrice());
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 计算节省百分比
     */
    public BigDecimal getSavePercentage() {
        if (isOnSale() && originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal saveAmount = getSaveAmount();
            return saveAmount.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 检查库存状态
     */
    public String getStockStatus() {
        if (stockQuantity == null) return "未知";
        
        if (stockQuantity == 0) {
            return "缺货";
        } else if (stockQuantity <= lowStockThreshold) {
            return "库存紧张 (" + stockQuantity + " " + (unit != null ? unit : "件") + ")";
        } else {
            return "充足 (" + stockQuantity + " " + (unit != null ? unit : "件") + ")";
        }
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
        
        return summary.length() > 0 ? summary.toString() : "暂无营养信息";
    }
    
    /**
     * 获取健康等级
     */
    public String getHealthLevel() {
        if (healthScore == null) return "未知";
        
        if (healthScore >= 90) return "优秀";
        else if (healthScore >= 80) return "良好";
        else if (healthScore >= 70) return "中等";
        else if (healthScore >= 60) return "一般";
        else return "较差";
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
    
    /**
     * 获取营销标签
     */
    public List<String> getMarketingTags() {
        List<String> tags = new java.util.ArrayList<>();
        
        if (Boolean.TRUE.equals(recommended)) tags.add("推荐");
        if (Boolean.TRUE.equals(hot)) tags.add("热门");
        if (Boolean.TRUE.equals(newArrival)) tags.add("新品");
        if (Boolean.TRUE.equals(onSale)) tags.add("促销");
        
        return tags;
    }
    
    /**
     * 获取主图
     */
    public String getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }
    
    /**
     * 获取评分显示
     */
    public String getRatingDisplay() {
        if (averageRating == null) return "暂无评分";
        return String.format("%.1f", averageRating) + " (" + totalReviews + "评价)";
    }
    
    /**
     * 获取销量显示
     */
    public String getSalesDisplay() {
        if (salesCount == null) return "0";
        if (salesCount >= 10000) {
            return String.format("%.1f万", salesCount / 10000.0);
        }
        return salesCount.toString();
    }
}