package com.healthyfood.vo.order;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项视图对象VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {

    private Long itemId;
    private Long orderId;
    private String orderNumber;
    
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private String productCategory;
    
    private Long shopId;
    private String shopName;
    
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal discountPrice;
    private BigDecimal totalPrice;
    
    // 规格信息
    private String size;
    private String flavor;
    private String spiceLevel;
    private String temperature; // HOT, COLD, ROOM
    private String packaging;
    private String specialInstructions;
    
    // 营养信息
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbohydrates;
    private BigDecimal fat;
    private BigDecimal fiber;
    private BigDecimal sugar;
    private Integer sodium;
    private Double healthScore;
    
    // 状态信息
    private String status; // PENDING, PREPARING, READY, SERVED, CANCELLED
    private String statusLabel;
    private String preparationNotes;
    private LocalDateTime preparationStartTime;
    private LocalDateTime preparationCompleteTime;
    
    // 评价信息
    private Integer rating;
    private String review;
    private LocalDateTime reviewTime;
    
    // 时间信息
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 业务方法
    
    /**
     * 获取订单项状态标签
     */
    public String getStatusLabel() {
        if (status == null) return "未知";
        
        switch (status) {
            case "PENDING": return "待处理";
            case "PREPARING": return "制作中";
            case "READY": return "已就绪";
            case "SERVED": return "已上菜";
            case "CANCELLED": return "已取消";
            case "REFUNDED": return "已退款";
            default: return status;
        }
    }
    
    /**
     * 获取订单项状态颜色
     */
    public String getStatusColor() {
        if (status == null) return "default";
        
        switch (status) {
            case "PENDING": return "orange";
            case "PREPARING": return "blue";
            case "READY": return "green";
            case "SERVED": return "success";
            case "CANCELLED": return "gray";
            case "REFUNDED": return "gray";
            default: return "default";
        }
    }
    
    /**
     * 计算节省金额
     */
    public BigDecimal getSaveAmount() {
        if (originalPrice != null && discountPrice != null) {
            return originalPrice.subtract(discountPrice).multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 计算节省百分比
     */
    public BigDecimal getSavePercentage() {
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal saveAmount = getSaveAmount();
            return saveAmount.divide(originalPrice.multiply(new BigDecimal(quantity)), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 检查是否为折扣商品
     */
    public boolean isDiscounted() {
        return discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 获取营养摘要
     */
    public String getNutritionSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (calories != null) {
            summary.append("热量: ").append(calories * quantity).append("千卡");
        }
        
        if (protein != null) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("蛋白质: ").append(protein.multiply(new BigDecimal(quantity))).append("g");
        }
        
        if (carbohydrates != null) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("碳水: ").append(carbohydrates.multiply(new BigDecimal(quantity))).append("g");
        }
        
        if (fat != null) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("脂肪: ").append(fat.multiply(new BigDecimal(quantity))).append("g");
        }
        
        return summary.length() > 0 ? summary.toString() : "暂无营养信息";
    }
    
    /**
     * 获取规格摘要
     */
    public String getSpecificationSummary() {
        StringBuilder spec = new StringBuilder();
        
        if (size != null) {
            spec.append("规格: ").append(size);
        }
        
        if (flavor != null) {
            if (spec.length() > 0) spec.append(" | ");
            spec.append("口味: ").append(flavor);
        }
        
        if (spiceLevel != null) {
            if (spec.length() > 0) spec.append(" | ");
            spec.append("辣度: ").append(spiceLevel);
        }
        
        if (temperature != null) {
            if (spec.length() > 0) spec.append(" | ");
            spec.append("温度: ").append(temperature);
        }
        
        return spec.toString();
    }
    
    /**
     * 获取价格显示
     */
    public String getPriceDisplay() {
        if (isDiscounted()) {
            return String.format("¥%.2f (原价¥%.2f)", 
                    discountPrice != null ? discountPrice.doubleValue() : unitPrice.doubleValue(),
                    originalPrice != null ? originalPrice.doubleValue() : unitPrice.doubleValue());
        }
        return String.format("¥%.2f", unitPrice != null ? unitPrice.doubleValue() : 0);
    }
    
    /**
     * 获取总价显示
     */
    public String getTotalPriceDisplay() {
        if (totalPrice != null) {
            return String.format("¥%.2f", totalPrice.doubleValue());
        }
        
        if (unitPrice != null) {
            BigDecimal calculatedTotal = unitPrice.multiply(new BigDecimal(quantity));
            if (isDiscounted() && discountPrice != null) {
                calculatedTotal = discountPrice.multiply(new BigDecimal(quantity));
            }
            return String.format("¥%.2f", calculatedTotal.doubleValue());
        }
        
        return "¥0.00";
    }
    
    /**
     * 检查是否可评价
     */
    public boolean isRateable() {
        return "SERVED".equals(status) && rating == null;
    }
    
    /**
     * 检查是否可取消
     */
    public boolean isCancellable() {
        return "PENDING".equals(status) || "PREPARING".equals(status);
    }
    
    /**
     * 获取制作进度
     */
    public Integer getPreparationProgress() {
        if (status == null) return 0;
        
        switch (status) {
            case "PENDING": return 0;
            case "PREPARING": return 50;
            case "READY": return 80;
            case "SERVED": return 100;
            case "CANCELLED": return 0;
            default: return 0;
        }
    }
    
    /**
     * 获取制作时间估算
     */
    public String getPreparationTimeEstimate() {
        if (preparationStartTime == null) {
            return "等待制作";
        }
        
        if (preparationCompleteTime != null) {
            return "制作完成";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(preparationStartTime, now).toMinutes();
        
        if (minutes < 5) {
            return "刚刚开始";
        } else if (minutes < 10) {
            return "制作中 (" + minutes + "分钟)";
        } else {
            return "制作中 (" + minutes + "分钟，请稍候)";
        }
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
}