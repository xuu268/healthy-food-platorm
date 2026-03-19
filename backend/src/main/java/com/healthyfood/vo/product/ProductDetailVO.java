package com.healthyfood.vo.product;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商品详情视图对象VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailVO {

    // 商品基本信息
    private Long productId;
    private String name;
    private String description;
    private String category;
    private String subCategory;
    
    // 商家信息
    private Long shopId;
    private String shopName;
    private String shopLogo;
    private String shopDescription;
    private Double shopRating;
    private Integer shopTotalOrders;
    private String shopContactPhone;
    private String shopAddress;
    
    // 价格信息
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean onSale;
    private BigDecimal discount;
    private BigDecimal discountPrice;
    private BigDecimal saveAmount;
    private BigDecimal savePercentage;
    
    // 库存信息
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private String stockStatus;
    private String unit;
    private BigDecimal weight;
    private String weightUnit;
    
    // 营养信息详情
    private NutritionalInfoVO nutritionalInfo;
    
    // 特殊饮食信息
    private DietaryInfoVO dietaryInfo;
    
    // 健康评分
    private Double healthScore;
    private String healthLevel;
    private String healthDescription;
    
    // 商品详情
    private String ingredients;
    private String cookingMethod;
    private String storageInstructions;
    private LocalDateTime expiryDate;
    private String shelfLife;
    
    // 图片和媒体
    private List<String> images;
    private List<String> thumbnails;
    private String videoUrl;
    
    // 标签
    private List<String> tags;
    private List<String> dietaryTags;
    private List<String> marketingTags;
    
    // 营销信息
    private Boolean recommended;
    private Boolean hot;
    private Boolean newArrival;
    private LocalDateTime promotionStartTime;
    private LocalDateTime promotionEndTime;
    
    // 统计信息
    private Double averageRating;
    private Integer totalReviews;
    private Integer salesCount;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer cartCount;
    
    // 评分分布
    private Map<Integer, Integer> ratingDistribution; // 1-5星分布
    
    // 用户评价摘要
    private List<ReviewSummaryVO> recentReviews;
    
    // 相关商品
    private List<RelatedProductVO> relatedProducts;
    
    // 推荐搭配
    private List<RecommendedComboVO> recommendedCombos;
    
    // 状态信息
    private String status;
    private String statusLabel;
    private String statusColor;
    
    // 时间信息
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime lastRestockTime;
    
    // 配送信息
    private BigDecimal deliveryFee;
    private Integer estimatedDeliveryTime; // 分钟
    private String deliveryRange;
    private Boolean freeDelivery; // 是否免配送费
    private BigDecimal freeDeliveryThreshold; // 免配送费门槛
    
    // 服务信息
    private Boolean supportTakeaway; // 支持自取
    private Boolean supportDelivery; // 支持配送
    private Boolean supportReservation; // 支持预订
    private String serviceHours; // 服务时间
    
    // 商家承诺
    private List<String> shopGuarantees; // 商家保障
    private Boolean qualityGuarantee; // 质量保证
    private Boolean freshnessGuarantee; // 新鲜保证
    private Boolean refundGuarantee; // 退款保证
    
    /**
     * 营养信息VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalInfoVO {
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
        private BigDecimal energy;       // 能量 (千焦)
        private BigDecimal calcium;      // 钙 (毫克)
        private BigDecimal iron;         // 铁 (毫克)
        
        // 每日推荐摄入量百分比
        private Integer caloriesPercent;
        private Integer proteinPercent;
        private Integer carbohydratesPercent;
        private Integer fatPercent;
        private Integer fiberPercent;
        private Integer sugarPercent;
        private Integer sodiumPercent;
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            if (calories != null) summary.append("热量: ").append(calories).append("千卡");
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
    }
    
    /**
     * 特殊饮食信息VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DietaryInfoVO {
        private Boolean vegetarian;
        private Boolean vegan;
        private Boolean glutenFree;
        private Boolean dairyFree;
        private Boolean nutFree;
        private Boolean soyFree;
        private Boolean eggFree;
        private Boolean seafoodFree;
        private String allergens;
        private String dietaryRestrictions;
        
        public List<String> getTags() {
            List<String> tags = new java.util.ArrayList<>();
            if (Boolean.TRUE.equals(vegetarian)) tags.add("素食");
            if (Boolean.TRUE.equals(vegan)) tags.add("纯素");
            if (Boolean.TRUE.equals(glutenFree)) tags.add("无麸质");
            if (Boolean.TRUE.equals(dairyFree)) tags.add("无乳制品");
            if (Boolean.TRUE.equals(nutFree)) tags.add("无坚果");
            if (Boolean.TRUE.equals(soyFree)) tags.add("无大豆");
            if (Boolean.TRUE.equals(eggFree)) tags.add("无蛋");
            if (Boolean.TRUE.equals(seafoodFree)) tags.add("无海鲜");
            return tags;
        }
    }
    
    /**
     * 评价摘要VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummaryVO {
        private Long reviewId;
        private Long userId;
        private String userName;
        private String userAvatar;
        private Integer rating;
        private String comment;
        private List<String> images;
        private LocalDateTime reviewTime;
        private Integer likes;
        private Boolean hasReply;
        private String shopReply;
        private LocalDateTime replyTime;
        
        public String getTimeAgo() {
            if (reviewTime == null) return "";
            // 简化实现，实际应该计算时间差
            return reviewTime.toString();
        }
    }
    
    /**
     * 相关商品VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedProductVO {
        private Long productId;
        private String name;
        private String image;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Boolean onSale;
        private Double rating;
        private Integer salesCount;
        private String relationType; // similar, complementary, same_shop, etc.
    }
    
    /**
     * 推荐搭配VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedComboVO {
        private String comboName;
        private String comboDescription;
        private List<ComboItemVO> items;
        private BigDecimal comboPrice;
        private BigDecimal originalTotalPrice;
        private BigDecimal saveAmount;
        private BigDecimal savePercentage;
        private Integer popularity; // 受欢迎程度
    }
    
    /**
     * 搭配商品项VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComboItemVO {
        private Long productId;
        private String name;
        private String image;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal itemTotal;
    }
    
    // 业务方法
    
    /**
     * 检查是否为促销商品
     */
    public boolean isOnSale() {
        return Boolean.TRUE.equals(onSale) && discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 计算折扣价格
     */
    public BigDecimal calculateDiscountPrice() {
        if (isOnSale() && price != null && discount != null) {
            return price.multiply(BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100"))));
        }
        return price;
    }
    
    /**
     * 计算节省金额
     */
    public BigDecimal calculateSaveAmount() {
        if (isOnSale() && price != null && originalPrice != null) {
            return originalPrice.subtract(calculateDiscountPrice());
        }
        return BigDecimal.ZERO;
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
     * 获取缩略图
     */
    public List<String> getThumbnailImages() {
        if (thumbnails != null && !thumbnails.isEmpty()) {
            return thumbnails;
        }
        if (images != null && images.size() > 1) {
            return images.subList(1, Math.min(images.size(), 5));
        }
        return new java.util.ArrayList<>();
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
    
    /**
     * 检查是否免配送费
     */
    public boolean isFreeDelivery() {
        return Boolean.TRUE.equals(freeDelivery) || 
               (freeDeliveryThreshold != null && price != null && price.compareTo(freeDeliveryThreshold) >= 0);
    }
    
    /**
     * 获取配送费显示
     */
    public String getDeliveryFeeDisplay() {
        if (isFreeDelivery()) {
            return "免配送费";
        } else if (deliveryFee != null) {
            return "¥" + deliveryFee;
        }
        return "配送费待定";
    }
    
    /**
     * 获取预计送达时间
     */
    public String getEstimatedDeliveryDisplay() {
        if (estimatedDeliveryTime != null) {
            if (estimatedDeliveryTime <= 30) {
                return estimatedDeliveryTime + "分钟";
            } else if (estimatedDeliveryTime <= 60) {
                return "约1小时";
            } else {
                return "约" + (estimatedDeliveryTime / 60) + "小时";
            }
        }
        return "时间待定";
    }
    
    /**
     * 获取保质期显示
     */
    public String getExpiryDisplay() {
        if (expiryDate != null) {
            return "保质期至: " + expiryDate.toLocalDate().toString();
        } else if (shelfLife != null) {
            return "保质期: " + shelfLife;
        }
        return "保质期信息待更新";
    }
    
    /**
     * 获取商品完整描述
     */
    public String getFullDescription() {
        StringBuilder desc = new StringBuilder();
        if (description != null) desc.append(description);
        
        if (ingredients != null) {
            desc.append("\n\n【成分】\n").append(ingredients);
        }
        
        if (cookingMethod != null) {
            desc.append("\n\n【烹饪方法】\n").append(cookingMethod);
        }
        
        if (storageInstructions != null) {
            desc.append("\n\n【存储说明】\n").append(storageInstructions);
        }
        
        return desc.toString();
    }
}