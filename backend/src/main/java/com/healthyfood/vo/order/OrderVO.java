package com.healthyfood.vo.order;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {

    private Long orderId;
    private String orderNumber;
    
    private Long userId;
    private String userName;
    private String userAvatar;
    
    private Long shopId;
    private String shopName;
    private String shopLogo;
    
    // 金额信息
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal deliveryFee;
    private BigDecimal finalAmount;
    private BigDecimal paidAmount;
    
    // 配送信息
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String contactName;
    private String contactPhone;
    private String deliveryInstructions;
    
    // 时间信息
    private LocalDateTime createTime;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime completionTime;
    private LocalDateTime cancelTime;
    
    // 状态信息
    private String status;
    private String statusLabel;
    private String statusColor;
    
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    private String paymentTransactionId;
    
    private String deliveryStatus;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    
    private String refundStatus;
    private BigDecimal refundAmount;
    private LocalDateTime refundRequestTime;
    private LocalDateTime refundTime;
    
    // 统计信息
    private Integer itemCount;
    private BigDecimal averageItemPrice;
    
    // 评价信息
    private Integer rating;
    private String review;
    private LocalDateTime reviewTime;
    
    // 操作信息
    private Boolean canCancel;
    private Boolean canRate;
    private Boolean canRefund;
    
    // 商品摘要
    private List<OrderItemSummaryVO> itemSummaries;
    
    /**
     * 订单项摘要VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemSummaryVO {
        private Long productId;
        private String productName;
        private String productImage;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String category;
    }
    
    // 业务方法
    
    /**
     * 获取订单状态标签
     */
    public String getStatusLabel() {
        if (status == null) return "未知";
        
        switch (status) {
            case "PENDING": return "待确认";
            case "CONFIRMED": return "已确认";
            case "PREPARING": return "准备中";
            case "READY_FOR_DELIVERY": return "待配送";
            case "OUT_FOR_DELIVERY": return "配送中";
            case "DELIVERED": return "已送达";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            case "REFUNDED": return "已退款";
            case "FAILED": return "失败";
            default: return status;
        }
    }
    
    /**
     * 获取订单状态颜色
     */
    public String getStatusColor() {
        if (status == null) return "default";
        
        switch (status) {
            case "PENDING": return "orange";
            case "CONFIRMED": return "blue";
            case "PREPARING": return "purple";
            case "READY_FOR_DELIVERY": return "cyan";
            case "OUT_FOR_DELIVERY": return "green";
            case "DELIVERED": return "success";
            case "COMPLETED": return "success";
            case "CANCELLED": return "gray";
            case "REFUNDED": return "gray";
            case "FAILED": return "red";
            default: return "default";
        }
    }
    
    /**
     * 获取支付状态标签
     */
    public String getPaymentStatusLabel() {
        if (paymentStatus == null) return "未知";
        
        switch (paymentStatus) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "FAILED": return "支付失败";
            case "REFUNDED": return "已退款";
            case "PARTIALLY_REFUNDED": return "部分退款";
            default: return paymentStatus;
        }
    }
    
    /**
     * 获取配送状态标签
     */
    public String getDeliveryStatusLabel() {
        if (deliveryStatus == null) return "未知";
        
        switch (deliveryStatus) {
            case "PENDING": return "待接单";
            case "ACCEPTED": return "已接单";
            case "PICKED_UP": return "已取货";
            case "OUT_FOR_DELIVERY": return "配送中";
            case "DELIVERED": return "已送达";
            case "FAILED": return "配送失败";
            case "CANCELLED": return "已取消";
            default: return deliveryStatus;
        }
    }
    
    /**
     * 检查订单是否完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "DELIVERED".equals(status);
    }
    
    /**
     * 检查订单是否可取消
     */
    public boolean isCancellable() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status) || "PREPARING".equals(status);
    }
    
    /**
     * 检查订单是否可评价
     */
    public boolean isRateable() {
        return isCompleted() && rating == null;
    }
    
    /**
     * 检查订单是否可退款
     */
    public boolean isRefundable() {
        return ("PAID".equals(paymentStatus) || "COMPLETED".equals(status)) && 
               !"REFUNDED".equals(refundStatus) && 
               !"CANCELLED".equals(status);
    }
    
    /**
     * 获取订单时间显示
     */
    public String getTimeDisplay() {
        if (createTime == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(createTime, now).toHours();
        
        if (hours < 1) {
            long minutes = java.time.Duration.between(createTime, now).toMinutes();
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else {
            long days = hours / 24;
            return days + "天前";
        }
    }
    
    /**
     * 获取预计送达时间显示
     */
    public String getEstimatedDeliveryDisplay() {
        if (estimatedDeliveryTime == null) return "时间待定";
        
        if (actualDeliveryTime != null) {
            return "已送达";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (estimatedDeliveryTime.isBefore(now)) {
            return "即将送达";
        }
        
        long minutes = java.time.Duration.between(now, estimatedDeliveryTime).toMinutes();
        if (minutes <= 30) {
            return minutes + "分钟后送达";
        } else if (minutes <= 60) {
            return "约1小时内送达";
        } else {
            return "约" + (minutes / 60) + "小时后送达";
        }
    }
    
    /**
     * 获取订单金额摘要
     */
    public String getAmountSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("¥").append(finalAmount.setScale(2, java.math.BigDecimal.ROUND_HALF_UP));
        
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            summary.append(" (已优惠¥").append(discountAmount.setScale(2, java.math.BigDecimal.ROUND_HALF_UP)).append(")");
        }
        
        if (deliveryFee != null && deliveryFee.compareTo(BigDecimal.ZERO) > 0) {
            summary.append(" 含配送费¥").append(deliveryFee.setScale(2, java.math.BigDecimal.ROUND_HALF_UP));
        }
        
        return summary.toString();
    }
    
    /**
     * 获取商品数量摘要
     */
    public String getItemCountSummary() {
        if (itemCount == null || itemCount == 0) {
            return "无商品";
        }
        
        if (itemSummaries != null && !itemSummaries.isEmpty()) {
            if (itemSummaries.size() == 1) {
                return itemSummaries.get(0).getProductName() + " ×" + itemSummaries.get(0).getQuantity();
            } else {
                return itemSummaries.get(0).getProductName() + " 等" + itemCount + "件商品";
            }
        }
        
        return itemCount + "件商品";
    }
    
    /**
     * 获取支付方式显示
     */
    public String getPaymentMethodDisplay() {
        if (paymentMethod == null) return "未知";
        
        switch (paymentMethod) {
            case "WECHAT_PAY": return "微信支付";
            case "ALIPAY": return "支付宝";
            case "CASH": return "现金";
            case "BALANCE": return "余额支付";
            case "CARD": return "银行卡";
            default: return paymentMethod;
        }
    }
    
    /**
     * 检查是否有退款
     */
    public boolean hasRefund() {
        return refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 获取退款显示
     */
    public String getRefundDisplay() {
        if (!hasRefund()) return "";
        
        if ("REFUNDED".equals(refundStatus)) {
            return "已退款 ¥" + refundAmount.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
        } else if ("REQUESTED".equals(refundStatus)) {
            return "退款申请中 ¥" + refundAmount.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
        } else if ("APPROVED".equals(refundStatus)) {
            return "退款已批准 ¥" + refundAmount.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
        }
        
        return "";
    }
}