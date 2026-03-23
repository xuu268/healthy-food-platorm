package com.healthyfood.repository;

import com.healthyfood.entity.IngredientAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 成分分析结果Repository接口
 */
@Repository
public interface IngredientAnalysisRepository extends JpaRepository<IngredientAnalysis, Long> {
    
    /**
     * 根据产品ID查找分析结果
     * @param productId 产品ID
     * @return 分析结果
     */
    Optional<IngredientAnalysis> findByProductId(Long productId);
    
    /**
     * 根据评分范围查找
     * @param minScore 最小评分
     * @param maxScore 最大评分
     * @return 分析结果列表
     */
    List<IngredientAnalysis> findByOverallScoreBetween(BigDecimal minScore, BigDecimal maxScore);
    
    /**
     * 根据安全评分查找
     * @param minScore 最小安全评分
     * @return 分析结果列表
     */
    List<IngredientAnalysis> findBySafetyScoreGreaterThanEqual(BigDecimal minScore);
    
    /**
     * 根据营养评分查找
     * @param minScore 最小营养评分
     * @return 分析结果列表
     */
    List<IngredientAnalysis> findByNutritionScoreGreaterThanEqual(BigDecimal minScore);
    
    /**
     * 查找高评分产品（综合评分≥0.8）
     * @return 分析结果列表
     */
    @Query("SELECT a FROM IngredientAnalysis a WHERE a.overallScore >= 0.8 ORDER BY a.overallScore DESC")
    List<IngredientAnalysis> findHighScoreAnalyses();
    
    /**
     * 查找低评分产品（综合评分≤0.4）
     * @return 分析结果列表
     */
    @Query("SELECT a FROM IngredientAnalysis a WHERE a.overallScore <= 0.4 ORDER BY a.overallScore ASC")
    List<IngredientAnalysis> findLowScoreAnalyses();
    
    /**
     * 统计分析数量
     * @return 分析数量
     */
    @Query("SELECT COUNT(a) FROM IngredientAnalysis a")
    Long countAnalyses();
    
    /**
     * 获取平均综合评分
     * @return 平均评分
     */
    @Query("SELECT AVG(a.overallScore) FROM IngredientAnalysis a")
    Optional<BigDecimal> getAverageOverallScore();
    
    /**
     * 获取平均安全评分
     * @return 平均安全评分
     */
    @Query("SELECT AVG(a.safetyScore) FROM IngredientAnalysis a")
    Optional<BigDecimal> getAverageSafetyScore();
    
    /**
     * 获取平均营养评分
     * @return 平均营养评分
     */
    @Query("SELECT AVG(a.nutritionScore) FROM IngredientAnalysis a")
    Optional<BigDecimal> getAverageNutritionScore();
    
    /**
     * 根据分析时间查找
     * @param days 天数
     * @return 分析结果列表
     */
    @Query("SELECT a FROM IngredientAnalysis a WHERE a.analyzedAt >= CURRENT_DATE - :days")
    List<IngredientAnalysis> findByAnalyzedWithinDays(int days);
    
    /**
     * 删除产品的分析结果
     * @param productId 产品ID
     * @return 删除的数量
     */
    @Query("DELETE FROM IngredientAnalysis a WHERE a.productId = :productId")
    Long deleteByProductId(Long productId);
}