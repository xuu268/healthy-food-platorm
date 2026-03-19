package com.healthyfood.vo.order;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单详情视图对象VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {

    // 订单基本信息
    private Long orderId;
    private String orderNumber;
    private String orderType; // NORMAL, GROUP, RESERVATION, etc.
    
    // 用户信息
    private Long userId;
    private String userName;
    private String userAvatar;
    private String userPhone;
    private String userEmail;
    
    // 商家信息
    private Long shopId;
    private String shopName;
    private String shopLogo;
    private String shopDescription;
    private String shopAddress;
    private String shopPhone;
    private String shopContactPerson;
    private Double shopRating;
    private Integer shopTotalOrders;
    
    // 金额详情
    private BigDecimal subtotalAmount;
    private BigDecimal discountAmount;
    private BigDecimal couponDiscount;
    private BigDecimal promotionDiscount;
    private BigDecimal deliveryFee;
    private BigDecimal packagingFee;
    private BigDecimal serviceFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;
    private BigDecimal paidAmount;
    
    // 优惠信息
    private String couponCode;
    private String couponName;
    private BigDecimal couponValue;
    private String promotionCode;
    private String promotionName;
    private BigDecimal promotionValue;
    
    // 配送详情
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private String contactName;
    private String contactPhone;
    private String deliveryInstructions;
    private String deliveryNotes;
    
    // 配送时间
    private LocalDateTime deliveryRequestTime;
    private LocalDateTime deliveryAcceptedTime;
    private LocalDateTime deliveryPickupTime;
    private LocalDateTime deliveryStartTime;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime deliveryCompletionTime;
    
    // 配送人员
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String deliveryPersonAvatar;
    private Double deliveryPersonRating;
    
    // 配送轨迹
    private List<DeliveryTrackVO> deliveryTracks;
    
    // 支付详情
    private String paymentMethod;
    private String paymentGateway;
    private String paymentTransactionId;
    private String paymentReference;
    private LocalDateTime paymentRequestTime;
    private LocalDateTime paymentCompletionTime;
    private LocalDateTime paymentConfirmationTime;
    private String paymentStatus;
    private String paymentErrorCode;
    private String paymentErrorMessage;
    
    // 退款详情
    private String refundStatus;
    private BigDecimal refundAmount;
    private String refundReason;
    private String refundProcessor;
    private LocalDateTime refundRequestTime;
    private LocalDateTime refundProcessTime;
    private LocalDateTime refundCompletionTime;
    private String refundTransactionId;
    private String refundNotes;
    
    // 订单状态
    private String status;
    private String statusHistory; // JSON格式的状态变更历史
    
    // 时间线
    private LocalDateTime createTime;
    private LocalDateTime confirmTime;
    private LocalDateTime preparingTime;
    private LocalDateTime readyTime;
    private LocalDateTime completionTime;
    private LocalDateTime cancelTime;
    private LocalDateTime lastUpdateTime;
    
    // 取消信息
    private String cancelReason;
    private String cancelBy; // USER, SHOP, SYSTEM
    private LocalDateTime cancelTime;
    
    // 商品详情
    private List<OrderItemDetailVO> items;
    private Integer totalItemCount;
    private BigDecimal averageItemPrice;
    
    // 营养统计
    private Integer totalCalories;
    private BigDecimal totalProtein;
    private BigDecimal totalCarbohydrates;
    private BigDecimal totalFat;
    private BigDecimal totalFiber;
    private BigDecimal totalSugar;
    private Integer totalSodium;
    private Double averageHealthScore;
    
    // 评价信息
    private Integer rating;
    private String review;
    private List<String> reviewImages;
    private LocalDateTime reviewTime;
    private Integer reviewLikes;
    private String shopReply;
    private LocalDateTime shopReplyTime;
    
    // 操作权限
    private Boolean canCancel;
    private Boolean canRate;
    private Boolean canRefund;
    private Boolean canContactShop;
    private Boolean canContactDelivery;
    private Boolean canReorder;
    
    // 商家操作
    private Boolean canAccept;
    private Boolean canReject;
    private Boolean canPrepare;
    private Boolean canReady;
    private Boolean canAssignDelivery;
    private Boolean canUpdateStatus;
    
    // 配送操作
    private Boolean canAcceptDelivery;
    private Boolean canPickup;
    private Boolean canStartDelivery;
    private Boolean canCompleteDelivery;
    private Boolean canReportIssue;
    
    // 订单日志
    private List<OrderLogVO> orderLogs;
    
    // 通知记录
    private List<OrderNotificationVO> notifications;
    
    // 相关订单
    private List<RelatedOrderVO> relatedOrders;
    
    // 统计信息
    private Map<String, Object> statistics;
    
    /**
     * 配送轨迹VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryTrackVO {
        private LocalDateTime timestamp;
        private String location;
        private Double latitude;
        private Double longitude;
        private String status;
        private String description;
        private String operator;
        
        public String getTimeAgo() {
            if (timestamp == null) return "";
            // 简化实现
            return timestamp.toString();
        }
    }
    
    /**
     * 订单项详情VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDetailVO {
        private Long itemId;
        private Long productId;
        private String productName;
        private String productDescription;
        private List<String> productImages;
        private String productCategory;
        
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal originalPrice;
        private BigDecimal discount;
        private BigDecimal discountPrice;
        private BigDecimal totalPrice;
        
        // 规格选项
        private String size;
        private String flavor;
        private String spiceLevel;
        private String temperature; // HOT, COLD, ROOM
        private List<String> toppings;
        private List<String> removals; // 不要的配料
        
        // 营养信息
        private Integer calories;
        private BigDecimal protein;
        private BigDecimal carbohydrates;
        private BigDecimal fat;
        private BigDecimal fiber;
        private BigDecimal sugar;
        private Integer sodium;
        private Double healthScore;
        
        // 特殊要求
        private String specialInstructions;
        private String packagingRequest;
        
        // 状态
        private String status; // PENDING, PREPARING, READY, SERVED
        private String preparationNotes;
        
        // 评价
        private Integer itemRating;
        private String itemReview;
        
        public BigDecimal getSaveAmount() {
            if (originalPrice != null && discountPrice != null) {
                return originalPrice.subtract(discountPrice).multiply(new BigDecimal(quantity));
            }
            return BigDecimal.ZERO;
        }
        
        public String getNutritionSummary() {
            StringBuilder summary = new StringBuilder();
            if (calories != null) summary.append("热量: ").append(calories).append("千卡");
            if (protein != null) {
                if (summary.length() > 0) summary.append(" | ");
                summary.append("蛋白质: ").append(protein).append("g");
            }
            return summary.toString();
        }
    }
    
    /**
     * 订单日志VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLogVO {
        private Long logId;
        private LocalDateTime logTime;
        private String action;
        private String description;
        private String operatorId;
        private String operatorName;
        private String operatorRole;
        private String ipAddress;
        private String userAgent;
        private Map<String, Object> details;
        
        public String getTimeAgo() {
            if (logTime == null) return "";
            // 简化实现
            return logTime.toString();
        }
    }
    
    /**
     * 订单通知VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderNotificationVO {
        private Long notificationId;
        private String notificationType;
        private String title;
        private String content;
        private LocalDateTime sendTime;
        private LocalDateTime readTime;
        private String recipient; // USER, SHOP, DELIVERY
        private Boolean isRead;
        private String actionUrl;
        private Map<String, Object> metadata;
    }
    
    /**
     * 相关订单VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedOrderVO {
        private Long orderId;
        private String orderNumber;
        private String relationType; // SAME_USER, SAME_SHOP, SIMILAR_ITEMS, etc.
        private LocalDateTime orderTime;
        private BigDecimal orderAmount;
        private String orderStatus;
    }
    
    // 业务方法
    
    /**
     * 获取订单状态显示
     */
    public String getStatusDisplay() {
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
     * 获取订单状态进度
     */
    public Integer getStatusProgress() {
        if (status == null) return 0;
        
        switch (status) {
            case "PENDING": return 10;
            case "CONFIRMED": return 20;
            case "PREPARING": return 40;
            case "READY_FOR_DELIVERY": return 60;
            case "OUT_FOR_DELIVERY": return 80;
            case "DELIVERED": return 90;
            case "COMPLETED": return 100;
            case "CANCELLED": return 0;
            case "REFUNDED": return 0;
            case "FAILED": return 0;
            default: return 0;
        }
    }
    
    /**
     * 获取状态时间线
     */
    public List<StatusTimelineVO> getStatusTimeline() {
        List<StatusTimelineVO> timeline = new java.util.ArrayList<>();
        
        if (createTime != null) {
            timeline.add(StatusTimelineVO.builder()
                    .status("创建订单")
                    .time(createTime)
                    .completed(true)
                    .build());
        }
        
        if (confirmTime != null) {
            timeline.add(StatusTimelineVO.builder()
                    .status("商家确认")
                    .time(confirmTime)
                    .completed(true)
                    .build());
        }
        
        if (preparingTime != null) {
            timeline.add(StatusTimelineVO.builder()
                    .status("准备中")
                    .time(preparingTime)
                    .completed(true)
                    .build());
        }
        
        if (readyTime != null) {
            timeline.add(StatusTimelineVO.builder()
                    .status("待配送")
                    .time(readyTime)
                    .completed(true)
                    .build());
        }
        
        if (deliveryPickupTime != null) {
            timeline.add(StatusTimelineVO.builder()
                    .status("已取货")
                    .time(deliveryPickupTime)
                    .completed(true)
                    .build());
        }
        
        if (actualDeliveryTime != null) {
            timeline.add(StatusTimelineVO.builder()
                    .status("已送达")
                    .time(actualDeliveryTime)
                    .completed(true)
                    .build());
        }
        
        if (completionTime != null) {
            timeline.add(StatusTimelineVO.builder()
                    .status("已完成")
                    .time(completionTime)
                    .completed(true)
                    .build());
        }
        
        // 添加当前状态
        String currentStatus = getStatusDisplay();
        if (!timeline.isEmpty() && !timeline.get(timeline.size() - 1).getStatus().equals(currentStatus)) {
            timeline.add(StatusTimelineVO.builder()
                    .status(currentStatus)
                    .time(LocalDateTime.now())
                    .completed(false)
                    .current(true)
                    .build());
        }
        
        return timeline;
    }
    
    /**
     * 状态时间线VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusTimelineVO {
        private String status;
        private LocalDateTime time;
        private Boolean completed;
        private Boolean current;
        
        public String getTimeDisplay() {
            if (time == null) return "";
            return time.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
    }
    
    /**
     * 计算总节省金额
     */
    public BigDecimal getTotalSaveAmount() {
        BigDecimal save = BigDecimal.ZERO;
        if (discountAmount != null) save = save.add(discountAmount);
        if (couponDiscount != null) save = save.add(couponDiscount);
        if (promotionDiscount != null) save = save.add(promotionDiscount);
        return save;
    }
    
    /**
     * 获取配送费显示
     */
    public String getDeliveryFeeDisplay() {
        if (deliveryFee == null || deliveryFee.compareTo(BigDecimal.ZERO) == 0) {
            return "免配送费";
        }
        return "¥" + deliveryFee.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 获取预计送达时间显示
     */
    public String getEstimatedDeliveryDisplay() {
        if (estimatedDeliveryTime == null) return "时间待定";
        
        if (actualDeliveryTime != null) {
            return "已送达 " + actualDeliveryTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (estimatedDeliveryTime.isBefore(now)) {
            return "即将送达";
        }
        
        long minutes = java.time.Duration.between(now, estimatedDeliveryTime).toMinutes();
        if (minutes <= 30) {
            return minutes + "分钟后";
        } else if (minutes <= 60) {
            return "约1小时内";
        } else {
            return "约" + (minutes / 60) + "小时后";
        }
    }
    
    /**
     * 检查订单是否超时
     */
    public boolean isOverdue() {
        if (estimatedDeliveryTime == null) return false;
        
        LocalDateTime now = LocalDateTime.now();
        return estimatedDeliveryTime.isBefore(now) && 
               !"DELIVERED".equals(status) && 
               !"COMPLETED".equals(status) &&
               !"CANCELLED".equals(status);
    }
    
    /**
     * 获取超时时间
     */
    public Long getOverdueMinutes() {
        if (!isOverdue()) return 0L;
        
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(estimatedDeliveryTime, now).toMinutes();
    }
    
    /**
     * 获取订单摘要
     */
    public String getOrderSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("订单号: ").append(orderNumber);
        
        if (items != null && !items.isEmpty()) {
            summary.append(" | ").append(items.size()).append("种商品");
        }
        
        if (finalAmount != null) {
            summary.append(" | 总价: ¥").append(finalAmount.setScale(2, java.math.BigDecimal.ROUND_HALF_UP));
        }
        
        return summary.toString();
    }
    
    /**
     * 获取营养摘要
     */
    public String getNutritionSummary() {
        if (totalCalories == null) return "暂无营养信息";
        
        StringBuilder summary = new StringBuilder();
        summary.append("总热量: ").append(totalCalories).append("千卡");
        
        if (totalProtein != null) {
            summary.append(" | 蛋白质: ").append(totalProtein).append("g");
        }
        
        if (totalCarbohydrates != null) {
            summary.append(" | 碳水: ").append(totalCarbohydrates).append("g");
        }
        
        if (totalFat != null) {
            summary.append(" | 脂肪: ").append(totalFat).append("g");
        }
        
        return summary.toString();
    }
}