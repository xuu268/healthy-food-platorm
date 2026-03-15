package com.healthyfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "`order`")
@TableName("`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单状态常量
     */
    public static final int STATUS_PENDING = 0;      // 待确认
    public static final int STATUS_CONFIRMED = 1;    // 已确认
    public static final int STATUS_PREPARING = 2;    // 制作中
    public static final int STATUS_COMPLETED = 3;    // 已完成
    public static final int STATUS_CANCELLED = 4;    // 已取消

    /**
     * 支付状态常量
     */
    public static final int PAYMENT_PENDING = 0;     // 待支付
    public static final int PAYMENT_PAID = 1;        // 已支付
    public static final int PAYMENT_FAILED = 2;      // 支付失败

    /**
     * 订单ID
     */
    @Id
    @Column(nullable = false, length = 32)
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 商家ID
     */
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 桌号
     */
    @Column(name = "table_number", length = 20)
    private String tableNumber;

    /**
     * 订单总金额
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    @Column(name = "discount_amount", precision = 10, scale = 2, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 实付金额
     */
    @Column(name = "actual_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualAmount;

    /**
     * 支付方式
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * 支付状态 0-待支付 1-已支付 2-支付失败
     */
    @Column(name = "payment_status", columnDefinition = "tinyint default 0")
    private Integer paymentStatus = PAYMENT_PENDING;

    /**
     * 支付时间
     */
    @Column(name = "payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 支付交易号
     */
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /**
     * 订单状态 0-待确认 1-已确认 2-制作中 3-已完成 4-已取消
     */
    @Column(columnDefinition = "tinyint default 0")
    private Integer status = STATUS_PENDING;

    /**
     * 取消原因
     */
    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    /**
     * 备注
     */
    @Column(columnDefinition = "text")
    private String note;

    /**
     * 预计制作时间(分钟)
     */
    @Column(name = "estimated_time")
    private Integer estimatedTime;

    /**
     * 完成时间
     */
    @Column(name = "completed_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedTime;

    /**
     * 评分
     */
    @Column
    private Integer rating;

    /**
     * 评价
     */
    @Column(columnDefinition = "text")
    private String review;

    /**
     * 评价时间
     */
    @Column(name = "review_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @TableLogic
    @Column(columnDefinition = "tinyint default 0")
    private Integer deleted = 0;

    /**
     * 订单项列表（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private List<OrderItem> items;

    /**
     * 检查订单是否可支付
     */
    public boolean canPay() {
        return paymentStatus == PAYMENT_PENDING && 
               status != STATUS_CANCELLED && 
               status != STATUS_COMPLETED;
    }

    /**
     * 检查订单是否可取消
     */
    public boolean canCancel() {
        return status == STATUS_PENDING || 
               status == STATUS_CONFIRMED || 
               status == STATUS_PREPARING;
    }

    /**
     * 检查订单是否可评价
     */
    public boolean canReview() {
        return status == STATUS_COMPLETED && 
               paymentStatus == PAYMENT_PAID && 
               rating == null;
    }

    /**
     * 获取订单状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case STATUS_PENDING:
                return "待确认";
            case STATUS_CONFIRMED:
                return "已确认";
            case STATUS_PREPARING:
                return "制作中";
            case STATUS_COMPLETED:
                return "已完成";
            case STATUS_CANCELLED:
                return "已取消";
            default:
                return "未知";
        }
    }

    /**
     * 获取支付状态描述
     */
    public String getPaymentStatusDescription() {
        switch (paymentStatus) {
            case PAYMENT_PENDING:
                return "待支付";
            case PAYMENT_PAID:
                return "已支付";
            case PAYMENT_FAILED:
                return "支付失败";
            default:
                return "未知";
        }
    }

    /**
     * 计算优惠比例
     */
    public BigDecimal getDiscountRate() {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return discountAmount.divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 计算预计完成时间
     */
    public LocalDateTime getEstimatedCompletionTime() {
        if (estimatedTime == null || estimatedTime <= 0) {
            return null;
        }
        LocalDateTime baseTime = paymentTime != null ? paymentTime : createdAt;
        return baseTime.plusMinutes(estimatedTime);
    }

    /**
     * 检查订单是否超时
     */
    public boolean isTimeout() {
        if (status == STATUS_COMPLETED || status == STATUS_CANCELLED) {
            return false;
        }
        
        LocalDateTime estimatedCompletion = getEstimatedCompletionTime();
        if (estimatedCompletion == null) {
            return false;
        }
        
        return LocalDateTime.now().isAfter(estimatedCompletion.plusMinutes(15)); // 给15分钟缓冲
    }

    /**
     * 获取订单简略信息
     */
    public String getBriefInfo() {
        return String.format("订单%s | %s | ¥%s", 
                id.substring(0, 8), 
                getStatusDescription(), 
                actualAmount);
    }

    /**
     * 检查订单是否包含特定商品
     */
    public boolean containsProduct(Long productId) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (OrderItem item : items) {
            if (productId.equals(item.getProductId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算订单总热量
     */
    public Integer calculateTotalCalories() {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        
        int total = 0;
        for (OrderItem item : items) {
            if (item.getProduct() != null && item.getProduct().getCalories() != null) {
                total += item.getProduct().getCalories() * item.getQuantity();
            }
        }
        return total;
    }

    /**
     * 获取订单营养摘要
     */
    public String getNutritionSummary() {
        if (items == null || items.isEmpty()) {
            return "无营养信息";
        }
        
        int totalCalories = calculateTotalCalories();
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product != null) {
                int quantity = item.getQuantity();
                if (product.getProtein() != null) {
                    totalProtein = totalProtein.add(product.getProtein().multiply(BigDecimal.valueOf(quantity)));
                }
                if (product.getCarbohydrates() != null) {
                    totalCarbs = totalCarbs.add(product.getCarbohydrates().multiply(BigDecimal.valueOf(quantity)));
                }
                if (product.getFat() != null) {
                    totalFat = totalFat.add(product.getFat().multiply(BigDecimal.valueOf(quantity)));
                }
            }
        }
        
        return String.format("热量: %d卡 | 蛋白质: %.1fg | 碳水: %.1fg | 脂肪: %.1fg", 
                totalCalories, totalProtein.doubleValue(), totalCarbs.doubleValue(), totalFat.doubleValue());
    }

    /**
     * 检查订单是否来自堂食
     */
    public boolean isDineIn() {
        return tableNumber != null && !tableNumber.trim().isEmpty();
    }

    /**
     * 检查订单是否来自外卖
     */
    public boolean isTakeaway() {
        return tableNumber == null || tableNumber.trim().isEmpty();
    }

    /**
     * 获取订单类型描述
     */
    public String getOrderTypeDescription() {
        return isDineIn() ? "堂食" : "外卖";
    }

    /**
     * 生成订单二维码内容
     */
    public String generateQRCodeContent() {
        return String.format("order:%s:shop:%d:amount:%s", 
                id, shopId, actualAmount);
    }

    /**
     * 检查订单是否需要发票
     */
    public boolean needsInvoice() {
        return actualAmount != null && actualAmount.compareTo(BigDecimal.valueOf(500)) >= 0;
    }

    /**
     * 获取订单评分描述
     */
    public String getRatingDescription() {
        if (rating == null) {
            return "未评价";
        }
        
        switch (rating) {
            case 1:
                return "很差";
            case 2:
                return "较差";
            case 3:
                return "一般";
            case 4:
                return "良好";
            case 5:
                return "优秀";
            default:
                return "未知";
        }
    }

    /**
     * 检查订单是否已过期（超过24小时未支付）
     */
    public boolean isExpired() {
        if (paymentStatus == PAYMENT_PAID) {
            return false;
        }
        
        return createdAt != null && 
               createdAt.isBefore(LocalDateTime.now().minusHours(24));
    }

    /**
     * 获取订单处理进度百分比
     */
    public int getProgressPercentage() {
        switch (status) {
            case STATUS_PENDING:
                return 20;
            case STATUS_CONFIRMED:
                return 40;
            case STATUS_PREPARING:
                return 70;
            case STATUS_COMPLETED:
                return 100;
            case STATUS_CANCELLED:
                return 0;
            default:
                return 0;
        }
    }
}