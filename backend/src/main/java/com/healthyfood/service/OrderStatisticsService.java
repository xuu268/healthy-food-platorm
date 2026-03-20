package com.healthyfood.service;

import com.healthyfood.common.ApiResult;
import com.healthyfood.common.ErrorCode;
import com.healthyfood.entity.Order;
import com.healthyfood.entity.OrderItem;
import com.healthyfood.repository.OrderRepository;
import com.healthyfood.repository.OrderItemRepository;
import com.healthyfood.vo.order.OrderStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单统计服务
 */
@Slf4j
@Service
public class OrderStatisticsService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    /**
     * 获取订单统计
     */
    public ApiResult<OrderStatisticsVO> getOrderStatistics(Long shopId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("获取订单统计: shopId={}, startDate={}, endDate={}", shopId, startDate, endDate);
            
            // 验证参数
            if (startDate == null || endDate == null) {
                return ApiResult.error(ErrorCode.PARAM_ERROR, "开始日期和结束日期不能为空");
            }
            
            if (startDate.isAfter(endDate)) {
                return ApiResult.error(ErrorCode.PARAM_ERROR, "开始日期不能晚于结束日期");
            }
            
            // 构建统计VO
            OrderStatisticsVO statistics = buildOrderStatistics(shopId, startDate, endDate);
            
            return ApiResult.success(statistics);
        } catch (Exception e) {
            log.error("获取订单统计失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取订单统计失败");
        }
    }
    
    /**
     * 构建订单统计
     */
    private OrderStatisticsVO buildOrderStatistics(Long shopId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        OrderStatisticsVO.OrderStatisticsVOBuilder builder = OrderStatisticsVO.builder()
                .shopId(shopId)
                .shopName(getShopName(shopId))
                .startDate(startDate)
                .endDate(endDate)
                .period(determinePeriod(startDate, endDate))
                .generatedTime(LocalDateTime.now())
                .timezone("Asia/Shanghai");
        
        // 获取时间段内的订单
        List<Order> orders = getOrdersInPeriod(shopId, startTime, endTime);
        
        // 设置基础统计
        setBasicStatistics(builder, orders);
        
        // 设置时间分布
        setTimeDistributions(builder, orders);
        
        // 设置状态分布
        setStatusDistributions(builder, orders);
        
        // 设置支付方式分布
        setPaymentMethodDistribution(builder, orders);
        
        // 设置商品统计
        setProductStatistics(builder, orders);
        
        // 设置客户统计
        setCustomerStatistics(builder, orders);
        
        // 设置配送统计
        setDeliveryStatistics(builder, orders);
        
        // 设置退款统计
        setRefundStatistics(builder, orders);
        
        // 设置评价统计
        setReviewStatistics(builder, orders);
        
        // 设置趋势分析
        setTrendAnalysis(builder, shopId, startDate, endDate);
        
        // 设置对比分析
        setComparisonAnalysis(builder, shopId, startDate, endDate);
        
        // 设置预测分析
        setPredictionAnalysis(builder, orders, startDate, endDate);
        
        return builder.build();
    }
    
    /**
     * 获取商家名称
     */
    private String getShopName(Long shopId) {
        if (shopId == null) return "所有商家";
        // 这里应该从数据库查询商家名称
        return "商家" + shopId;
    }
    
    /**
     * 确定统计周期
     */
    private String determinePeriod(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        if (days == 1) return "DAY";
        else if (days == 7) return "WEEK";
        else if (days >= 28 && days <= 31) return "MONTH";
        else if (days >= 85 && days <= 95) return "QUARTER";
        else if (days >= 360 && days <= 370) return "YEAR";
        else return "CUSTOM";
    }
    
    /**
     * 获取时间段内的订单
     */
    private List<Order> getOrdersInPeriod(Long shopId, LocalDateTime startTime, LocalDateTime endTime) {
        if (shopId != null) {
            return orderRepository.findByShopIdAndCreateTimeBetween(shopId, startTime, endTime);
        } else {
            return orderRepository.findByCreateTimeBetween(startTime, endTime);
        }
    }
    
    /**
     * 设置基础统计
     */
    private void setBasicStatistics(OrderStatisticsVO.OrderStatisticsVOBuilder builder, List<Order> orders) {
        // 订单数量统计
        long totalOrders = orders.size();
        long completedOrders = orders.stream().filter(o -> "COMPLETED".equals(o.getStatus().name())).count();
        long cancelledOrders = orders.stream().filter(o -> "CANCELLED".equals(o.getStatus().name())).count();
        long pendingOrders = orders.stream().filter(o -> 
                "PENDING".equals(o.getStatus().name()) || 
                "CONFIRMED".equals(o.getStatus().name()) || 
                "PREPARING".equals(o.getStatus().name())).count();
        long refundedOrders = orders.stream().filter(o -> "REFUNDED".equals(o.getStatus().name())).count();
        
        builder.totalOrders((int) totalOrders)
                .completedOrders((int) completedOrders)
                .cancelledOrders((int) cancelledOrders)
                .pendingOrders((int) pendingOrders)
                .refundedOrders((int) refundedOrders);
        
        // 金额统计
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal maxOrderValue = BigDecimal.ZERO;
        BigDecimal minOrderValue = totalOrders > 0 ? null : BigDecimal.ZERO;
        
        for (Order order : orders) {
            if (order.getFinalAmount() != null) {
                BigDecimal amount = order.getFinalAmount();
                totalRevenue = totalRevenue.add(amount);
                
                if (amount.compareTo(maxOrderValue) > 0) {
                    maxOrderValue = amount;
                }
                
                if (minOrderValue == null || amount.compareTo(minOrderValue) < 0) {
                    minOrderValue = amount;
                }
            }
        }
        
        BigDecimal averageOrderValue = totalOrders == 0 ? BigDecimal.ZERO :
                totalRevenue.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
        
        builder.totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .maxOrderValue(maxOrderValue)
                .minOrderValue(minOrderValue != null ? minOrderValue : BigDecimal.ZERO);
        
        // 客户统计
        Set<Long> customerIds = orders.stream()
                .map(Order::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        builder.totalCustomers(customerIds.size());
        
        // 商品统计
        int totalItemsSold = 0;
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            totalItemsSold += items.stream().mapToInt(OrderItem::getQuantity).sum();
        }
        
        BigDecimal averageItemsPerOrder = totalOrders == 0 ? BigDecimal.ZERO :
                new BigDecimal(totalItemsSold).divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
        
        builder.totalItemsSold(totalItemsSold)
                .averageItemsPerOrder(averageItemsPerOrder);
    }
    
    /**
     * 设置时间分布
     */
    private void setTimeDistributions(OrderStatisticsVO.OrderStatisticsVOBuilder builder, List<Order> orders) {
        // 小时分布
        Map<String, Integer> hourlyDistribution = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyDistribution.put(String.format("%02d", i), 0);
        }
        
        for (Order order : orders) {
            if (order.getCreateTime() != null) {
                int hour = order.getCreateTime().getHour();
                String hourKey = String.format("%02d", hour);
                hourlyDistribution.put(hourKey, hourlyDistribution.get(hourKey) + 1);
            }
        }
        
        builder.hourlyDistribution(hourlyDistribution);
        
        // 星期分布
        Map<String, Integer> dailyDistribution = new HashMap<>();
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (String day : days) {
            dailyDistribution.put(day, 0);
        }
        
        for (Order order : orders) {
            if (order.getCreateTime() != null) {
                String dayOfWeek = order.getCreateTime().getDayOfWeek().name().substring(0, 3);
                dailyDistribution.put(dayOfWeek, dailyDistribution.get(dayOfWeek) + 1);
            }
        }
        
        builder.dailyDistribution(dailyDistribution);
        
        // 月份分布（如果跨月）
        Map<String, Integer> monthlyDistribution = new HashMap<>();
        for (Order order : orders) {
            if (order.getCreateTime() != null) {
                String monthKey = order.getCreateTime().getMonthValue() + "月";
                monthlyDistribution.put(monthKey, monthlyDistribution.getOrDefault(monthKey, 0) + 1);
            }
        }
        
        builder.monthlyDistribution(monthlyDistribution);
    }
    
    /**
     * 设置状态分布
     */
    private void setStatusDistributions(OrderStatisticsVO.OrderStatisticsVOBuilder builder, List<Order> orders) {
        Map<String, Integer> statusDistribution = new HashMap<>();
        Map<String, Integer> paymentStatusDistribution = new HashMap<>();
        Map<String, Integer> deliveryStatusDistribution = new HashMap<>();
        
        for (Order order : orders) {
            // 订单状态
            String status = order.getStatus().name();
            statusDistribution.put(status, statusDistribution.getOrDefault(status, 0) + 1);
            
            // 支付状态
            String paymentStatus = order.getPaymentStatus();
            if (paymentStatus != null) {
                paymentStatusDistribution.put(paymentStatus, 
                        paymentStatusDistribution.getOrDefault(paymentStatus, 0) + 1);
            }
            
            // 配送状态
            String deliveryStatus = order.getDeliveryStatus();
            if (deliveryStatus != null) {
                deliveryStatusDistribution.put(deliveryStatus, 
                        deliveryStatusDistribution.getOrDefault(deliveryStatus, 0) + 1);
            }
        }
        
        builder.statusDistribution(statusDistribution)
                .paymentStatusDistribution(paymentStatusDistribution)
                .deliveryStatusDistribution(deliveryStatusDistribution);
    }
    
    /**
     * 设置支付方式分布
     */
    private void setPaymentMethodDistribution(OrderStatisticsVO.OrderStatisticsVOBuilder builder, List<Order> orders) {
        Map<String, BigDecimal> paymentMethodDistribution = new HashMap<>();
        
        for (Order order : orders) {
            String paymentMethod = order.getPaymentMethod();
            if (paymentMethod != null && order.getFinalAmount() != null) {
                BigDecimal current = paymentMethodDistribution.getOrDefault(paymentMethod, BigDecimal.ZERO);
                paymentMethodDistribution.put(paymentMethod, current.add(order.getFinalAmount()));
            }
        }
        
        builder.paymentMethodDistribution(paymentMethodDistribution);
    }
    
    /**
     * 设置商品统计
     */
    private void setProductStatistics(OrderStatisticsVO.OrderStatisticsVOBuilder builder, List<Order> orders) {
        // 商品销售统计
        Map<Long, ProductStat> productStats = new HashMap<>();
        Map<String, CategoryStat> categoryStats = new HashMap<>();
        
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                // 商品统计
                ProductStat productStat = productStats.getOrDefault(item.getProductId(), new ProductStat());
                productStat.quantitySold += item.getQuantity();
                productStat.revenue = productStat.revenue.add(
                        item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                );
                productStat.orderCount++;
                productStats.put(item.getProductId(), productStat);
                
                // 分类统计（简化）
                String category = "默认分类";
                CategoryStat categoryStat = categoryStats.getOrDefault(category, new CategoryStat());
                categoryStat.quantitySold += item.getQuantity();
                categoryStat.revenue = categoryStat.revenue.add(
                        item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                );
                categoryStat.productCount = Math.max(categoryStat.productCount, productStats.size());
                categoryStats.put(category, categoryStat);
            }
        }
        
        // 热门商品
        List<OrderStatisticsVO.ProductStatVO> topProducts = productStats.entrySet().stream()
                .sorted((a, b) -> b.getValue().revenue.compareTo(a.getValue().revenue))
                .limit(10)
                .map(entry -> {
                    ProductStat stat = entry.getValue();
                    return OrderStatisticsVO.ProductStatVO.builder()
                            .productId(entry.getKey())
                            .productName("商品" + entry.getKey())
                            .quantitySold(stat.quantitySold)
                            .revenue(stat.revenue)
                            .orderCount(stat.orderCount)
                            .percentageOfTotal(calculatePercentage(stat.revenue, builder.totalRevenue))
                            .build();
                })
                .collect(Collectors.toList());
        
        // 热门分类
        List<OrderStatisticsVO.CategoryStatVO> topCategories = categoryStats.entrySet().stream()
                .sorted((a, b) -> b.getValue().revenue.compareTo(a.getValue().revenue))
                .limit(5)
                .map(entry -> {
                    CategoryStat stat = entry.getValue();
                    return OrderStatisticsVO.CategoryStatVO.builder()
                            .category(entry.getKey())
                            .quantitySold(stat.quantitySold)
                            .revenue(stat.revenue)
                            .percentageOfTotal(calculatePercentage(stat.revenue, builder.totalRevenue))
                            .productCount(stat.productCount)
                            .build();
                })
                .collect(Collectors.toList());
        
        builder.topProducts(topProducts)
                .topCategories(topCategories);
    }
    
    /**
     * 设置客户统计
     */
    private void setCustomerStatistics(OrderStatisticsVO.OrderStatisticsVOBuilder builder, List<Order> orders) {
        // 客户消费统计
        Map<Long, CustomerStat> customerStats = new HashMap<>();
        
        for (Order order : orders) {
            Long customerId = order.getUserId();
            if (customerId != null) {
                CustomerStat stat = customerStats.getOrDefault(customerId, new CustomerStat());
                stat.orderCount++;
                stat.totalSpent = stat.totalSpent.add(
                        order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO
                );
                
                if (stat.firstOrderDate == null || order.getCreateTime().isBefore(stat.firstOrderDate)) {
                    stat.firstOrderDate = order.getCreateTime();
                }
                if (stat.lastOrderDate == null || order.getCreateTime().isAfter(stat.lastOrderDate)) {
                    stat.lastOrderDate = order.getCreateTime();
                }
                
                customerStats.put(customerId, stat);
            }
        }
        
        // 热门客户
        List<OrderStatisticsVO.CustomerStatVO> topCustomers = customerStats.entrySet().stream()
                .sorted((a, b) -> b.getValue().totalSpent.compareTo(a.getValue().totalSpent))
                .limit(10)
                .map(entry -> {
                    CustomerStat stat = entry.getValue();
                    return OrderStatisticsVO.CustomerStatVO.builder()
                            .customerId(entry.getKey())
                            .customerName("客户" + entry.getKey())
                            .orderCount(stat.orderCount)
                            .totalSpent(stat.totalSpent)
                            .averageOrderValue(stat.orderCount > 0 ? 
                                    stat.totalSpent.divide(new BigDecimal(stat.orderCount), 2, BigDecimal.ROUND_HALF_UP) : 
                                    BigDecimal.ZERO)
                            .firstOrderDate(stat.firstOrderDate)
                            .lastOrderDate(stat.lastOrderDate)
                            .daysSinceLastOrder(stat.lastOrderDate != null ? 
                                    (int) ChronoUnit.DAYS.between(stat.lastOrderDate, LocalDateTime.now()) : 0)
                            .build();
                })
                .collect(Collectors.toList());
        
        // 客户区域分布（简化）
        Map<String, Integer> customerRegionDistribution = new HashMap<>();
        for (Order order : orders) {
            String region = extractRegionFromAddress(order.getDeliveryAddress());
            if (region != null) {
                customerRegionDistribution.put(region, 
                        customerRegionDistribution.getOrDefault(region, 0) + 1);
            }
        }
        
        builder.topCustomers(topCustomers)
                .customerRegionDistribution(customerRegionDistribution);
    }
    
    /**
     * 设置配送统计
     */
    private void setDeliveryStatistics(OrderStatisticsVO.OrderStatisticsVOBuilder builder, List<Order> orders) {
        // 计算平均配送时间和准时率（简化）
        long totalDeliveryTime = 0;
        long onTimeDeliveries = 0;
        long totalDeliveries = 0;
        Map<String, Integer> deliveryPersonPerformance = new HashMap<>();
        
        for (Order order : orders) {
            if (order.getDeliveryStatus() != null && order.getEstimatedDeliveryTime() != null && 
                order.getActualDeliveryTime() != null) {
                
                // 计算配送时间
                long deliveryMinutes = ChronoUnit.MINUTES.between(
                        order.getEstimatedDeliveryTime(),