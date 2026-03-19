package com.healthyfood.vo.shop;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商家注册响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegisterResponse {
    
    /**
     * 商家ID
     */
    private Long shopId;
    
    /**
     * 商家名称
     */
    private String shopName;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 商家状态
     */
    private String status;
    
    /**
     * 注册时间
     */
    private LocalDateTime registrationTime;
    
    /**
     * 消息
     */
    private String message;
}