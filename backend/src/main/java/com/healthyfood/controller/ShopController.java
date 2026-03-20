package com.healthyfood.controller;

import com.healthyfood.common.ApiResult;
import com.healthyfood.service.ShopService;
import com.healthyfood.vo.shop.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 商家控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController extends BaseController {

    private final ShopService shopService;

    /**
     * 商家注册
     */
    @PostMapping("/register")
    public ApiResult<ShopRegisterResponse> register(@Valid @RequestBody ShopRegisterRequest request,
                                                  HttpServletRequest httpRequest) {
        log.info("商家注册: {}", request.getShopName());
        ApiResult<ShopRegisterResponse> result = shopService.register(request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 商家登录
     */
    @PostMapping("/login")
    public ApiResult<ShopLoginResponse> login(@Valid @RequestBody ShopLoginRequest request,
                                            HttpServletRequest httpRequest) {
        log.info("商家登录: {}", request.getUsername());
        ApiResult<ShopLoginResponse> result = shopService.login(request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取商家信息
     */
    @GetMapping("/{shopId}")
    public ApiResult<ShopDetailVO> getShopDetail(@PathVariable Long shopId,
                                               HttpServletRequest httpRequest) {
        log.info("获取商家信息: shopId={}", shopId);
        ApiResult<ShopDetailVO> result = shopService.getShopDetail(shopId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新商家信息
     */
    @PutMapping("/{shopId}")
    public ApiResult<ShopDetailVO> updateShopDetail(@PathVariable Long shopId,
                                                  @Valid @RequestBody ShopDetailVO request,
                                                  HttpServletRequest httpRequest) {
        log.info("更新商家信息: shopId={}", shopId);
        ApiResult<ShopDetailVO> result = shopService.updateShopDetail(shopId, request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取商家订单列表
     */
    @GetMapping("/{shopId}/orders")
    public ApiResult<Object> getShopOrders(@PathVariable Long shopId,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String startDate,
                                         @RequestParam(required = false) String endDate,
                                         HttpServletRequest httpRequest) {
        log.info("获取商家订单列表: shopId={}, page={}, size={}, status={}, startDate={}, endDate={}",
                shopId, page, size, status, startDate, endDate);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = shopService.getShopOrders(shopId, pageNum, pageSize, status, startDate, endDate);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家今日订单
     */
    @GetMapping("/{shopId}/orders/today")
    public ApiResult<Object> getTodayOrders(@PathVariable Long shopId,
                                          @RequestParam(required = false) String status,
                                          HttpServletRequest httpRequest) {
        log.info("获取商家今日订单: shopId={}, status={}", shopId, status);
        ApiResult<Object> result = shopService.getTodayOrders(shopId, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取待处理订单
     */
    @GetMapping("/{shopId}/orders/pending")
    public ApiResult<Object> getPendingOrders(@PathVariable Long shopId,
                                            HttpServletRequest httpRequest) {
        log.info("获取待处理订单: shopId={}", shopId);
        ApiResult<Object> result = shopService.getPendingOrders(shopId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 确认订单
     */
    @PutMapping("/{shopId}/orders/{orderId}/confirm")
    public ApiResult<Void> confirmOrder(@PathVariable Long shopId,
                                      @PathVariable Long orderId,
                                      HttpServletRequest httpRequest) {
        log.info("确认订单: shopId={}, orderId={}", shopId, orderId);
        ApiResult<Void> result = shopService.confirmOrder(shopId, orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 开始制作订单
     */
    @PutMapping("/{shopId}/orders/{orderId}/start-preparing")
    public ApiResult<Void> startPreparingOrder(@PathVariable Long shopId,
                                             @PathVariable Long orderId,
                                             HttpServletRequest httpRequest) {
        log.info("开始制作订单: shopId={}, orderId={}", shopId, orderId);
        ApiResult<Void> result = shopService.startPreparingOrder(shopId, orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 订单准备完成
     */
    @PutMapping("/{shopId}/orders/{orderId}/ready")
    public ApiResult<Void> markOrderAsReady(@PathVariable Long shopId,
                                          @PathVariable Long orderId,
                                          HttpServletRequest httpRequest) {
        log.info("订单准备完成: shopId={}, orderId={}", shopId, orderId);
        ApiResult<Void> result = shopService.markOrderAsReady(shopId, orderId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 取消订单
     */
    @PutMapping("/{shopId}/orders/{orderId}/cancel")
    public ApiResult<Void> cancelOrder(@PathVariable Long shopId,
                                     @PathVariable Long orderId,
                                     @RequestParam(required = false) String reason,
                                     HttpServletRequest httpRequest) {
        log.info("取消订单: shopId={}, orderId={}, reason={}", shopId, orderId, reason);
        ApiResult<Void> result = shopService.cancelOrder(shopId, orderId, reason);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家商品列表
     */
    @GetMapping("/{shopId}/products")
    public ApiResult<Object> getShopProducts(@PathVariable Long shopId,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer size,
                                           @RequestParam(required = false) String category,
                                           @RequestParam(required = false) String status,
                                           HttpServletRequest httpRequest) {
        log.info("获取商家商品列表: shopId={}, page={}, size={}, category={}, status={}",
                shopId, page, size, category, status);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = shopService.getShopProducts(shopId, pageNum, pageSize, category, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 添加商品
     */
    @PostMapping("/{shopId}/products")
    public ApiResult<Object> addProduct(@PathVariable Long shopId,
                                      @Valid @RequestBody Object productRequest,
                                      HttpServletRequest httpRequest) {
        log.info("添加商品: shopId={}", shopId);
        ApiResult<Object> result = shopService.addProduct(shopId, productRequest);
        logApiAccess(httpRequest, productRequest, result);
        return result;
    }

    /**
     * 更新商品
     */
    @PutMapping("/{shopId}/products/{productId}")
    public ApiResult<Object> updateProduct(@PathVariable Long shopId,
                                         @PathVariable Long productId,
                                         @Valid @RequestBody Object productRequest,
                                         HttpServletRequest httpRequest) {
        log.info("更新商品: shopId={}, productId={}", shopId, productId);
        ApiResult<Object> result = shopService.updateProduct(shopId, productId, productRequest);
        logApiAccess(httpRequest, productRequest, result);
        return result;
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{shopId}/products/{productId}")
    public ApiResult<Void> deleteProduct(@PathVariable Long shopId,
                                       @PathVariable Long productId,
                                       HttpServletRequest httpRequest) {
        log.info("删除商品: shopId={}, productId={}", shopId, productId);
        ApiResult<Void> result = shopService.deleteProduct(shopId, productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 上架/下架商品
     */
    @PutMapping("/{shopId}/products/{productId}/status")
    public ApiResult<Void> updateProductStatus(@PathVariable Long shopId,
                                             @PathVariable Long productId,
                                             @RequestParam String status,
                                             HttpServletRequest httpRequest) {
        log.info("更新商品状态: shopId={}, productId={}, status={}", shopId, productId, status);
        ApiResult<Void> result = shopService.updateProductStatus(shopId, productId, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家评价
     */
    @GetMapping("/{shopId}/reviews")
    public ApiResult<Object> getShopReviews(@PathVariable Long shopId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size,
                                          @RequestParam(required = false) Integer rating,
                                          HttpServletRequest httpRequest) {
        log.info("获取商家评价: shopId={}, page={}, size={}, rating={}",
                shopId, page, size, rating);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = shopService.getShopReviews(shopId, pageNum, pageSize, rating);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 回复评价
     */
    @PostMapping("/{shopId}/reviews/{reviewId}/reply")
    public ApiResult<Void> replyToReview(@PathVariable Long shopId,
                                       @PathVariable Long reviewId,
                                       @Valid @RequestBody Object replyRequest,
                                       HttpServletRequest httpRequest) {
        log.info("回复评价: shopId={}, reviewId={}", shopId, reviewId);
        ApiResult<Void> result = shopService.replyToReview(shopId, reviewId, replyRequest);
        logApiAccess(httpRequest, replyRequest, result);
        return result;
    }

    /**
     * 获取商家统计
     */
    @GetMapping("/{shopId}/statistics")
    public ApiResult<Object> getShopStatistics(@PathVariable Long shopId,
                                             @RequestParam(required = false) String period,
                                             @RequestParam(required = false) String startDate,
                                             @RequestParam(required = false) String endDate,
                                             HttpServletRequest httpRequest) {
        log.info("获取商家统计: shopId={}, period={}, startDate={}, endDate={}",
                shopId, period, startDate, endDate);
        
        ApiResult<Object> result = shopService.getShopStatistics(shopId, period, startDate, endDate);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家收入统计
     */
    @GetMapping("/{shopId}/revenue-statistics")
    public ApiResult<Object> getRevenueStatistics(@PathVariable Long shopId,
                                                @RequestParam(required = false) String period,
                                                HttpServletRequest httpRequest) {
        log.info("获取商家收入统计: shopId={}, period={}", shopId, period);
        ApiResult<Object> result = shopService.getRevenueStatistics(shopId, period);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家员工列表
     */
    @GetMapping("/{shopId}/staff")
    public ApiResult<Object> getShopStaff(@PathVariable Long shopId,
                                        @RequestParam(required = false) Integer page,
                                        @RequestParam(required = false) Integer size,
                                        HttpServletRequest httpRequest) {
        log.info("获取商家员工列表: shopId={}, page={}, size={}", shopId, page, size);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = shopService.getShopStaff(shopId, pageNum, pageSize);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 添加员工
     */
    @PostMapping("/{shopId}/staff")
    public ApiResult<Object> addStaff(@PathVariable Long shopId,
                                    @Valid @RequestBody Object staffRequest,
                                    HttpServletRequest httpRequest) {
        log.info("添加员工: shopId={}", shopId);
        ApiResult<Object> result = shopService.addStaff(shopId, staffRequest);
        logApiAccess(httpRequest, staffRequest, result);
        return result;
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{shopId}/staff/{staffId}")
    public ApiResult<Object> updateStaff(@PathVariable Long shopId,
                                       @PathVariable Long staffId,
                                       @Valid @RequestBody Object staffRequest,
                                       HttpServletRequest httpRequest) {
        log.info("更新员工信息: shopId={}, staffId={}", shopId, staffId);
        ApiResult<Object> result = shopService.updateStaff(shopId, staffId, staffRequest);
        logApiAccess(httpRequest, staffRequest, result);
        return result;
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{shopId}/staff/{staffId}")
    public ApiResult<Void> deleteStaff(@PathVariable Long shopId,
                                     @PathVariable Long staffId,
                                     HttpServletRequest httpRequest) {
        log.info("删除员工: shopId={}, staffId={}", shopId, staffId);
        ApiResult<Void> result = shopService.deleteStaff(shopId, staffId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家通知
     */
    @GetMapping("/{shopId}/notifications")
    public ApiResult<Object> getShopNotifications(@PathVariable Long shopId,
                                                @RequestParam(required = false) Integer page,
                                                @RequestParam(required = false) Integer size,
                                                HttpServletRequest httpRequest) {
        log.info("获取商家通知: shopId={}, page={}, size={}", shopId, page, size);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = shopService.getShopNotifications(shopId, pageNum, pageSize);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家设置
     */
    @GetMapping("/{shopId}/settings")
    public ApiResult<Object> getShopSettings(@PathVariable Long shopId,
                                           HttpServletRequest httpRequest) {
        log.info("获取商家设置: shopId={}", shopId);
        ApiResult<Object> result = shopService.getShopSettings(shopId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新商家设置
     */
    @PutMapping("/{shopId}/settings")
    public ApiResult<Object> updateShopSettings(@PathVariable Long shopId,
                                              @Valid @RequestBody Object settingsRequest,
                                              HttpServletRequest httpRequest) {
        log.info("更新商家设置: shopId={}", shopId);
        ApiResult<Object> result = shopService.updateShopSettings(shopId, settingsRequest);
        logApiAccess(httpRequest, settingsRequest, result);
        return result;
    }

    /**
     * 更新营业状态
     */
    @PutMapping("/{shopId}/business-status")
    public ApiResult<Void> updateBusinessStatus(@PathVariable Long shopId,
                                              @RequestParam String status,
                                              HttpServletRequest httpRequest) {
        log.info("更新营业状态: shopId={}, status={}", shopId, status);
        ApiResult<Void> result = shopService.updateBusinessStatus(shopId, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新营业时间
     */
    @PutMapping("/{shopId}/business-hours")
    public ApiResult<Void> updateBusinessHours(@PathVariable Long shopId,
                                             @Valid @RequestBody Object hoursRequest,
                                             HttpServletRequest httpRequest) {
        log.info("更新营业时间: shopId={}", shopId);
        ApiResult<Void> result = shopService.updateBusinessHours(shopId, hoursRequest);
        logApiAccess(httpRequest, hoursRequest, result);
        return result;
    }

    /**
     * 更新配送设置
     */
    @PutMapping("/{shopId}/delivery-settings")
    public ApiResult<Void> updateDeliverySettings(@PathVariable Long shopId,
                                                @Valid @RequestBody Object deliveryRequest,
                                                HttpServletRequest httpRequest) {
        log.info("更新配送设置: shopId={}", shopId);
        ApiResult<Void> result = shopService.updateDeliverySettings(shopId, deliveryRequest);
        logApiAccess(httpRequest, deliveryRequest, result);
        return result;
    }

    /**
     * 获取商家提现记录
     */
    @GetMapping("/{shopId}/withdrawals")
    public ApiResult<Object> getWithdrawals(@PathVariable Long shopId,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size,
                                          HttpServletRequest httpRequest) {
        log.info("获取商家提现记录: shopId={}, page={}, size={}", shopId, page, size);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = shopService.getWithdrawals(shopId, pageNum,