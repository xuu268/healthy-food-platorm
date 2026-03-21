package com.healthyfood.controller;

import com.healthyfood.common.ApiResult;
import com.healthyfood.service.OrderService;
import com.healthyfood.vo.order.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResult<OrderVO> createOrder(@Valid @RequestBody OrderCreateRequest request,
                                        HttpServletRequest httpRequest) {
        log.info("创建订单: userId={}, shopId={}", request.getUserId(), request.getShopId());
        ApiResult<OrderVO> result = orderService.createOrder(request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResult<OrderDetailVO> getOrderDetail(@PathVariable Long orderId,
                                                 HttpServletRequest httpRequest) {
        log.info("获取订单详情: orderId={}", orderId);
        ApiResult<OrderDetailVO> result = orderService.getOrderDetail(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新订单
     */
    @PutMapping("/{orderId}")
    public ApiResult<OrderVO> updateOrder(@PathVariable Long orderId,
                                        @Valid @RequestBody OrderUpdateRequest request,
                                        HttpServletRequest httpRequest) {
        log.info("更新订单: orderId={}", orderId);
        ApiResult<OrderVO> result = orderService.updateOrder(orderId, request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public ApiResult<Void> cancelOrder(@PathVariable Long orderId,
                                     @RequestParam(required = false) String reason,
                                     HttpServletRequest httpRequest) {
        log.info("取消订单: orderId={}, reason={}", orderId, reason);
        ApiResult<Void> result = orderService.cancelOrder(orderId, reason);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 确认订单
     */
    @PutMapping("/{orderId}/confirm")
    public ApiResult<Void> confirmOrder(@PathVariable Long orderId,
                                      HttpServletRequest httpRequest) {
        log.info("确认订单: orderId={}", orderId);
        ApiResult<Void> result = orderService.confirmOrder(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 开始制作订单
     */
    @PutMapping("/{orderId}/start-preparing")
    public ApiResult<Void> startPreparingOrder(@PathVariable Long orderId,
                                             HttpServletRequest httpRequest) {
        log.info("开始制作订单: orderId={}", orderId);
        ApiResult<Void> result = orderService.startPreparingOrder(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 订单准备完成
     */
    @PutMapping("/{orderId}/ready")
    public ApiResult<Void> markOrderAsReady(@PathVariable Long orderId,
                                          HttpServletRequest httpRequest) {
        log.info("订单准备完成: orderId={}", orderId);
        ApiResult<Void> result = orderService.markOrderAsReady(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 开始配送订单
     */
    @PutMapping("/{orderId}/start-delivery")
    public ApiResult<Void> startDelivery(@PathVariable Long orderId,
                                       HttpServletRequest httpRequest) {
        log.info("开始配送订单: orderId={}", orderId);
        ApiResult<Void> result = orderService.startDelivery(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 完成配送
     */
    @PutMapping("/{orderId}/complete-delivery")
    public ApiResult<Void> completeDelivery(@PathVariable Long orderId,
                                          HttpServletRequest httpRequest) {
        log.info("完成配送: orderId={}", orderId);
        ApiResult<Void> result = orderService.completeDelivery(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 完成订单
     */
    @PutMapping("/{orderId}/complete")
    public ApiResult<Void> completeOrder(@PathVariable Long orderId,
                                       HttpServletRequest httpRequest) {
        log.info("完成订单: orderId={}", orderId);
        ApiResult<Void> result = orderService.completeOrder(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 申请退款
     */
    @PostMapping("/{orderId}/refund")
    public ApiResult<Void> requestRefund(@PathVariable Long orderId,
                                       @RequestParam String reason,
                                       @RequestParam(required = false) Double amount,
                                       HttpServletRequest httpRequest) {
        log.info("申请退款: orderId={}, reason={}, amount={}", orderId, reason, amount);
        ApiResult<Void> result = orderService.requestRefund(orderId, reason, amount);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 处理退款
     */
    @PutMapping("/{orderId}/refund/process")
    public ApiResult<Void> processRefund(@PathVariable Long orderId,
                                       @RequestParam String action,
                                       @RequestParam(required = false) String notes,
                                       HttpServletRequest httpRequest) {
        log.info("处理退款: orderId={}, action={}, notes={}", orderId, action, notes);
        ApiResult<Void> result = orderService.processRefund(orderId, action, notes);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取订单列表
     */
    @GetMapping
    public ApiResult<Object> getOrderList(@RequestParam(required = false) Integer page,
                                        @RequestParam(required = false) Integer size,
                                        @RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) Long shopId,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String sortBy,
                                        @RequestParam(required = false) String sortOrder,
                                        HttpServletRequest httpRequest) {
        log.info("获取订单列表: page={}, size={}, userId={}, shopId={}, status={}, startDate={}, endDate={}, keyword={}, sortBy={}, sortOrder={}",
                page, size, userId, shopId, status, startDate, endDate, keyword, sortBy, sortOrder);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        String validatedSortOrder = validateSort(sortBy, sortOrder);
        
        ApiResult<Object> result = orderService.getOrderList(pageNum, pageSize, userId, shopId, 
                status, startDate, endDate, keyword, sortBy, validatedSortOrder);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户订单
     */
    @GetMapping("/user/{userId}")
    public ApiResult<Object> getUserOrders(@PathVariable Long userId,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(required = false) String status,
                                         HttpServletRequest httpRequest) {
        log.info("获取用户订单: userId={}, page={}, size={}, status={}", 
                userId, page, size, status);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = orderService.getUserOrders(userId, pageNum, pageSize, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家订单
     */
    @GetMapping("/shop/{shopId}")
    public ApiResult<Object> getShopOrders(@PathVariable Long shopId,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate,
                                         HttpServletRequest httpRequest) {
        log.info("获取商家订单: shopId={}, page={}, size={}, status={}, startDate={}, endDate={}",
                shopId, page, size, status, startDate, endDate);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = orderService.getShopOrders(shopId, pageNum, pageSize, status, startDate, endDate);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取今日订单
     */
    @GetMapping("/today")
    public ApiResult<Object> getTodayOrders(@RequestParam(required = false) Long shopId,
                                          @RequestParam(required = false) String status,
                                          HttpServletRequest httpRequest) {
        log.info("获取今日订单: shopId={}, status={}", shopId, status);
        ApiResult<Object> result = orderService.getTodayOrders(shopId, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取待处理订单
     */
    @GetMapping("/pending")
    public ApiResult<Object> getPendingOrders(@RequestParam(required = false) Long shopId,
                                            HttpServletRequest httpRequest) {
        log.info("获取待处理订单: shopId={}", shopId);
        ApiResult<Object> result = orderService.getPendingOrders(shopId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取配送中订单
     */
    @GetMapping("/delivering")
    public ApiResult<Object> getDeliveringOrders(@RequestParam(required = false) Long shopId,
                                               HttpServletRequest httpRequest) {
        log.info("获取配送中订单: shopId={}", shopId);
        ApiResult<Object> result = orderService.getDeliveringOrders(shopId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/statistics")
    public ApiResult<OrderStatisticsVO> getOrderStatistics(@RequestParam(required = false) Long shopId,
                                                         @RequestParam(required = false) String startDate,
                                                         @RequestParam(required = false) String endDate,
                                                         HttpServletRequest httpRequest) {
        log.info("获取订单统计: shopId={}, startDate={}, endDate={}", shopId, startDate, endDate);
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        ApiResult<OrderStatisticsVO> result = orderService.getOrderStatistics(shopId, start, end);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取订单收入统计
     */
    @GetMapping("/revenue-statistics")
    public ApiResult<Object> getRevenueStatistics(@RequestParam(required = false) Long shopId,
                                                @RequestParam(required = false) String period,
                                                HttpServletRequest httpRequest) {
        log.info("获取订单收入统计: shopId={}, period={}", shopId, period);
        ApiResult<Object> result = orderService.getRevenueStatistics(shopId, period);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取订单趋势
     */
    @GetMapping("/trend")
    public ApiResult<Object> getOrderTrend(@RequestParam(required = false) Long shopId,
                                         @RequestParam(required = false) String period,
                                         HttpServletRequest httpRequest) {
        log.info("获取订单趋势: shopId={}, period={}", shopId, period);
        ApiResult<Object> result = orderService.getOrderTrend(shopId, period);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取热门商品统计
     */
    @GetMapping("/top-products")
    public ApiResult<Object> getTopProducts(@RequestParam(required = false) Long shopId,
                                          @RequestParam(required = false) String period,
                                          @RequestParam(required = false) Integer limit,
                                          HttpServletRequest httpRequest) {
        log.info("获取热门商品统计: shopId={}, period={}, limit={}", shopId, period, limit);
        
        int productLimit = limit != null ? limit : 10;
        ApiResult<Object> result = orderService.getTopProducts(shopId, period, productLimit);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取订单评价
     */
    @GetMapping("/{orderId}/review")
    public ApiResult<Object> getOrderReview(@PathVariable Long orderId,
                                          HttpServletRequest httpRequest) {
        log.info("获取订单评价: orderId={}", orderId);
        ApiResult<Object> result = orderService.getOrderReview(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 提交订单评价
     */
    @PostMapping("/{orderId}/review")
    public ApiResult<Void> submitOrderReview(@PathVariable Long orderId,
                                           @RequestParam Integer rating,
                                           @RequestParam(required = false) String comment,
                                           @RequestParam(required = false) String[] images,
                                           HttpServletRequest httpRequest) {
        log.info("提交订单评价: orderId={}, rating={}", orderId, rating);
        ApiResult<Void> result = orderService.submitOrderReview(orderId, rating, comment, images);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新订单评价
     */
    @PutMapping("/{orderId}/review")
    public ApiResult<Void> updateOrderReview(@PathVariable Long orderId,
                                           @RequestParam Integer rating,
                                           @RequestParam(required = false) String comment,
                                           @RequestParam(required = false) String[] images,
                                           HttpServletRequest httpRequest) {
        log.info("更新订单评价: orderId={}, rating={}", orderId, rating);
        ApiResult<Void> result = orderService.updateOrderReview(orderId, rating, comment, images);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 删除订单评价
     */
    @DeleteMapping("/{orderId}/review")
    public ApiResult<Void> deleteOrderReview(@PathVariable Long orderId,
                                           HttpServletRequest httpRequest) {
        log.info("删除订单评价: orderId={}", orderId);
        ApiResult<Void> result = orderService.deleteOrderReview(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取订单配送信息
     */
    @GetMapping("/{orderId}/delivery")
    public ApiResult<Object> getOrderDeliveryInfo(@PathVariable Long orderId,
                                                HttpServletRequest httpRequest) {
        log.info("获取订单配送信息: orderId={}", orderId);
        ApiResult<Object> result = orderService.getOrderDeliveryInfo(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新订单配送信息
     */
    @PutMapping("/{orderId}/delivery")
    public ApiResult<Void> updateOrderDeliveryInfo(@PathVariable Long orderId,
                                                 @Valid @RequestBody Object deliveryRequest,
                                                 HttpServletRequest httpRequest) {
        log.info("更新订单配送信息: orderId={}", orderId);
        ApiResult<Void> result = orderService.updateOrderDeliveryInfo(orderId, deliveryRequest);
        logApiAccess(httpRequest, deliveryRequest, result);
        return result;
    }

    /**
     * 获取订单支付信息
     */
    @GetMapping("/{orderId}/payment")
    public ApiResult<Object> getOrderPaymentInfo(@PathVariable Long orderId,
                                               HttpServletRequest httpRequest) {
        log.info("获取订单支付信息: orderId={}", orderId);
        ApiResult<Object> result = orderService.getOrderPaymentInfo(orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 发起支付
     */
    @PostMapping("/{orderId}/payment")
    public ApiResult<Object> initiatePayment(@PathVariable Long orderId,
                                           @RequestParam String paymentMethod,
                                           HttpServletRequest httpRequest) {
        log.info("发起支付: orderId={}, paymentMethod={}", orderId, paymentMethod);
        ApiResult<Object> result = orderService.initiatePayment(orderId, paymentMethod);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 确认支付
     */
    @PutMapping("/{orderId}/payment/confirm")
    public ApiResult<Void> confirmPayment(@PathVariable Long orderId,
                                        @RequestParam String transactionId,
                                        HttpServletRequest httpRequest) {
        log.info("确认支付: orderId={}, transactionId={}", orderId, transactionId);
        ApiResult<Void> result = orderService.confirmPayment(orderId, transactionId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取订单日志
     */
    @GetMapping("/{orderId}/logs")
    public ApiResult<Object> getOrderLogs(@PathVariable Long orderId,
                                        HttpServletRequest httpRequest) {
        log.info("获取订单日志: orderId={}", orderId);
        ApiResult<Object> result = orderService.getOrderLogs(orderId);
        logApiAccess(httpRequest, null, result);
