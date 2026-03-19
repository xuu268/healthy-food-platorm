package com.healthyfood.repository;

import com.healthyfood.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商品数据访问接口
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    /**
     * 根据商家ID查找商品
     */
    List<Product> findByShopId(Long shopId);
    
    /**
     * 根据商家ID和状态查找商品
     */
    List<Product> findByShopIdAndStatus(Long shopId, Product.ProductStatus status);
    
    /**
     * 根据分类查找商品
     */
    List<Product> findByCategory(String category);
    
    /**
     * 根据分类和状态查找商品
     */
    List<Product> findByCategoryAndStatus(String category, Product.ProductStatus status);
    
    /**
     * 根据标签查找商品
     */
    List<Product> findByTagsContaining(String tag);
    
    /**
     * 根据名称查找商品
     */
    List<Product> findByNameContaining(String name);
    
    /**
     * 根据名称和商家ID查找商品
     */
    List<Product> findByNameContainingAndShopId(String name, Long shopId);
    
    /**
     * 根据状态查找商品
     */
    List<Product> findByStatus(Product.ProductStatus status);
    
    /**
     * 根据价格范围查找商品
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    /**
     * 根据热量范围查找商品
     */
    List<Product> findByCaloriesBetween(Double minCalories, Double maxCalories);
    
    /**
     * 根据蛋白质范围查找商品
     */
    List<Product> findByProteinBetween(Double minProtein, Double maxProtein);
    
    /**
     * 根据碳水化合物范围查找商品
     */
    List<Product> findByCarbohydratesBetween(Double minCarbs, Double maxCarbs);
    
    /**
     * 根据脂肪范围查找商品
     */
    List<Product> findByFatBetween(Double minFat, Double maxFat);
    
    /**
     * 根据是否推荐查找商品
     */
    List<Product> findByRecommended(Boolean recommended);
    
    /**
     * 根据是否热门查找商品
     */
    List<Product> findByHot(Boolean hot);
    
    /**
     * 根据是否新品查找商品
     */
    List<Product> findByNewArrival(Boolean newArrival);
    
    /**
     * 根据是否有折扣查找商品
     */
    List<Product> findByOnSale(Boolean onSale);
    
    /**
     * 查找商家推荐的商品
     */
    List<Product> findByShopIdAndRecommended(Long shopId, Boolean recommended);
    
    /**
     * 查找商家的热门商品
     */
    List<Product> findByShopIdAndHot(Long shopId, Boolean hot);
    
    /**
     * 查找商家的新品
     */
    List<Product> findByShopIdAndNewArrival(Long shopId, Boolean newArrival);
    
    /**
     * 查找商家的折扣商品
     */
    List<Product> findByShopIdAndOnSale(Long shopId, Boolean onSale);
    
    /**
     * 根据多个ID查找商品
     */
    List<Product> findByIdIn(List<Long> ids);
    
    /**
     * 查找销量最高的商品
     */
    List<Product> findTop10ByOrderBySalesCountDesc();
    
    /**
     * 查找评分最高的商品
     */
    List<Product> findTop10ByOrderByAverageRatingDesc();
    
    /**
     * 查找浏览数最高的商品
     */
    List<Product> findTop10ByOrderByViewCountDesc();
    
    /**
     * 查找收藏数最高的商品
     */
    List<Product> findTop10ByOrderByFavoriteCountDesc();
    
    /**
     * 查找最新上架的商品
     */
    List<Product> findTop10ByOrderByCreateTimeDesc();
    
    /**
     * 查找商家的销量最高商品
     */
    List<Product> findTop5ByShopIdOrderBySalesCountDesc(Long shopId);
    
    /**
     * 查找商家的评分最高商品
     */
    List<Product> findTop5ByShopIdOrderByAverageRatingDesc(Long shopId);
    
    /**
     * 检查商品名称在商家中是否已存在
     */
    boolean existsByShopIdAndName(Long shopId, String name);
    
    /**
     * 统计商家的商品数量
     */
    Long countByShopId(Long shopId);
    
    /**
     * 统计商家的活跃商品数量
     */
    Long countByShopIdAndStatus(Long shopId, Product.ProductStatus status);
    
    /**
     * 统计各分类的商品数量
     */
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.status = 'ACTIVE' GROUP BY p.category")
    List<Object[]> countProductsByCategory();
    
    /**
     * 统计各价格区间的商品数量
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN p.price < 10 THEN '10元以下' " +
           "  WHEN p.price < 20 THEN '10-20元' " +
           "  WHEN p.price < 30 THEN '20-30元' " +
           "  WHEN p.price < 50 THEN '30-50元' " +
           "  WHEN p.price < 100 THEN '50-100元' " +
           "  ELSE '100元以上' " +
           "END as priceRange, " +
           "COUNT(p) " +
           "FROM Product p " +
           "WHERE p.status = 'ACTIVE' " +
           "GROUP BY priceRange")
    List<Object[]> countProductsByPriceRange();
    
    /**
     * 统计各热量区间的商品数量
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN p.calories < 200 THEN '低卡 (<200)' " +
           "  WHEN p.calories < 400 THEN '中卡 (200-400)' " +
           "  WHEN p.calories < 600 THEN '高卡 (400-600)' " +
           "  ELSE '超高卡 (>600)' " +
           "END as calorieRange, " +
           "COUNT(p) " +
           "FROM Product p " +
           "WHERE p.status = 'ACTIVE' " +
           "GROUP BY calorieRange")
    List<Object[]> countProductsByCalorieRange();
    
    /**
     * 计算商品的平均价格
     */
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.status = 'ACTIVE'")
    Double calculateAveragePrice();
    
    /**
     * 计算商品的平均热量
     */
    @Query("SELECT AVG(p.calories) FROM Product p WHERE p.status = 'ACTIVE' AND p.calories IS NOT NULL")
    Double calculateAverageCalories();
    
    /**
     * 查找适合特定健康目标的商品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'ACTIVE' AND " +
           "(:goal = 'WEIGHT_LOSS' AND p.calories < 400) OR " +
           "(:goal = 'MUSCLE_GAIN' AND p.protein > 20) OR " +
           "(:goal = 'HEALTH_MAINTENANCE' AND p.healthScore > 70)")
    List<Product> findProductsForHealthGoal(@Param("goal") String goal);
    
    /**
     * 查找符合特定营养需求的商品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'ACTIVE' AND " +
           "(:maxCalories IS NULL OR p.calories <= :maxCalories) AND " +
           "(:minProtein IS NULL OR p.protein >= :minProtein) AND " +
           "(:maxCarbs IS NULL OR p.carbohydrates <= :maxCarbs) AND " +
           "(:maxFat IS NULL OR p.fat <= :maxFat)")
    List<Product> findProductsByNutritionalNeeds(
            @Param("maxCalories") Double maxCalories,
            @Param("minProtein") Double minProtein,
            @Param("maxCarbs") Double maxCarbs,
            @Param("maxFat") Double maxFat);
    
    /**
     * 查找不含特定过敏原的商品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'ACTIVE' AND " +
           "(:allergen IS NULL OR p.allergens NOT LIKE CONCAT('%', :allergen, '%'))")
    List<Product> findProductsWithoutAllergen(@Param("allergen") String allergen);
    
    /**
     * 查找符合特定饮食限制的商品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'ACTIVE' AND " +
           "(:restriction = 'VEGETARIAN' AND p.vegetarian = true) OR " +
           "(:restriction = 'VEGAN' AND p.vegan = true) OR " +
           "(:restriction = 'GLUTEN_FREE' AND p.glutenFree = true) OR " +
           "(:restriction = 'DAIRY_FREE' AND p.dairyFree = true)")
    List<Product> findProductsForDietaryRestriction(@Param("restriction") String restriction);
    
    /**
     * 更新商品销量
     */
    @Query("UPDATE Product p SET p.salesCount = p.salesCount + :quantity WHERE p.id = :productId")
    void updateSalesCount(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 更新商品评分
     */
    @Query("UPDATE Product p SET p.averageRating = :rating, p.totalReviews = p.totalReviews + 1 WHERE p.id = :productId")
    void updateProductRating(@Param("productId") Long productId, @Param("rating") Double rating);
    
    /**
     * 更新商品浏览数
     */
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :productId")
    void incrementViewCount(@Param("productId") Long productId);
    
    /**
     * 更新商品收藏数
     */
    @Query("UPDATE Product p SET p.favoriteCount = p.favoriteCount + :increment WHERE p.id = :productId")
    void updateFavoriteCount(@Param("productId") Long productId, @Param("increment") Integer increment);
    
    /**
     * 查找库存紧张的商品
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.stockQuantity > 0 AND p.stockQuantity <= p.lowStockThreshold")
    List<Product> findLowStockProducts();
    
    /**
     * 查找已售罄的商品
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();
    
    /**
     * 查找即将过期的商品
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.expiryDate IS NOT NULL AND p.expiryDate <= CURRENT_DATE + 3")
    List<Product> findExpiringProducts();
    
    /**
     * 查找今日新上架的商品
     */
    @Query("SELECT p FROM Product p WHERE DATE(p.createTime) = CURRENT_DATE")
    List<Product> findTodayNewProducts();
    
    /**
     * 查找需要下架的商品（长时间无销量）
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.lastSaleTime < CURRENT_DATE - 30")
    List<Product> findProductsNeedDeactivation();
    
    /**
     * 根据多个条件搜索商品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "p.status = 'ACTIVE' AND " +
           "(:shopId IS NULL OR p.shopId = :shopId) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minRating IS NULL OR p.averageRating >= :minRating) AND " +
           "(:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%') OR p.description LIKE CONCAT('%', :keyword, '%') OR p.tags LIKE CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(
            @Param("shopId") Long shopId,
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minRating") Double minRating,
            @Param("keyword") String keyword);
}