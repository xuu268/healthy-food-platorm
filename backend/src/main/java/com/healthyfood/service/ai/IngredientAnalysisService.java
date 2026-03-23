package com.healthyfood.service.ai;

import com.healthyfood.entity.*;
import com.healthyfood.entity.ProductIngredient.NutritionData;
import com.healthyfood.repository.ProductIngredientRepository;
import com.healthyfood.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成分分析服务
 * 负责协调Product和ProductIngredient的关系
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientAnalysisService {
    
    private final ProductRepository productRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final IngredientKeywordService keywordService;
    private final IngredientAnalysisRepository analysisRepository;
    
    /**
     * 从成分计算产品总营养
     * @param productId 产品ID
     * @return 更新后的产品营养信息
     */
    @Transactional
    public Product calculateNutritionFromIngredients(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("产品不存在: " + productId));
        
        List<ProductIngredient> ingredients = productIngredientRepository.findByProductId(productId);
        
        if (ingredients.isEmpty()) {
            log.warn("产品 {} 没有成分数据", productId);
            return product;
        }
        
        // 计算总份量（假设所有成分的单位都是克）
        BigDecimal totalQuantity = ingredients.stream()
                .map(ProductIngredient::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算总营养
        Product.NutritionInfo totalNutrition = new Product.NutritionInfo();
        
        for (ProductIngredient ingredient : ingredients) {
            Product.NutritionInfo contribution = ingredient.calculateNutritionContribution(totalQuantity);
            addNutritionInfo(totalNutrition, contribution);
        }
        
        // 更新产品营养字段
        product.setCalories(totalNutrition.getCalories());
        product.setProtein(totalNutrition.getProtein());
        product.setFat(totalNutrition.getFat());
        product.setCarbohydrates(totalNutrition.getCarbohydrates());
        product.setFiber(totalNutrition.getFiber());
        product.setSugar(totalNutrition.getSugar());
        product.setSodium(totalNutrition.getSodium());
        
        // 重新计算健康评分
        recalculateHealthScore(product);
        
        return productRepository.save(product);
    }
    
    /**
     * 执行完整的成分分析
     * @param productId 产品ID
     * @return 分析结果
     */
    @Transactional
    public IngredientAnalysis performFullAnalysis(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("产品不存在: " + productId));
        
        List<ProductIngredient> ingredients = productIngredientRepository.findByProductId(productId);
        
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("产品没有成分数据，无法进行分析");
        }
        
        // 1. 计算营养评分
        BigDecimal nutritionScore = calculateNutritionScore(ingredients);
        
        // 2. 计算安全评分
        BigDecimal safetyScore = calculateSafetyScore(ingredients);
        
        // 3. 计算健康评分
        BigDecimal healthScore = calculateHealthScore(ingredients);
        
        // 4. 提取关键词
        List<String> extractedKeywords = extractKeywords(ingredients);
        
        // 5. 创建分析结果
        IngredientAnalysis analysis = new IngredientAnalysis();
        analysis.setProductId(productId);
        analysis.setNutritionScore(nutritionScore);
        analysis.setSafetyScore(safetyScore);
        analysis.setHealthScore(healthScore);
        analysis.calculateOverallScore();
        analysis.setAnalyzedAt(LocalDateTime.now());
        
        // 6. 保存分析结果
        IngredientAnalysis savedAnalysis = analysisRepository.save(analysis);
        
        // 7. 关联关键词
        keywordService.associateKeywordsWithProduct(productId, extractedKeywords, savedAnalysis.getId());
        
        // 8. 更新产品健康评分
        product.setHealthScore(savedAnalysis.getOverallScore());
        productRepository.save(product);
        
        log.info("完成产品 {} 的成分分析，综合评分: {}", productId, savedAnalysis.getOverallScore());
        
        return savedAnalysis;
    }
    
    /**
     * 添加营养信息（累加）
     */
    private void addNutritionInfo(Product.NutritionInfo total, Product.NutritionInfo addition) {
        if (addition.getCalories() != null) {
            total.setCalories((total.getCalories() != null ? total.getCalories() : 0) + addition.getCalories());
        }
        
        if (addition.getProtein() != null) {
            total.setProtein(addBigDecimal(total.getProtein(), addition.getProtein()));
        }
        
        if (addition.getFat() != null) {
            total.setFat(addBigDecimal(total.getFat(), addition.getFat()));
        }
        
        if (addition.getCarbohydrates() != null) {
            total.setCarbohydrates(addBigDecimal(total.getCarbohydrates(), addition.getCarbohydrates()));
        }
        
        if (addition.getFiber() != null) {
            total.setFiber(addBigDecimal(total.getFiber(), addition.getFiber()));
        }
        
        if (addition.getSugar() != null) {
            total.setSugar(addBigDecimal(total.getSugar(), addition.getSugar()));
        }
        
        if (addition.getSodium() != null) {
            total.setSodium(addBigDecimal(total.getSodium(), addition.getSodium()));
        }
    }
    
    private BigDecimal addBigDecimal(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.add(b);
    }
    
    /**
     * 重新计算健康评分
     */
    private void recalculateHealthScore(Product product) {
        // 基于营养信息计算健康评分
        double score = 5.0; // 基础分
        
        // 热量评估
        if (product.getCalories() != null) {
            if (product.getCalories() > 800) score -= 1.5;
            else if (product.getCalories() > 600) score -= 1.0;
            else if (product.getCalories() > 400) score -= 0.5;
            else if (product.getCalories() < 200) score += 0.5;
        }
        
        // 蛋白质评估
        if (product.getProtein() != null) {
            double protein = product.getProtein().doubleValue();
            if (protein >= 25) score += 1.0;
            else if (protein >= 15) score += 0.5;
            else if (protein < 5) score -= 0.5;
        }
        
        // 脂肪评估
        if (product.getFat() != null) {
            double fat = product.getFat().doubleValue();
            if (fat > 30) score -= 1.5;
            else if (fat > 20) score -= 1.0;
            else if (fat > 10) score -= 0.5;
            else if (fat < 5) score += 0.5;
        }
        
        // 糖分评估
        if (product.getSugar() != null) {
            double sugar = product.getSugar().doubleValue();
            if (sugar > 30) score -= 1.5;
            else if (sugar > 20) score -= 1.0;
            else if (sugar > 10) score -= 0.5;
            else if (sugar < 5) score += 0.5;
        }
        
        // 钠含量评估
        if (product.getSodium() != null) {
            double sodium = product.getSodium().doubleValue();
            if (sodium > 1000) score -= 1.5;
            else if (sodium > 500) score -= 1.0;
            else if (sodium > 200) score -= 0.5;
            else if (sodium < 100) score += 0.5;
        }
        
        // 确保评分在0-5之间
        score = Math.max(0, Math.min(5, score));
        product.setHealthScore(BigDecimal.valueOf(score).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    
    /**
     * 计算营养评分
     */
    private BigDecimal calculateNutritionScore(List<ProductIngredient> ingredients) {
        // 简化的营养评分算法
        double score = 0.0;
        int count = 0;
        
        for (ProductIngredient ingredient : ingredients) {
            NutritionData nutrition = ingredient.getNutritionDataObject();
            
            // 蛋白质含量加分
            if (nutrition.getProtein() != null && nutrition.getProtein().doubleValue() > 10) {
                score += 0.1;
            }
            
            // 低脂肪加分
            if (nutrition.getFat() != null && nutrition.getFat().doubleValue() < 5) {
                score += 0.1;
            }
            
            // 高纤维加分
            if (nutrition.getFiber() != null && nutrition.getFiber().doubleValue() > 3) {
                score += 0.1;
            }
            
            // 低糖加分
            if (nutrition.getSugar() != null && nutrition.getSugar().doubleValue() < 5) {
                score += 0.1;
            }
            
            count++;
        }
        
        if (count == 0) return BigDecimal.ZERO;
        
        double normalizedScore = score / count;
        return BigDecimal.valueOf(Math.min(1.0, normalizedScore * 2.5))  // 放大并限制在0-1之间
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算安全评分
     */
    private BigDecimal calculateSafetyScore(List<ProductIngredient> ingredients) {
        double score = 1.0; // 起始满分
        
        for (ProductIngredient ingredient : ingredients) {
            // 过敏原扣分
            if (Boolean.TRUE.equals(ingredient.getAllergenFlag())) {
                score -= 0.2;
            }
            
            // 安全等级警告扣分
            if (ingredient.getSafetyLevel() == ProductIngredient.SafetyLevel.WARNING) {
                score -= 0.3;
            } else if (ingredient.getSafetyLevel() == ProductIngredient.SafetyLevel.CAUTION) {
                score -= 0.1;
            }
            
            // 添加剂扣分
            if (ingredient.getIngredientType() == ProductIngredient.IngredientType.ADDITIVE) {
                score -= 0.1;
            }
        }
        
        return BigDecimal.valueOf(Math.max(0, Math.min(1, score)))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 计算健康评分
     */
    private BigDecimal calculateHealthScore(List<ProductIngredient> ingredients) {
        // 健康评分基于营养评分和安全评分的加权平均
        BigDecimal nutritionScore = calculateNutritionScore(ingredients);
        BigDecimal safetyScore = calculateSafetyScore(ingredients);
        
        // 权重：营养60%，安全40%
        return nutritionScore.multiply(new BigDecimal("0.6"))
                .add(safetyScore.multiply(new BigDecimal("0.4")))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 从成分中提取关键词
     */
    private List<String> extractKeywords(List<ProductIngredient> ingredients) {
        return ingredients.stream()
                .flatMap(ingredient -> {
                    NutritionData nutrition = ingredient.getNutritionDataObject();
                    return keywordService.extractKeywordsFromNutrition(nutrition).stream();
                })
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 获取产品的成分分析摘要
     * @param productId 产品ID
     * @return 分析摘要
     */
    public Map<String, Object> getAnalysisSummary(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("产品不存在: " + productId));
        
        List<ProductIngredient> ingredients = productIngredientRepository.findByProductId(productId);
        IngredientAnalysis analysis = analysisRepository.findByProductId(productId).orElse(null);
        
        Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("productId", productId);
        summary.put("productName", product.getName());
        summary.put("ingredientCount", ingredients.size());
        summary.put("hasAnalysis", analysis != null);
        
        if (analysis != null) {
            summary.put("overallScore", analysis.getOverallScore());
            summary.put("safetyScore", analysis.getSafetyScore());
            summary.put("nutritionScore", analysis.getNutritionScore());
            summary.put("healthScore", analysis.getHealthScore());
            summary.put("analyzedAt", analysis.getAnalyzedAt());
        }
        
        // 成分类型统计
        Map<String, Long> typeStats = ingredients.stream()
                .collect(Collectors.groupingBy(
                    i -> i.getIngredientType().name(),
                    Collectors.counting()
                ));
        summary.put("ingredientTypeStats", typeStats);
        
        // 安全等级统计
        Map<String, Long> safetyStats = ingredients.stream()
                .collect(Collectors.groupingBy(
                    i -> i.getSafetyLevel().name(),
                    Collectors.counting()
                ));
        summary.put("safetyLevelStats", safetyStats);
        
        return summary;
    }
}