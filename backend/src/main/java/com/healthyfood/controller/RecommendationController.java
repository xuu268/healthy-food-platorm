package com.healthyfood.controller;

import com.healthyfood.common.ApiResult;
import com.healthyfood.service.RecommendationService;
import com.healthyfood.vo.recommendation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 推荐控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController extends BaseController {

    private final RecommendationService recommendationService;

    /**
     * 获取个性化推荐
     */
    @PostMapping("/personalized")
    public ApiResult<RecommendationVO> getPersonalizedRecommendations(@Valid @RequestBody RecommendationRequest request,
                                                                    HttpServletRequest httpRequest) {
        log.info("获取个性化推荐: userId={}, recommendationType={}", 
                request.getUserId(), request.getRecommendationType());
        ApiResult<RecommendationVO> result = recommendationService.getPersonalizedRecommendations(request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取热门推荐
     */
    @GetMapping("/popular")
    public ApiResult<RecommendationVO> getPopularRecommendations(@RequestParam(required = false) Long userId,
                                                               @RequestParam(required = false) Integer limit,
                                                               @RequestParam(required = false) String category,
                                                               HttpServletRequest httpRequest) {
        log.info("获取热门推荐: userId={}, limit={}, category={}", userId, limit, category);
        
        int recommendationLimit = limit != null ? limit : 10;
        ApiResult<RecommendationVO> result = recommendationService.getPopularRecommendations(userId, recommendationLimit, category);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取基于内容的推荐
     */
    @GetMapping("/content-based")
    public ApiResult<RecommendationVO> getContentBasedRecommendations(@RequestParam Long itemId,
                                                                    @RequestParam(required = false) Integer limit,
                                                                    HttpServletRequest httpRequest) {
        log.info("获取基于内容的推荐: itemId={}, limit={}", itemId, limit);
        
        int recommendationLimit = limit != null ? limit : 5;
        ApiResult<RecommendationVO> result = recommendationService.getContentBasedRecommendations(itemId, recommendationLimit);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取协同过滤推荐
     */
    @GetMapping("/collaborative-filtering")
    public ApiResult<RecommendationVO> getCollaborativeFilteringRecommendations(@RequestParam Long userId,
                                                                              @RequestParam(required = false) Integer limit,
                                                                              HttpServletRequest httpRequest) {
        log.info("获取协同过滤推荐: userId={}, limit={}", userId, limit);
        
        int recommendationLimit = limit != null ? limit : 10;
        ApiResult<RecommendationVO> result = recommendationService.getCollaborativeFilteringRecommendations(userId, recommendationLimit);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取健康饮食推荐
     */
    @GetMapping("/healthy-diet")
    public ApiResult<RecommendationVO> getHealthyDietRecommendations(@RequestParam Long userId,
                                                                   @RequestParam(required = false) Integer limit,
                                                                   @RequestParam(required = false) String healthGoal,
                                                                   HttpServletRequest httpRequest) {
        log.info("获取健康饮食推荐: userId={}, limit={}, healthGoal={}", userId, limit, healthGoal);
        
        int recommendationLimit = limit != null ? limit : 10;
        ApiResult<RecommendationVO> result = recommendationService.getHealthyDietRecommendations(userId, recommendationLimit, healthGoal);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取季节推荐
     */
    @GetMapping("/seasonal")
    public ApiResult<RecommendationVO> getSeasonalRecommendations(@RequestParam(required = false) Long userId,
                                                                @RequestParam(required = false) Integer limit,
                                                                @RequestParam(required = false) String season,
                                                                HttpServletRequest httpRequest) {
        log.info("获取季节推荐: userId={}, limit={}, season={}", userId, limit, season);
        
        int recommendationLimit = limit != null ? limit : 10;
        ApiResult<RecommendationVO> result = recommendationService.getSeasonalRecommendations(userId, recommendationLimit, season);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取天气推荐
     */
    @GetMapping("/weather-based")
    public ApiResult<RecommendationVO> getWeatherBasedRecommendations(@RequestParam(required = false) Long userId,
                                                                    @RequestParam(required = false) Integer limit,
                                                                    @RequestParam(required = false) String weatherCondition,
                                                                    @RequestParam(required = false) Double temperature,
                                                                    HttpServletRequest httpRequest) {
        log.info("获取天气推荐: userId={}, limit={}, weatherCondition={}, temperature={}", 
                userId, limit, weatherCondition, temperature);
        
        int recommendationLimit = limit != null ? limit : 10;
        ApiResult<RecommendationVO> result = recommendationService.getWeatherBasedRecommendations(
                userId, recommendationLimit, weatherCondition, temperature);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取时间推荐
     */
    @GetMapping("/time-based")
    public ApiResult<RecommendationVO> getTimeBasedRecommendations(@RequestParam(required = false) Long userId,
                                                                 @RequestParam(required = false) Integer limit,
                                                                 @RequestParam(required = false) String timeOfDay,
                                                                 HttpServletRequest httpRequest) {
        log.info("获取时间推荐: userId={}, limit={}, timeOfDay={}", userId, limit, timeOfDay);
        
        int recommendationLimit = limit != null ? limit : 10;
        ApiResult<RecommendationVO> result = recommendationService.getTimeBasedRecommendations(userId, recommendationLimit, timeOfDay);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取位置推荐
     */
    @GetMapping("/location-based")
    public ApiResult<RecommendationVO> getLocationBasedRecommendations(@RequestParam(required = false) Long userId,
                                                                     @RequestParam(required = false) Integer limit,
                                                                     @RequestParam(required = false) Double latitude,
                                                                     @RequestParam(required = false) Double longitude,
                                                                     HttpServletRequest httpRequest) {
        log.info("获取位置推荐: userId={}, limit={}, latitude={}, longitude={}", 
                userId, limit, latitude, longitude);
        
        int recommendationLimit = limit != null ? limit : 10;
        ApiResult<RecommendationVO> result = recommendationService.getLocationBasedRecommendations(
                userId, recommendationLimit, latitude, longitude);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取组合推荐
     */
    @GetMapping("/hybrid")
    public ApiResult<RecommendationVO> getHybridRecommendations(@RequestParam(required = false) Long userId,
                                                              @RequestParam(required = false) Integer limit,
                                                              @RequestParam(required = false) String[] recommendationTypes,
                                                              HttpServletRequest httpRequest) {
        log.info("获取组合推荐: userId={}, limit={}, recommendationTypes={}", 
                userId, limit, recommendationTypes);
        
        int recommendationLimit = limit != null ? limit : 15;
        ApiResult<RecommendationVO> result = recommendationService.getHybridRecommendations(
                userId, recommendationLimit, recommendationTypes);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取推荐解释
     */
    @GetMapping("/explanation")
    public ApiResult<Object> getRecommendationExplanation(@RequestParam Long recommendationId,
                                                        HttpServletRequest httpRequest) {
        log.info("获取推荐解释: recommendationId={}", recommendationId);
        ApiResult<Object> result = recommendationService.getRecommendationExplanation(recommendationId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 反馈推荐结果
     */
    @PostMapping("/feedback")
    public ApiResult<Void> submitRecommendationFeedback(@RequestParam Long recommendationId,
                                                      @RequestParam String feedbackType,
                                                      @RequestParam(required = false) String feedbackDetails,
                                                      HttpServletRequest httpRequest) {
        log.info("反馈推荐结果: recommendationId={}, feedbackType={}, feedbackDetails={}", 
                recommendationId, feedbackType, feedbackDetails);
        ApiResult<Void> result = recommendationService.submitRecommendationFeedback(
                recommendationId, feedbackType, feedbackDetails);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取推荐历史
     */
    @GetMapping("/history")
    public ApiResult<Object> getRecommendationHistory(@RequestParam Long userId,
                                                    @RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer size,
                                                    HttpServletRequest httpRequest) {
        log.info("获取推荐历史: userId={}, page={}, size={}", userId, page, size);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = recommendationService.getRecommendationHistory(userId, pageNum, pageSize);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 清除推荐历史
     */
    @DeleteMapping("/history")
    public ApiResult<Void> clearRecommendationHistory(@RequestParam Long userId,
                                                    HttpServletRequest httpRequest) {
        log.info("清除推荐历史: userId={}", userId);
        ApiResult<Void> result = recommendationService.clearRecommendationHistory(userId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取推荐模型信息
     */
    @GetMapping("/model-info")
    public ApiResult<Object> getRecommendationModelInfo(@RequestParam(required = false) String modelType,
                                                      HttpServletRequest httpRequest) {
        log.info("获取推荐模型信息: modelType={}", modelType);
        ApiResult<Object> result = recommendationService.getRecommendationModelInfo(modelType);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 训练推荐模型
     */
    @PostMapping("/train-model")
    public ApiResult<Void> trainRecommendationModel(@RequestParam String modelType,
                                                  HttpServletRequest httpRequest) {
        log.info("训练推荐模型: modelType={}", modelType);
        ApiResult<Void> result = recommendationService.trainRecommendationModel(modelType);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 评估推荐模型
     */
    @GetMapping("/evaluate-model")
    public ApiResult<Object> evaluateRecommendationModel(@RequestParam String modelType,
                                                       HttpServletRequest httpRequest) {
        log.info("评估推荐模型: modelType={}", modelType);
        ApiResult<Object> result = recommendationService.evaluateRecommendationModel(modelType);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取推荐统计
     */
    @GetMapping("/statistics")
    public ApiResult<Object> getRecommendationStatistics(@RequestParam(required = false) String period,
                                                       HttpServletRequest httpRequest) {
        log.info("获取推荐统计: period={}", period);
        ApiResult<Object> result = recommendationService.getRecommendationStatistics(period);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取推荐效果分析
     */
    @GetMapping("/performance-analysis")
    public ApiResult<Object> getRecommendationPerformanceAnalysis(@RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate,
                                                                HttpServletRequest httpRequest) {
        log.info("获取推荐效果分析: startDate={}, endDate={}", startDate, endDate);
        ApiResult<Object> result = recommendationService.getRecommendationPerformanceAnalysis(startDate, endDate);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取A/B测试结果
     */
    @GetMapping("/ab-test-results")
    public ApiResult<Object> getABTestResults(@RequestParam String testId,
                                            HttpServletRequest httpRequest) {
        log.info("获取A/B测试结果: testId={}", testId);
        ApiResult<Object> result = recommendationService.getABTestResults(testId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 创建A/B测试
     */
    @PostMapping("/ab-test")
    public ApiResult<Object> createABTest(@Valid @RequestBody Object abTestRequest,
                                        HttpServletRequest httpRequest) {
        log.info("创建A/B测试");
        ApiResult<Object> result = recommendationService.createABTest(abTestRequest);
        logApiAccess(httpRequest, abTestRequest, result);
        return result;
    }

    /**
     * 获取推荐配置
     */
    @GetMapping("/configuration")
    public ApiResult<Object> getRecommendationConfiguration(HttpServletRequest httpRequest) {
        log.info("获取推荐配置");
        ApiResult<Object> result = recommendationService.getRecommendationConfiguration();
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新推荐配置
     */
    @PutMapping("/configuration")
    public ApiResult<Void> updateRecommendationConfiguration(@Valid @RequestBody Object configRequest,
                                                           HttpServletRequest httpRequest) {
        log.info("更新推荐配置");
        ApiResult<Void> result = recommendationService.updateRecommendationConfiguration(configRequest);
        logApiAccess(httpRequest, configRequest, result);
        return result;
    }

    /**
     * 获取实时推荐
     */
    @GetMapping("/realtime")
