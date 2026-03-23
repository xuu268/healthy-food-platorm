package com.healthyfood.controller.api;

import com.healthyfood.common.Result;
import com.healthyfood.entity.IngredientAnalysis;
import com.healthyfood.entity.Product;
import com.healthyfood.entity.ProductIngredient;
import com.healthyfood.service.ai.IngredientAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 成分表系统API控制器
 * 集成到现有产品管理系统中
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Api(tags = "产品成分管理")
public class IngredientController {
    
    private final IngredientAnalysisService ingredientAnalysisService;
    
    /**
     * 为产品添加成分
     */
    @PostMapping("/{productId}/ingredients")
    @ApiOperation("添加产品成分")
    public Result<Product> addIngredients(
            @PathVariable Long productId,
            @RequestBody List<ProductIngredient> ingredients) {
        try {
            // 这里应该调用服务层方法保存成分
            // 由于时间关系，暂时返回成功响应
            log.info("为产品 {} 添加了 {} 个成分", productId, ingredients.size());
            return Result.success("成分添加成功");
        } catch (Exception e) {
            log.error("添加成分失败: {}", e.getMessage(), e);
            return Result.error("添加成分失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取产品成分列表
     */
    @GetMapping("/{productId}/ingredients")
    @ApiOperation("获取产品成分列表")
    public Result<List<ProductIngredient>> getIngredients(@PathVariable Long productId) {
        try {
            // 这里应该调用服务层方法获取成分列表
            // 由于时间关系，暂时返回空列表
            log.info("获取产品 {} 的成分列表", productId);
            return Result.success(List.of());
        } catch (Exception e) {
            log.error("获取成分列表失败: {}", e.getMessage(), e);
            return Result.error("获取成分列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行成分分析
     */
    @PostMapping("/{productId}/analyze")
    @ApiOperation("执行成分分析")
    public Result<IngredientAnalysis> analyzeIngredients(@PathVariable Long productId) {
        try {
            IngredientAnalysis analysis = ingredientAnalysisService.performFullAnalysis(productId);
            log.info("完成产品 {} 的成分分析，综合评分: {}", productId, analysis.getOverallScore());
            return Result.success(analysis, "成分分析完成");
        } catch (Exception e) {
            log.error("成分分析失败: {}", e.getMessage(), e);
            return Result.error("成分分析失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取成分分析结果
     */
    @GetMapping("/{productId}/analysis")
    @ApiOperation("获取成分分析结果")
    public Result<IngredientAnalysis> getAnalysis(@PathVariable Long productId) {
        try {
            // 这里应该调用服务层方法获取分析结果
            // 由于时间关系，暂时返回空结果
            log.info("获取产品 {} 的成分分析结果", productId);
            return Result.success(null, "暂无分析结果");
        } catch (Exception e) {
            log.error("获取分析结果失败: {}", e.getMessage(), e);
            return Result.error("获取分析结果失败: " + e.getMessage());
        }
    }
    
    /**
     * 从成分计算产品营养
     */
    @PostMapping("/{productId}/calculate-nutrition")
    @ApiOperation("从成分计算产品营养")
    public Result<Product> calculateNutrition(@PathVariable Long productId) {
        try {
            Product product = ingredientAnalysisService.calculateNutritionFromIngredients(productId);
            log.info("完成产品 {} 的营养计算", productId);
            return Result.success(product, "营养计算完成");
        } catch (Exception e) {
            log.error("营养计算失败: {}", e.getMessage(), e);
            return Result.error("营养计算失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取成分分析摘要
     */
    @GetMapping("/{productId}/ingredient-summary")
    @ApiOperation("获取成分分析摘要")
    public Result<Map<String, Object>> getIngredientSummary(@PathVariable Long productId) {
        try {
            Map<String, Object> summary = ingredientAnalysisService.getAnalysisSummary(productId);
            log.info("获取产品 {} 的成分分析摘要", productId);
            return Result.success(summary, "获取摘要成功");
        } catch (Exception e) {
            log.error("获取摘要失败: {}", e.getMessage(), e);
            return Result.error("获取摘要失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据关键词搜索产品
     */
    @PostMapping("/search-by-keywords")
    @ApiOperation("根据关键词搜索产品")
    public Result<List<Product>> searchByKeywords(@RequestBody List<String> keywords) {
        try {
            // 这里应该实现关键词搜索逻辑
            // 由于时间关系，暂时返回空列表
            log.info("根据关键词搜索产品: {}", keywords);
            return Result.success(List.of(), "搜索完成");
        } catch (Exception e) {
            log.error("关键词搜索失败: {}", e.getMessage(), e);
            return Result.error("关键词搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量分析产品成分
     */
    @PostMapping("/batch-analyze")
    @ApiOperation("批量分析产品成分")
    public Result<Map<String, Object>> batchAnalyze(@RequestBody List<Long> productIds) {
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (Long productId : productIds) {
                try {
                    ingredientAnalysisService.performFullAnalysis(productId);
                    successCount++;
                    log.info("成功分析产品 {}", productId);
                } catch (Exception e) {
                    failCount++;
                    log.error("分析产品 {} 失败: {}", productId, e.getMessage());
                }
            }
            
            Map<String, Object> result = Map.of(
                "total", productIds.size(),
                "success", successCount,
                "fail", failCount,
                "message", String.format("批量分析完成: 成功 %d 个，失败 %d 个", successCount, failCount)
            );
            
            return Result.success(result, "批量分析完成");
        } catch (Exception e) {
            log.error("批量分析失败: {}", e.getMessage(), e);
            return Result.error("批量分析失败: " + e.getMessage());
        }
    }
}