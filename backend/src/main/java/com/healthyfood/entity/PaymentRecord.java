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

/**
 * 支付记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "payment_record")
@TableName("payment_record")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付方式常量
     */
    public static final String METHOD_WECHAT = "wechat";          // 微信支付
    public static final String METHOD_ALIPAY = "alipay";          // 支付宝
    public static final String METHOD_UNIONPAY = "unionpay";      // 银联
    public static final String METHOD_CASH = "cash";              // 现金
    public static final String METHOD_BALANCE = "balance";        // 余额支付
    public static final String METHOD_POINTS = "points";          // 积分支付

    /**
     * 支付状态常量
     */
    public static final int STATUS_PENDING = 0;       // 待支付
    public static final int STATUS_SUCCESS = 1;       // 支付成功
    public static final int STATUS_FAILED = 2;        // 支付失败
    public static final int STATUS_REFUNDED = 3;      // 已退款
    public static final int STATUS_CLOSED = 4;        // 已关闭

    /**
     * 货币常量
     */
    public static final String CURRENCY_CNY = "CNY";  // 人民币
    public static final String CURRENCY_USD = "USD";  // 美元

    /**
     * 支付记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @Column(name = "order_id", nullable = false, length = 32)
    private String orderId;

    /**
     * 支付方式
     */
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    /**
     * 交易号
     */
    @Column(name = "transaction_id", length = 100, unique = true)
    private String transactionId;

    /**
     * 支付金额
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 货币
     */
    @Column(length = 10, columnDefinition = "varchar(10) default 'CNY'")
    private String currency = CURRENCY_CNY;

    /**
     * 支付状态 0-待支付 1-支付成功 2-支付失败 3-退款 4-已关闭
     */
    @Column(nullable = false, columnDefinition = "tinyint default 0")
    private Integer status = STATUS_PENDING;

    /**
     * 支付者ID
     */
    @Column(name = "payer_id")
    private Long payerId;

    /**
     * 支付者信息
     */
    @Column(name = "payer_info", columnDefinition = "json")
    private String payerInfo;

    /**
     * 支付时间
     */
    @Column(name = "payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 支付通知数据
     */
    @Column(name = "notify_data", columnDefinition = "text")
    private String notifyData;

    /**
     * 退款金额
     */
    @Column(name = "refund_amount", precision = 10, scale = 2, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal refundAmount = BigDecimal.ZERO;

    /**
     * 退款时间
     */
    @Column(name = "refund_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

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
     * 订单信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private Order order;

    /**
     * 支付者信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private User payer;

    /**
     * 检查支付是否成功
     */
    public boolean isSuccess() {
        return status != null && status == STATUS_SUCCESS;
    }

    /**
     * 检查支付是否失败
     */
    public boolean isFailed() {
        return status != null && status == STATUS_FAILED;
    }

    /**
     * 检查支付是否待处理
     */
    public boolean isPending() {
        return status != null && status == STATUS_PENDING;
    }

    /**
     * 检查是否已退款
     */
    public boolean isRefunded() {
        return status != null && status == STATUS_REFUNDED;
    }

    /**
     * 检查是否已关闭
     */
    public boolean isClosed() {
        return status != null && status == STATUS_CLOSED;
    }

    /**
     * 获取支付方式描述
     */
    public String getPaymentMethodDescription() {
        switch (paymentMethod) {
            case METHOD_WECHAT:
                return "微信支付";
            case METHOD_ALIPAY:
                return "支付宝";
            case METHOD_UNIONPAY:
                return "银联支付";
            case METHOD_CASH:
                return "现金支付";
            case METHOD_BALANCE:
                return "余额支付";
            case METHOD_POINTS:
                return "积分支付";
            default:
                return paymentMethod;
        }
    }

    /**
     * 获取支付方式图标
     */
    public String getPaymentMethodIcon() {
        switch (paymentMethod) {
            case METHOD_WECHAT:
                return "💬";
            case METHOD_ALIPAY:
                return "💰";
            case METHOD_UNIONPAY:
                return "💳";
            case METHOD_CASH:
                return "💵";
            case METHOD_BALANCE:
                return "⚖️";
            case METHOD_POINTS:
                return "⭐";
            default:
                return "💸";
        }
    }

    /**
     * 获取支付状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case STATUS_PENDING:
                return "待支付";
            case STATUS_SUCCESS:
                return "支付成功";
            case STATUS_FAILED:
                return "支付失败";
            case STATUS_REFUNDED:
                return "已退款";
            case STATUS_CLOSED:
                return "已关闭";
            default:
                return "未知";
        }
    }

    /**
     * 获取支付状态颜色（用于UI显示）
     */
    public String getStatusColor() {
        switch (status) {
            case STATUS_PENDING:
                return "orange";
            case STATUS_SUCCESS:
                return "green";
            case STATUS_FAILED:
                return "red";
            case STATUS_REFUNDED:
                return "blue";
            case STATUS_CLOSED:
                return "gray";
            default:
                return "black";
        }
    }

    /**
     * 获取货币符号
     */
    public String getCurrencySymbol() {
        switch (currency) {
            case CURRENCY_CNY:
                return "¥";
            case CURRENCY_USD:
                return "$";
            default:
                return currency;
        }
    }

    /**
     * 获取格式化金额
     */
    public String getFormattedAmount() {
        return getCurrencySymbol() + amount.setScale(2).toString();
    }

    /**
     * 获取格式化退款金额
     */
    public String getFormattedRefundAmount() {
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "¥0.00";
        }
        return getCurrencySymbol() + refundAmount.setScale(2).toString();
    }

    /**
     * 计算实际支付金额（支付金额 - 退款金额）
     */
    public BigDecimal getActualPaidAmount() {
        return amount.subtract(refundAmount);
    }

    /**
     * 获取格式化实际支付金额
     */
    public String getFormattedActualPaidAmount() {
        return getCurrencySymbol() + getActualPaidAmount().setScale(2).toString();
    }

    /**
     * 计算退款比例
     */
    public BigDecimal getRefundRatio() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return refundAmount.divide(amount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 检查是否全额退款
     */
    public boolean isFullRefund() {
        return refundAmount != null && refundAmount.compareTo(amount) == 0;
    }

    /**
     * 检查是否部分退款
     */
    public boolean isPartialRefund() {
        return refundAmount != null && 
               refundAmount.compareTo(BigDecimal.ZERO) > 0 && 
               refundAmount.compareTo(amount) < 0;
    }

    /**
     * 获取支付记录摘要
     */
    public String getRecordSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPaymentMethodIcon()).append(" ");
        sb.append(getPaymentMethodDescription()).append("\n");
        sb.append("💰 金额: ").append(getFormattedAmount()).append("\n");
        sb.append("📊 状态: ").append(getStatusDescription());
        
        if (paymentTime != null) {
            sb.append("\n⏰ 时间: ").append(paymentTime.toLocalTime());
        }
        
        if (isRefunded() && refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("\n↩️ 退款: ").append(getFormattedRefundAmount());
            if (refundTime != null) {
                sb.append(" (").append(refundTime.toLocalDate()).append(")");
            }
        }
        
        return sb.toString();
    }

    /**
     * 获取支付详情
     */
    public String getPaymentDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("订单号: ").append(orderId).append("\n");
        sb.append("支付方式: ").append(getPaymentMethodDescription()).append("\n");
        sb.append("支付金额: ").append(getFormattedAmount()).append("\n");
        sb.append("支付状态: ").append(getStatusDescription()).append("\n");
        
        if (transactionId != null) {
            sb.append("交易号: ").append(transactionId).append("\n");
        }
        
        if (paymentTime != null) {
            sb.append("支付时间: ").append(paymentTime).append("\n");
        }
        
        if (isRefunded() && refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("退款金额: ").append(getFormattedRefundAmount()).append("\n");
            if (refundTime != null) {
                sb.append("退款时间: ").append(refundTime).append("\n");
            }
        }
        
        sb.append("实际支付: ").append(getFormattedActualPaidAmount());
        
        return sb.toString();
    }

    /**
     * 检查支付是否超时（创建后30分钟未支付）
     */
    public boolean isTimeout() {
        if (isSuccess() || isFailed() || isClosed()) {
            return false;
        }
        
        return createdAt != null && 
               createdAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    /**
     * 获取支付超时剩余时间（秒）
     */
    public Long getTimeoutRemainingSeconds() {
        if (!isPending() || createdAt == null) {
            return 0L;
        }
        
        LocalDateTime timeoutAt = createdAt.plusMinutes(30);
        if (LocalDateTime.now().isAfter(timeoutAt)) {
            return 0L;
        }
        
        return java.time.Duration.between(LocalDateTime.now(), timeoutAt).getSeconds();
    }

    /**
     * 获取支付超时描述
     */
    public String getTimeoutDescription() {
        if (!isPending()) {
            return "无需超时检查";
        }
        
        Long remainingSeconds = getTimeoutRemainingSeconds();
        if (remainingSeconds <= 0) {
            return "已超时";
        }
        
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;
        
        if (minutes > 0) {
            return String.format("%d分%d秒后超时", minutes, seconds);
        } else {
            return String.format("%d秒后超时", seconds);
        }
    }

    /**
     * 检查是否可以退款
     */
    public boolean canRefund() {
        // 只有支付成功的订单可以退款
        if (!isSuccess()) {
            return false;
        }
        
        // 不能重复退款
        if (isRefunded() && isFullRefund()) {
            return false;
        }
        
        // 支付时间不能超过30天（简化规则）
        if (paymentTime != null && 
            paymentTime.isBefore(LocalDateTime.now().minusDays(30))) {
            return false;
        }
        
        return true;
    }

    /**
     * 检查是否可以关闭
     */
    public boolean canClose() {
        return isPending() && !isTimeout();
    }

    /**
     * 检查是否可以重新支付
     */
    public boolean canRetry() {
        return isFailed() || (isPending() && isTimeout());
    }

    /**
     * 执行退款
     */
    public RefundResult refund(BigDecimal refundAmount, String reason) {
        RefundResult result = new RefundResult();
        
        if (!canRefund()) {
            result.setSuccess(false);
            result.setMessage("当前支付记录不支持退款");
            return result;
        }
        
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            result.setSuccess(false);
            result.setMessage("退款金额必须大于0");
            return result;
        }
        
        if (refundAmount.compareTo(amount.subtract(this.refundAmount)) > 0) {
            result.setSuccess(false);
            result.setMessage("退款金额不能超过可退金额");
            return result;
        }
        
        // 执行退款
        this.refundAmount = this.refundAmount.add(refundAmount);
        this.refundTime = LocalDateTime.now();
        
        // 如果全额退款，更新状态为已退款
        if (this.refundAmount.compareTo(amount) == 0) {
            this.status = STATUS_REFUNDED;
        }
        
        result.setSuccess(true);
        result.setMessage("退款成功");
        result.setRefundAmount(refundAmount);
        result.setRefundTime(this.refundTime);
        
        return result;
    }

    /**
     * 退款结果类
     */
    @Data
    public static class RefundResult {
        private boolean success;
        private String message;
        private BigDecimal refundAmount;
        private LocalDateTime refundTime;
    }

    /**
     * 验证支付记录数据
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        if (orderId == null || orderId.trim().isEmpty()) {
            result.addError("订单ID不能为空");
        }
        
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            result.addError("支付方式不能为空");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("支付金额必须大于0");
        }
        
        if (currency == null || currency.trim().isEmpty()) {
            result.addError("货币不能为空");
        }
        
        if (status == null) {
            result.addError("支付状态不能为空");
        } else if (status < STATUS_PENDING || status > STATUS_CLOSED) {
            result.addError("无效的支付状态");
        }
        
        // 如果支付成功，必须有支付时间和交易号
        if (isSuccess()) {
            if (paymentTime == null) {
                result.addError("支付成功必须包含支付时间");
            }
            if (transactionId == null || transactionId.trim().isEmpty()) {
                result.addError("支付成功必须包含交易号");
            }
        }
        
        // 如果已退款，必须有退款时间和退款金额
        if (isRefunded()) {
            if (refundTime == null) {
                result.addError("退款必须包含退款时间");
            }
            if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
                result.addError("退款必须包含退款金额");
            }
        }
        
        return result;
    }

    /**
     * 验证结果类
     */
    @Data
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();
        
        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }
        
        public String getErrorMessage() {
            return errors.toString();
        }
    }

    /**
     * 获取支付渠道类型
     */
    public String getPaymentChannel() {
        switch (paymentMethod) {
            case METHOD_WECHAT:
            case METHOD_ALIPAY:
            case METHOD_UNIONPAY:
                return "第三方支付";
            case METHOD_CASH:
                return "现金";
            case METHOD_BALANCE:
                return "余额";
            case METHOD_POINTS:
                return "积分";
            default:
                return "其他";
        }
    }

    /**
     * 检查是否是线上支付
     */
    public boolean isOnlinePayment() {
        return METHOD_WECHAT.equals(paymentMethod) ||
               METHOD_ALIPAY.equals(paymentMethod) ||
               METHOD_UNIONPAY.equals(paymentMethod);
    }

    /**
     * 检查是否是线下支付
     */
    public boolean isOfflinePayment() {
        return METHOD_CASH.equals(paymentMethod);
    }

    /**
     * 检查是否是平台内支付
     */
    public boolean isPlatformPayment() {
        return METHOD_BALANCE.equals(paymentMethod) ||
               METHOD_POINTS.equals(paymentMethod);
    }

    /**
     * 获取支付处理方式
     */
    public String getPaymentProcessingType() {
        if (isOnlinePayment()) {
            return "在线即时支付";
        } else if (isOfflinePayment()) {
            return "线下现金支付";
        } else if (isPlatformPayment()) {
            return "平台内支付";
        } else {
            return "其他支付";
        }
    }

    /**
     * 获取支付记录唯一标识
     */
    public String getPaymentIdentifier() {
        if (transactionId != null && !transactionId.trim().isEmpty()) {
            return transactionId;
        } else {
            return "PAY-" + id;
        }
    }

    /**
     * 检查支付记录是否完整
     */
    public boolean isComplete() {
        if (isSuccess()) {
            return paymentTime != null && 
                   transactionId != null && 
                   !transactionId.trim().isEmpty();
        } else if (isRefunded()) {
            return refundTime != null && 
                   refundAmount != null && 
                   refundAmount.compareTo(BigDecimal.ZERO) > 0;
        }
        return true;
    }

    /**
     * 获取支付记录质量评分
     */
    public int getQualityScore() {
        int score = 0;
        
        // 基本信息完整度
        if (orderId != null && !orderId.trim().isEmpty()) score += 20;
        if (paymentMethod != null && !paymentMethod.trim().isEmpty()) score += 20;
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) score += 20;
        
        // 状态相关完整性
        if (isSuccess()) {
            if (paymentTime != null) score += 20;
            if (transactionId != null && !transactionId.trim().isEmpty()) score += 20;
        } else if (isRefunded()) {
            if (refundTime != null) score += 20;
            if (refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0) score += 20;
        } else {
            score += 40; // 其他状态的基础分
        }
        
        return score;
    }

    /**
     * 获取支付记录质量描述
     */
    public String getQualityDescription() {
        int score = getQualityScore();
        
        if (score >= 90) {
            return "完整记录";
        } else if (score >= 70) {
            return "良好记录";
        } else if (score >= 50) {
            return "一般记录";
        } else {
            return "不完整记录";
        }
    }

    /**
     * 创建支付成功记录
     */
    public static PaymentRecord createSuccessRecord(String orderId, String paymentMethod, 
                                                   String transactionId, BigDecimal amount, 
                                                   Long payerId, String payerInfo) {
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setPaymentMethod(paymentMethod);
        record.setTransactionId(transactionId);
        record.setAmount(amount);
        record.setStatus(STATUS_SUCCESS);
        record.setPayerId(payerId);
        record.setPayerInfo(payerInfo);
        record.setPaymentTime(LocalDateTime.now());
        return record;
    }

    /**
     * 创建待支付记录
     */
    public static PaymentRecord createPendingRecord(String orderId, String paymentMethod, 
                                                   BigDecimal amount, Long payerId) {
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setPaymentMethod(paymentMethod);
        record.setAmount(amount);
        record.setStatus(STATUS_PENDING);
        record.setPayerId(payerId);
        return record;
    }

    /**
     * 创建支付失败记录
     */
    public static PaymentRecord createFailedRecord(String orderId, String paymentMethod, 
                                                  BigDecimal amount, Long payerId, 
                                                  String errorMessage) {
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setPaymentMethod(paymentMethod);
        record.setAmount(amount);
        record.setStatus(STATUS_FAILED);
        record.setPayerId(payerId);
        record.setNotifyData("{\"error\": \"" + errorMessage + "\"}");
        return record;
    }

    /**
     * 获取支付记录统计信息
     */
    public PaymentStats getPaymentStats() {
        PaymentStats stats = new PaymentStats();
        stats.setTotalAmount(amount);
        stats.setRefundAmount(refundAmount);
        stats.setActualAmount(getActualPaidAmount());
        stats.setRefundRatio(getRefundRatio());
        stats.setPaymentMethod(paymentMethod);
        stats.setStatus(status);
        
        if (paymentTime != null) {
            stats.setPaymentDate(paymentTime.toLocalDate());
            stats.setPaymentHour(paymentTime.getHour());
        }
        
        return stats;
    }

    /**
     * 支付统计类
     */
    @Data
    public static class PaymentStats {
        private BigDecimal totalAmount;
        private BigDecimal refundAmount;
        private BigDecimal actualAmount;
        private BigDecimal refundRatio;
        private String paymentMethod;
        private Integer status;
        private java.time.LocalDate paymentDate;
        private Integer paymentHour;
        
        public String getStatsSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("总金额: ¥").append(totalAmount).append("\n");
            sb.append("退款金额: ¥").append(refundAmount).append("\n");
            sb.append("实际支付: ¥").append(actualAmount).append("\n");
            sb.append("退款比例: ").append(refundRatio).append("%");
            return sb.toString();
        }
    }

    /**
     * 检查是否是高风险支付
     */
    public boolean isHighRiskPayment() {
        // 大额支付
        if (amount != null && amount.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            return true;
        }
        
        // 频繁退款
        if (isRefunded() && refundAmount != null && 
            refundAmount.compareTo(amount.multiply(BigDecimal.valueOf(0.5))) >= 0) {
            return true;
        }
        
        // 异常时间支付（凌晨2-5点）
        if (paymentTime != null) {
            int hour = paymentTime.getHour();
            if (hour >= 2 && hour <= 5) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 获取风险等级
     */
    public String getRiskLevel() {
        if (isHighRiskPayment()) {
            return "高风险";
        } else if (amount != null && amount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return "中风险";
        } else {
            return "低风险";
        }
    }

    /**
     * 获取支付记录归档状态
     */
    public String getArchiveStatus() {
        if (createdAt == null) {
            return "未归档";
        }
        
        long daysOld = java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
        
        if (daysOld >= 365) {
            return "已归档（一年以上）";
        } else if (daysOld >= 180) {
            return "待归档（半年以上）";
        } else if (daysOld >= 90) {
            return "近期记录（三个月以上）";
        } else {
            return "当前记录（三个月内）";
        }
    }

    /**
     * 检查是否需要归档
     */
    public boolean needsArchiving() {
        if (createdAt == null) {
            return false;
        }
        
        return createdAt.isBefore(LocalDateTime.now().minusDays(365));
    }

    /**
     * 获取支付记录显示优先级
     */
    public int getDisplayPriority() {
        int priority = 0;
        
        // 状态优先级
        if (isPending()) priority += 100;
        if (isFailed()) priority += 80;
        if (isSuccess()) priority += 60;
        if (isRefunded()) priority += 40;
        if (isClosed()) priority += 20;
        
        // 时间优先级（越新优先级越高）
        if (createdAt != null) {
            long hoursOld = java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
            priority += Math.max(0, 100 - (int)(hoursOld / 24)); // 每天减1分
        }
        
        // 金额优先级
        if (amount != null) {
            if (amount.compareTo(BigDecimal.valueOf(1000)) >= 0) priority += 30;
            else if (amount.compareTo(BigDecimal.valueOf(100)) >= 0) priority += 20;
            else priority += 10;
        }
        
        return priority;
    }
}