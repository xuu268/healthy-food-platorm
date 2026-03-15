package com.healthyfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "health_record")
@TableName("health_record")
public class HealthRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 餐次常量
     */
    public static final String MEAL_BREAKFAST = "breakfast";    // 早餐
    public static final String MEAL_LUNCH = "lunch";            // 午餐
    public static final String MEAL_DINNER = "dinner";          // 晚餐
    public static final String MEAL_SNACK = "snack";            // 零食

    /**
     * 记录ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 记录日期
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * 餐次 breakfast/lunch/dinner/snack
     */
    @Column(name = "meal_type", length = 20)
    private String mealType;

    /**
     * 摄入热量
     */
    @Column(name = "calories_intake", columnDefinition = "int default 0")
    private Integer caloriesIntake = 0;

    /**
     * 消耗热量
     */
    @Column(name = "calories_burned", columnDefinition = "int default 0")
    private Integer caloriesBurned = 0;

    /**
     * 蛋白质摄入(g)
     */
    @Column(name = "protein_intake", precision = 5, scale = 2, columnDefinition = "decimal(5,2) default 0.00")
    private BigDecimal proteinIntake = BigDecimal.ZERO;

    /**
     * 碳水摄入(g)
     */
    @Column(name = "carb_intake", precision = 5, scale = 2, columnDefinition = "decimal(5,2) default 0.00")
    private BigDecimal carbIntake = BigDecimal.ZERO;

    /**
     * 脂肪摄入(g)
     */
    @Column(name = "fat_intake", precision = 5, scale = 2, columnDefinition = "decimal(5,2) default 0.00")
    private BigDecimal fatIntake = BigDecimal.ZERO;

    /**
     * 水分摄入(L)
     */
    @Column(name = "water_intake", precision = 5, scale = 2, columnDefinition = "decimal(5,2) default 0.00")
    private BigDecimal waterIntake = BigDecimal.ZERO;

    /**
     * 步数
     */
    @Column(columnDefinition = "int default 0")
    private Integer steps = 0;

    /**
     * 睡眠时长
     */
    @Column(name = "sleep_hours", precision = 3, scale = 1, columnDefinition = "decimal(3,1) default 0.0")
    private BigDecimal sleepHours = BigDecimal.ZERO;

    /**
     * 体重(kg)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    /**
     * 体脂率(%)
     */
    @Column(name = "body_fat", precision = 4, scale = 2)
    private BigDecimal bodyFat;

    /**
     * 血压
     */
    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    /**
     * 血糖(mmol/L)
     */
    @Column(name = "blood_sugar", precision = 4, scale = 2)
    private BigDecimal bloodSugar;

    /**
     * 心情
     */
    @Column(length = 20)
    private String mood;

    /**
     * 备注
     */
    @Column(columnDefinition = "text")
    private String notes;

    /**
     * 设备类型
     */
    @Column(name = "device_type", length = 50)
    private String deviceType;

    /**
     * 设备ID
     */
    @Column(name = "device_id", length = 100)
    private String deviceId;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @TableLogic
    @Column(columnDefinition = "tinyint default 0")
    private Integer deleted = 0;

    /**
     * 用户信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private User user;

    /**
     * 计算净热量（摄入-消耗）
     */
    public Integer calculateNetCalories() {
        return caloriesIntake - caloriesBurned;
    }

    /**
     * 计算热量平衡状态
     */
    public String getCalorieBalanceStatus() {
        int netCalories = calculateNetCalories();
        
        if (netCalories > 500) {
            return "热量盈余";
        } else if (netCalories < -500) {
            return "热量赤字";
        } else {
            return "热量平衡";
        }
    }

    /**
     * 计算营养比例
     */
    public NutritionRatio calculateNutritionRatio() {
        NutritionRatio ratio = new NutritionRatio();
        
        // 计算总热量（1g蛋白质=4卡，1g碳水=4卡，1g脂肪=9卡）
        BigDecimal proteinCalories = proteinIntake.multiply(BigDecimal.valueOf(4));
        BigDecimal carbCalories = carbIntake.multiply(BigDecimal.valueOf(4));
        BigDecimal fatCalories = fatIntake.multiply(BigDecimal.valueOf(9));
        
        BigDecimal totalCalories = proteinCalories.add(carbCalories).add(fatCalories);
        
        if (totalCalories.compareTo(BigDecimal.ZERO) > 0) {
            ratio.setProteinRatio(proteinCalories.divide(totalCalories, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            ratio.setCarbRatio(carbCalories.divide(totalCalories, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            ratio.setFatRatio(fatCalories.divide(totalCalories, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
        }
        
        return ratio;
    }

    /**
     * 检查营养是否均衡
     */
    public boolean isNutritionBalanced() {
        NutritionRatio ratio = calculateNutritionRatio();
        
        // 理想比例：蛋白质15-25%，碳水45-65%，脂肪20-35%
        boolean proteinOk = ratio.getProteinRatio().compareTo(BigDecimal.valueOf(15)) >= 0 &&
                           ratio.getProteinRatio().compareTo(BigDecimal.valueOf(25)) <= 0;
        boolean carbOk = ratio.getCarbRatio().compareTo(BigDecimal.valueOf(45)) >= 0 &&
                        ratio.getCarbRatio().compareTo(BigDecimal.valueOf(65)) <= 0;
        boolean fatOk = ratio.getFatRatio().compareTo(BigDecimal.valueOf(20)) >= 0 &&
                       ratio.getFatRatio().compareTo(BigDecimal.valueOf(35)) <= 0;
        
        return proteinOk && carbOk && fatOk;
    }

    /**
     * 获取餐次描述
     */
    public String getMealDescription() {
        if (mealType == null) {
            return "全天记录";
        }
        
        switch (mealType) {
            case MEAL_BREAKFAST:
                return "早餐";
            case MEAL_LUNCH:
                return "午餐";
            case MEAL_DINNER:
                return "晚餐";
            case MEAL_SNACK:
                return "零食";
            default:
                return mealType;
        }
    }

    /**
     * 获取心情图标
     */
    public String getMoodIcon() {
        if (mood == null) {
            return "😐";
        }
        
        switch (mood.toLowerCase()) {
            case "happy":
            case "高兴":
                return "😊";
            case "sad":
            case "悲伤":
                return "😢";
            case "angry":
            case "生气":
                return "😠";
            case "tired":
            case "疲劳":
                return "😴";
            case "stressed":
            case "压力":
                return "😰";
            case "energetic":
            case "精力充沛":
                return "💪";
            default:
                return "😐";
        }
    }

    /**
     * 计算睡眠质量
     */
    public String getSleepQuality() {
        if (sleepHours == null) {
            return "未知";
        }
        
        double hours = sleepHours.doubleValue();
        if (hours >= 7 && hours <= 9) {
            return "良好";
        } else if (hours >= 6 && hours < 7) {
            return "一般";
        } else if (hours >= 9 && hours <= 10) {
            return "较多";
        } else if (hours > 10) {
            return "过多";
        } else {
            return "不足";
        }
    }

    /**
     * 计算活动水平
     */
    public String getActivityLevel() {
        if (steps == null) {
            return "未知";
        }
        
        if (steps >= 10000) {
            return "非常活跃";
        } else if (steps >= 7500) {
            return "活跃";
        } else if (steps >= 5000) {
            return "一般";
        } else if (steps >= 3000) {
            return "较少";
        } else {
            return "久坐";
        }
    }

    /**
     * 检查水分摄入是否充足
     */
    public boolean isWaterIntakeSufficient() {
        if (waterIntake == null) {
            return false;
        }
        
        // 成人每日建议饮水量：1.5-2.0升
        return waterIntake.compareTo(BigDecimal.valueOf(1.5)) >= 0;
    }

    /**
     * 获取血压分类
     */
    public String getBloodPressureCategory() {
        if (bloodPressure == null || bloodPressure.isEmpty()) {
            return "未知";
        }
        
        try {
            String[] parts = bloodPressure.split("/");
            if (parts.length == 2) {
                int systolic = Integer.parseInt(parts[0].trim());
                int diastolic = Integer.parseInt(parts[1].trim());
                
                if (systolic < 120 && diastolic < 80) {
                    return "正常";
                } else if (systolic < 130 && diastolic < 85) {
                    return "正常高值";
                } else if (systolic < 140 && diastolic < 90) {
                    return "高血压1级";
                } else if (systolic < 180 && diastolic < 110) {
                    return "高血压2级";
                } else {
                    return "高血压3级";
                }
            }
        } catch (Exception e) {
            // 解析失败
        }
        
        return "数据异常";
    }

    /**
     * 获取血糖分类
     */
    public String getBloodSugarCategory() {
        if (bloodSugar == null) {
            return "未知";
        }
        
        double value = bloodSugar.doubleValue();
        
        // 空腹血糖分类
        if (value < 3.9) {
            return "低血糖";
        } else if (value < 6.1) {
            return "正常";
        } else if (value < 7.0) {
            return "糖尿病前期";
        } else {
            return "糖尿病";
        }
    }

    /**
     * 获取BMI（如果提供了体重）
     */
    public BigDecimal calculateBMI(BigDecimal height) {
        if (weight == null || height == null || height.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        
        BigDecimal heightInMeters = height.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal bmi = weight.divide(heightInMeters.pow(2), 2, BigDecimal.ROUND_HALF_UP);
        
        return bmi;
    }

    /**
     * 获取BMI分类
     */
    public String getBMICategory(BigDecimal height) {
        BigDecimal bmi = calculateBMI(height);
        if (bmi == null) {
            return "未知";
        }
        
        double value = bmi.doubleValue();
        if (value < 18.5) {
            return "偏瘦";
        } else if (value < 24) {
            return "正常";
        } else if (value < 28) {
            return "超重";
        } else {
            return "肥胖";
        }
    }

    /**
     * 获取健康评分
     */
    public HealthScore calculateHealthScore(BigDecimal height) {
        HealthScore score = new HealthScore();
        int totalScore = 0;
        int maxScore = 0;
        
        // 1. 热量平衡 (20分)
        maxScore += 20;
        String calorieStatus = getCalorieBalanceStatus();
        if ("热量平衡".equals(calorieStatus)) {
            totalScore += 20;
        } else if ("热量盈余".equals(calorieStatus) || "热量赤字".equals(calorieStatus)) {
            totalScore += 10;
        }
        
        // 2. 营养均衡 (20分)
        maxScore += 20;
        if (isNutritionBalanced()) {
            totalScore += 20;
        } else {
            totalScore += 10;
        }
        
        // 3. 水分摄入 (10分)
        maxScore += 10;
        if (isWaterIntakeSufficient()) {
            totalScore += 10;
        } else {
            totalScore += 5;
        }
        
        // 4. 睡眠质量 (15分)
        maxScore += 15;
        String sleepQuality = getSleepQuality();
        if ("良好".equals(sleepQuality)) {
            totalScore += 15;
        } else if ("一般".equals(sleepQuality)) {
            totalScore += 10;
        } else {
            totalScore += 5;
        }
        
        // 5. 活动水平 (15分)
        maxScore += 15;
        String activityLevel = getActivityLevel();
        if ("非常活跃".equals(activityLevel)) {
            totalScore += 15;
        } else if ("活跃".equals(activityLevel)) {
            totalScore += 12;
        } else if ("一般".equals(activityLevel)) {
            totalScore += 8;
        } else {
            totalScore += 4;
        }
        
        // 6. 血压 (10分)
        maxScore += 10;
        String bpCategory = getBloodPressureCategory();
        if ("正常".equals(bpCategory)) {
            totalScore += 10;
        } else if ("正常高值".equals(bpCategory)) {
            totalScore += 7;
        } else {
            totalScore += 3;
        }
        
        // 7. 血糖 (10分)
        maxScore += 10;
        String bsCategory = getBloodSugarCategory();
        if ("正常".equals(bsCategory)) {
            totalScore += 10;
        } else if ("糖尿病前期".equals(bsCategory)) {
            totalScore += 6;
        } else {
            totalScore += 2;
        }
        
        score.setTotalScore(totalScore);
        score.setMaxScore(maxScore);
        score.setPercentage((double) totalScore / maxScore * 100);
        
        return score;
    }

    /**
     * 获取记录摘要
     */
    public String getRecordSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(date).append(" ").append(getMealDescription()).append("\n");
        
        if (caloriesIntake > 0) {
            sb.append("🔥 摄入: ").append(caloriesIntake).append("卡 ");
        }
        
        if (caloriesBurned > 0) {
            sb.append("消耗: ").append(caloriesBurned).append("卡\n");
        }
        
        if (proteinIntake.compareTo(BigDecimal.ZERO) > 0 || 
            carbIntake.compareTo(BigDecimal.ZERO) > 0 || 
            fatIntake.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("🍎 营养: ");
            if (proteinIntake.compareTo(BigDecimal.ZERO) > 0) {
                sb.append("蛋").append(proteinIntake).append("g ");
            }
            if (carbIntake.compareTo(BigDecimal.ZERO) > 0) {
                sb.append("碳").append(carbIntake).append("g ");
            }
            if (fatIntake.compareTo(BigDecimal.ZERO) > 0) {
                sb.append("脂").append(fatIntake).append("g");
            }
            sb.append("\n");
        }
        
        if (steps > 0) {
            sb.append("🚶 步数: ").append(steps).append(" ");
        }
        
        if (sleepHours.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("😴 睡眠: ").append(sleepHours).append("小时");
        }
        
        return sb.toString();
    }

    /**
     * 检查记录是否完整
     */
    public boolean isCompleteRecord() {
        return caloriesIntake > 0 || 
               caloriesBurned > 0 || 
               proteinIntake.compareTo(BigDecimal.ZERO) > 0 ||
               carbIntake.compareTo(BigDecimal.ZERO) > 0 ||
               fatIntake.compareTo(BigDecimal.ZERO) > 0 ||
               steps > 0 ||
               sleepHours.compareTo(BigDecimal.ZERO) > 0 ||
               weight != null ||
               bodyFat != null ||
               bloodPressure != null ||
               bloodSugar != null;
    }

    /**
     * 合并记录（用于合并同一餐次的多次记录）
     */
    public void mergeRecord(HealthRecord other) {
        if (other == null || !date.equals(other.getDate()) || 
            !mealType.equals(other.getMealType())) {
            return;
        }
        
        this.caloriesIntake += other.getCaloriesIntake();
        this.caloriesBurned += other.getCaloriesBurned();
        this.proteinIntake = this.proteinIntake.add(other.getProteinIntake());
        this.carbIntake = this.carbIntake.add(other.getCarbIntake());
        this.fatIntake = this.fatIntake.add(other.getFatIntake());
        this.waterIntake = this.waterIntake.add(other.getWaterIntake());
        this.steps += other.getSteps();
        
        // 对于数值型数据，取平均值
        if (other.getSleepHours() != null && other.getSleepHours().compareTo(BigDecimal.ZERO) > 0) {
            if (this.sleepHours.compareTo(BigDecimal.ZERO) == 0) {
                this.sleepHours = other.getSleepHours();
            } else {
                this.sleepHours = this.sleepHours.add(other.getSleepHours())
                        .divide(BigDecimal.valueOf(2), 1, BigDecimal.ROUND_HALF_UP);
            }
        }
        
        // 保留最新的测量数据
        if (other.getWeight() != null) {
            this.weight = other.getWeight();
        }
        if (other.getBodyFat() != null) {
            this.bodyFat = other.getBodyFat();
        }
        if (other.getBloodPressure() != null) {
            this.bloodPressure = other.getBloodPressure();
        }
        if (other.getBloodSugar() != null) {
            this.bloodSugar = other.getBloodSugar();
        }
        
        // 合并备注
        if (other.getNotes() != null && !other.getNotes().isEmpty()) {
            if (this.notes == null || this.notes.isEmpty()) {
                this.notes = other.getNotes();
            } else {
                this.notes += "; " + other.getNotes();
            }
        }
    }

    /**
     * 营养比例类
     */
    @Data
    public static class NutritionRatio {
        private BigDecimal proteinRatio = BigDecimal.ZERO;  // 蛋白质比例%
        private BigDecimal carbRatio = BigDecimal.ZERO;     // 碳水比例%
        private BigDecimal fatRatio = BigDecimal.ZERO;      // 脂肪比例%
        
        public String getRatioDescription() {
            return String.format("蛋白质: %.1f%%, 碳水: %.1f%%, 脂肪: %.1f%%",
                    proteinRatio.doubleValue(), carbRatio.doubleValue(), fatRatio.doubleValue());
        }
    }

    /**
     * 健康评分类
     */
    @Data
    public static class HealthScore {
        private int totalScore;
        private int maxScore;
        private double percentage;
        
        public String getScoreDescription() {
            if (percentage >= 80) {
                return "优秀";
            } else if (percentage >= 60) {
                return "良好";
            } else if (percentage >= 40) {
                return "一般";
            } else {
                return "需改进";
            }
        }
        
        public String getScoreSummary() {
            return String.format("%d/%d分 (%.1f%%) - %s", 
                    totalScore, maxScore, percentage, getScoreDescription());
        }
    }

    /**
     * 验证记录数据
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        if (userId == null) {
            result.addError("用户ID不能为空");
        }
        
        if (date == null) {
            result.addError("记录日期不能为空");
        } else if (date.isAfter(LocalDate.now())) {
            result.addError("记录日期不能晚于今天");
        }
        
        if (caloriesIntake < 0) {
            result.addError("摄入热量不能为负数");
        }
        
        if (caloriesBurned < 0) {
            result.addError("消耗热量不能为负数");
        }
        
        if (proteinIntake.compareTo(BigDecimal.ZERO) < 0 ||
            carbIntake.compareTo(BigDecimal.ZERO) < 0 ||
            fatIntake.compareTo(BigDecimal.ZERO) < 0) {
            result.addError("营养摄入量不能为负数");
        }
        
        if (waterIntake.compareTo(BigDecimal.ZERO) < 0) {
            result.addError("水分摄入量不能为负数");
        }
        
        if (steps < 0) {
            result.addError("步数不能为负数");
        }
        
        if (sleepHours.compareTo(BigDecimal.ZERO) < 0) {
            result.addError("睡眠时长不能为负数");
        }
        
        return result;
    }

    /**
     * 验证结果类
     */
    @Data
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();
        
        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }
        
        public String getErrorMessage() {
            return errors.toString();
        }
    }

    /**
     * 获取设备类型描述
     */
    public String getDeviceDescription() {
        if (deviceType == null) {
            return "手动记录";
        }
        
        switch (deviceType.toLowerCase()) {
            case "smartwatch":
                return "智能手表";
            case "fitnessband":
                return "运动手环";
            case "smartscale":
                return "智能体重秤";
            case "bloodpressuremonitor":
                return "血压计";
            case "bloodglucosemeter":
                return "血糖仪";
            case "app":
                return "手机应用";
            default:
                return deviceType;
        }
    }

    /**
     * 检查是否是设备自动记录
     */
    public boolean isAutoRecorded() {
        return deviceType != null && deviceId != null;
    }

    /**
     * 获取记录来源
     */
    public String getRecordSource() {
        if (isAutoRecorded()) {
            return getDeviceDescription() + "自动记录";
        } else {
            return "手动记录";
        }
    }

    /**
     * 检查是否是全天总结记录
     */
    public boolean isDailySummary() {
        return mealType == null;
    }

    /**
     * 获取记录类型
     */
    public String getRecordType() {
        if (isDailySummary()) {
            return "全天总结";
        } else {
            return getMealDescription() + "记录";
        }
    }

    /**
     * 计算记录完整性百分比
     */
    public int calculateCompletenessPercentage() {
        int filledFields = 0;
        int totalFields = 0;
        
        // 基本字段
        totalFields += 2; // date, mealType
        filledFields += (date != null ? 1 : 0);
        filledFields += (mealType != null ? 1 : 0);
        
        // 营养字段
        totalFields += 6; // caloriesIntake, caloriesBurned, protein, carb, fat, water
        filledFields += (caloriesIntake > 0 ? 1 : 0);
        filledFields += (caloriesBurned > 0 ? 1 : 0);
        filledFields += (proteinIntake.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        filledFields += (carbIntake.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        filledFields += (fatIntake.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        filledFields += (waterIntake.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        
        // 运动睡眠字段
        totalFields += 2; // steps, sleepHours
        filledFields += (steps > 0 ? 1 : 0);
        filledFields += (sleepHours.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        
        // 健康测量字段
        totalFields += 4; // weight, bodyFat, bloodPressure, bloodSugar
        filledFields += (weight != null ? 1 : 0);
        filledFields += (bodyFat != null ? 1 : 0);
        filledFields += (bloodPressure != null ? 1 : 0);
        filledFields += (bloodSugar != null ? 1 : 0);
        
        if (totalFields == 0) {
            return 0;
        }
        
        return (filledFields * 100) / totalFields;
    }

    /**
     * 获取记录质量描述
     */
    public String getRecordQuality() {
        int completeness = calculateCompletenessPercentage();
        
        if (completeness >= 80) {
            return "详细记录";
        } else if (completeness >= 50) {
            return "一般记录";
        } else if (completeness >= 20) {
            return "简略记录";
        } else {
            return "基础记录";
        }
    }
}