package com.healthyfood.repository;

import com.healthyfood.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    
    /**
     * 根据用户ID查找订单
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据用户ID和状态查找订单
     */
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
    /**
     * 根据商家ID查找订单
     */
    List<Order> findByShopId(Long shopId);
    
    /**
     * 根据商家ID和状态查找订单
     */
    List<Order> findByShopIdAndStatus(Long shopId, Order.OrderStatus status);
    
    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根据状态查找订单
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * 根据支付状态查找订单
     */
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    /**
     * 根据配送状态查找订单
     */
    List<Order> findByDeliveryStatus(Order.DeliveryStatus deliveryStatus);
    
    /**
     * 根据用户ID和支付状态查找订单
     */
    List<Order> findByUserIdAndPaymentStatus(Long userId, Order.PaymentStatus paymentStatus);
    
    /**
     * 根据用户ID和配送状态查找订单
     */
    List<Order> findByUserIdAndDeliveryStatus(Long userId, Order.DeliveryStatus deliveryStatus);
    
    /**
     * 根据创建时间范围查找订单
     */
    List<Order> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据预计送达时间范围查找订单
     */
    List<Order> findByEstimatedDeliveryTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据实际送达时间范围查找订单
     */
    List<Order> findByActualDeliveryTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据订单金额范围查找订单
     */
    List<Order> findByTotalAmountBetween(Double minAmount, Double maxAmount);
    
    /**
     * 查找用户的最近订单
     */
    List<Order> findTop10ByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 查找商家的最近订单
     */
    List<Order> findTop10ByShopIdOrderByCreateTimeDesc(Long shopId);
    
    /**
     * 查找待处理的订单（新订单）
     */
    List<Order> findByStatusOrderByCreateTimeAsc(Order.OrderStatus status);
    
    /**
     * 查找需要配送的订单
     */
    List<Order> findByDeliveryStatusOrderByEstimatedDeliveryTimeAsc(Order.DeliveryStatus deliveryStatus);
    
    /**
     * 查找待支付的订单
     */
    List<Order> findByPaymentStatusOrderByCreateTimeAsc(Order.PaymentStatus paymentStatus);
    
    /**
     * 检查订单号是否存在
     */
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * 统计用户的订单数量
     */
    Long countByUserId(Long userId);
    
    /**
     * 统计商家的订单数量
     */
    Long countByShopId(Long shopId);
    
    /**
     * 统计各状态的订单数量
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
    
    /**
     * 统计各支付状态的订单数量
     */
    @Query("SELECT o.paymentStatus, COUNT(o) FROM Order o GROUP BY o.paymentStatus")
    List<Object[]> countOrdersByPaymentStatus();
    
    /**
     * 统计各配送状态的订单数量
     */
    @Query("SELECT o.deliveryStatus, COUNT(o) FROM Order o GROUP BY o.deliveryStatus")
    List<Object[]> countOrdersByDeliveryStatus();
    
    /**
     * 计算用户的订单总金额
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.userId = :userId")
    Double calculateUserTotalSpent(@Param("userId") Long userId);
    
    /**
     * 计算商家的订单总金额
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.shopId = :shopId")
    Double calculateShopTotalRevenue(@Param("shopId") Long shopId);
    
    /**
     * 计算用户的平均订单金额
     */
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.userId = :userId")
    Double calculateUserAverageOrderAmount(@Param("userId") Long userId);
    
    /**
     * 计算商家的平均订单金额
     */
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.shopId = :shopId")
    Double calculateShopAverageOrderAmount(@Param("shopId") Long shopId);
    
    /**
     * 查找今日订单
     */
    @Query("SELECT o FROM Order o WHERE DATE(o.createTime) = CURRENT_DATE")
    List<Order> findTodayOrders();
    
    /**
     * 查找本周订单
     */
    @Query("SELECT o FROM Order o WHERE YEARWEEK(o.createTime) = YEARWEEK(CURRENT_DATE)")
    List<Order> findThisWeekOrders();
    
    /**
     * 查找本月订单
     */
    @Query("SELECT o FROM Order o WHERE YEAR(o.createTime) = YEAR(CURRENT_DATE) AND MONTH(o.createTime) = MONTH(CURRENT_DATE)")
    List<Order> findThisMonthOrders();
    
    /**
     * 统计今日订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.createTime) = CURRENT_DATE")
    Long countTodayOrders();
    
    /**
     * 统计今日订单金额
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE DATE(o.createTime) = CURRENT_DATE")
    Double calculateTodayRevenue();
    
    /**
     * 统计本周订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE YEARWEEK(o.createTime) = YEARWEEK(CURRENT_DATE)")
    Long countThisWeekOrders();
    
    /**
     * 统计本周订单金额
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE YEARWEEK(o.createTime) = YEARWEEK(CURRENT_DATE)")
    Double calculateThisWeekRevenue();
    
    /**
     * 统计本月订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE YEAR(o.createTime) = YEAR(CURRENT_DATE) AND MONTH(o.createTime) = MONTH(CURRENT_DATE)")
    Long countThisMonthOrders();
    
    /**
     * 统计本月订单金额
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE YEAR(o.createTime) = YEAR(CURRENT_DATE) AND MONTH(o.createTime) = MONTH(CURRENT_DATE)")
    Double calculateThisMonthRevenue();
    
    /**
     * 查找超时未支付的订单
     */
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PENDING' AND o.createTime < :timeoutTime")
    List<Order> findTimeoutPaymentOrders(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 查找超时未配送的订单
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryStatus = 'PREPARING' AND o.createTime < :timeoutTime")
    List<Order> findTimeoutDeliveryOrders(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 查找需要评价的订单（已送达但未评价）
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryStatus = 'DELIVERED' AND o.rating IS NULL AND o.actualDeliveryTime < :timeoutTime")
    List<Order> findOrdersNeedRating(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 查找需要退款的订单
     */
    @Query("SELECT o FROM Order o WHERE o.refundStatus = 'REQUESTED'")
    List<Order> findRefundRequestedOrders();
    
    /**
     * 查找取消的订单
     */
    List<Order> findByStatusOrderByCreateTimeDesc(Order.OrderStatus status);
    
    /**
     * 查找完成的订单
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'COMPLETED' AND o.rating IS NOT NULL")
    List<Order> findCompletedAndRatedOrders();
    
    /**
     * 根据配送员ID查找订单
     */
    List<Order> findByDeliveryPersonId(Long deliveryPersonId);
    
    /**
     * 根据配送员ID和状态查找订单
     */
    List<Order> findByDeliveryPersonIdAndDeliveryStatus(Long deliveryPersonId, Order.DeliveryStatus deliveryStatus);
    
    /**
     * 查找待分配的配送订单
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryStatus = 'PENDING' AND o.deliveryPersonId IS NULL")
    List<Order> findUnassignedDeliveryOrders();
    
    /**
     * 查找配送中的订单
     */
    List<Order> findByDeliveryStatusOrderByEstimatedDeliveryTimeAsc(Order.DeliveryStatus deliveryStatus);
    
    /**
     * 计算订单的平均配送时间
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, o.createTime, o.actualDeliveryTime)) FROM Order o WHERE o.actualDeliveryTime IS NOT NULL")
    Double calculateAverageDeliveryTime();
    
    /**
     * 计算订单的平均评分
     */
    @Query("SELECT AVG(o.rating) FROM Order o WHERE o.rating IS NOT NULL")
    Double calculateAverageOrderRating();
    
    /**
     * 查找高频下单用户
     */
    @Query("SELECT o.userId, COUNT(o) as orderCount FROM Order o GROUP BY o.userId HAVING orderCount > :minOrders ORDER BY orderCount DESC")
    List<Object[]> findFrequentUsers(@Param("minOrders") Integer minOrders);
    
    /**
     * 查找高价值用户
     */
    @Query("SELECT o.userId, SUM(o.totalAmount) as totalSpent FROM Order o GROUP BY o.userId HAVING totalSpent > :minAmount ORDER BY totalSpent DESC")
    List<Object[]> findHighValueUsers(@Param("minAmount") Double minAmount);
    
    /**
     * 查找热门商家
     */
    @Query("SELECT o.shopId, COUNT(o) as orderCount FROM Order o GROUP BY o.shopId ORDER BY orderCount DESC")
    List<Object[]> findPopularShops();
    
    /**
     * 查找高收入商家
     */
    @Query("SELECT o.shopId, SUM(o.totalAmount) as totalRevenue FROM Order o GROUP BY o.shopId ORDER BY totalRevenue DESC")
    List<Object[]> findHighRevenueShops();
    
    /**
     * 查找高峰时段订单
     */
    @Query("SELECT HOUR(o.createTime) as hour, COUNT(o) as orderCount FROM Order o GROUP BY HOUR(o.createTime) ORDER BY orderCount DESC")
    List<Object[]> findPeakHours();
    
    /**
     * 查找热门商品（通过订单项）
     */
    @Query("SELECT oi.productId, SUM(oi.quantity) as totalQuantity FROM OrderItem oi GROUP BY oi.productId ORDER BY totalQuantity DESC")
    List<Object[]> findPopularProducts();
    
    /**
     * 更新订单状态
     */
    @Query("UPDATE Order o SET o.status = :status, o.lastUpdateTime = CURRENT_TIMESTAMP WHERE o.id = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Order.OrderStatus status);
    
    /**
     * 更新支付状态
     */
    @Query("UPDATE Order o SET o.paymentStatus = :paymentStatus, o.paymentTime = :paymentTime WHERE o.id = :orderId")
    void updatePaymentStatus(@Param("orderId") Long orderId, 
                            @Param("paymentStatus") Order.PaymentStatus paymentStatus,
                            @Param("paymentTime") LocalDateTime paymentTime);
    
    /**
     * 更新配送状态
     */
    @Query("UPDATE Order o SET o.deliveryStatus = :deliveryStatus, o.deliveryPersonId = :deliveryPersonId WHERE o.id = :orderId")
    void updateDeliveryStatus(@Param("orderId") Long orderId,
                             @Param("deliveryStatus") Order.DeliveryStatus deliveryStatus,
                             @Param("deliveryPersonId") Long deliveryPersonId);
    
    /**
     * 更新订单评分
     */
    @Query("UPDATE Order o SET o.rating = :rating, o.review = :review, o.reviewTime = CURRENT_TIMESTAMP WHERE o.id = :orderId")
    void updateOrderRating(@Param("orderId") Long orderId,
                          @Param("rating") Double rating,
                          @Param("review") String review);
    
    /**
     * 更新退款状态
     */
    @Query("UPDATE Order o SET o.refundStatus = :refundStatus, o.refundAmount = :refundAmount, o.refundTime = CURRENT_TIMESTAMP WHERE o.id = :orderId")
    void updateRefundStatus(@Param("orderId") Long orderId,
                           @Param("refundStatus") Order.RefundStatus refundStatus,
                           @Param("refundAmount") Double refundAmount);
    
    /**
     * 批量更新订单状态
     */
    @Query("UPDATE Order o SET o.status = :status WHERE o.id IN :orderIds")
    void batchUpdateOrderStatus(@Param("orderIds") List<Long> orderIds,
                               @Param("status") Order.OrderStatus status);
    
    /**
     * 查找需要生成发票的订单
     */
    @Query("SELECT o FROM Order o WHERE o.invoiceGenerated = false AND o.paymentStatus = 'PAID'")
    List<Order> findOrdersNeedInvoice();
    
    /**
     * 标记发票已生成
     */
    @Query("UPDATE Order o SET o.invoiceGenerated = true, o.invoiceNumber = :invoiceNumber WHERE o.id = :orderId")
    void markInvoiceGenerated(@Param("orderId") Long orderId,
                             @Param("invoiceNumber") String invoiceNumber);
}