package com.healthyfood.repository;

import com.healthyfood.entity.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 产品成分Repository接口
 */
@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Long> {
    
    /**
     * 根据产品ID查找成分
     * @param productId 产品ID
     * @return 成分列表
     */
    List<ProductIngredient> findByProductId(Long productId);
    
    /**
     * 根据产品ID和成分名称查找
     * @param productId 产品ID
     * @param ingredientName 成分名称
     * @return 成分
     */
    Optional<ProductIngredient> findByProductIdAndIngredientName(Long productId, String ingredientName);
    
    /**
     * 根据产品ID和成分类型查找
     * @param productId 产品ID
     * @param ingredientType 成分类型
     * @return 成分列表
     */
    List<ProductIngredient> findByProductIdAndIngredientType(Long productId, ProductIngredient.IngredientType ingredientType);
    
    /**
     * 根据安全等级查找
     * @param safetyLevel 安全等级
     * @return 成分列表
     */
    List<ProductIngredient> findBySafetyLevel(ProductIngredient.SafetyLevel safetyLevel);
    
    /**
     * 查找包含过敏原的成分
     * @param allergenFlag 过敏原标志
     * @return 成分列表
     */
    List<ProductIngredient> findByAllergenFlag(Boolean allergenFlag);
    
    /**
     * 统计产品的成分数量
     * @param productId 产品ID
     * @return 成分数量
     */
    @Query("SELECT COUNT(p) FROM ProductIngredient p WHERE p.productId = :productId")
    Long countByProductId(Long productId);
    
    /**
     * 统计产品的过敏原数量
     * @param productId 产品ID
     * @return 过敏原数量
     */
    @Query("SELECT COUNT(p) FROM ProductIngredient p WHERE p.productId = :productId AND p.allergenFlag = true")
    Long countAllergensByProductId(Long productId);
    
    /**
     * 删除产品的所有成分
     * @param productId 产品ID
     * @return 删除的数量
     */
    @Query("DELETE FROM ProductIngredient p WHERE p.productId = :productId")
    Long deleteByProductId(Long productId);
    
    /**
     * 查找包含特定成分的产品ID
     * @param ingredientName 成分名称
     * @return 产品ID列表
     */
    @Query("SELECT DISTINCT p.productId FROM ProductIngredient p WHERE p.ingredientName LIKE %:ingredientName%")
    List<Long> findProductIdsByIngredientName(String ingredientName);
    
    /**
     * 获取产品的总重量（克）
     * @param productId 产品ID
     * @return 总重量
     */
    @Query("SELECT SUM(p.quantity) FROM ProductIngredient p WHERE p.productId = :productId AND p.unit = 'g'")
    Optional<BigDecimal> getTotalWeightByProductId(Long productId);
    
    /**
     * 获取产品的总热量
     * @param productId 产品ID
     * @return 总热量
     */
    @Query("SELECT SUM(CAST(JSON_EXTRACT(p.nutritionData, '$.calories') AS INTEGER)) FROM ProductIngredient p WHERE p.productId = :productId")
    Optional<Integer> getTotalCaloriesByProductId(Long productId);
}