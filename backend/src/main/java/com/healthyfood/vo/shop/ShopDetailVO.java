package com.healthyfood.vo.shop;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 商家详情视图对象VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailVO {

    // 商家基本信息
    private Long shopId;
    private String shopName;
    private String shopLogo;
    private String shopDescription;
    private String shopType; // RESTAURANT, CAFE, BAKERY, etc.
    private String shopCategory; // CHINESE, WESTERN, JAPANESE, etc.
    private String[] shopTags;
    
    // 联系信息
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String emergencyContact;
    private String emergencyPhone;
    
    // 地址信息
    private String address;
    private String city;
    private String district;
    private String street;
    private String building;
    private String floor;
    private String roomNumber;
    private String postalCode;
    
    // 地理位置
    private Double latitude;
    private Double longitude;
    private String locationDescription;
    
    // 营业信息
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String[] businessDays; // MON, TUE, WED, THU, FRI, SAT, SUN
    private Boolean is24Hours;
    private Boolean hasBreakTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    
    // 配送信息
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private Integer deliveryRadius; // 配送半径（米）
    private Integer estimatedDeliveryTime; // 预计配送时间（分钟）
    private String[] deliveryAreas;
    private Boolean supportsSelfPickup;
    private Boolean supportsDelivery;
    
    // 支付信息
    private String[] acceptedPaymentMethods;
    private Boolean supportsCashOnDelivery;
    private Boolean supportsOnlinePayment;
    private String paymentAccount;
    private String paymentAccountName;
    private String paymentBank;
    
    // 店铺状态
    private String status; // ACTIVE, INACTIVE, SUSPENDED, PENDING, REJECTED
    private String verificationStatus; // VERIFIED, UNVERIFIED, PENDING
    private Boolean isFeatured;
    private Integer featuredPriority;
    private LocalDateTime featuredUntil;
    
    // 统计信息
    private Integer totalOrders;
    private Integer monthlyOrders;
    private Integer weeklyOrders;
    private Integer todayOrders;
    
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal todayRevenue;
    
    private Double averageRating;
    private Integer totalReviews;
    private Integer fiveStarReviews;
    private Integer fourStarReviews;
    private Integer threeStarReviews;
    private Integer twoStarReviews;
    private Integer oneStarReviews;
    
    private Integer totalCustomers;
    private Integer returningCustomers;
    private Double customerRetentionRate;
    
    // 评价详情
    private List<ReviewSummaryVO> recentReviews;
    private Map<String, Integer> reviewTags; // 评价标签统计
    
    // 商品信息
    private Integer totalProducts;
    private Integer activeProducts;
    private Integer outOfStockProducts;
    private List<ProductCategoryVO> productCategories;
    private List<TopProductVO> topProducts;
    
    // 员工信息
    private Integer totalStaff;
    private Integer activeStaff;
    private List<StaffSummaryVO> keyStaff;
    
    // 设施信息
    private Boolean hasWifi;
    private Boolean hasParking;
    private Boolean hasOutdoorSeating;
    private Boolean hasIndoorSeating;
    private Integer seatingCapacity;
    private Boolean isPetFriendly;
    private Boolean isWheelchairAccessible;
    private Boolean hasKidsArea;
    
    // 证书和许可
    private String businessLicense;
    private String foodSafetyLicense;
    private String healthCertificate;
    private LocalDateTime licenseExpiryDate;
    private String[] certifications; // 其他认证
    
    // 社交媒体
    private String website;
    private String facebook;
    private String instagram;
    private String twitter;
    private String tiktok;
    private String wechatOfficialAccount;
    private String wechatMiniProgram;
    
    // 时间信息
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastOrderTime;
    private LocalDateTime lastReviewTime;
    
    // 操作权限
    private Boolean canEdit;
    private Boolean canManageProducts;
    private Boolean canManageOrders;
    private Boolean canManageStaff;
    private Boolean canViewAnalytics;
    private Boolean canWithdrawFunds;
    
    // 通知设置
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
    private Boolean orderNotifications;
    private Boolean reviewNotifications;
    private Boolean systemNotifications;
    
    // 偏好设置
    private String defaultLanguage;
    private String defaultCurrency;
    private String timezone;
    private String dateFormat;
    private String timeFormat;
    
    /**
     * 评价摘要VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummaryVO {
        private Long reviewId;
        private Long customerId;
        private String customerName;
        private String customerAvatar;
        private Integer rating;
        private String comment;
        private List<String> images;
        private LocalDateTime reviewTime;
        private String[] tags;
        private Integer helpfulCount;
        private String shopReply;
        private LocalDateTime shopReplyTime;
        
        public String getTimeAgo() {
            if (reviewTime == null) return "";
            
            LocalDateTime now = LocalDateTime.now();
            long hours = java.time.Duration.between(reviewTime, now).toHours();
            
            if (hours < 1) {
                long minutes = java.time.Duration.between(reviewTime, now).toMinutes();
                return minutes + "分钟前";
            } else if (hours < 24) {
                return hours + "小时前";
            } else {
                long days = hours / 24;
                return days + "天前";
            }
        }
        
        public String getRatingStars() {
            if (rating == null) return "☆☆☆☆☆";
            
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                if (i < rating) {
                    stars.append("★");
                } else {
                    stars.append("☆");
                }
            }
            return stars.toString();
        }
    }
    
    /**
     * 商品分类VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCategoryVO {
        private String category;
        private Integer productCount;
        private BigDecimal averagePrice;
        private Integer totalSales;
        private Double averageRating;
        
        public String getAveragePriceDisplay() {
            return averagePrice != null ? 
                String.format("¥%.2f", averagePrice.doubleValue()) : "¥0.00";
        }
    }
    
    /**
     * 热门商品VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductVO {
        private Long productId;
        private String productName;
        private String productImage;
        private Integer salesCount;
        private BigDecimal totalRevenue;
        private Double averageRating;
        private Integer stockQuantity;
        
        public String getRevenueDisplay() {
            return totalRevenue != null ? 
                String.format("¥%.2f", totalRevenue.doubleValue()) : "¥0.00";
        }
        
        public String getSalesDisplay() {
            return salesCount != null ? salesCount + "份" : "0份";
        }
    }
    
    /**
     * 员工摘要VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffSummaryVO {
        private Long staffId;
        private String staffName;
        private String staffRole;
        private String staffAvatar;
        private String contactPhone;
        private String status; // ACTIVE, INACTIVE, ON_LEAVE
        private LocalDateTime joinDate;
        
        public String getStatusLabel() {
            if (status == null) return "未知";
            
            switch (status) {
                case "ACTIVE": return "在职";
                case "INACTIVE": return "离职";
                case "ON_LEAVE": return "休假";
                default: return status;
            }
        }
        
        public String getStatusColor() {
            if (status == null) return "default";
            
            switch (status) {
                case "ACTIVE": return "success";
                case "INACTIVE": return "gray";
                case "ON_LEAVE": return "warning";
                default: return "default";
            }
        }
    }
    
    // 业务方法
    
    /**
     * 获取店铺状态标签
     */
    public String getStatusLabel() {
        if (status == null) return "未知";
        
        switch (status) {
            case "ACTIVE": return "营业中";
            case "INACTIVE": return "已停业";
            case "SUSPENDED": return "已暂停";
            case "PENDING": return "审核中";
            case "REJECTED": return "审核未通过";
            default: return status;
        }
    }
    
    /**
     * 获取店铺状态颜色
     */
    public String getStatusColor() {
        if (status == null) return "default";
        
        switch (status) {
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
        return "ACTIVE".equals(status);
    }
    
    /**
     * 检查店铺是否正在营业（根据时间）
     */
    public boolean isCurrentlyOpen() {
        if (!isShopActive()) return false;
        if (is24Hours != null && is24Hours) return true;
        
        LocalTime now = LocalTime.now();
        
        // 检查是否在营业时间内
        if (openingTime != null && closingTime != null) {
            if (openingTime.isBefore(closingTime)) {
                // 正常营业时间（如 9:00-21:00）
                if (now.isBefore(openingTime) || now.isAfter(closingTime)) {
                    return false;
                }
            } else {
                // 跨天营业时间（如 22:00-6:00）
                if (now.isBefore(openingTime) && now.isAfter(closingTime)) {
                    return false;
                }
            }
        }
        
        // 检查是否在休息时间
        if (hasBreakTime != null && hasBreakTime && 
            breakStartTime != null && breakEndTime != null) {
            if (breakStartTime.isBefore(breakEndTime)) {
                // 正常休息时间
                if (now.isAfter(breakStartTime) && now.isBefore(breakEndTime)) {
                    return false;
                }
            } else {
                // 跨天休息时间
                if (now.isAfter(breakStartTime) || now.isBefore(breakEndTime)) {
                    return false;
                }
            }
        }
        
        // 检查是否为营业日
        if (businessDays != null && businessDays.length > 0) {
            String today = java.time.DayOfWeek.from(java.time.LocalDate.now()).name().substring(0, 3);
            boolean isBusinessDay = false;
            for (String day : businessDays) {
                if (day.equalsIgnoreCase(today)) {
                    isBusinessDay = true;
                    break;
                }
            }
            if (!isBusinessDay) return false;
        }
        
        return true;
    }
    
    /**
     * 获取营业时间显示
     */
    public String getBusinessHoursDisplay() {
        if (is24Hours != null && is24Hours) {
            return "24小时营业";
        }
        
        if (openingTime == null || closingTime == null) {
            return "营业时间未设置";
        }
        
        StringBuilder display = new StringBuilder();
        display.append(openingTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        display.append(" - ");
        display.append(closingTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        
        if (hasBreakTime != null && hasBreakTime && 
            breakStartTime != null && breakEndTime != null) {
            display.append(" (休息时间: ");
            display.append(breakStartTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            display.append("-");
            display.append(breakEndTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            display.append(")");
        }
        
        return display.toString();
    }
    
    /**
     * 获取配送费显示
     */
    public String getDeliveryFeeDisplay() {
        if (deliveryFee == null || deliveryFee.compareTo(BigDecimal.ZERO) == 0) {
            return "免配送费";
        }
        return "¥" + deliveryFee.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 获取最低消费显示
     */
    public String getMinOrderAmountDisplay() {
        if (minOrderAmount == null || minOrderAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "无最低消费";
        }
        return "¥" + minOrderAmount.setScale(2, BigDecimal.ROUND_HALF_UP) + "起送";
    }
    
    /**
     * 获取评分显示
     */
    public String getRatingDisplay() {
        if (averageRating == null) {
            return "暂无评分";
        }
        return String.format("%.1f", averageRating) + "分";
    }
    
    /**
     * 获取评分星级
     */
    public String getRatingStars() {
        if (averageRating == null) {
            return "☆☆☆☆☆";
        }
        
        int fullStars = (int) Math.floor(averageRating);
        int halfStar = (averageRating - fullStars) >= 0.5 ? 1 : 0;
        int emptyStars = 5 - fullStars - halfStar;
        
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) stars.append("★");
        for (int i = 0; i < halfStar; i++) stars.append("½");
        for (int i = 0; i < emptyStars; i++) stars.append("☆");
        
        return stars.toString();
    }
    
    /**
     * 获取评价分布
     */
    public Map<Integer, Integer> getRatingDistribution() {
        Map<Integer, Integer> distribution = new java.util.HashMap<>();
        if (fiveStarReviews != null) distribution.put(5, fiveStarReviews);
        if (fourStarReviews != null) distribution.put(4, fourStarReviews);
        if (threeStarReviews != null) distribution.put(3, threeStarReviews);
        if (twoStarReviews != null) distribution.put(2, twoStarReviews);
        if (oneStarReviews != null) distribution.put(1, oneStarReviews);
        return distribution;
    }
    
    /**
     * 获取好评率
     */
    public BigDecimal getPositiveRatingRate() {
        if (totalReviews == null || totalReviews == 0) {
            return BigDecimal.ZERO;
        }
        
        int positiveReviews = 0;
        if (fiveStarReviews != null) positiveReviews += fiveStarReviews;
        if (fourStarReviews != null) positiveReviews += fourStarReviews;
        
        return new BigDecimal(positiveReviews)
                .divide(new BigDecimal(totalReviews), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
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
     * 获取地址摘要
     */
    public String getAddressSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (district != null) {
            summary.append(district);
        }
        
        if (street != null) {
            if (summary.length() > 0) summary.append(" ");
            summary.append(street);
        }
        
        if (building != null) {
            if (summary.length() > 0) summary.append(" ");
            summary.append(building);
        }
        
        return summary.toString();
    }
    
    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        
        if (city != null) {
            fullAddress.append(city);
        }
        
        if (district != null) {
            if (fullAddress.length() > 0) fullAddress.append(" ");
            fullAddress.append(district);
        }
        
        if (street != null) {
            if (fullAddress.length() > 0) fullAddress.append(" ");
            fullAddress.append(street);
        }
        
        if (building != null) {
            if (fullAddress.length() > 0) fullAddress.append(" ");
            fullAddress.append(building);
        }
        
        if (floor != null) {
            if (fullAddress.length() > 0) fullAddress.append(" ");
            fullAddress.append(floor).append("楼");
        }
        
        if (roomNumber != null) {
            if (fullAddress.length() > 0) fullAddress.append(" ");
            fullAddress.append(roomNumber).append("室");
        }
        
        return fullAddress.toString();
    }
    
    /**
     * 获取设施摘要
     */
    public String getFacilitiesSummary() {
        List<String> facilities = new java.util.ArrayList<>();
        
        if (hasWifi != null && hasWifi) facilities.add("WiFi");
        if (hasParking != null && hasParking) facilities.add("停车位");
        if (hasOutdoorSeating != null && hasOutdoorSeating) facilities.add("户外座位");
        if (isPetFriendly != null