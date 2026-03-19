package com.healthyfood.vo.order;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 订单更新请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {

    // 配送信息更新
    @Size(max = 200, message = "配送地址长度不能超过200个字符")
    private String deliveryAddress;

    private Double deliveryLatitude;
    private Double deliveryLongitude;

    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    private String contactName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    @Size(max = 500, message = "配送说明长度不能超过500个字符")
    private String deliveryInstructions;

    // 订单状态更新
    private String status; // PENDING, CONFIRMED, PREPARING, etc.
    private String paymentStatus; // PENDING, PAID, FAILED, REFUNDED
    private String deliveryStatus; // PENDING, PICKED_UP, DELIVERED, FAILED

    // 支付信息更新
    private String paymentMethod;
    private String paymentTransactionId;
    private LocalDateTime paymentTime;

    // 配送信息更新
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;

    // 取消或退款信息
    private String cancelReason;
    private String refundReason;
    private String refundAmount;
    private String refundProcessor;

    // 备注信息
    @Size(max = 1000, message = "备注长度不能超过1000个字符")
    private String remark;

    // 操作者信息
    private String operatorId;
    private String operatorName;
    private String operatorRole; // USER, SHOP, ADMIN, SYSTEM

    /**
     * 检查是否有更新
     */
    public boolean hasUpdates() {
        return deliveryAddress != null || deliveryLatitude != null || deliveryLongitude != null ||
               contactName != null || contactPhone != null || deliveryInstructions != null ||
               status != null || paymentStatus != null || deliveryStatus != null ||
               paymentMethod != null || paymentTransactionId != null || paymentTime != null ||
               deliveryPersonId != null || deliveryPersonName != null || deliveryPersonPhone != null ||
               estimatedDeliveryTime != null || actualDeliveryTime != null ||
               cancelReason != null || refundReason != null || refundAmount != null || refundProcessor != null ||
               remark != null || operatorId != null || operatorName != null || operatorRole != null;
    }
    
    /**
     * 验证更新数据
     */
    public void validate() {
        if (contactPhone != null && !contactPhone.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("联系电话格式不正确");
        }
        
        if (deliveryLatitude != null && (deliveryLatitude < -90 || deliveryLatitude > 90)) {
            throw new IllegalArgumentException("纬度必须在-90到90之间");
        }
        
        if (deliveryLongitude != null && (deliveryLongitude < -180 || deliveryLongitude > 180)) {
            throw new IllegalArgumentException("经度必须在-180到180之间");
        }
        
        if (refundAmount != null) {
            try {
                java.math.BigDecimal amount = new java.math.BigDecimal(refundAmount);
                if (amount.compareTo(java.math.BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("退款金额不能为负数");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("退款金额格式不正确");
            }
        }
    }
    
    /**
     * 获取更新类型
     */
    public String getUpdateType() {
        if (status != null) return "STATUS_UPDATE";
        if (paymentStatus != null) return "PAYMENT_UPDATE";
        if (deliveryStatus != null) return "DELIVERY_UPDATE";
        if (deliveryAddress != null || contactName != null || contactPhone != null) return "DELIVERY_INFO_UPDATE";
        if (cancelReason != null) return "CANCEL_UPDATE";
        if (refundReason != null || refundAmount != null) return "REFUND_UPDATE";
        if (remark != null) return "REMARK_UPDATE";
        return "OTHER_UPDATE";
    }
    
    /**
     * 获取更新摘要
     */
    public String getUpdateSummary() {
        java.util.List<String> updates = new java.util.ArrayList<>();
        
        if (status != null) updates.add("状态: " + status);
        if (paymentStatus != null) updates.add("支付状态: " + paymentStatus);
        if (deliveryStatus != null) updates.add("配送状态: " + deliveryStatus);
        if (deliveryAddress != null) updates.add("配送地址更新");
        if (contactName != null) updates.add("联系人更新");
        if (contactPhone != null) updates.add("联系电话更新");
        if (cancelReason != null) updates.add("取消订单");
        if (refundReason != null) updates.add("申请退款");
        if (remark != null) updates.add("添加备注");
        
        return String.join("; ", updates);
    }
    
    /**
     * 检查是否为配送信息更新
     */
    public boolean isDeliveryInfoUpdate() {
        return deliveryAddress != null || deliveryLatitude != null || deliveryLongitude != null ||
               contactName != null || contactPhone != null || deliveryInstructions != null;
    }
    
    /**
     * 检查是否为状态更新
     */
    public boolean isStatusUpdate() {
        return status != null || paymentStatus != null || deliveryStatus != null;
    }
    
    /**
     * 检查是否为支付更新
     */
    public boolean isPaymentUpdate() {
        return paymentStatus != null || paymentMethod != null || paymentTransactionId != null || paymentTime != null;
    }
    
    /**
     * 检查是否为配送更新
     */
    public boolean isDeliveryUpdate() {
        return deliveryStatus != null || deliveryPersonId != null || deliveryPersonName != null ||
               deliveryPersonPhone != null || estimatedDeliveryTime != null || actualDeliveryTime != null;
    }
    
    /**
     * 检查是否为取消更新
     */
    public boolean isCancelUpdate() {
        return cancelReason != null;
    }
    
    /**
     * 检查是否为退款更新
     */
    public boolean isRefundUpdate() {
        return refundReason != null || refundAmount != null || refundProcessor != null;
    }
    
    /**
     * 获取操作者信息
     */
    public String getOperatorInfo() {
        if (operatorName != null && operatorRole != null) {
            return operatorName + " (" + operatorRole + ")";
        } else if (operatorName != null) {
            return operatorName;
        } else if (operatorId != null) {
            return "用户" + operatorId;
        }
        return "系统";
    }
    
    /**
     * 验证地理位置信息
     */
    public boolean hasValidLocation() {
        if (deliveryLatitude == null || deliveryLongitude == null) {
            return false;
        }
        return deliveryLatitude >= -90 && deliveryLatitude <= 90 &&
               deliveryLongitude >= -180 && deliveryLongitude <= 180;
    }
    
    /**
     * 获取位置坐标
     */
    public String getLocationCoordinates() {
        if (hasValidLocation()) {
            return String.format("%.6f,%.6f", deliveryLatitude, deliveryLongitude);
        }
        return null;
    }
    
    /**
     * 检查是否需要通知用户
     */
    public boolean requiresUserNotification() {
        return status != null || paymentStatus != null || deliveryStatus != null ||
               cancelReason != null || refundReason != null;
    }
    
    /**
     * 检查是否需要通知商家
     */
    public boolean requiresShopNotification() {
        return status != null || deliveryStatus != null || cancelReason != null || refundReason != null;
    }
    
    /**
     * 获取通知消息
     */
    public String getNotificationMessage() {
        if (status != null) {
            return "订单状态已更新为: " + status;
        } else if (paymentStatus != null) {
            return "支付状态已更新为: " + paymentStatus;
        } else if (deliveryStatus != null) {
            return "配送状态已更新为: " + deliveryStatus;
        } else if (cancelReason != null) {
            return "订单已取消: " + cancelReason;
        } else if (refundReason != null) {
            return "退款申请: " + refundReason;
        }
        return "订单信息已更新";
    }
}