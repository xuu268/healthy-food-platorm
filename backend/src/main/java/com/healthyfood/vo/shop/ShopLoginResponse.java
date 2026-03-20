package com.healthyfood.vo.shop;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 商家登录响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopLoginResponse {

    // 商家基本信息
    private Long shopId;
    private String shopName;
    private String shopLogo;
    private String shopType; // RESTAURANT, CAFE, BAKERY, etc.
    private String shopStatus; // ACTIVE, INACTIVE, SUSPENDED
    
    // 认证信息
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn; // 过期时间（秒）
    private LocalDateTime tokenExpiryTime;
    
    // 会话信息
    private String sessionId;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivityTime;
    
    // 权限信息
    private String[] permissions;
    private String[] roles;
    private Boolean isAdmin;
    private Boolean isVerified;
    
    // 店铺统计信息
    private Integer totalOrders;
    private Integer todayOrders;
    private BigDecimal todayRevenue;
    private Double shopRating;
    private Integer totalReviews;
    
    // 通知信息
    private Integer unreadNotifications;
    private Integer pendingOrders;
    private Integer pendingReviews;
    
    // 账户状态
    private Boolean needCompleteProfile;
    private Boolean needVerifyIdentity;
    private Boolean needSetupPayment;
    private String[] setupSteps; // 需要完成的设置步骤
    
    // 安全信息
    private String loginDevice;
    private String loginLocation;
    private Boolean newDeviceLogin;
    private Boolean suspiciousActivity;
    
    // 功能开关
    private Boolean canAcceptOrders;
    private Boolean canManageProducts;
    private Boolean canViewAnalytics;
    private Boolean canManageStaff;
    private Boolean canWithdrawFunds;
    
    // 时间信息
    private LocalDateTime accountCreateTime;
    private LocalDateTime lastLoginTime;
    private Integer consecutiveLoginDays;
    
    // 业务方法
    
    /**
     * 获取欢迎消息
     */
    public String getWelcomeMessage() {
        StringBuilder message = new StringBuilder();
        message.append("欢迎回来，").append(shopName).append("！");
        
        if (todayOrders != null && todayOrders > 0) {
            message.append(" 今日已有 ").append(todayOrders).append(" 个订单");
            if (todayRevenue != null && todayRevenue.compareTo(BigDecimal.ZERO) > 0) {
                message.append("，营收 ¥").append(todayRevenue.setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        } else {
            message.append(" 今日暂无订单");
        }
        
        if (pendingOrders != null && pendingOrders > 0) {
            message.append("，有 ").append(pendingOrders).append(" 个待处理订单");
        }
        
        return message.toString();
    }
    
    /**
     * 获取认证头信息
     */
    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }
    
    /**
     * 检查令牌是否即将过期
     */
    public boolean isTokenExpiringSoon() {
        if (tokenExpiryTime == null) return false;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime warningTime = tokenExpiryTime.minusMinutes(5); // 提前5分钟警告
        
        return now.isAfter(warningTime);
    }
    
    /**
     * 获取令牌剩余时间（分钟）
     */
    public Long getTokenRemainingMinutes() {
        if (tokenExpiryTime == null) return null;
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(tokenExpiryTime)) {
            return 0L;
        }
        
        return java.time.Duration.between(now, tokenExpiryTime).toMinutes();
    }
    
    /**
     * 检查是否需要刷新令牌
     */
    public boolean needsTokenRefresh() {
        return isTokenExpiringSoon() || getTokenRemainingMinutes() <= 10;
    }
    
    /**
     * 获取店铺状态标签
     */
    public String getShopStatusLabel() {
        if (shopStatus == null) return "未知";
        
        switch (shopStatus) {
            case "ACTIVE": return "营业中";
            case "INACTIVE": return "已停业";
            case "SUSPENDED": return "已暂停";
            case "PENDING": return "审核中";
            case "REJECTED": return "审核未通过";
            default: return shopStatus;
        }
    }
    
    /**
     * 获取店铺状态颜色
     */
    public String getShopStatusColor() {
        if (shopStatus == null) return "default";
        
        switch (shopStatus) {
            case "ACTIVE": return "success";
            case "INACTIVE": return "gray";
            case "SUSPENDED": return "warning";
            case "PENDING": return "orange";
            case "REJECTED": return "error";
            default: return "default";
        }
    }
    
    /**
     * 检查店铺是否营业
     */
    public boolean isShopActive() {
        return "ACTIVE".equals(shopStatus);
    }
    
    /**
     * 获取店铺类型显示
     */
    public String getShopTypeDisplay() {
        if (shopType == null) return "未知";
        
        switch (shopType) {
            case "RESTAURANT": return "餐厅";
            case "CAFE": return "咖啡厅";
            case "BAKERY": return "面包店";
            case "FAST_FOOD": return "快餐店";
            case "DESSERT": return "甜品店";
            case "HEALTHY_FOOD": return "健康餐";
            case "VEGETARIAN": return "素食餐厅";
            case "INTERNATIONAL": return "国际美食";
            default: return shopType;
        }
    }
    
    /**
     * 获取今日营收显示
     */
    public String getTodayRevenueDisplay() {
        if (todayRevenue == null) {
            return "¥0.00";
        }
        return "¥" + todayRevenue.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 获取评分显示
     */
    public String getRatingDisplay() {
        if (shopRating == null) {
            return "暂无评分";
        }
        return String.format("%.1f", shopRating) + "分";
    }
    
    /**
     * 获取评分星级
     */
    public String getRatingStars() {
        if (shopRating == null) {
            return "☆☆☆☆☆";
        }
        
        int fullStars = (int) Math.floor(shopRating);
        int halfStar = (shopRating - fullStars) >= 0.5 ? 1 : 0;
        int emptyStars = 5 - fullStars - halfStar;
        
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) stars.append("★");
        for (int i = 0; i < halfStar; i++) stars.append("½");
        for (int i = 0; i < emptyStars; i++) stars.append("☆");
        
        return stars.toString();
    }
    
    /**
     * 获取待办事项数量
     */
    public Integer getTodoCount() {
        int count = 0;
        if (unreadNotifications != null) count += unreadNotifications;
        if (pendingOrders != null) count += pendingOrders;
        if (pendingReviews != null) count += pendingReviews;
        return count;
    }
    
    /**
     * 获取设置进度
     */
    public Integer getSetupProgress() {
        int totalSteps = 4; // 基本设置步骤
        int completedSteps = 0;
        
        if (!needCompleteProfile) completedSteps++;
        if (!needVerifyIdentity) completedSteps++;
        if (!needSetupPayment) completedSteps++;
        if (isVerified != null && isVerified) completedSteps++;
        
        return (completedSteps * 100) / totalSteps;
    }
    
    /**
     * 获取设置进度显示
     */
    public String getSetupProgressDisplay() {
        int progress = getSetupProgress();
        if (progress >= 100) {
            return "已完成所有设置";
        }
        return "设置进度: " + progress + "%";
    }
    
    /**
     * 获取连续登录天数显示
     */
    public String getConsecutiveLoginDisplay() {
        if (consecutiveLoginDays == null || consecutiveLoginDays == 0) {
            return "今日首次登录";
        }
        return "已连续登录 " + consecutiveLoginDays + " 天";
    }
    
    /**
     * 获取账户创建时间显示
     */
    public String getAccountCreateTimeDisplay() {
        if (accountCreateTime == null) {
            return "未知";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(accountCreateTime, now).toDays();
        
        if (days < 1) {
            return "今天创建";
        } else if (days < 30) {
            return days + "天前创建";
        } else if (days < 365) {
            return (days / 30) + "个月前创建";
        } else {
            return (days / 365) + "年前创建";
        }
    }
    
    /**
     * 获取最后登录时间显示
     */
    public String getLastLoginTimeDisplay() {
        if (lastLoginTime == null) {
            return "首次登录";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(lastLoginTime, now).toHours();
        
        if (hours < 1) {
            long minutes = java.time.Duration.between(lastLoginTime, now).toMinutes();
            return minutes + "分钟前登录";
        } else if (hours < 24) {
            return hours + "小时前登录";
        } else {
            long days = hours / 24;
            return days + "天前登录";
        }
    }
    
    /**
     * 检查是否有安全警告
     */
    public boolean hasSecurityWarning() {
        return newDeviceLogin != null && newDeviceLogin || 
               suspiciousActivity != null && suspiciousActivity;
    }
    
    /**
     * 获取安全警告消息
     */
    public String getSecurityWarning() {
        if (!hasSecurityWarning()) {
            return null;
        }
        
        StringBuilder warning = new StringBuilder();
        if (newDeviceLogin != null && newDeviceLogin) {
            warning.append("检测到新设备登录");
        }
        if (suspiciousActivity != null && suspiciousActivity) {
            if (warning.length() > 0) warning.append("，");
            warning.append("检测到可疑活动");
        }
        
        return warning.toString();
    }
    
    /**
     * 清理敏感信息
     */
    public void sanitize() {
        this.accessToken = null;
        this.refreshToken = null;
        this.sessionId = null;
    }
    
    /**
     * 复制对象（不包含敏感信息）
     */
    public ShopLoginResponse copyWithoutSensitiveInfo() {
        return ShopLoginResponse.builder()
                .shopId(this.shopId)
                .shopName(this.shopName)
                .shopLogo(this.shopLogo)
                .shopType(this.shopType)
                .shopStatus(this.shopStatus)
                .tokenType(this.tokenType)
                .expiresIn(this.expiresIn)
                .tokenExpiryTime(this.tokenExpiryTime)
                .loginTime(this.loginTime)
                .lastActivityTime(this.lastActivityTime)
                .permissions(this.permissions)
                .roles(this.roles)
                .isAdmin(this.isAdmin)
                .isVerified(this.isVerified)
                .totalOrders(this.totalOrders)
                .todayOrders(this.todayOrders)
                .todayRevenue(this.todayRevenue)
                .shopRating(this.shopRating)
                .totalReviews(this.totalReviews)
                .unreadNotifications(this.unreadNotifications)
                .pendingOrders(this.pendingOrders)
                .pendingReviews(this.pendingReviews)
                .needCompleteProfile(this.needCompleteProfile)
                .needVerifyIdentity(this.needVerifyIdentity)
                .needSetupPayment(this.needSetupPayment)
                .setupSteps(this.setupSteps)
                .loginDevice(this.loginDevice)
                .loginLocation(this.loginLocation)
                .newDeviceLogin(this.newDeviceLogin)
                .suspiciousActivity(this.suspiciousActivity)
                .canAcceptOrders(this.canAcceptOrders)
                .canManageProducts(this.canManageProducts)
                .canViewAnalytics(this.canViewAnalytics)
                .canManageStaff(this.canManageStaff)
                .canWithdrawFunds(this.canWithdrawFunds)
                .accountCreateTime(this.accountCreateTime)
                .lastLoginTime(this.lastLoginTime)
                .consecutiveLoginDays(this.consecutiveLoginDays)
                .build();
    }
}