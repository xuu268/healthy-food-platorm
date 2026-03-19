package com.healthyfood.repository;

import com.healthyfood.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商家数据访问接口
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {
    
    /**
     * 根据手机号查找商家
     */
    Optional<Shop> findByPhone(String phone);
    
    /**
     * 根据邮箱查找商家
     */
    Optional<Shop> findByEmail(String email);
    
    /**
     * 根据商家名称查找
     */
    List<Shop> findByShopNameContaining(String shopName);
    
    /**
     * 根据商家类型查找
     */
    List<Shop> findByShopType(Shop.ShopType shopType);
    
    /**
     * 根据状态查找商家
     */
    List<Shop> findByStatus(Shop.ShopStatus status);
    
    /**
     * 根据菜系类型查找商家
     */
    List<Shop> findByCuisineTypesContaining(String cuisineType);
    
    /**
     * 根据标签查找商家
     */
    List<Shop> findByTagsContaining(String tag);
    
    /**
     * 检查手机号是否已注册
     */
    boolean existsByPhone(String phone);
    
    /**
     * 检查邮箱是否已注册
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据评分范围查找商家
     */
    List<Shop> findByAverageRatingBetween(Double minRating, Double maxRating);
    
    /**
     * 根据订单数量范围查找商家
     */
    List<Shop> findByTotalOrdersBetween(Integer minOrders, Integer maxOrders);
    
    /**
     * 根据收入范围查找商家
     */
    List<Shop> findByTotalRevenueBetween(Double minRevenue, Double maxRevenue);
    
    /**
     * 查找热门商家（按订单数排序）
     */
    List<Shop> findTop10ByOrderByTotalOrdersDesc();
    
    /**
     * 查找高评分商家（按评分排序）
     */
    List<Shop> findTop10ByOrderByAverageRatingDesc();
    
    /**
     * 查找新注册商家
     */
    List<Shop> findTop10ByOrderByRegistrationTimeDesc();
    
    /**
     * 根据地理位置查找附近的商家
     */
    @Query(value = "SELECT * FROM shop s WHERE " +
           "ST_Distance_Sphere(point(s.longitude, s.latitude), point(:longitude, :latitude)) <= :distance * 1000 " +
           "AND s.status = 'ACTIVE'", nativeQuery = true)
    List<Shop> findNearbyShops(@Param("latitude") Double latitude,
                               @Param("longitude") Double longitude,
                               @Param("distance") Double distance);
    
    /**
     * 计算商家的平均评分
     */
    @Query("SELECT AVG(s.averageRating) FROM Shop s WHERE s.status = 'ACTIVE'")
    Double calculateAverageRatingOfAllShops();
    
    /**
     * 统计各类型的商家数量
     */
    @Query("SELECT s.shopType, COUNT(s) FROM Shop s WHERE s.status = 'ACTIVE' GROUP BY s.shopType")
    List<Object[]> countShopsByType();
    
    /**
     * 统计各评分区间的商家数量
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN s.averageRating >= 4.5 THEN '优秀' " +
           "  WHEN s.averageRating >= 4.0 THEN '良好' " +
           "  WHEN s.averageRating >= 3.5 THEN '中等' " +
           "  WHEN s.averageRating >= 3.0 THEN '及格' " +
           "  ELSE '待改善' " +
           "END as level, " +
           "COUNT(s) " +
           "FROM Shop s " +
           "WHERE s.status = 'ACTIVE' " +
           "GROUP BY level")
    List<Object[]> countShopsByRatingLevel();
    
    /**
     * 查找需要关注的商家（评分低或订单少）
     */
    @Query("SELECT s FROM Shop s WHERE " +
           "(s.averageRating < 3.0 OR s.totalOrders < 10) AND " +
           "s.status = 'ACTIVE'")
    List<Shop> findShopsNeedAttention();
    
    /**
     * 查找今日新注册的商家
     */
    @Query("SELECT s FROM Shop s WHERE DATE(s.registrationTime) = CURRENT_DATE")
    List<Shop> findTodayRegisteredShops();
    
    /**
     * 查找待审核的商家
     */
    List<Shop> findByStatusOrderByRegistrationTimeAsc(Shop.ShopStatus status);
    
    /**
     * 根据多个ID查找商家
     */
    List<Shop> findByIdIn(List<Long> ids);
    
    /**
     * 根据商家名称和类型查找
     */
    List<Shop> findByShopNameContainingAndShopType(String shopName, Shop.ShopType shopType);
    
    /**
     * 根据商家名称和状态查找
     */
    List<Shop> findByShopNameContainingAndStatus(String shopName, Shop.ShopStatus status);
    
    /**
     * 根据类型和状态查找商家
     */
    List<Shop> findByShopTypeAndStatus(Shop.ShopType shopType, Shop.ShopStatus status);
    
    /**
     * 统计活跃商家数量
     */
    Long countByStatus(Shop.ShopStatus status);
    
    /**
     * 统计今日登录的商家数量
     */
    @Query("SELECT COUNT(s) FROM Shop s WHERE DATE(s.lastLoginTime) = CURRENT_DATE")
    Long countTodayLoginShops();
    
    /**
     * 查找连续多天未登录的商家
     */
    @Query("SELECT s FROM Shop s WHERE s.lastLoginTime < CURRENT_DATE - :days")
    List<Shop> findInactiveShops(@Param("days") Integer days);
    
    /**
     * 更新商家评分
     */
    @Query("UPDATE Shop s SET s.averageRating = :rating, s.totalReviews = s.totalReviews + 1 WHERE s.id = :shopId")
    void updateShopRating(@Param("shopId") Long shopId, @Param("rating") Double rating);
    
    /**
     * 更新商家订单统计
     */
    @Query("UPDATE Shop s SET s.totalOrders = s.totalOrders + 1, s.totalRevenue = s.totalRevenue + :amount WHERE s.id = :shopId")
    void updateShopStatistics(@Param("shopId") Long shopId, @Param("amount") Double amount);
    
    /**
     * 更新商家浏览数
     */
    @Query("UPDATE Shop s SET s.viewCount = s.viewCount + 1 WHERE s.id = :shopId")
    void incrementViewCount(@Param("shopId") Long shopId);
    
    /**
     * 更新商家收藏数
     */
    @Query("UPDATE Shop s SET s.favoriteCount = s.favoriteCount + :increment WHERE s.id = :shopId")
    void updateFavoriteCount(@Param("shopId") Long shopId, @Param("increment") Integer increment);
    
    /**
     * 更新商家分享数
     */
    @Query("UPDATE Shop s SET s.shareCount = s.shareCount + 1 WHERE s.id = :shopId")
    void incrementShareCount(@Param("shopId") Long shopId);
}