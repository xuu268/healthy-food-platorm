package com.healthyfood.vo.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * 健康档案视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileVO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 基本信息
     */
    private BasicInfoVO basicInfo;
    
    /**
     * 身体测量数据
     */
    private BodyMeasurementsVO bodyMeasurements;
    
    /**
     * 健康目标
     */
    private HealthGoalsVO healthGoals;
    
    /**
     * 饮食偏好和限制
     */
    private DietaryPreferencesVO dietaryPreferences;
    
    /**
     * 运动习惯
     */
    private ExerciseHabitsVO exerciseHabits;
    
    /**
     * 生活方式
     */
    private LifestyleVO lifestyle;
    
    /**
     * 医疗历史
     */
    private MedicalHistoryVO medicalHistory;
    
    /**
     * 营养需求计算
     */
    private NutritionalNeedsVO nutritionalNeeds;
    
    /**
     * 健康评分
     */
    private HealthScoreVO healthScore;
    
    /**
     * 趋势分析
     */
    private TrendAnalysisVO trendAnalysis;
    
    /**
     * 建议和提醒
     */
    private RecommendationsVO recommendations;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 基本信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfoVO {
        private String nickname;
        private String avatar;
        private String gender;
        private LocalDate birthday;
        private Integer age;
        private String bloodType;           // 血型
        private String occupation;          // 职业
        private String workNature;          // 工作性质
        private Double workStressLevel;     // 工作压力等级 (0-10)
        private Double sleepQuality;        // 睡眠质量 (0-10)
        private Integer averageSleepHours;  // 平均睡眠小时数
    }
    
    /**
     * 身体测量数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BodyMeasurementsVO {
        private Double height;              // 身高 (cm)
        private Double weight;              // 体重 (kg)
        private Double waistCircumference;  // 腰围 (cm)
        private Double hipCircumference;    // 臀围 (cm)
        private Double chestCircumference;  // 胸围 (cm)
        private Double bodyFatPercentage;   // 体脂率 (%)
        private Double muscleMass;          // 肌肉量 (kg)
        private Double boneMass;            // 骨量 (kg)
        private Double waterPercentage;     // 水分率 (%)
        private Double visceralFatLevel;    // 内脏脂肪等级
        private Double metabolicAge;        // 代谢年龄
        private Double bmi;                 // BMI
        private String bmiCategory;         // BMI分类
        private Double whr;                 // 腰臀比
        private String whrCategory;         // 腰臀比分类
        private LocalDate measurementDate;  // 测量日期
    }
    
    /**
     * 健康目标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthGoalsVO {
        private Set<String> primaryGoals;           // 主要目标
        private Set<String> secondaryGoals;         // 次要目标
        private Double targetWeight;                // 目标体重 (kg)
        private Double targetBodyFat;               // 目标体脂率 (%)
        private Double targetBmi;                   // 目标BMI
        private Integer targetDailySteps;           // 目标每日步数
        private Integer targetExerciseMinutes;      // 目标运动分钟数
        private Double targetCalorieIntake;         // 目标热量摄入
        private LocalDate targetDate;               // 目标达成日期
        private String motivation;                  // 动力来源
        private Double confidenceLevel;             // 信心等级 (0-10)
        private Map<String, Double> goalProgress;   // 目标进度
    }
    
    /**
     * 饮食偏好和限制
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DietaryPreferencesVO {
        private Set<String> preferredCuisines;      // 偏好菜系
        private Set<String> dislikedFoods;          // 不喜欢的食物
        private Set<String> foodAllergies;          // 食物过敏
        private Set<String> foodIntolerances;       // 食物不耐受
        private Set<String> dietaryRestrictions;    // 饮食限制
        private String dietaryPattern;              // 饮食模式
        private Integer mealsPerDay;                // 每日餐数
        private Boolean preferOrganic;              // 偏好有机食品
        private Boolean preferLocal;                // 偏好本地食品
        private Boolean preferSeasonal;             // 偏好时令食品
        private Double saltPreference;              // 盐偏好 (0-10)
        private Double sugarPreference;             // 糖偏好 (0-10)
        private Double oilPreference;               // 油偏好 (0-10)
        private Double spicePreference;             // 辣度偏好 (0-10)
        private Map<String, Double> tasteProfile;   // 口味画像
    }
    
    /**
     * 运动习惯
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseHabitsVO {
        private String activityLevel;               // 活动水平
        private Set<String> exerciseTypes;          // 运动类型
        private Integer exerciseFrequency;          // 运动频率 (次/周)
        private Integer averageExerciseDuration;    // 平均运动时长 (分钟)
        private Integer averageDailySteps;          // 平均每日步数
        private String preferredExerciseTime;       // 偏好运动时间
        private String exerciseIntensity;           // 运动强度
        private Boolean hasPersonalTrainer;         // 是否有私人教练
        private String gymMembership;               // 健身房会员
        private Map<String, Integer> exerciseHistory; // 运动历史记录
        private String exerciseGoals;               // 运动目标
        private String exerciseBarriers;            // 运动障碍
    }
    
    /**
     * 生活方式
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LifestyleVO {
        private String smokingStatus;               // 吸烟状态
        private Integer smokingYears;               // 吸烟年数
        private Integer cigarettesPerDay;           // 每日吸烟量
        private String alcoholConsumption;          // 饮酒情况
        private Integer alcoholFrequency;           // 饮酒频率
        private String alcoholType;                 // 酒类偏好
        private String caffeineConsumption;         // 咖啡因摄入
        private Integer coffeeCupsPerDay;           // 每日咖啡杯数
        private String waterIntake;                 // 饮水情况
        private Integer waterGlassesPerDay;         // 每日水杯数
        private String stressManagement;            // 压力管理方式
        private String relaxationMethods;           // 放松方法
        private String workLifeBalance;             // 工作生活平衡
        private String socialActivityLevel;         // 社交活动水平
        private String travelFrequency;             // 旅行频率
    }
    
    /**
     * 医疗历史
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalHistoryVO {
        private Set<String> chronicConditions;      // 慢性疾病
        private Set<String> pastIllnesses;          // 既往病史
        private Set<String> surgeries;              // 手术史
        private Set<String> hospitalizations;       // 住院史
        private Set<String> medications;            // 当前用药
        private Set<String> supplements;            // 营养补充剂
        private Set<String> allergies;              // 过敏史
        private String familyMedicalHistory;        // 家族病史
        private String vaccinationStatus;           // 疫苗接种状态
        private String lastMedicalCheckup;          // 最近体检时间
        private String checkupResults;              // 体检结果
        private String doctorRecommendations;       // 医生建议
    }
    
    /**
     * 营养需求计算
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionalNeedsVO {
        private Double bmr;                         // 基础代谢率
        private Double tdee;                        // 每日总能量消耗
        private Double dailyCalorieNeeds;           // 每日热量需求
        private Double proteinNeeds;                // 蛋白质需求 (g)
        private Double carbNeeds;                   // 碳水化合物需求 (g)
        private Double fatNeeds;                    // 脂肪需求 (g)
        private Double fiberNeeds;                  // 纤维需求 (g)
        private Double waterNeeds;                  // 水分需求 (ml)
        private Map<String, Double> vitaminNeeds;   // 维生素需求
        private Map<String, Double> mineralNeeds;   // 矿物质需求
        private Map<String, Double> aminoAcidNeeds; // 氨基酸需求
        private String calculationMethod;           // 计算方法
        private LocalDateTime lastCalculated;       // 最后计算时间
    }
    
    /**
     * 健康评分
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthScoreVO {
        private Double overallScore;                // 总体健康评分
        private Double nutritionScore;              // 营养评分
        private Double exerciseScore;               // 运动评分
        private Double lifestyleScore;              // 生活方式评分
        private Double bodyCompositionScore;        // 身体成分评分
        private Double metabolicHealthScore;        // 代谢健康评分
        private String healthLevel;                 // 健康等级
        private String riskAssessment;              // 风险评估
        private Map<String, Double> scoreDetails;   // 评分详情
        private LocalDateTime scoreDate;            // 评分日期
        private Double scoreChange;                 // 评分变化
        private String scoreTrend;                  // 评分趋势
    }
    
    /**
     * 趋势分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendAnalysisVO {
        private Map<LocalDate, Double> weightTrend;         // 体重趋势
        private Map<LocalDate, Double> bmiTrend;            // BMI趋势
        private Map<LocalDate, Double> bodyFatTrend;        // 体脂率趋势
        private Map<LocalDate, Double> calorieIntakeTrend;  // 热量摄入趋势
        private Map<LocalDate, Double> exerciseTrend;       // 运动趋势
        private Map<LocalDate, Double> healthScoreTrend;    // 健康评分趋势
        private String overallTrend;                        // 总体趋势
        private Map<String, String> improvementAreas;       // 改善领域
        private Map<String, String> riskFactors;            // 风险因素
        private Map<String, Double> progressMetrics;        // 进展指标
    }
    
    /**
     * 建议和提醒
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationsVO {
        private Set<String> dietaryRecommendations;         // 饮食建议
        private Set<String> exerciseRecommendations;        // 运动建议
        private Set<String> lifestyleRecommendations;       // 生活方式建议
        private Set<String> healthCheckRecommendations;     // 健康检查建议
        private Set<String> supplementRecommendations;      // 补充剂建议
        private Map<String, String> personalizedTips;       // 个性化提示
        private Set<String> warnings;                       // 警告事项
        private Set<String> reminders;                      // 提醒事项
        private String nextReviewDate;                      // 下次复查日期
        private String priorityActions;                     // 优先行动
    }
    
    /**
     * 计算健康档案完整度
     */
    public Integer calculateCompletionRate() {
        int totalSections = 0;
        int completedSections = 0;
        
        // 检查每个部分
        if (basicInfo != null && isBasicInfoComplete()) {
            completedSections++;
        }
        totalSections++;
        
        if (bodyMeasurements != null && isBodyMeasurementsComplete()) {
            completedSections++;
        }
        totalSections++;
        
        if (healthGoals != null && isHealthGoalsComplete()) {
            completedSections++;
        }
        totalSections++;
        
        if (dietaryPreferences != null && isDietaryPreferencesComplete()) {
            completedSections++;
        }
        totalSections++;
        
        if (exerciseHabits != null && isExerciseHabitsComplete()) {
            completedSections++;
        }
        totalSections++;
        
        if (lifestyle != null && isLifestyleComplete()) {
            completedSections++;
        }
        totalSections++;
        
        if (medicalHistory != null && isMedicalHistoryComplete()) {
            completedSections++;
        }
        totalSections++;
        
        return totalSections > 0 ? (completedSections * 100 / totalSections) : 0;
    }
    
    private boolean isBasicInfoComplete() {
        return basicInfo.getHeight() != null && 
               basicInfo.getWeight() != null && 
               basicInfo.getGender() != null && 
               basicInfo.getBirthday() != null;
    }
    
    private boolean isBodyMeasurementsComplete() {
        return bodyMeasurements.getHeight() != null && 
               bodyMeasurements.getWeight() != null;
    }
    
    private boolean isHealthGoalsComplete() {
        return healthGoals.getPrimaryGoals() != null && 
               !healthGoals.getPrimaryGoals().isEmpty();
    }
    
    private boolean isDietaryPreferencesComplete() {
        return dietaryPreferences.getDietaryRestrictions() != null;
    }
    
    private boolean isExerciseHabitsComplete() {
        return exerciseHabits.getActivityLevel() != null;
    }
    
    private boolean isLifestyleComplete() {
        return lifestyle.getSmokingStatus() != null && 
               lifestyle.getAlcoholConsumption() != null;
    }
    
    private boolean isMedicalHistoryComplete() {
        return medicalHistory.getAllergies() != null;
    }
    
    /**
     * 获取健康档案摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (basicInfo != null) {
            summary.append("基本信息: ").append(basicInfo.getGender())
                   .append(", ").append(basicInfo.getAge()).append("岁");
        }
        
        if (bodyMeasurements != null && bodyMeasurements.getBmi() != null) {
            summary.append("\nBMI: ").append(String.format("%.1f", bodyMeasurements.getBmi()))
                   .append(" (").append(bodyMeasurements.getBmiCategory()).append(")");
        }
        
        if (healthScore != null && healthScore.getOverallScore() != null) {
            summary.append("\n健康评分: ").append(String.format("%.1f", healthScore.getOverallScore()))
                   .append("/100 (").append(healthScore.getHealthLevel()).append(")");
        }
        
        if (healthGoals != null && healthGoals.getPrimaryGoals() != null) {
            summary.append("\n健康目标: ").append(String.join(", ", healthGoals.getPrimaryGoals()));
        }
        
        return summary.toString();
    }
    
    /**
     * 获取关键健康指标
     */
    public Map<String, Object> getKeyHealthIndicators() {
        return Map.of(
            "bmi", bodyMeasurements != null ? bodyMeasurements.getBmi() : null,
            "bodyFat", bodyMeasurements != null ? bodyMeasurements.getBodyFatPercentage() : null,
            "healthScore", healthScore != null ? healthScore.getOverallScore() : null,
            "dailyCalories", nutritionalNeeds != null ? nutritionalNeeds.getDailyCalorieNeeds() : null,
            "activityLevel", exerciseHabits != null ? exerciseHabits.getActivityLevel() : null,
            "completionRate", calculateCompletionRate()
        );
    }
}