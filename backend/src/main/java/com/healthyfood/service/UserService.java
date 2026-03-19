package com.healthyfood.service;

import com.healthyfood.entity.User;
import com.healthyfood.entity.HealthRecord;
import com.healthyfood.vo.user.UserRegisterRequest;
import com.healthyfood.vo.user.UserLoginRequest;
import com.healthyfood.vo.user.UserProfileVO;
import com.healthyfood.vo.user.HealthProfileVO;
import com.healthyfood.common.ApiResult;
import com.healthyfood.common.ErrorCode;
import com.healthyfood.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务 - 处理用户注册、登录、健康档案管理等核心业务
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisService redisService;
    
    /**
     * 用户注册
     */
    @Transactional
    public ApiResult<UserProfileVO> register(UserRegisterRequest request) {
        log.info("用户注册: {}", request.getPhone());
        
        // 1. 验证手机号是否已注册
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new BusinessException(ErrorCode.USER_PHONE_EXISTS, "手机号已注册");
        }
        
        // 2. 验证邮箱是否已注册
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.USER_EMAIL_EXISTS, "邮箱已注册");
        }
        
        // 3. 密码加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            (request.getPassword() + Constants.PASSWORD_SALT).getBytes()
        );
        
        // 4. 创建用户
        User user = User.builder()
            .phone(request.getPhone())
            .email(request.getEmail())
            .password(encryptedPassword)
            .nickname(request.getNickname())
            .avatar(request.getAvatar())
            .gender(request.getGender())
            .birthday(request.getBirthday())
            .height(request.getHeight())
            .weight(request.getWeight())
            .activityLevel(request.getActivityLevel())
            .healthGoals(request.getHealthGoals())
            .foodRestrictions(request.getFoodRestrictions())
            .allergens(request.getAllergens())
            .tastePreferences(request.getTastePreferences())
            .status(User.Status.ACTIVE)
            .registerTime(LocalDateTime.now())
            .lastLoginTime(LocalDateTime.now())
            .build();
        
        // 5. 计算初始健康数据
        calculateInitialHealthData(user);
        
        // 6. 保存用户
        user = userRepository.save(user);
        
        // 7. 创建初始健康记录
        createInitialHealthRecord(user);
        
        // 8. 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        
        // 9. 缓存用户信息
        cacheUserInfo(user, token);
        
        log.info("用户注册成功: {}, ID: {}", user.getPhone(), user.getId());
        
        return ApiResult.success(convertToProfileVO(user, token));
    }
    
    /**
     * 用户登录
     */
    public ApiResult<UserProfileVO> login(UserLoginRequest request) {
        log.info("用户登录: {}", request.getPhone());
        
        // 1. 查找用户
        User user = userRepository.findByPhone(request.getPhone())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS, "用户不存在"));
        
        // 2. 验证密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            (request.getPassword() + Constants.PASSWORD_SALT).getBytes()
        );
        
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR, "密码错误");
        }
        
        // 3. 验证用户状态
        if (user.getStatus() == User.Status.DISABLED) {
            throw new BusinessException(ErrorCode.USER_DISABLED, "用户已被禁用");
        }
        
        if (user.getStatus() == User.Status.FROZEN) {
            throw new BusinessException(ErrorCode.USER_FROZEN, "用户已被冻结");
        }
        
        // 4. 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        userRepository.save(user);
        
        // 5. 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        
        // 6. 缓存用户信息
        cacheUserInfo(user, token);
        
        log.info("用户登录成功: {}, ID: {}", user.getPhone(), user.getId());
        
        return ApiResult.success(convertToProfileVO(user, token));
    }
    
    /**
     * 获取用户资料
     */
    public ApiResult<UserProfileVO> getProfile(Long userId) {
        log.info("获取用户资料: {}", userId);
        
        User user = getUserById(userId);
        
        // 获取最新健康记录
        HealthRecord latestHealthRecord = healthRecordRepository
            .findTopByUserIdOrderByRecordTimeDesc(userId)
            .orElse(null);
        
        UserProfileVO profileVO = convertToProfileVO(user, null);
        
        if (latestHealthRecord != null) {
            profileVO.setLatestHealthRecord(convertToHealthProfileVO(latestHealthRecord));
        }
        
        return ApiResult.success(profileVO);
    }
    
    /**
     * 更新用户资料
     */
    @Transactional
    public ApiResult<UserProfileVO> updateProfile(Long userId, UserProfileVO updateRequest) {
        log.info("更新用户资料: {}", userId);
        
        User user = getUserById(userId);
        
        // 更新基本信息
        if (updateRequest.getNickname() != null) {
            user.setNickname(updateRequest.getNickname());
        }
        
        if (updateRequest.getAvatar() != null) {
            user.setAvatar(updateRequest.getAvatar());
        }
        
        if (updateRequest.getGender() != null) {
            user.setGender(updateRequest.getGender());
        }
        
        if (updateRequest.getBirthday() != null) {
            user.setBirthday(updateRequest.getBirthday());
            // 生日更新需要重新计算年龄相关数据
            calculateAgeBasedHealthData(user);
        }
        
        // 更新身体数据
        boolean healthDataUpdated = false;
        if (updateRequest.getHeight() != null && !updateRequest.getHeight().equals(user.getHeight())) {
            user.setHeight(updateRequest.getHeight());
            healthDataUpdated = true;
        }
        
        if (updateRequest.getWeight() != null && !updateRequest.getWeight().equals(user.getWeight())) {
            user.setWeight(updateRequest.getWeight());
            healthDataUpdated = true;
        }
        
        if (updateRequest.getActivityLevel() != null) {
            user.setActivityLevel(updateRequest.getActivityLevel());
            healthDataUpdated = true;
        }
        
        // 更新健康目标
        if (updateRequest.getHealthGoals() != null) {
            user.setHealthGoals(updateRequest.getHealthGoals());
        }
        
        // 更新饮食限制
        if (updateRequest.getFoodRestrictions() != null) {
            user.setFoodRestrictions(updateRequest.getFoodRestrictions());
        }
        
        // 更新过敏原
        if (updateRequest.getAllergens() != null) {
            user.setAllergens(updateRequest.getAllergens());
        }
        
        // 更新口味偏好
        if (updateRequest.getTastePreferences() != null) {
            user.setTastePreferences(updateRequest.getTastePreferences());
        }
        
        // 如果健康数据有更新，重新计算
        if (healthDataUpdated) {
            calculateHealthData(user);
            
            // 创建新的健康记录
            createHealthRecord(user, "资料更新");
        }
        
        user.setUpdateTime(LocalDateTime.now());
        user = userRepository.save(user);
        
        // 更新缓存
        updateUserCache(user);
        
        log.info("用户资料更新成功: {}", userId);
        
        return ApiResult.success(convertToProfileVO(user, null));
    }
    
    /**
     * 更新健康数据
     */
    @Transactional
    public ApiResult<HealthProfileVO> updateHealthData(Long userId, HealthProfileVO healthData) {
        log.info("更新用户健康数据: {}", userId);
        
        User user = getUserById(userId);
        
        // 更新身体数据
        if (healthData.getHeight() != null) {
            user.setHeight(healthData.getHeight());
        }
        
        if (healthData.getWeight() != null) {
            user.setWeight(healthData.getWeight());
        }
        
        if (healthData.getActivityLevel() != null) {
            user.setActivityLevel(healthData.getActivityLevel());
        }
        
        // 重新计算健康数据
        calculateHealthData(user);
        
        user.setUpdateTime(LocalDateTime.now());
        user = userRepository.save(user);
        
        // 创建健康记录
        HealthRecord healthRecord = createHealthRecord(user, "手动更新健康数据");
        
        // 更新缓存
        updateUserCache(user);
        
        log.info("用户健康数据更新成功: {}", userId);
        
        return ApiResult.success(convertToHealthProfileVO(healthRecord));
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public ApiResult<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: {}", userId);
        
        User user = getUserById(userId);
        
        // 验证旧密码
        String encryptedOldPassword = DigestUtils.md5DigestAsHex(
            (oldPassword + Constants.PASSWORD_SALT).getBytes()
        );
        
        if (!user.getPassword().equals(encryptedOldPassword)) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR, "原密码错误");
        }
        
        // 加密新密码
        String encryptedNewPassword = DigestUtils.md5DigestAsHex(
            (newPassword + Constants.PASSWORD_SALT).getBytes()
        );
        
        // 更新密码
        user.setPassword(encryptedNewPassword);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 清除token缓存，强制重新登录
        clearUserTokenCache(userId);
        
        log.info("密码修改成功: {}", userId);
        
        return ApiResult.success();
    }
    
    /**
     * 重置密码（通过手机验证码）
     */
    @Transactional
    public ApiResult<Void> resetPassword(String phone, String verifyCode, String newPassword) {
        log.info("重置密码: {}", phone);
        
        // 1. 验证手机验证码
        if (!verifySmsCode(phone, verifyCode)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR, "验证码错误");
        }
        
        // 2. 查找用户
        User user = userRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS, "用户不存在"));
        
        // 3. 加密新密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(
            (newPassword + Constants.PASSWORD_SALT).getBytes()
        );
        
        // 4. 更新密码
        user.setPassword(encryptedPassword);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 5. 清除token缓存
        clearUserTokenCache(user.getId());
        
        // 6. 清除验证码
        clearSmsCode(phone);
        
        log.info("密码重置成功: {}", phone);
        
        return ApiResult.success();
    }
    
    /**
     * 发送手机验证码
     */
    public ApiResult<Void> sendVerifyCode(String phone, String type) {
        log.info("发送验证码: {}, 类型: {}", phone, type);
        
        // 1. 检查发送频率
        if (isVerifyCodeFrequently(phone)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_FREQUENT, "验证码发送过于频繁");
        }
        
        // 2. 生成验证码
        String verifyCode = generateVerifyCode();
        
        // 3. 发送短信（实际项目中调用短信服务）
        boolean sendSuccess = sendSms(phone, verifyCode, type);
        
        if (!sendSuccess) {
            throw new BusinessException(ErrorCode.SMS_SEND_FAILED, "短信发送失败");
        }
        
        // 4. 缓存验证码
        cacheVerifyCode(phone, verifyCode, type);
        
        log.info("验证码发送成功: {}, 类型: {}", phone, type);
        
        return ApiResult.success();
    }
    
    /**
     * 获取用户健康报告
     */
    public ApiResult<HealthReportVO> getHealthReport(Long userId, String period) {
        log.info("获取健康报告: {}, 周期: {}", userId, period);
        
        User user = getUserById(userId);
        
        // 获取指定周期的健康记录
        LocalDateTime startTime = calculatePeriodStartTime(period);
        LocalDateTime endTime = LocalDateTime.now();
        
        // 查询健康记录
        List<HealthRecord> healthRecords = healthRecordRepository
            .findByUserIdAndRecordTimeBetween(userId, startTime, endTime);
        
        // 生成健康报告
        HealthReportVO report = generateHealthReport(user, healthRecords, period);
        
        return ApiResult.success(report);
    }
    
    /**
     * 计算用户BMI
     */
    public Double calculateBMI(User user) {
        if (user.getHeight() == null || user.getWeight() == null || user.getHeight() <= 0) {
            return null;
        }
        
        double heightInMeters = user.getHeight() / 100.0;
        return user.getWeight() / (heightInMeters * heightInMeters);
    }
    
    /**
     * 计算基础代谢率 (BMR)
     */
    public Double calculateBMR(User user) {
        if (user.getGender() == null || user.getWeight() == null || 
            user.getHeight() == null || user.getBirthday() == null) {
            return null;
        }
        
        // 计算年龄
        int age = calculateAge(user.getBirthday());
        
        // 使用Mifflin-St Jeor公式
        if (user.getGender() == User.Gender.MALE) {
            return 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * age + 5;
        } else {
            return 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * age - 161;
        }
    }
    
    /**
     * 计算每日热量需求
     */
    public Double calculateDailyCalorieNeeds(User user) {
        Double bmr = calculateBMR(user);
        if (bmr == null || user.getActivityLevel() == null) {
            return null;
        }
        
        // 根据活动水平调整
        double activityMultiplier = getActivityMultiplier(user.getActivityLevel());
        return bmr * activityMultiplier;
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXISTS, "用户不存在"));
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 计算初始健康数据
     */
    private void calculateInitialHealthData(User user) {
        // 计算BMI
        Double bmi = calculateBMI(user);
        if (bmi != null) {
            user.setBmi(bmi);
            user.setBmiCategory(calculateBMICategory(bmi));
        }
        
        // 计算BMR
        Double bmr = calculateBMR(user);
        if (bmr != null) {
            user.setBmr(bmr);
        }
        
        // 计算每日热量需求
        Double dailyCalorieNeeds = calculateDailyCalorieNeeds(user);
        if (dailyCalorieNeeds != null) {
            user.setDailyCalorieNeeds(dailyCalorieNeeds);
        }
        
        // 计算营养需求
        calculateNutritionNeeds(user);
    }
    
    /**
     * 计算健康数据
     */
    private void calculateHealthData(User user) {
        calculateInitialHealthData(user);
    }
    
    /**
     * 计算年龄相关健康数据
     */
    private void calculateAgeBasedHealthData(User user) {
        // 重新计算BMR和热量需求
        Double bmr = calculateBMR(user);
        if (bmr != null) {
            user.setBmr(bmr);
        }
        
        Double dailyCalorieNeeds = calculateDailyCalorieNeeds(user);
        if (dailyCalorieNeeds != null) {
            user.setDailyCalorieNeeds(dailyCalorieNeeds);
        }
        
        calculateNutritionNeeds(user);
    }
    
    /**
     * 计算营养需求
     */
    private void calculateNutritionNeeds(User user) {
        if (user.getDailyCalorieNeeds() == null) {
            return;
        }
        
        double calorieNeeds = user.getDailyCalorieNeeds();
        
        // 蛋白质需求 (15-20% of calories)
        double proteinCalories = calorieNeeds * 0.175; // 取中间值
        double proteinGrams = proteinCalories / 4; // 1g蛋白质 = 4卡路里
        user.setProteinNeed(proteinGrams);
        
        // 碳水化合物需求 (45-65% of calories)
        double carbCalories = calorieNeeds * 0.55; // 取中间值
        double carbGrams = carbCalories / 4; // 1g碳水化合物 = 4卡路里
        user.setCarbohydrateNeed(carbGrams);
        
        // 脂肪需求 (20-35% of calories)
        double fatCalories = calorieNeeds * 0.275; // 取中间值
        double fatGrams = fatCalories / 9; // 1g脂肪 = 9卡路里
        user.setFatNeed(fatGrams);
        
        // 膳食纤维需求 (25-30g per day)
        user.setFiberNeed(27.5); // 取中间值
    }
    
    /**
     * 计算BMI分类
     */
    private String calculateBMICategory(Double bmi) {
        if (bmi < 18.5) {
            return "underweight";
        } else if (bmi < 24) {
            return "normal";
        } else if (bmi < 28) {
            return "overweight";
        } else {
            return "obese";
        }
    }
    
    /**
     * 计算年龄
     */
    private int calculateAge(LocalDate birthday) {
        return Period.between(birthday, LocalDate.now()).getYears();
    }
    
    /**
     * 获取活动水平乘数
     */
    private double getActivityMultiplier(User.ActivityLevel activityLevel) {
        switch (activityLevel) {
            case SEDENTARY:     return 1.2;   // 久坐
            case LIGHT:         return 1.375; // 轻度活动
            case MODERATE:      return 1.55;  // 中度活动
            case ACTIVE:        return 1.725; // 活跃
            case VERY_ACTIVE:   return 1.9;   // 非常活跃
            default:            return 1.2;
        }
    }
    
    /**
     * 创建初始健康记录
     */
    private HealthRecord createInitialHealthRecord(User user) {
        return createHealthRecord(user, "初始健康记录");
    }
    
    /**
     * 创建健康记录
     */
    private HealthRecord createHealthRecord(User user, String remark) {
        HealthRecord record = HealthRecord.builder()
            .userId(user.getId())
            .height(user.getHeight())
            .weight(user.getWeight())
            .bmi(user.getBmi())
            .bmiCategory(user.getBmiCategory())
            .bmr(user.getBmr())
            .dailyCalorieNeeds(user.getDailyCalorieNeeds())
            .proteinNeed(user.getProteinNeed())
            .carbohydrateNeed(user.getCarbohydrateNeed())
            .fatNeed(user.getFatNeed())
            .fiberNeed(user.getFiberNeed())
            .activityLevel(user.getActivityLevel())
            .healthScore(calculateHealthScore(user))
            .remark(remark)
            .recordTime(LocalDateTime.now())
            .build();
        
        return healthRecordRepository.save(record);
    }
    
    /**
     * 计算健康评分
     */
    private Double calculateHealthScore(User user) {
        double score = 100.0;
        
        // BMI评分 (40%)
        if (user.getBmi() != null) {
            double bmiScore = calculateBMIScore(user.getBmi());
            score = score * 0.6 + bmiScore * 0.4;
        }
        
        // 活动水平评分 (30%)
        if (user.getActivityLevel() != null) {
            double activityScore = calculateActivityScore(user.getActivityLevel());
            score = score * 0.7 + activityScore * 0.3;
        }
        
        // 饮食多样性评分 (30%) - 需要饮食记录数据
        // 暂时给基础分
        
        return Math.round(score * 10) / 10.0;
    }
    
    /**
     * 计算BMI评分
     */
    private double calculateBMIScore(Double bmi) {
        if (bmi >= 18.5 && bmi < 24) {
            return 100.0; // 正常范围
        } else if (bmi >= 17 && bmi < 18.5) {
            return 80.0; // 偏瘦
        } else if (bmi >= 24 && bmi < 27) {
            return 70.0; // 超重
        } else if (bmi >= 27 && bmi < 30) {
            return 50.0; // 肥胖
        } else if (bmi >= 16 && bmi < 17) {
            return 60.0; // 中度偏瘦
        } else if (bmi >= 30 && bmi < 35) {
            return 40.0; // 中度肥胖
        } else {
            return 30.0; // 严重偏瘦或肥胖
        }
    }
    
    /**
     * 计算活动水平评分
     */
    private double calculateActivityScore(User.ActivityLevel activityLevel) {
        switch (activityLevel) {
            case VERY_ACTIVE:   return 100.0;
            case ACTIVE:        return 85.0;
            case MODERATE:      return 70.0;
            case LIGHT:         return 60.0;
            case SEDENTARY:     return 40.0;
            default:            return 50.0;
        }
    }
    
    /**
     * 转换用户资料VO
     */
    private UserProfileVO convertToProfileVO(User user, String token) {
        return UserProfileVO.builder()
            .id(user.getId())
            .phone(user.getPhone())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .avatar(user.getAvatar())
            .gender(user.getGender())
            .birthday(user.getBirthday())
            .height(user.getHeight())
            .weight(user.getWeight())
            .bmi(user.getBmi())
            .bmiCategory(user.getBmiCategory())
            .bmr(user.getBmr())
            .dailyCalorieNeeds(user.getDailyCalorieNeeds())
            .proteinNeed(user.getProteinNeed())
            .carbohydrateNeed(user.getCarbohydrateNeed())
            .fatNeed(user.getFatNeed())
            .fiberNeed(user.getFiberNeed())
            .activityLevel(user.getActivityLevel())
            .healthGoals(user.getHealthGoals())
            .foodRestrictions(user.getFoodRestrictions())
            .allergens(user.getAllergens())
            .tastePreferences(user.getTastePreferences())
            .status(user.getStatus())
            .registerTime(user.getRegisterTime())
            .lastLoginTime(user.getLastLoginTime())
            .loginCount(user.getLoginCount())
            .token(token)
            .build();
    }
    
    /**
     * 转换健康资料VO
     */
    private HealthProfileVO convertToHealthProfileVO(HealthRecord record) {
        return HealthProfileVO.builder()
            .id(record.getId())
            .userId(record.getUserId())
            .height(record.getHeight())
            .weight(record.getWeight())
            .bmi(record.getBmi())
            .bmiCategory(record.getBmiCategory())
            .bmr(record.getBmr())
            .dailyCalorieNeeds(record.getDailyCalorieNeeds())
            .proteinNeed(record.getProteinNeed())
            .carbohydrateNeed(record.getCarbohydrateNeed())
            .fatNeed(record.getFatNeed())
            .fiberNeed(record.getFiberNeed())
            .activityLevel(record.getActivityLevel())
            .healthScore(record.getHealthScore())
            .remark(record.getRemark())
            .recordTime(record.getRecordTime())
            .build();
    }
    
    /**
     * 缓存用户信息
     */
    private void cacheUserInfo(User user, String token) {
        String userKey = Constants.REDIS_KEY_USER_PREFIX + user.getId();
        String tokenKey = Constants.REDIS_KEY_TOKEN_PREFIX + token;
        
        // 缓存用户信息 (30分钟)
        redisService.set(userKey, user, 30 * 60);
        
        // 缓存token-userId映射 (30分钟)
        redisService.set(tokenKey, user.getId().toString(), 30 * 60);
    }
    
    /**
     * 更新用户缓存
     */
    private void updateUserCache(User user) {
        String userKey = Constants.REDIS_KEY_USER_PREFIX + user.getId();
        redisService.set(userKey, user, 30 * 60);
    }
    
    /**
     * 清除用户token缓存
     */
    private void clearUserTokenCache(Long userId) {
        // 实际项目中需要更复杂的token管理
        // 这里简化处理
    }
    
    /**
     * 验证短信验证码
     */
    private boolean verifySmsCode(String phone, String verifyCode) {
        String cacheKey = Constants.REDIS_KEY_SMS_PREFIX + phone;
        String cachedCode = redisService.get(cacheKey);
        
        return verifyCode.equals(cachedCode);
    }
    
    /**
     * 检查验证码发送频率
     */
    private boolean isVerifyCodeFrequently(String phone) {
        String frequencyKey = Constants.REDIS_KEY_SMS_FREQUENCY_PREFIX + phone;
        String lastSendTime = redisService.get(frequencyKey);
        
        if (lastSendTime == null) {
            return false;
        }
        
        long lastTime = Long.parseLong(lastSendTime);
        long currentTime = System.currentTimeMillis();
        
        // 60秒内只能发送一次
        return currentTime - lastTime < 60 * 1000;
    }
    
    /**
     * 生成验证码
     */
    private String generateVerifyCode() {
        // 生成6位数字验证码
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    /**
     * 发送短信
     */
    private boolean sendSms(String phone, String verifyCode, String type) {
        // 实际项目中调用短信服务
        // 这里模拟发送成功
        log.info("模拟发送短信: 手机号={}, 验证码={}, 类型={}", phone, verifyCode, type);
        return true;
    }
    
    /**
     * 缓存验证码
     */
    private void cacheVerifyCode(String phone, String verifyCode, String type) {
        String codeKey = Constants.REDIS_KEY_SMS_PREFIX + phone;
        String frequencyKey = Constants.REDIS_KEY_SMS_FREQUENCY_PREFIX + phone;
        
        // 缓存验证码 (5分钟有效)
        redisService.set(codeKey, verifyCode, 5 * 60);
        
        // 记录发送时间
        redisService.set(frequencyKey, String.valueOf(System.currentTimeMillis()), 60);
    }
    
    /**
     * 清除验证码
     */
    private void clearSmsCode(String phone) {
        String codeKey = Constants.REDIS_KEY_SMS_PREFIX + phone;
        redisService.delete(codeKey);
    }
    
    /**
     * 计算周期开始时间
     */
    private LocalDateTime calculatePeriodStartTime(String period) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (period) {
            case "week":
                return now.minusWeeks(1);
            case "month":
                return now.minusMonths(1);
            case "quarter":
                return now.minusMonths(3);
            case "year":
                return now.minusYears(1);
            default:
                return now.minusDays(7); // 默认一周
        }
    }
    
    /**
     * 生成健康报告
     */
    private HealthReportVO generateHealthReport(User user, List<HealthRecord> records, String period) {
        HealthReportVO report = new HealthReportVO();
        report.setUserId(user.getId());
        report.setPeriod(period);
        report.setGenerateTime(LocalDateTime.now());
        
        if (records.isEmpty()) {
            report.setMessage("该周期内无健康记录数据");
            return report;
        }
        
        // 分析体重趋势
        analyzeWeightTrend(report, records);
        
        // 分析BMI趋势
        analyzeBMITrend(report, records);
        
        // 分析健康评分趋势
        analyzeHealthScoreTrend(report, records);
        
        // 生成建议
        generateSuggestions(report, user, records);
        
        return report;
    }
    
    /**
     * 分析体重趋势
     */
    private void analyzeWeightTrend(HealthReportVO report, List<HealthRecord> records) {
        if (records.size() < 2) {
            report.setWeightTrend("数据不足，无法分析趋势");
            return;
        }
        
        HealthRecord first = records.get(0);
        HealthRecord last = records.get(records.size() - 1);
        
        if (first.getWeight() == null || last.getWeight() == null) {
            report.setWeightTrend("体重数据不完整");
            return;
        }
        
        double weightChange = last.getWeight() - first.getWeight();
        double changePercentage = (weightChange / first.getWeight()) * 100;
        
        if (Math.abs(changePercentage) < 1) {
            report.setWeightTrend("体重保持稳定");
        } else if (weightChange > 0) {
            report.setWeightTrend(String.format("体重增加 %.1fkg (%.1f%%)", weightChange, changePercentage));
        } else {
            report.setWeightTrend(String.format("体重减少 %.1fkg (%.1f%%)", -weightChange, -changePercentage));
        }
        
        report.setWeightChange(weightChange);
        report.setWeightChangePercentage(changePercentage);
    }
    
    /**
     * 分析BMI趋势
     */
    private void analyzeBMITrend(HealthReportVO report, List<HealthRecord> records) {
        if (records.size() < 2) {
            report.setBmiTrend("数据不足，无法分析趋势");
            return;
        }
        
        HealthRecord first = records.get(0);
        HealthRecord last = records.get(records.size() - 1);
        
        if (first.getBmi() == null || last.getBmi() == null) {
            report.setBmiTrend("BMI数据不完整");
            return;
        }
        
        double bmiChange = last.getBmi() - first.getBmi();
        
        if (Math.abs(bmiChange) < 0.1) {
            report.setBmiTrend("BMI保持稳定");
        } else if (bmiChange > 0) {
            report.setBmiTrend(String.format("BMI上升 %.2f", bmiChange));
        } else {
            report.setBmiTrend(String.format("BMI下降 %.2f", -bmiChange));
        }
        
        report.setCurrentBmi(last.getBmi());
        report.setCurrentBmiCategory(last.getBmiCategory());
    }
    
    /**
     * 分析健康评分趋势
     */
    private void analyzeHealthScoreTrend(HealthReportVO report, List<HealthRecord> records) {
        if (records.size() < 2) {
            report.setHealthScoreTrend("数据不足，无法分析趋势");
            return;
        }
        
        HealthRecord first = records.get(0);
        HealthRecord last = records.get(records.size() - 1);
        
        if (first.getHealthScore() == null || last.getHealthScore() == null) {
            report.setHealthScoreTrend("健康评分数据不完整");
            return;
        }
        
        double scoreChange = last.getHealthScore() - first.getHealthScore();
        
        if (Math.abs(scoreChange) < 1) {
            report.setHealthScoreTrend("健康评分保持稳定");
        } else if (scoreChange > 0) {
            report.setHealthScoreTrend(String.format("健康评分提升 %.1f分", scoreChange));
        } else {
            report.setHealthScoreTrend(String.format("健康评分下降 %.1f分", -scoreChange));
        }
        
        report.setCurrentHealthScore(last.getHealthScore());
    }
    
    /**
     * 生成建议
     */
    private void generateSuggestions(HealthReportVO report, User user, List<HealthRecord> records) {
        List<String> suggestions = new ArrayList<>();
        
        HealthRecord latest = records.get(records.size() - 1);
        
        // BMI建议
        if (latest.getBmiCategory() != null) {
            switch (latest.getBmiCategory()) {
                case "underweight":
                    suggestions.add("您的BMI偏低，建议适当增加营养摄入，特别是优质蛋白质");
                    break;
                case "overweight":
                case "obese":
                    suggestions.add("您的BMI偏高，建议控制总热量摄入，增加运动量");
                    break;
            }
        }
        
        // 体重趋势建议
        if (report.getWeightChange() != null && Math.abs(report.getWeightChange()) > 2) {
            if (report.getWeightChange() > 0) {
                suggestions.add("近期体重增长明显，建议关注饮食结构和运动量");
            } else {
                suggestions.add("近期体重下降明显，请确保营养摄入充足");
            }
        }
        
        // 健康评分建议
        if (latest.getHealthScore() != null && latest.getHealthScore() < 70) {
            suggestions.add("健康评分有待提升，建议改善生活习惯，均衡饮食");
        }
        
        // 活动水平建议
        if (user.getActivityLevel() == User.ActivityLevel.SEDENTARY) {
            suggestions.add("您属于久坐人群，建议每天进行30分钟中等强度运动");
        }
        
        report.setSuggestions(suggestions);
    }
}