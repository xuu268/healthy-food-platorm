package com.healthyfood.repository;

import com.healthyfood.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据昵称查找用户
     */
    Optional<User> findByNickname(String nickname);
    
    /**
     * 根据状态查找用户列表
     */
    List<User> findByStatus(User.Status status);
    
    /**
     * 根据注册时间范围查找用户
     */
    List<User> findByRegisterTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据最后登录时间查找活跃用户
     */
    List<User> findByLastLoginTimeAfter(LocalDateTime time);
    
    /**
     * 根据健康目标查找用户
     */
    @Query("SELECT u FROM User u WHERE :goal MEMBER OF u.healthGoals")
    List<User> findByHealthGoal(@Param("goal") String goal);
    
    /**
     * 根据饮食限制查找用户
     */
    @Query("SELECT u FROM User u WHERE :restriction MEMBER OF u.foodRestrictions")
    List<User> findByFoodRestriction(@Param("restriction") String restriction);
    
    /**
     * 根据口味偏好查找用户
     */
    @Query("SELECT u FROM User u WHERE :preference MEMBER OF u.tastePreferences")
    List<User> findByTastePreference(@Param("preference") String preference);
    
    /**
     * 统计用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countByStatus(@Param("status") User.Status status);
    
    /**
     * 统计每日新增用户
     */
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.registerTime) = CURRENT_DATE")
    Long countTodayRegisteredUsers();
    
    /**
     * 查找BMI在指定范围的用户
     */
    @Query("SELECT u FROM User u WHERE u.bmi BETWEEN :minBmi AND :maxBmi")
    List<User> findByBmiRange(@Param("minBmi") Double minBmi, @Param("maxBmi") Double maxBmi);
    
    /**
     * 查找需要健康关注的用户（BMI异常或久未登录）
     */
    @Query("SELECT u FROM User u WHERE " +
           "(u.bmi < 18.5 OR u.bmi >= 24) OR " +
           "u.lastLoginTime < :inactiveTime")
    List<User> findUsersNeedAttention(@Param("inactiveTime") LocalDateTime inactiveTime);
    
    /**
     * 根据多个ID查找用户
     */
    List<User> findByIdIn(List<Long> ids);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查昵称是否存在
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 删除过期未激活用户
     */
    @Query("DELETE FROM User u WHERE u.status = 'INACTIVE' AND u.registerTime < :expireTime")
    int deleteInactiveUsers(@Param("expireTime") LocalDateTime expireTime);
}