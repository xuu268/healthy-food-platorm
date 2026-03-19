package com.healthyfood.vo.shop;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;
import java.util.Set;

/**
 * 商家注册请求对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegisterRequest {
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    /**
     * 商家名称
     */
    @NotBlank(message = "商家名称不能为空")
    @Size(min = 2, max = 50, message = "商家名称长度必须在2-50位之间")
    private String shopName;
    
    /**
     * 商家类型
     */
    @NotNull(message = "商家类型不能为空")
    private ShopType shopType;
    
    /**
     * 商家描述
     */
    @Size(max = 500, message = "商家描述不能超过500字")
    private String description;
    
    /**
     * 商家地址
     */
    @NotBlank(message = "商家地址不能为空")
    private String address;
    
    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度范围错误")
    @DecimalMax(value = "90.0", message = "纬度范围错误")
    private Double latitude;
    
    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度范围错误")
    @DecimalMax(value = "180.0", message = "经度范围错误")
    private Double longitude;
    
    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空")
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;
    
    /**
     * 营业执照号
     */
    @NotBlank(message = "营业执照号不能为空")
    private String businessLicense;
    
    /**
     * 卫生许可证号
     */
    @NotBlank(message = "卫生许可证号不能为空")
    private String healthPermit;
    
    /**
     * 营业时间
     */
    private String openingHours;
    
    /**
     * 配送范围 (公里)
     */
    @NotNull(message = "配送范围不能为空")
    @Min(value = 1, message = "配送范围最小为1公里")
    @Max(value = 50, message = "配送范围最大为50公里")
    private Integer deliveryRange;
    
    /**
     * 最低起送金额
     */
    @NotNull(message = "最低起送金额不能为空")
    @DecimalMin(value = "0.0", message = "最低起送金额不能为负数")
    private Double minOrderAmount;
    
    /**
     * 配送费
     */
    @NotNull(message = "配送费不能为空")
    @DecimalMin(value = "0.0", message = "配送费不能为负数")
    private Double deliveryFee;
    
    /**
     * 预计配送时间 (分钟)
     */
    @NotNull(message = "预计配送时间不能为空")
    @Min(value = 10, message = "预计配送时间最小为10分钟")
    @Max(value = 180, message = "预计配送时间最大为180分钟")
    private Integer estimatedDeliveryTime;
    
    /**
     * 支付方式
     */
    private Set<String> paymentMethods;
    
    /**
     * 菜系类型
     */
    private Set<String> cuisineTypes;
    
    /**
     * 特色菜品
     */
    private Set<String> specialties;
    
    /**
     * 标签
     */
    private Set<String> tags;
    
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位数字")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String verifyCode;
    
    /**
     * 邀请码 (可选)
     */
    private String inviteCode;
    
    /**
     * 注册来源
     */
    private String registerSource;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 商家类型枚举
     */
    public enum ShopType {
        RESTAURANT,         // 餐厅
        CAFE,               // 咖啡馆
        BAKERY,             // 面包店
        FAST_FOOD,          // 快餐店
        DESSERT_SHOP,       // 甜品店
        HEALTH_FOOD,        // 健康食品店
        VEGETARIAN,         // 素食店
        FOOD_TRUCK,         // 餐车
        GROCERY,            // 食品杂货店
        OTHER               // 其他
    }
    
    /**
     * 验证密码是否一致
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
    
    /**
     * 验证必填信息
     */
    public boolean validate() {
        return phone != null && !phone.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               shopName != null && !shopName.trim().isEmpty() &&
               shopType != null &&
               address != null && !address.trim().isEmpty() &&
               latitude != null && longitude != null &&
               contactPerson != null && !contactPerson.trim().isEmpty() &&
               contactPhone != null && !contactPhone.trim().isEmpty() &&
               businessLicense != null && !businessLicense.trim().isEmpty() &&
               healthPermit != null && !healthPermit.trim().isEmpty() &&
               deliveryRange != null && deliveryRange >= 1 && deliveryRange <= 50 &&
               minOrderAmount != null && minOrderAmount >= 0 &&
               deliveryFee != null && deliveryFee >= 0 &&
               estimatedDeliveryTime != null && estimatedDeliveryTime >= 10 && estimatedDeliveryTime <= 180 &&
               verifyCode != null && verifyCode.length() == 6;
    }
    
    /**
     * 获取简化的商家信息（用于日志等）
     */
    public String getSimplifiedInfo() {
        return String.format("ShopRegisterRequest{phone=%s, shopName=%s, type=%s}", 
                phone, shopName, shopType);
    }
}