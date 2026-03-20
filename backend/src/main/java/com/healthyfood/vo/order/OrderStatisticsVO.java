package com.healthyfood.vo.order;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单统计视图对象VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsVO {

    // 统计范围
    private Long shopId;
    private String shopName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String period; // DAY, WEEK, MONTH, QUARTER, YEAR, CUSTOM
    
    // 基础统计
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer cancelledOrders;
    private Integer pendingOrders;
    private Integer refundedOrders;
    
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private BigDecimal maxOrderValue;
    private BigDecimal minOrderValue;
    
    private Integer totalCustomers;
    private Integer newCustomers;
    private Integer returningCustomers;
    
    private Integer totalItemsSold;
    private BigDecimal averageItemsPerOrder;
    
    // 时间分布
    private Map<String, Integer> hourlyDistribution; // 24小时分布
    private Map<String, Integer> dailyDistribution;  // 周分布
    private Map<String, Integer> monthlyDistribution; // 月分布
    
    // 状态分布
    private Map<String, Integer> statusDistribution;
    private Map<String, Integer> paymentStatusDistribution;
    private Map<String, Integer> deliveryStatusDistribution;
    
    // 支付方式分布
    private Map<String, BigDecimal> paymentMethodDistribution;
    
    // 商品统计
    private List<ProductStatVO> topProducts;
    private List<CategoryStatVO> topCategories;
    
    // 客户统计
    private List<CustomerStatVO> topCustomers;
    private Map<String, Integer> customerRegionDistribution;
    
    // 配送统计
    private BigDecimal averageDeliveryTime; // 分钟
    private BigDecimal onTimeDeliveryRate; // 准时率
    private Map<String, Integer> deliveryPersonPerformance;
    
    // 退款统计
    private Integer totalRefunds;
    private BigDecimal totalRefundAmount;
    private BigDecimal refundRate; // 退款率
    private Map<String, Integer> refundReasonDistribution;
    
    // 评价统计
    private Double averageRating;
    private Integer totalReviews;
    private Map<Integer, Integer> ratingDistribution; // 1-5星分布
    
    // 趋势分析
    private List<RevenueTrendVO> revenueTrend;
    private List<OrderTrendVO> orderTrend;
    private List<CustomerTrendVO> customerTrend;
    
    // 对比分析
    private OrderStatisticsVO previousPeriod; // 上一周期对比
    private BigDecimal revenueGrowthRate;
    private BigDecimal orderGrowthRate;
    private BigDecimal customerGrowthRate;
    
    // 预测分析
    private BigDecimal predictedNextPeriodRevenue;
    private Integer predictedNextPeriodOrders;
    private List<String> recommendations;
    
    // 时间信息
    private LocalDateTime generatedTime;
    private String timezone;
    
    /**
     * 商品统计VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductStatVO {
        private Long productId;
        private String productName;
        private String productCategory;
        private Integer quantitySold;
        private BigDecimal revenue;
        private Double percentageOfTotal; // 占总销售额百分比
        private Integer orderCount;
        private Double averageRating;
        private Integer returnRate; // 退货率
        private BigDecimal profitMargin;
        
        public String getRevenueDisplay() {
            return revenue != null ? String.format("¥%.2f", revenue.doubleValue()) : "¥0.00";
        }
        
        public String getPercentageDisplay() {
            return percentageOfTotal != null ? String.format("%.1f%%", percentageOfTotal) : "0%";
        }
    }
    
    /**
     * 分类统计VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStatVO {
        private String category;
        private Integer quantitySold;
        private BigDecimal revenue;
        private Double percentageOfTotal;
        private Integer productCount;
        private Double averageRating;
        private BigDecimal averagePrice;
        
        public String getRevenueDisplay() {
            return revenue != null ? String.format("¥%.2f", revenue.doubleValue()) : "¥0.00";
        }
    }
    
    /**
     * 客户统计VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerStatVO {
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private Integer orderCount;
        private BigDecimal totalSpent;
        private BigDecimal averageOrderValue;
        private LocalDateTime firstOrderDate;
        private LocalDateTime lastOrderDate;
        private Double averageRating;
        private Integer daysSinceLastOrder;
        
        public String getCustomerValueLevel() {
            if (totalSpent == null) return "新客户";
            
            double spent = totalSpent.doubleValue();
            if (spent >= 1000) return "高价值客户";
            else if (spent >= 500) return "中价值客户";
            else if (spent >= 100) return "普通客户";
            else return "低价值客户";
        }
        
        public String getLoyaltyLevel() {
            if (orderCount == null) return "新客户";
            
            if (orderCount >= 10) return "忠诚客户";
            else if (orderCount >= 5) return "常客";
            else if (orderCount >= 2) return "回头客";
            else return "新客户";
        }
    }
    
    /**
     * 收入趋势VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueTrendVO {
        private String period; // 日期或时间段
        private BigDecimal revenue;
        private Integer orderCount;
        private Integer customerCount;
        private BigDecimal averageOrderValue;
        
        public String getRevenueDisplay() {
            return revenue != null ? String.format("¥%.2f", revenue.doubleValue()) : "¥0.00";
        }
    }
    
    /**
     * 订单趋势VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderTrendVO {
        private String period;
        private Integer orderCount;
        private Integer completedOrders;
        private Integer cancelledOrders;
        private BigDecimal completionRate;
        
        public String getCompletionRateDisplay() {
            return completionRate != null ? String.format("%.1f%%", completionRate.doubleValue()) : "0%";
        }
    }
    
    /**
     * 客户趋势VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerTrendVO {
        private String period;
        private Integer newCustomers;
        private Integer returningCustomers;
        private Integer totalCustomers;
        private BigDecimal retentionRate;
        
        public String getRetentionRateDisplay() {
            return retentionRate != null ? String.format("%.1f%%", retentionRate.doubleValue()) : "0%";
        }
    }
    
    // 业务方法
    
    /**
     * 计算完成率
     */
    public BigDecimal getCompletionRate() {
        if (totalOrders == null || totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(completedOrders != null ? completedOrders : 0)
                .divide(new BigDecimal(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
    
    /**
     * 计算取消率
     */
    public BigDecimal getCancellationRate() {
        if (totalOrders == null || totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(cancelledOrders != null ? cancelledOrders : 0)
                .divide(new BigDecimal(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
    
    /**
     * 计算客单价
     */
    public BigDecimal getAverageOrderValue() {
        if (totalOrders == null || totalOrders == 0 || totalRevenue == null) {
            return BigDecimal.ZERO;
        }
        return totalRevenue.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算每单平均商品数
     */
    public BigDecimal getAverageItemsPerOrder() {
        if (totalOrders == null || totalOrders == 0 || totalItemsSold == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(totalItemsSold)
                .divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 获取收入显示
     */
    public String getRevenueDisplay() {
        return totalRevenue != null ? String.format("¥%.2f", totalRevenue.doubleValue()) : "¥0.00";
    }
    
    /**
     * 获取平均订单价值显示
     */
    public String getAverageOrderValueDisplay() {
        BigDecimal avg = getAverageOrderValue();
        return String.format("¥%.2f", avg.doubleValue());
    }
    
    /**
     * 获取统计摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append("统计周期: ").append(startDate).append(" 至 ").append(endDate).append("\n");
        summary.append("总订单: ").append(totalOrders != null ? totalOrders : 0).append(" 单\n");
        summary.append("总营收: ").append(getRevenueDisplay()).append("\n");
        summary.append("完成率: ").append(getCompletionRate().setScale(1, BigDecimal.ROUND_HALF_UP)).append("%\n");
        summary.append("客单价: ").append(getAverageOrderValueDisplay());
        
        return summary.toString();
    }
    
    /**
     * 获取高峰时段
     */
    public String getPeakHours() {
        if (hourlyDistribution == null || hourlyDistribution.isEmpty()) {
            return "暂无数据";
        }
        
        List<Map.Entry<String, Integer>> sorted = hourlyDistribution.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .collect(java.util.stream.Collectors.toList());
        
        if (sorted.isEmpty()) {
            return "暂无数据";
        }
        
        return sorted.stream()
                .map(entry -> entry.getKey() + ":00-" + (Integer.parseInt(entry.getKey()) + 1) + ":00 (" + entry.getValue() + "单)")
                .collect(java.util.stream.Collectors.joining(", "));
    }
    
    /**
     * 获取热门商品
     */
    public String getTopProductsSummary() {
        if (topProducts == null || topProducts.isEmpty()) {
            return "暂无数据";
        }
        
        return topProducts.stream()
                .limit(3)
                .map(product -> product.getProductName() + " (" + product.getQuantitySold() + "份)")
                .collect(java.util.stream.Collectors.joining(", "));
    }
    
    /**
     * 获取最佳客户
     */
    public String getTopCustomersSummary() {
        if (topCustomers == null || topCustomers.isEmpty()) {
            return "暂无数据";
        }
        
        return topCustomers.stream()
                .limit(3)
                .map(customer -> customer.getCustomerName() + " (" + customer.getOrderCount() + "单)")
                .collect(java.util.stream.Collectors.joining(", "));
    }
    
    /**
     * 检查是否有增长
     */
    public boolean hasGrowth() {
        return revenueGrowthRate != null && revenueGrowthRate.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 获取增长显示
     */
    public String getGrowthDisplay() {
        if (revenueGrowthRate == null) {
            return "暂无对比数据";
        }
        
        if (revenueGrowthRate.compareTo(BigDecimal.ZERO) > 0) {
            return "增长 " + revenueGrowthRate.setScale(1, BigDecimal.ROUND_HALF_UP) + "%";
        } else if (revenueGrowthRate.compareTo(BigDecimal.ZERO) < 0) {
            return "下降 " + revenueGrowthRate.abs().setScale(1, BigDecimal.ROUND_HALF_UP) + "%";
        } else {
            return "持平";
        }
    }
    
    /**
     * 获取统计报告时间
     */
    public String getGeneratedTimeDisplay() {
        if (generatedTime == null) {
            return "未知";
        }
        return generatedTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    /**
     * 获取建议行动
     */
    public List<String> getActionableRecommendations() {
        List<String> recommendations = new java.util.ArrayList<>();
        
        // 基于数据分析给出建议
        if (cancellationRate != null && getCancellationRate().compareTo(new BigDecimal("10")) > 0) {
            recommendations.add("取消率较高(" + getCancellationRate().setScale(1, BigDecimal.ROUND_HALF_UP) + "%)，建议优化服务流程");
        }
        
        if (averageRating != null && averageRating < 4.0) {
            recommendations.add("平均评分较低(" + averageRating + ")，建议收集客户反馈改进服务");
        }
        
        if (refundRate != null && refundRate.compareTo(new BigDecimal("5")) > 0) {
            recommendations.add("退款率较高(" + refundRate.setScale(1, BigDecimal.ROUND_HALF_UP) + "%)，建议检查商品质量");
        }
        
        if (onTimeDeliveryRate != null && onTimeDeliveryRate.compareTo(new BigDecimal("90")) < 0) {
            recommendations.add("准时送达率较低(" + onTimeDeliveryRate.setScale(1, BigDecimal.ROUND_HALF_UP) + "%)，建议优化配送流程");
        }
        
        // 基于高峰时段建议
        if (hourlyDistribution != null && !hourlyDistribution.isEmpty()) {
            String peakHours = getPeakHours();
            recommendations.add("高峰时段: " + peakHours + "，建议提前准备");
        }
        
        // 基于热门商品建议
        if (topProducts != null && !topProducts.isEmpty()) {
            String topProductsSummary = getTopProductsSummary();
            recommendations.add("热门商品: " + topProductsSummary + "，建议保持库存充足");
        }
        
        return recommendations;
    }
}