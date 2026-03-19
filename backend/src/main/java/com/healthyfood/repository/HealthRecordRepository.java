package com.healthyfood.repository;

import com.healthyfood.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 健康记录数据访问接口
 */
@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    
    /**
     * 根据用户ID查找健康记录
     */
    List<HealthRecord> findByUserId(Long userId);
    
    /**
     * 根据用户ID和时间范围查找健康记录
     */
    List<HealthRecord> findByUserIdAndRecordTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找用户最新的健康记录
     */
    Optional<HealthRecord> findTopByUserIdOrderByRecordTimeDesc(Long userId);
    
    /**
     * 查找用户最早的健康记录
     */
    Optional<HealthRecord> findTopByUserIdOrderByRecordTimeAsc(Long userId);
    
    /**
     * 根据BMI分类查找健康记录
     */
    List<HealthRecord> findByBmiCategory(String bmiCategory);
    
    /**
     * 根据健康评分范围查找记录
     */
    List<HealthRecord> findByHealthScoreBetween(Double minScore, Double maxScore);
    
    /**
     * 根据活动水平查找记录
     */
    List<HealthRecord> findByActivityLevel(HealthRecord.ActivityLevel activityLevel);
    
    /**
     * 统计用户的健康记录数量
     */
    Long countByUserId(Long userId);
    
    /**
     * 计算用户的平均健康评分
     */
    @Query("SELECT AVG(h.healthScore) FROM HealthRecord h WHERE h.userId = :userId")
    Double calculateAverageHealthScore(@Param("userId") Long userId);
    
    /**
     * 计算用户的平均BMI
     */
    @Query("SELECT AVG(h.bmi) FROM HealthRecord h WHERE h.userId = :userId")
    Double calculateAverageBmi(@Param("userId") Long userId);
    
    /**
     * 查找用户体重变化趋势
     */
    @Query("SELECT h FROM HealthRecord h WHERE h.userId = :userId AND h.weight IS NOT NULL ORDER BY h.recordTime")
    List<HealthRecord> findWeightTrend(@Param("userId") Long userId);
    
    /**
     * 查找用户BMI变化趋势
     */
    @Query("SELECT h FROM HealthRecord h WHERE h.userId = :userId AND h.bmi IS NOT NULL ORDER BY h.recordTime")
    List<HealthRecord> findBmiTrend(@Param("userId") Long userId);
    
    /**
     * 查找用户健康评分变化趋势
     */
    @Query("SELECT h FROM HealthRecord h WHERE h.userId = :userId AND h.healthScore IS NOT NULL ORDER BY h.recordTime")
    List<HealthRecord> findHealthScoreTrend(@Param("userId") Long userId);
    
    /**
     * 查找需要关注的健康记录（评分低或BMI异常）
     */
    @Query("SELECT h FROM HealthRecord h WHERE " +
           "(h.healthScore < 60 OR h.bmi < 18.5 OR h.bmi >= 28) AND " +
           "h.recordTime > :startTime")
    List<HealthRecord> findRecordsNeedAttention(@Param("startTime") LocalDateTime startTime);
    
    /**
     * 查找用户的体重峰值记录
     */
    @Query("SELECT h FROM HealthRecord h WHERE h.userId = :userId AND h.weight = " +
           "(SELECT MAX(h2.weight) FROM HealthRecord h2 WHERE h2.userId = :userId)")
    Optional<HealthRecord> findMaxWeightRecord(@Param("userId") Long userId);
    
    /**
     * 查找用户的体重谷值记录
     */
    @Query("SELECT h FROM HealthRecord h WHERE h.userId = :userId AND h.weight = " +
           "(SELECT MIN(h2.weight) FROM HealthRecord h2 WHERE h2.userId = :userId)")
    Optional<HealthRecord> findMinWeightRecord(@Param("userId") Long userId);
    
    /**
     * 查找用户的最佳健康评分记录
     */
    @Query("SELECT h FROM HealthRecord h WHERE h.userId = :userId AND h.healthScore = " +
           "(SELECT MAX(h2.healthScore) FROM HealthRecord h2 WHERE h2.userId = :userId)")
    Optional<HealthRecord> findBestHealthScoreRecord(@Param("userId") Long userId);
    
    /**
     * 根据多个用户ID查找最新健康记录
     */
    @Query("SELECT h FROM HealthRecord h WHERE h.id IN " +
           "(SELECT MAX(h2.id) FROM HealthRecord h2 WHERE h2.userId IN :userIds GROUP BY h2.userId)")
    List<HealthRecord> findLatestRecordsByUserIds(@Param("userIds") List<Long> userIds);
    
    /**
     * 删除用户的旧健康记录（保留最近N条）
     */
    @Query(nativeQuery = true, value = 
           "DELETE FROM health_record WHERE user_id = :userId AND id NOT IN " +
           "(SELECT id FROM (SELECT id FROM health_record WHERE user_id = :userId " +
           "ORDER BY record_time DESC LIMIT :keepCount) AS keep_ids)")
    int deleteOldRecords(@Param("userId") Long userId, @Param("keepCount") int keepCount);
    
    /**
     * 统计每日新增健康记录
     */
    @Query("SELECT COUNT(h) FROM HealthRecord h WHERE DATE(h.recordTime) = CURRENT_DATE")
    Long countTodayRecords();
    
    /**
     * 统计各BMI分类的用户数量
     */
    @Query("SELECT h.bmiCategory, COUNT(DISTINCT h.userId) FROM HealthRecord h " +
           "WHERE h.recordTime = (SELECT MAX(h2.recordTime) FROM HealthRecord h2 WHERE h2.userId = h.userId) " +
           "GROUP BY h.bmiCategory")
    List<Object[]> countUsersByBmiCategory();
    
    /**
     * 统计各健康评分区间的用户数量
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN h.healthScore >= 90 THEN '优秀' " +
           "  WHEN h.healthScore >= 80 THEN '良好' " +
           "  WHEN h.healthScore >= 70 THEN '中等' " +
           "  WHEN h.healthScore >= 60 THEN '及格' " +
           "  ELSE '待改善' " +
           "END as level, " +
           "COUNT(DISTINCT h.userId) " +
           "FROM HealthRecord h " +
           "WHERE h.recordTime = (SELECT MAX(h2.recordTime) FROM HealthRecord h2 WHERE h2.userId = h.userId) " +
           "GROUP BY level")
    List<Object[]> countUsersByHealthScoreLevel();
}