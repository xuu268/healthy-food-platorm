package com.healthyfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "points_record")
@TableName("points_record")
public class PointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 积分类型常量
     */
    public static final String TYPE_CONTENT_PUBLISH = "content_publish";          // 发布内容
    public static final String TYPE_CONTENT_QUALITY = "content_quality";          // 优质内容
    public static final String TYPE_CONTENT_VIRAL = "content_viral";              // 爆款内容
    public static final String TYPE_SHOP_CONNECTION = "shop_connection";          // 商家对接
    public static final String TYPE_SHOP_REVIEW = "shop_review";                  // 商家评价
    public static final String TYPE_SHOP_REPURCHASE = "shop_repurchase";          // 商家复购
    public static final String TYPE_INVITE_CREATOR = "invite_creator";            // 邀请创作者
    public static final String TYPE_DAILY_CHECKIN = "daily_checkin";              // 每日签到
    public static final String TYPE_HEALTH_RECORD = "health_record";              // 健康记录
    public static final String TYPE_ORDER_COMPLETE = "order_complete";            // 订单完成
    public static final String TYPE_SHARE_APP = "share_app";                      // 分享应用
    public static final String TYPE_FEEDBACK = "feedback";                        // 反馈建议
    public static final String TYPE_ACTIVITY_PARTICIPATION = "activity_participation"; // 活动参与
    public static final String TYPE_SYSTEM_REWARD = "system_reward";              // 系统奖励
    public static final String TYPE_POINTS_EXCHANGE = "points_exchange";          // 积分兑换
    public static final String TYPE_POINTS_DEDUCTION = "points_deduction";        // 积分扣除

    /**
     * 记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 积分类型
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * 积分数量（正数为增加，负数为减少）
     */
    @Column(nullable = false)
    private Integer amount;

    /**
     * 积分余额
     */
    @Column(nullable = false)
    private Integer balance;

    /**
     * 描述
     */
    @Column(nullable = false, length = 200)
    private String description;

    /**
     * 关联类型
     */
    @Column(name = "reference_type", length = 50)
    private String referenceType;

    /**
     * 关联ID
     */
    @Column(name = "reference_id", length = 100)
    private String referenceId;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 用户信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private User user;

    /**
     * 获取积分类型描述
     */
    public String getTypeDescription() {
        switch (type) {
            case TYPE_CONTENT_PUBLISH:
                return "发布内容";
            case TYPE_CONTENT_QUALITY:
                return "优质内容";
            case TYPE_CONTENT_VIRAL:
                return "爆款内容";
            case TYPE_SHOP_CONNECTION:
                return "商家对接";
            case TYPE_SHOP_REVIEW:
                return "商家评价";
            case TYPE_SHOP_REPURCHASE:
                return "商家复购";
            case TYPE_INVITE_CREATOR:
                return "邀请创作者";
            case TYPE_DAILY_CHECKIN:
                return "每日签到";
            case TYPE_HEALTH_RECORD:
                return "健康记录";
            case TYPE_ORDER_COMPLETE:
                return "订单完成";
            case TYPE_SHARE_APP:
                return "分享应用";
            case TYPE_FEEDBACK:
                return "反馈建议";
            case TYPE_ACTIVITY_PARTICIPATION:
                return "活动参与";
            case TYPE_SYSTEM_REWARD:
                return "系统奖励";
            case TYPE_POINTS_EXCHANGE:
                return "积分兑换";
            case TYPE_POINTS_DEDUCTION:
                return "积分扣除";
            default:
                return "积分变动";
        }
    }

    /**
     * 获取积分类型图标
     */
    public String getTypeIcon() {
        switch (type) {
            case TYPE_CONTENT_PUBLISH:
            case TYPE_CONTENT_QUALITY:
            case TYPE_CONTENT_VIRAL:
                return "📝";
            case TYPE_SHOP_CONNECTION:
            case TYPE_SHOP_REVIEW:
            case TYPE_SHOP_REPURCHASE:
                return "🏪";
            case TYPE_INVITE_CREATOR:
                return "👥";
            case TYPE_DAILY_CHECKIN:
                return "📅";
            case TYPE_HEALTH_RECORD:
                return "❤️";
            case TYPE_ORDER_COMPLETE:
                return "🛒";
            case TYPE_SHARE_APP:
                return "📱";
            case TYPE_FEEDBACK:
                return "💡";
            case TYPE_ACTIVITY_PARTICIPATION:
                return "🎉";
            case TYPE_SYSTEM_REWARD:
                return "🏆";
            case TYPE_POINTS_EXCHANGE:
                return "🔄";
            case TYPE_POINTS_DEDUCTION:
                return "⚠️";
            default:
                return "⭐";
        }
    }

    /**
     * 检查是否是增加积分
     */
    public boolean isIncrease() {
        return amount > 0;
    }

    /**
     * 检查是否是减少积分
     */
    public boolean isDecrease() {
        return amount < 0;
    }

    /**
     * 获取积分变动方向
     */
    public String getChangeDirection() {
        return isIncrease() ? "增加" : "减少";
    }

    /**
     * 获取积分变动符号
     */
    public String getChangeSymbol() {
        return isIncrease() ? "+" : "";
    }

    /**
     * 获取完整的积分变动描述
     */
    public String getFullDescription() {
        return String.format("%s%s积分 (%s)", 
                getChangeSymbol(), Math.abs(amount), description);
    }

    /**
     * 获取记录摘要
     */
    public String getRecordSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTypeIcon()).append(" ");
        sb.append(getTypeDescription()).append(": ");
        sb.append(getChangeSymbol()).append(Math.abs(amount)).append("积分\n");
        sb.append("📝 ").append(description).append("\n");
        sb.append("💰 余额: ").append(balance).append("积分");
        
        if (referenceType != null && referenceId != null) {
            sb.append("\n🔗 关联: ").append(referenceType).append(" #").append(referenceId);
        }
        
        return sb.toString();
    }

    /**
     * 检查是否是系统奖励
     */
    public boolean isSystemReward() {
        return TYPE_SYSTEM_REWARD.equals(type);
    }

    /**
     * 检查是否是用户行为奖励
     */
    public boolean isUserActionReward() {
        return TYPE_CONTENT_PUBLISH.equals(type) ||
               TYPE_CONTENT_QUALITY.equals(type) ||
               TYPE_CONTENT_VIRAL.equals(type) ||
               TYPE_SHOP_CONNECTION.equals(type) ||
               TYPE_SHOP_REVIEW.equals(type) ||
               TYPE_SHOP_REPURCHASE.equals(type) ||
               TYPE_INVITE_CREATOR.equals(type) ||
               TYPE_DAILY_CHECKIN.equals(type) ||
               TYPE_HEALTH_RECORD.equals(type) ||
               TYPE_ORDER_COMPLETE.equals(type) ||
               TYPE_SHARE_APP.equals(type) ||
               TYPE_FEEDBACK.equals(type) ||
               TYPE_ACTIVITY_PARTICIPATION.equals(type);
    }

    /**
     * 检查是否是积分操作
     */
    public boolean isPointsOperation() {
        return TYPE_POINTS_EXCHANGE.equals(type) ||
               TYPE_POINTS_DEDUCTION.equals(type);
    }

    /**
     * 获取积分价值（1积分 = 0.01元）
     */
    public Double getMonetaryValue() {
        return Math.abs(amount) * 0.01;
    }

    /**
     * 获取积分价值描述
     */
    public String getMonetaryValueDescription() {
        double value = getMonetaryValue();
        return String.format("约¥%.2f", value);
    }

    /**
     * 检查记录是否有效
     */
    public boolean isValid() {
        // 积分变动后余额不能为负数
        if (balance < 0) {
            return false;
        }
        
        // 积分数量不能为0
        if (amount == 0) {
            return false;
        }
        
        // 描述不能为空
        if (description == null || description.trim().isEmpty()) {
            return false;
        }
        
        return true;
    }

    /**
     * 验证记录数据
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        if (userId == null) {
            result.addError("用户ID不能为空");
        }
        
        if (type == null || type.trim().isEmpty()) {
            result.addError("积分类型不能为空");
        }
        
        if (amount == 0) {
            result.addError("积分数量不能为0");
        }
        
        if (balance == null) {
            result.addError("积分余额不能为空");
        } else if (balance < 0) {
            result.addError("积分余额不能为负数");
        }
        
        if (description == null || description.trim().isEmpty()) {
            result.addError("描述不能为空");
        } else if (description.length() > 200) {
            result.addError("描述不能超过200字符");
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
     * 获取关联信息
     */
    public String getReferenceInfo() {
        if (referenceType == null || referenceId == null) {
            return "无关联";
        }
        
        return referenceType + " #" + referenceId;
    }

    /**
     * 检查是否有关联对象
     */
    public boolean hasReference() {
        return referenceType != null && referenceId != null;
    }

    /**
     * 获取记录时间描述
     */
    public String getTimeDescription() {
        if (createdAt == null) {
            return "时间未知";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) { // 24小时
            long hours = minutes / 60;
            return hours + "小时前";
        } else if (minutes < 10080) { // 7天
            long days = minutes / 1440;
            return days + "天前";
        } else {
            return createdAt.toLocalDate().toString();
        }
    }

    /**
     * 检查是否是今日记录
     */
    public boolean isTodayRecord() {
        if (createdAt == null) {
            return false;
        }
        
        return createdAt.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * 检查是否是本周记录
     */
    public boolean isThisWeekRecord() {
        if (createdAt == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        
        return createdAt.isAfter(weekAgo) && !createdAt.isAfter(now);
    }

    /**
     * 检查是否是本月记录
     */
    public boolean isThisMonthRecord() {
        if (createdAt == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return createdAt.getMonth() == now.getMonth() && 
               createdAt.getYear() == now.getYear();
    }

    /**
     * 获取积分变动原因分类
     */
    public String getReasonCategory() {
        if (isSystemReward()) {
            return "系统奖励";
        } else if (isUserActionReward()) {
            return "行为奖励";
        } else if (isPointsOperation()) {
            return "积分操作";
        } else {
            return "其他";
        }
    }

    /**
     * 获取积分重要性等级
     */
    public String getImportanceLevel() {
        int absAmount = Math.abs(amount);
        
        if (absAmount >= 500) {
            return "重要";
        } else if (absAmount >= 100) {
            return "中等";
        } else if (absAmount >= 10) {
            return "一般";
        } else {
            return "轻微";
        }
    }

    /**
     * 获取记录颜色（用于UI显示）
     */
    public String getRecordColor() {
        if (isIncrease()) {
            return "green";
        } else {
            return "red";
        }
    }

    /**
     * 复制记录（用于创建新记录）
     */
    public PointsRecord copy() {
        PointsRecord copy = new PointsRecord();
        copy.setUserId(this.userId);
        copy.setType(this.type);
        copy.setAmount(this.amount);
        copy.setBalance(this.balance);
        copy.setDescription(this.description);
        copy.setReferenceType(this.referenceType);
        copy.setReferenceId(this.referenceId);
        return copy;
    }

    /**
     * 创建增加积分记录
     */
    public static PointsRecord createIncreaseRecord(Long userId, String type, 
                                                   Integer amount, Integer currentBalance, 
                                                   String description, String referenceType, 
                                                   String referenceId) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setAmount(Math.abs(amount));
        record.setBalance(currentBalance + Math.abs(amount));
        record.setDescription(description);
        record.setReferenceType(referenceType);
        record.setReferenceId(referenceId);
        return record;
    }

    /**
     * 创建减少积分记录
     */
    public static PointsRecord createDecreaseRecord(Long userId, String type, 
                                                   Integer amount, Integer currentBalance, 
                                                   String description, String referenceType, 
                                                   String referenceId) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setAmount(-Math.abs(amount));
        record.setBalance(currentBalance - Math.abs(amount));
        record.setDescription(description);
        record.setReferenceType(referenceType);
        record.setReferenceId(referenceId);
        return record;
    }

    /**
     * 获取默认积分数量（基于类型）
     */
    public static Integer getDefaultPointsAmount(String type) {
        switch (type) {
            case TYPE_CONTENT_PUBLISH:
                return 10;
            case TYPE_CONTENT_QUALITY:
                return 50;
            case TYPE_CONTENT_VIRAL:
                return 200;
            case TYPE_SHOP_CONNECTION:
                return 100;
            case TYPE_SHOP_REVIEW:
                return 50;
            case TYPE_SHOP_REPURCHASE:
                return 200;
            case TYPE_INVITE_CREATOR:
                return 500;
            case TYPE_DAILY_CHECKIN:
                return 5;
            case TYPE_HEALTH_RECORD:
                return 10;
            case TYPE_ORDER_COMPLETE:
                return 20;
            case TYPE_SHARE_APP:
                return 30;
            case TYPE_FEEDBACK:
                return 15;
            case TYPE_ACTIVITY_PARTICIPATION:
                return 50;
            case TYPE_SYSTEM_REWARD:
                return 100;
            default:
                return 0;
        }
    }
}