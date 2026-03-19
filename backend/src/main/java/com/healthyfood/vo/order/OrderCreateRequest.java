package com.healthyfood.vo.order;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建请求VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "商家ID不能为空")
    private Long shopId;

    @NotEmpty(message = "订单商品不能为空")
    private List<OrderItemRequest> items;

    @NotBlank(message = "配送地址不能为空")
    @Size(max = 200, message = "配送地址长度不能超过200个字符")
    private String deliveryAddress;

    private Double deliveryLatitude;
    private Double deliveryLongitude;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    @Size(max = 500, message = "配送说明长度不能超过500个字符")
    private String deliveryInstructions;

    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod; // WECHAT_PAY, ALIPAY, CASH, etc.

    private String couponCode;
    private String remark;

    /**
     * 订单项请求VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "商品数量不能为空")
        @Min(value = 1, message = "商品数量必须大于0")
        @Max(value = 999, message = "商品数量不能超过999")
        private Integer quantity;

        @Size(max = 200, message = "商品备注长度不能超过200个字符")
        private String remark;

        // 验证方法
        public void validate() {
            if (quantity == null || quantity < 1) {
                throw new IllegalArgumentException("商品数量必须大于0");
            }
            if (quantity > 999) {
                throw new IllegalArgumentException("单商品数量不能超过999");
            }
        }
    }

    // 验证方法
    public void validate() {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("订单商品不能为空");
        }
        
        for (OrderItemRequest item : items) {
            item.validate();
        }
        
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("配送地址不能为空");
        }
        
        if (contactName == null || contactName.trim().isEmpty()) {
            throw new IllegalArgumentException("联系人姓名不能为空");
        }
        
        if (contactPhone == null || !contactPhone.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("联系电话格式不正确");
        }
        
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("支付方式不能为空");
        }
    }
    
    /**
     * 获取商品ID列表
     */
    public List<Long> getProductIds() {
        return items.stream()
                .map(OrderItemRequest::getProductId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取商品数量映射
     */
    public java.util.Map<Long, Integer> getProductQuantityMap() {
        java.util.Map<Long, Integer> map = new java.util.HashMap<>();
        for (OrderItemRequest item : items) {
            map.put(item.getProductId(), item.getQuantity());
        }
        return map;
    }
    
    /**
     * 计算商品总数
     */
    public Integer getTotalItemCount() {
        return items.stream()
                .mapToInt(OrderItemRequest::getQuantity)
                .sum();
    }
    
    /**
     * 检查是否有特殊要求
     */
    public boolean hasSpecialRequirements() {
        return deliveryInstructions != null && !deliveryInstructions.trim().isEmpty() ||
               remark != null && !remark.trim().isEmpty() ||
               items.stream().anyMatch(item -> item.getRemark() != null && !item.getRemark().trim().isEmpty());
    }
    
    /**
     * 获取特殊要求摘要
     */
    public String getSpecialRequirementsSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (deliveryInstructions != null && !deliveryInstructions.trim().isEmpty()) {
            summary.append("配送说明: ").append(deliveryInstructions);
        }
        
        if (remark != null && !remark.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("订单备注: ").append(remark);
        }
        
        // 检查商品备注
        List<String> itemRemarks = items.stream()
                .filter(item -> item.getRemark() != null && !item.getRemark().trim().isEmpty())
                .map(item -> "商品" + item.getProductId() + ": " + item.getRemark())
                .collect(java.util.stream.Collectors.toList());
        
        if (!itemRemarks.isEmpty()) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("商品备注: ").append(String.join(", ", itemRemarks));
        }
        
        return summary.toString();
    }
    
    /**
     * 检查是否使用优惠券
     */
    public boolean hasCoupon() {
        return couponCode != null && !couponCode.trim().isEmpty();
    }
    
    /**
     * 获取订单摘要
     */
    public String getOrderSummary() {
        return String.format("订单包含%d种商品，共%d件", 
                items.size(), getTotalItemCount());
    }
    
    /**
     * 验证地理位置信息
     */
    public boolean hasValidLocation() {
        return deliveryLatitude != null && deliveryLongitude != null &&
               deliveryLatitude >= -90 && deliveryLatitude <= 90 &&
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
}