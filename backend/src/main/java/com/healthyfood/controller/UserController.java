package com.healthyfood.controller;

import com.healthyfood.common.ApiResult;
import com.healthyfood.service.UserService;
import com.healthyfood.vo.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResult<UserProfileVO> register(@Valid @RequestBody UserRegisterRequest request,
                                           HttpServletRequest httpRequest) {
        log.info("用户注册: {}", request.getUsername());
        ApiResult<UserProfileVO> result = userService.register(request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResult<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request,
                                            HttpServletRequest httpRequest) {
        log.info("用户登录: {}", request.getUsername());
        ApiResult<UserLoginResponse> result = userService.login(request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public ApiResult<UserProfileVO> getUserProfile(@PathVariable Long userId,
                                                 HttpServletRequest httpRequest) {
        log.info("获取用户信息: userId={}", userId);
        ApiResult<UserProfileVO> result = userService.getUserProfile(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public ApiResult<UserProfileVO> updateUserProfile(@PathVariable Long userId,
                                                    @Valid @RequestBody UserProfileVO request,
                                                    HttpServletRequest httpRequest) {
        log.info("更新用户信息: userId={}", userId);
        ApiResult<UserProfileVO> result = userService.updateUserProfile(userId, request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取健康报告
     */
    @GetMapping("/{userId}/health-report")
    public ApiResult<HealthReportVO> getHealthReport(@PathVariable Long userId,
                                                   HttpServletRequest httpRequest) {
        log.info("获取健康报告: userId={}", userId);
        ApiResult<HealthReportVO> result = userService.getHealthReport(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新健康档案
     */
    @PutMapping("/{userId}/health-profile")
    public ApiResult<HealthProfileVO> updateHealthProfile(@PathVariable Long userId,
                                                        @Valid @RequestBody HealthProfileVO request,
                                                        HttpServletRequest httpRequest) {
        log.info("更新健康档案: userId={}", userId);
        ApiResult<HealthProfileVO> result = userService.updateHealthProfile(userId, request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/{userId}/orders")
    public ApiResult<Object> getUserOrders(@PathVariable Long userId,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size,
                                         @RequestParam(required = false) String status,
                                         HttpServletRequest httpRequest) {
        log.info("获取用户订单列表: userId={}, page={}, size={}, status={}", 
                userId, page, size, status);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = userService.getUserOrders(userId, pageNum, pageSize, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户收藏
     */
    @GetMapping("/{userId}/favorites")
    public ApiResult<Object> getUserFavorites(@PathVariable Long userId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size,
                                            HttpServletRequest httpRequest) {
        log.info("获取用户收藏: userId={}, page={}, size={}", userId, page, size);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = userService.getUserFavorites(userId, pageNum, pageSize);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户地址列表
     */
    @GetMapping("/{userId}/addresses")
    public ApiResult<Object> getUserAddresses(@PathVariable Long userId,
                                            HttpServletRequest httpRequest) {
        log.info("获取用户地址列表: userId={}", userId);
        ApiResult<Object> result = userService.getUserAddresses(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 添加用户地址
     */
    @PostMapping("/{userId}/addresses")
    public ApiResult<Object> addUserAddress(@PathVariable Long userId,
                                          @Valid @RequestBody Object addressRequest,
                                          HttpServletRequest httpRequest) {
        log.info("添加用户地址: userId={}", userId);
        ApiResult<Object> result = userService.addUserAddress(userId, addressRequest);
        logApiAccess(httpRequest, addressRequest, result);
        return result;
    }

    /**
     * 修改密码
     */
    @PutMapping("/{userId}/password")
    public ApiResult<Void> changePassword(@PathVariable Long userId,
                                        @Valid @RequestBody Object passwordRequest,
                                        HttpServletRequest httpRequest) {
        log.info("修改密码: userId={}", userId);
        ApiResult<Void> result = userService.changePassword(userId, passwordRequest);
        logApiAccess(httpRequest, passwordRequest, result);
        return result;
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ApiResult<Void> resetPassword(@Valid @RequestBody Object resetRequest,
                                       HttpServletRequest httpRequest) {
        log.info("重置密码请求");
        ApiResult<Void> result = userService.resetPassword(resetRequest);
        logApiAccess(httpRequest, resetRequest, result);
        return result;
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send-verification-code")
    public ApiResult<Void> sendVerificationCode(@Valid @RequestBody Object codeRequest,
                                              HttpServletRequest httpRequest) {
        log.info("发送验证码请求");
        ApiResult<Void> result = userService.sendVerificationCode(codeRequest);
        logApiAccess(httpRequest, codeRequest, result);
        return result;
    }

    /**
     * 验证手机号
     */
    @PostMapping("/verify-phone")
    public ApiResult<Void> verifyPhone(@Valid @RequestBody Object verifyRequest,
                                     HttpServletRequest httpRequest) {
        log.info("验证手机号请求");
        ApiResult<Void> result = userService.verifyPhone(verifyRequest);
        logApiAccess(httpRequest, verifyRequest, result);
        return result;
    }

    /**
     * 绑定微信
     */
    @PostMapping("/{userId}/bind-wechat")
    public ApiResult<Void> bindWechat(@PathVariable Long userId,
                                    @Valid @RequestBody Object wechatRequest,
                                    HttpServletRequest httpRequest) {
        log.info("绑定微信: userId={}", userId);
        ApiResult<Void> result = userService.bindWechat(userId, wechatRequest);
        logApiAccess(httpRequest, wechatRequest, result);
        return result;
    }

    /**
     * 解绑微信
     */
    @PostMapping("/{userId}/unbind-wechat")
    public ApiResult<Void> unbindWechat(@PathVariable Long userId,
                                      HttpServletRequest httpRequest) {
        log.info("解绑微信: userId={}", userId);
        ApiResult<Void> result = userService.unbindWechat(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户积分
     */
    @GetMapping("/{userId}/points")
    public ApiResult<Object> getUserPoints(@PathVariable Long userId,
                                         HttpServletRequest httpRequest) {
        log.info("获取用户积分: userId={}", userId);
        ApiResult<Object> result = userService.getUserPoints(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户优惠券
     */
    @GetMapping("/{userId}/coupons")
    public ApiResult<Object> getUserCoupons(@PathVariable Long userId,
                                          @RequestParam(required = false) String status,
                                          HttpServletRequest httpRequest) {
        log.info("获取用户优惠券: userId={}, status={}", userId, status);
        ApiResult<Object> result = userService.getUserCoupons(userId, status);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户通知
     */
    @GetMapping("/{userId}/notifications")
    public ApiResult<Object> getUserNotifications(@PathVariable Long userId,
                                                @RequestParam(required = false) Integer page,
                                                @RequestParam(required = false) Integer size,
                                                HttpServletRequest httpRequest) {
        log.info("获取用户通知: userId={}, page={}, size={}", userId, page, size);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = userService.getUserNotifications(userId, pageNum, pageSize);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{userId}/notifications/{notificationId}/read")
    public ApiResult<Void> markNotificationAsRead(@PathVariable Long userId,
                                                @PathVariable Long notificationId,
                                                HttpServletRequest httpRequest) {
        log.info("标记通知为已读: userId={}, notificationId={}", userId, notificationId);
        ApiResult<Void> result = userService.markNotificationAsRead(userId, notificationId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{userId}/notifications/{notificationId}")
    public ApiResult<Void> deleteNotification(@PathVariable Long userId,
                                            @PathVariable Long notificationId,
                                            HttpServletRequest httpRequest) {
        log.info("删除通知: userId={}, notificationId={}", userId, notificationId);
        ApiResult<Void> result = userService.deleteNotification(userId, notificationId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户设置
     */
    @GetMapping("/{userId}/settings")
    public ApiResult<Object> getUserSettings(@PathVariable Long userId,
                                           HttpServletRequest httpRequest) {
        log.info("获取用户设置: userId={}", userId);
        ApiResult<Object> result = userService.getUserSettings(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新用户设置
     */
    @PutMapping("/{userId}/settings")
    public ApiResult<Object> updateUserSettings(@PathVariable Long userId,
                                              @Valid @RequestBody Object settingsRequest,
                                              HttpServletRequest httpRequest) {
        log.info("更新用户设置: userId={}", userId);
        ApiResult<Object> result = userService.updateUserSettings(userId, settingsRequest);
        logApiAccess(httpRequest, settingsRequest, result);
        return result;
    }

    /**
     * 用户注销
     */
    @PostMapping("/{userId}/logout")
    public ApiResult<Void> logout(@PathVariable Long userId,
                                HttpServletRequest httpRequest) {
        log.info("用户注销: userId={}", userId);
        ApiResult<Void> result = userService.logout(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 用户反馈
     */
    @PostMapping("/{userId}/feedback")
    public ApiResult<Void> submitFeedback(@PathVariable Long userId,
                                        @Valid @RequestBody Object feedbackRequest,
                                        HttpServletRequest httpRequest) {
        log.info("用户反馈: userId={}", userId);
        ApiResult<Void> result = userService.submitFeedback(userId, feedbackRequest);
        logApiAccess(httpRequest, feedbackRequest, result);
        return result;
    }

    /**
     * 获取用户活跃度
     */
    @GetMapping("/{userId}/activity")
    public ApiResult<Object> getUserActivity(@PathVariable Long userId,
                                           @RequestParam(required = false) Integer days,
                                           HttpServletRequest httpRequest) {
        log.info("获取用户活跃度: userId={}, days={}", userId, days);
        ApiResult<Object> result = userService.getUserActivity(userId, days != null ? days : 30);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 搜索用户（管理员）
     */
    @GetMapping("/search")
    public ApiResult<Object> searchUsers(@RequestParam String keyword,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size,
                                       HttpServletRequest httpRequest) {
        log.info("搜索用户: keyword={}, page={}, size={}", keyword, page, size);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = userService.searchUsers(keyword, pageNum, pageSize);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取用户统计（管理员）
     */
    @GetMapping("/statistics")
    public ApiResult<Object> getUserStatistics(@RequestParam(required = false) String period,
                                             HttpServletRequest httpRequest) {
        log.info("获取用户统计: period={}", period);
        ApiResult<Object> result = userService.getUserStatistics(period);
        logApiAccess(httpRequest, null, result);
        return result;
    }
}