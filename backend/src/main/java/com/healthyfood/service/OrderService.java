package com.healthyfood.service;

import com.healthyfood.common.ApiResult;
import com.healthyfood.common.ErrorCode;
import com.healthyfood.common.Constants;
import com.healthyfood.entity.*;
import com.healthyfood.repository.*;
import com.healthyfood.vo.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private RedisService redisService;
    
    /**
     * 创建订单
     */
    @Transactional
    public ApiResult<OrderVO> createOrder(Long userId, OrderCreateRequest request) {
        try {
            // 1. 验证用户
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ApiResult.error(ErrorCode.USER_NOT_EXISTS, "用户不存在");
            }
            
            // 2. 验证商家
            Shop shop = shopRepository.findById(request.getShopId()).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            if (shop.getStatus() != Shop.ShopStatus.ACTIVE) {
                return ApiResult.error(ErrorCode.SHOP_NOT_ACTIVE, "商家未营业");
            }
            
            // 3. 验证商品
            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ApiResult.error(ErrorCode.PARAM_ERROR, "订单商品不能为空");
            }
            
            List<OrderItemRequest> items = request.getItems();
            Map<Long, Product> productMap = new HashMap<>();
            Map<Long, Integer> quantityMap = new HashMap<>();
            
            for (OrderItemRequest item : items) {
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                if (product == null) {
                    return ApiResult.error(ErrorCode.PRODUCT_NOT_EXISTS, "商品不存在: " + item.getProductId());
                }
                
                if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                    return ApiResult.error(ErrorCode.PRODUCT_NOT_ACTIVE, "商品已下架: " + product.getName());
                }
                
                if (product.getShopId() != request.getShopId()) {
                    return ApiResult.error(ErrorCode.PRODUCT_SHOP_MISMATCH, "商品不属于该商家: " + product.getName());
                }
                
                if (product.getStockQuantity() < item.getQuantity()) {
                    return ApiResult.error(ErrorCode.INSUFFICIENT_STOCK, "库存不足: " + product.getName());
                }
                
                productMap.put(product.getId(), product);
                quantityMap.put(product.getId(), item.getQuantity());
            }
            
            // 4. 生成订单号
            String orderNumber = generateOrderNumber();
            
            // 5. 计算订单金额
            OrderCalculationResult calculation = calculateOrderAmount(items, productMap, shop);
            
            // 6. 验证配送地址
            if (!validateDeliveryAddress(user, shop, request.getDeliveryAddress())) {
                return ApiResult.error(ErrorCode.DELIVERY_OUT_OF_RANGE, "超出配送范围");
            }
            
            // 7. 创建订单
            Order order = Order.builder()
                    .orderNumber(orderNumber)
                    .userId(userId)
                    .shopId(request.getShopId())
                    .totalAmount(calculation.getTotalAmount())
                    .discountAmount(calculation.getDiscountAmount())
                    .deliveryFee(calculation.getDeliveryFee())
                    .finalAmount(calculation.getFinalAmount())
                    .deliveryAddress(request.getDeliveryAddress())
                    .deliveryLatitude(request.getDeliveryLatitude())
                    .deliveryLongitude(request.getDeliveryLongitude())
                    .contactName(request.getContactName())
                    .contactPhone(request.getContactPhone())
                    .deliveryInstructions(request.getDeliveryInstructions())
                    .paymentMethod(request.getPaymentMethod())
                    .estimatedDeliveryTime(calculateEstimatedDeliveryTime(shop, request.getDeliveryAddress()))
                    .status(Order.OrderStatus.PENDING)
                    .paymentStatus(Order.PaymentStatus.PENDING)
                    .deliveryStatus(Order.DeliveryStatus.PENDING)
                    .refundStatus(Order.RefundStatus.NONE)
                    .createTime(LocalDateTime.now())
                    .lastUpdateTime(LocalDateTime.now())
                    .build();
            
            // 8. 保存订单
            order = orderRepository.save(order);
            
            // 9. 创建订单项
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemRequest item : items) {
                Product product = productMap.get(item.getProductId());
                
                OrderItem orderItem = OrderItem.builder()
                        .orderId(order.getId())
                        .productId(product.getId())
                        .productName(product.getName())
                        .productImage(product.getImages() != null && !product.getImages().isEmpty() ? 
                                product.getImages().get(0) : null)
                        .quantity(item.getQuantity())
                        .unitPrice(product.getPrice())
                        .totalPrice(product.getPrice() * item.getQuantity())
                        .calories(product.getCalories() != null ? product.getCalories() * item.getQuantity() : null)
                        .protein(product.getProtein() != null ? product.getProtein() * item.getQuantity() : null)
                        .carbohydrates(product.getCarbohydrates() != null ? product.getCarbohydrates() * item.getQuantity() : null)
                        .fat(product.getFat() != null ? product.getFat() * item.getQuantity() : null)
                        .createTime(LocalDateTime.now())
                        .build();
                
                orderItems.add(orderItem);
                
                // 更新商品销量和库存
                productRepository.updateSalesCount(product.getId(), item.getQuantity());
                productRepository.decrementStock(product.getId(), item.getQuantity());
            }
            
            orderItemRepository.saveAll(orderItems);
            
            // 10. 更新商家统计
            shopRepository.updateShopStatistics(shop.getId(), calculation.getFinalAmount());
            
            // 11. 返回响应
            OrderVO orderVO = convertToOrderVO(order, shop, user);
            
            log.info("订单创建成功: orderId={}, orderNumber={}, userId={}, amount={}", 
                    order.getId(), orderNumber, userId, calculation.getFinalAmount());
            return ApiResult.success(orderVO);
            
        } catch (Exception e) {
            log.error("创建订单失败: userId={}", userId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "创建订单失败");
        }
    }
    
    /**
     * 获取订单详情
     */
    public ApiResult<OrderDetailVO> getOrderDetail(Long orderId, Long userId) {
        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ApiResult.error(ErrorCode.ORDER_NOT_EXISTS, "订单不存在");
            }
            
            // 2. 验证权限（用户只能查看自己的订单，商家可以查看自己的订单）
            if (!order.getUserId().equals(userId)) {
                // 检查是否是商家查看自己的订单
                Shop shop = shopRepository.findById(order.getShopId()).orElse(null);
                // 这里简化处理，实际应该检查商家权限
            }
            
            // 3. 获取用户信息
            User user = userRepository.findById(order.getUserId()).orElse(null);
            
            // 4. 获取商家信息
            Shop shop = shopRepository.findById(order.getShopId()).orElse(null);
            
            // 5. 获取订单项
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            
            // 6. 构建订单详情
            OrderDetailVO detail = buildOrderDetailVO(order, shop, user, orderItems);
            
            return ApiResult.success(detail);
            
        } catch (Exception e) {
            log.error("获取订单详情失败: orderId={}", orderId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取订单详情失败");
        }
    }
    
    /**
     * 获取用户订单列表
     */
    public ApiResult<Page<OrderListVO>> getUserOrders(Long userId, OrderSearchRequest request) {
        try {
            // 1. 验证用户
            if (!userRepository.existsById(userId)) {
                return ApiResult.error(ErrorCode.USER_NOT_EXISTS, "用户不存在");
            }
            
            // 2. 构建分页和排序
            Pageable pageable = PageRequest.of(
                    request.getPage() != null ? request.getPage() : 0,
                    request.getSize() != null ? request.getSize() : 10,
                    Sort.by(Sort.Direction.DESC, "createTime")
            );
            
            // 3. 构建查询条件
            Specification<Order> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 限定用户
                predicates.add(cb.equal(root.get("userId"), userId));
                
                // 状态筛选
                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), request.getStatus()));
                }
                
                // 支付状态筛选
                if (request.getPaymentStatus() != null) {
                    predicates.add(cb.equal(root.get("paymentStatus"), request.getPaymentStatus()));
                }
                
                // 配送状态筛选
                if (request.getDeliveryStatus() != null) {
                    predicates.add(cb.equal(root.get("deliveryStatus"), request.getDeliveryStatus()));
                }
                
                // 时间范围筛选
                if (request.getStartTime() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), request.getStartTime()));
                }
                
                if (request.getEndTime() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), request.getEndTime()));
                }
                
                // 金额范围筛选
                if (request.getMinAmount() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("finalAmount"), request.getMinAmount()));
                }
                
                if (request.getMaxAmount() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("finalAmount"), request.getMaxAmount()));
                }
                
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            
            // 4. 执行查询
            Page<Order> orderPage = orderRepository.findAll(spec, pageable);
            
            // 5. 获取商家信息
            Map<Long, Shop> shopMap = new HashMap<>();
            if (!orderPage.isEmpty()) {
                List<Long> shopIds = orderPage.getContent().stream()
                        .map(Order::getShopId)
                        .distinct()
                        .collect(Collectors.toList());
                
                List<Shop> shops = shopRepository.findByIdIn(shopIds);
                shopMap = shops.stream()
                        .collect(Collectors.toMap(Shop::getId, shop -> shop));
            }
            
            // 6. 转换为VO
            Page<OrderListVO> resultPage = orderPage.map(order -> {
                Shop shop = shopMap.get(order.getShopId());
                return convertToOrderListVO(order, shop);
            });
            
            return ApiResult.success(resultPage);
            
        } catch (Exception e) {
            log.error("获取用户订单列表失败: userId={}", userId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取用户订单列表失败");
        }
    }
    
    /**
     * 更新订单状态
     */
    @Transactional
    public ApiResult<Void> updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ApiResult.error(ErrorCode.ORDER_NOT_EXISTS, "订单不存在");
            }
            
            // 2. 验证状态转换
            if (!isValidStatusTransition(order.getStatus(), request.getStatus())) {
                return ApiResult.error(ErrorCode.ORDER_STATUS_ERROR, "无效的状态转换");
            }
            
            // 3. 更新状态
            Order.OrderStatus oldStatus = order.getStatus();
            order.setStatus(request.getStatus());
            order.setLastUpdateTime(LocalDateTime.now());
            
            // 4. 处理特殊状态
            switch (request.getStatus()) {
                case CANCELLED:
                    handleOrderCancellation(order, request.getReason());
                    break;
                case COMPLETED:
                    order.setCompletionTime(LocalDateTime.now());
                    break;
                case REFUNDED:
                    handleOrderRefund(order, request.getRefundAmount(), request.getReason());
                    break;
            }
            
            // 5. 保存更新
            orderRepository.save(order);
            
            // 6. 记录状态变更日志
            logOrderStatusChange(orderId, oldStatus, request.getStatus(), 
                               request.getReason(), request.getOperator());
            
            // 7. 发送状态变更通知
            sendOrderStatusNotification(order, oldStatus, request.getStatus());
            
            log.info("订单状态更新成功: orderId={}, oldStatus={}, newStatus={}", 
                    orderId, oldStatus, request.getStatus());
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("更新订单状态失败: orderId={}", orderId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "更新订单状态失败");
        }
    }
    
    /**
     * 更新支付状态
     */
    @Transactional
    public ApiResult<Void> updatePaymentStatus(Long orderId, PaymentStatusUpdateRequest request) {
        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ApiResult.error(ErrorCode.ORDER_NOT_EXISTS, "订单不存在");
            }
            
            // 2. 验证支付状态转换
            if (!isValidPaymentStatusTransition(order.getPaymentStatus(), request.getPaymentStatus())) {
                return ApiResult.error(ErrorCode.PAYMENT_STATUS_ERROR, "无效的支付状态转换");
            }
            
            // 3. 更新支付状态
            Order.PaymentStatus oldStatus = order.getPaymentStatus();
            order.setPaymentStatus(request.getPaymentStatus());
            order.setPaymentTime(request.getPaymentTime() != null ? request.getPaymentTime() : LocalDateTime.now());
            order.setPaymentTransactionId(request.getTransactionId());
            order.setLastUpdateTime(LocalDateTime.now());
            
            // 4. 如果支付成功，更新订单状态为已确认
            if (request.getPaymentStatus() == Order.PaymentStatus.PAID) {
                order.setStatus(Order.OrderStatus.CONFIRMED);
            }
            
            // 5. 保存更新
            orderRepository.save(order);
            
            // 6. 记录支付日志
            logPaymentStatusChange(orderId, oldStatus, request.getPaymentStatus(), 
                                 request.getTransactionId(), request.getPaymentMethod());
            
            log.info("支付状态更新成功: orderId={}, oldStatus={}, newStatus={}", 
                    orderId, oldStatus, request.getPaymentStatus());
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("更新支付状态失败: orderId={}", orderId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "更新支付状态失败");
        }
    }
    
    /**
     * 更新配送状态
     */
    @Transactional
    public ApiResult<Void> updateDeliveryStatus(Long orderId, DeliveryStatusUpdateRequest request) {
        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ApiResult.error(ErrorCode.ORDER_NOT_EXISTS, "订单不存在");
            }
            
            // 2. 验证配送状态转换
            if (!isValidDeliveryStatusTransition(order.getDeliveryStatus(), request.getDeliveryStatus())) {
                return ApiResult.error(ErrorCode.DELIVERY_STATUS_ERROR, "无效的配送状态转换");
            }
            
            // 3. 更新配送状态
            Order.DeliveryStatus oldStatus = order.getDeliveryStatus();
            order.setDeliveryStatus(request.getDeliveryStatus());
            order.setDeliveryPersonId(request.getDeliveryPersonId());
            order.setLastUpdateTime(LocalDateTime.now());
            
            // 4. 处理特殊状态
            switch (request.getDeliveryStatus()) {
                case PICKED_UP:
                    order.setPickupTime(LocalDateTime.now());
                    break;
                case DELIVERED:
                    order.setActualDeliveryTime(LocalDateTime.now());
                    order.setStatus(Order.OrderStatus.COMPLETED);
                    order.setCompletionTime(LocalDateTime.now());
                    break;
                case FAILED:
                    order.setDeliveryFailureReason(request.getFailureReason());
                    break;
            }
            
            // 5. 保存更新
            orderRepository.save(order);
            
            // 6. 记录配送日志
            logDeliveryStatusChange(orderId, oldStatus, request.getDeliveryStatus(), 
                                  request.getDeliveryPersonId(), request.getNotes());
            
            log.info("配送状态更新成功: orderId={}, oldStatus={}, newStatus={}", 
                    orderId, oldStatus, request.getDeliveryStatus());
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("更新配送状态失败: orderId={}", orderId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "更新配送状态失败");
        }
    }
    
    /**
     * 提交订单评价
     */
    @Transactional
    public ApiResult<Void> submitOrderRating(Long orderId, Long userId, OrderRatingRequest request) {
        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ApiResult.error(ErrorCode.ORDER_NOT_EXISTS, "订单不存在");
            }
            
            // 2. 验证用户权限
            if (!order.getUserId().equals(userId)) {
                return ApiResult.error(ErrorCode.PERMISSION_DENIED, "无权评价此订单");
            }
            
            // 3. 验证订单状态
            if (order.getStatus() != Order.OrderStatus.COMPLETED) {
                return ApiResult.error(ErrorCode.ORDER_NOT_COMPLETED, "订单未完成，不能评价");
            }
            
            // 4. 检查是否已评价
            if (order.getRating() != null) {
                return ApiResult.error(ErrorCode.ORDER_ALREADY_RATED, "订单已评价");
            }
            
            // 5. 更新订单评价
            order.setRating(request.getRating());
            order.setReview(request.getReview());
            order.setReviewTime(LocalDateTime.now());
            order.setLastUpdateTime(LocalDateTime.now());
            
            // 6. 更新商家评分
            updateShopRating(order.getShopId(), request.getRating());
            
            // 7. 更新商品评分（通过订单项）
            updateProductsRating(orderId, request.getItemRatings());
            
            // 8. 保存更新
            orderRepository.save(order);
            
            // 9. 记录评价日志
            logOrderRating(orderId, request.getRating(), request.getReview());
            
            log.info("订单评价提交成功: orderId={}, rating={}", orderId, request.getRating());
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("提交订单评价失败: orderId={}", orderId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "提交订单评价失败");
        }
    }
    
    /**
     * 申请退款
     */
    @Transactional
    public ApiResult<Void> requestRefund(Long orderId, Long userId, RefundRequest request) {
        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ApiResult.error(ErrorCode.ORDER_NOT_EXISTS, "订单不存在");
            }
            
            // 2. 验证用户权限
            if (!order.getUserId().equals(userId)) {
                return ApiResult.error(ErrorCode.PERMISSION_DENIED, "无权申请退款");
            }
            
            // 3. 验证订单状态
            if (!isRefundable(order)) {
                return ApiResult.error(ErrorCode.ORDER_NOT_REFUNDABLE, "订单不可退款");
            }
            
            // 4. 更新退款状态
            order.setRefundStatus(Order.RefundStatus.REQUESTED);
            order.setRefundReason(request.getReason());
            order.setRefundAmount(request.getAmount() != null ? request.getAmount() : order.getFinalAmount());
            order.setRefundRequestTime(LocalDateTime.now());
            order.setLastUpdateTime(LocalDateTime.now());
            
            // 5. 保存更新
            orderRepository.save(order);
            
            // 6. 记录退款申请日志
            logRefundRequest(orderId, request.getReason(), request.getAmount());
            
            // 7. 发送退款申请通知
            sendRefundRequestNotification(order, request.getReason());
            
            log.info("退款申请提交成功: orderId={}, reason={}, amount={}", 
                    orderId, request.getReason(), request.getRefundAmount());
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("申请退款失败: orderId={}", orderId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "申请退款失败");
        }
    }
    
    /**
     * 处理退款申请
     */
    @Transactional
    public ApiResult<Void> processRefund(Long orderId, RefundProcessRequest request) {
        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ApiResult.error(ErrorCode.ORDER_NOT_EXISTS, "订单不存在");
            }
            
            // 2. 验证退款状态
            if (order.getRefundStatus() != Order.RefundStatus.REQUESTED) {
                return ApiResult.error(ErrorCode.REFUND_NOT_REQUESTED, "退款未申请");
            }
            
            // 3. 更新退款状态
            order.setRefundStatus(request.getApproved() ? Order.RefundStatus.APPROVED : Order.RefundStatus.REJECTED);
            order.setRefundProcessor(request.getProcessor());
            order.setRefundProcessTime(LocalDateTime.now());
            order.setRefundProcessNotes(request.getNotes());
            order.setLastUpdateTime(LocalDateTime.now());
            
            if (request.getApproved()) {
                order.setStatus(Order.OrderStatus.REFUNDED);
                order.setRefundTime(LocalDateTime.now());
            }
            
            // 4. 保存更新
            orderRepository.save(order);
            
            // 5. 记录退款处理日志
            logRefundProcess(orderId, request.getApproved(), request.getProcessor(), request.getNotes());
            
            // 6. 发送退款处理通知
            sendRefundProcessNotification(order, request.getApproved(), request.getNotes());
            
            log.info("退款处理完成: orderId={}, approved={}, processor={}", 
                    orderId, request.getApproved(), request.getProcessor());
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("处理退款失败: orderId={}", orderId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "处理退款失败");
        }
    }
    
    /**
     * 获取订单统计
     */
    public ApiResult<OrderStatisticsVO> getOrderStatistics(Long shopId, StatisticsPeriod period) {
        try {
            // 1. 验证商家
            Shop shop = shopRepository.findById(shopId).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 2. 构建统计信息
            OrderStatisticsVO statistics = OrderStatisticsVO.builder()
                    .shopId(shopId)
                    .shopName(shop.getShopName())
                    .period(period)
                    .totalOrders(shop.getTotalOrders())
                    .totalRevenue(shop.getTotalRevenue())
                    .averageOrderAmount(shop.getTotalOrders() > 0 ? 
                            shop.getTotalRevenue() / shop.getTotalOrders() : 0)
                    .build();
            
            // 3. 计算趋势（这里简化处理）
            statistics.setOrderTrend(calculateOrderTrend(shopId, period));
            statistics.setRevenueTrend(calculateRevenueTrend(shopId, period));
            statistics.setCustomerTrend(calculateCustomerTrend(shopId, period));
            
            // 4. 计算状态分布
            statistics.setStatusDistribution(calculateStatusDistribution(shopId));
            statistics.setPaymentStatusDistribution(calculatePaymentStatusDistribution(shopId));
            statistics.setDeliveryStatusDistribution(calculateDeliveryStatusDistribution(shopId));
            
            // 5. 计算热门商品
            statistics.setPopularProducts(calculatePopularProducts(shopId, period));
            
            // 6. 计算高峰时段
            statistics.setPeakHours(calculatePeakHours(shopId, period));
            
            return ApiResult.success(statistics);
            
        } catch (Exception e) {
            log.error("获取订单统计失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取订单统计失败");
        }
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        // 格式: 年月日 + 随机数 + 序列号
        String datePart = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));
        
        // 使用Redis生成序列号
        String sequenceKey = Constants.REDIS_KEY_ORDER_SEQUENCE_PREFIX + datePart;
        Long sequence = redisService.increment(sequenceKey);
        redisService.expire(sequenceKey, 24 * 3600); // 24小时过期
        
        String sequencePart = String.format("%06d", sequence);
        
        return datePart + randomPart + sequencePart;
    }
    
    /**
     * 计算订单金额
     */
    private OrderCalculationResult calculateOrderAmount(List<OrderItemRequest> items, 
                                                       Map<Long, Product> productMap, 
                                                       Shop shop) {
        double subtotal = 0.0;
        double discountAmount = 0.0;
        
        // 计算商品小计和折扣
        for (OrderItemRequest item : items) {
            Product product = productMap.get(item.getProductId());
            double itemTotal = product.getPrice() * item.getQuantity();
            subtotal += itemTotal;
            
            // 计算商品折扣
            if (Boolean.TRUE.equals(product.getOnSale()) && product.getDiscount() != null) {
                double itemDiscount = itemTotal * product.getDiscount() / 100;
                discountAmount += itemDiscount;
            }
        }
        
        // 计算配送费
        double deliveryFee = shop.getDeliveryFee() != null ? shop.getDeliveryFee() : 0.0;
        
        // 检查是否达到最低起送金额
        if (subtotal - discountAmount < shop.getMinOrderAmount()) {
            throw new RuntimeException("未达到最低起送金额");
        }
        
        // 计算最终金额
        double finalAmount = subtotal - discountAmount + deliveryFee;
        
        return OrderCalculationResult.builder()
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .deliveryFee(deliveryFee)
                .totalAmount(subtotal + deliveryFee)
                .finalAmount(finalAmount)
                .build();
    }
    
    /**
     * 验证配送地址
     */
    private boolean validateDeliveryAddress(User user, Shop shop, String deliveryAddress) {
        // 这里简化处理，实际应该计算距离
        if (shop.getDeliveryRange() == null) {
            return true; // 如果没有设置配送范围，默认可以配送
        }
        
        // 如果有用户位置和商家位置，计算距离
        if (user.getLatitude() != null && user.getLongitude() != null &&
            shop.getLatitude() != null && shop.getLongitude() != null) {
            
            double distance = calculateDistance(
                    user.getLatitude(), user.getLongitude(),
                    shop.getLatitude(), shop.getLongitude()
            );
            
            return distance <= shop.getDeliveryRange();
        }
        
        return true; // 没有位置信息，默认可以配送
    }
    
    /**
     * 计算预计送达时间
     */
    private LocalDateTime calculateEstimatedDeliveryTime(Shop shop, String deliveryAddress) {
        // 基础准备时间 + 配送时间
        int preparationTime = 15; // 分钟
        int deliveryTime = shop.getEstimatedDeliveryTime() != null ? shop.getEstimatedDeliveryTime() : 30;
        
        return LocalDateTime.now().plusMinutes(preparationTime + deliveryTime);
    }
    
    /**
     * 计算两点间距离（公里）
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 验证状态转换是否有效
     */
    private boolean isValidStatusTransition(Order.OrderStatus oldStatus, Order.OrderStatus newStatus) {
        // 这里定义状态转换规则
        Map<Order.OrderStatus, Set<Order.OrderStatus>> validTransitions = new HashMap<>();
        
        validTransitions.put(Order.OrderStatus.PENDING, Set.of(
                Order.OrderStatus.CONFIRMED, Order.OrderStatus.CANCELLED
        ));
        
        validTransitions.put(Order.OrderStatus.CONFIRMED, Set.of(
                Order.OrderStatus.PREPARING, Order.OrderStatus.CANCELLED
        ));
        
        validTransitions.put(Order.OrderStatus.PREPARING, Set.of(
                Order.OrderStatus.READY_FOR_DELIVERY, Order.OrderStatus.CANCELLED
        ));
        
        validTransitions.put(Order.OrderStatus.READY_FOR_DELIVERY, Set.of(
                Order.OrderStatus.OUT_FOR_DELIVERY, Order.OrderStatus.CANCELLED
        ));
        
        validTransitions.put(Order.OrderStatus.OUT_FOR_DELIVERY, Set.of(
                Order.OrderStatus.DELIVERED, Order.OrderStatus.FAILED
        ));
        
        valid
