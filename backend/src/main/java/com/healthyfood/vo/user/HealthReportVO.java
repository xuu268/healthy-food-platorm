package com.healthyfood.vo.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 健康报告视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthReportVO {
    
    /**
     * 报告ID
     */
    private String reportId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 报告标题
     */
    private String title;
    
    /**
     * 报告类型
     */
    private ReportType reportType;
    
    /**
     * 报告期间
     */
    private PeriodVO period;
    
    /**
     * 执行摘要
     */
    private ExecutiveSummaryVO executiveSummary;
    
    /**
     * 关键指标
     */
    private KeyMetricsVO keyMetrics;
    
    /**
     * 详细分析
     */
    private DetailedAnalysisVO detailedAnalysis;
    
    /**
     * 趋势分析
     */
    private TrendAnalysisVO trendAnalysis;
    
    /**
     * 风险评估
     */
    private RiskAssessmentVO riskAssessment;
    
    /**
     * 建议和行动计划
     */
    private RecommendationsVO recommendations;
    
    /**
     * 比较分析
     */
    private ComparativeAnalysisVO comparativeAnalysis;
    
    /**
     * 附件和参考资料
     */
    private AttachmentsVO attachments;
    
    /**
     * 报告元数据
     */
    private MetadataVO metadata;
    
    /**
     * 报告类型枚举
     */
    public enum ReportType {
        DAILY,          // 日报
        WEEKLY,         // 周报
        MONTHLY,        // 月报
        QUARTERLY,      // 季报
        YEARLY,         // 年报
        CUSTOM,         // 自定义报告
        COMPREHENSIVE,  // 综合报告
        NUTRITION,      // 营养报告
        EXERCISE,       // 运动报告
        LIFESTYLE,      // 生活方式报告
        PROGRESS        // 进展报告
    }
    
    /**
     * 报告期间
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodVO {
        private LocalDate startDate;
        private LocalDate endDate;
        private String periodName;
        private Integer durationDays;
        
        public String getFormattedPeriod() {
            return startDate + " 至 " + endDate + " (" + periodName + ")";
        }
    }
    
    /**
     * 执行摘要
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutiveSummaryVO {
        private String overview;                     // 总体概述
        private String keyFindings;                  // 主要发现
        private String overallAssessment;            // 总体评估
        private Map<String, String> highlights;      // 亮点
        private Map<String, String> concerns;        // 关注点
        private String summaryScore;                 // 摘要评分
        private String trendSummary;                 // 趋势摘要
        private String nextSteps;                    // 下一步行动
    }
    
    /**
     * 关键指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyMetricsVO {
        // 身体指标
        private Double weight;                       // 体重 (kg)
        private Double weightChange;                 // 体重变化
        private Double bmi;                          // BMI
        private Double bmiChange;                    // BMI变化
        private Double bodyFatPercentage;            // 体脂率 (%)
        private Double bodyFatChange;                // 体脂率变化
        private Double waistCircumference;           // 腰围 (cm)
        private Double waistChange;                  // 腰围变化
        
        // 健康指标
        private Double healthScore;                  // 健康评分
        private Double scoreChange;                  // 评分变化
        private String healthLevel;                  // 健康等级
        private String levelChange;                  // 等级变化
        
        // 营养指标
        private Double averageDailyCalories;         // 平均每日热量
        private Double calorieBalance;               // 热量平衡
        private Double proteinIntake;                // 蛋白质摄入 (g)
        private Double carbIntake;                   // 碳水化合物摄入 (g)
        private Double fatIntake;                    // 脂肪摄入 (g)
        private Map<String, Double> nutrientRatios;  // 营养素比例
        
        // 运动指标
        private Integer totalExerciseMinutes;        // 总运动分钟数
        private Integer averageDailySteps;           // 平均每日步数
        private Integer exerciseDays;                // 运动天数
        private Double exerciseConsistency;          // 运动一致性
        
        // 行为指标
        private Double sleepQuality;                 // 睡眠质量
        private Double stressLevel;                  // 压力水平
        private Double waterIntake;                  // 水分摄入
        private Double mealRegularity;               // 饮食规律性
    }
    
    /**
     * 详细分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailedAnalysisVO {
        // 营养分析
        private NutritionAnalysisVO nutritionAnalysis;
        
        // 运动分析
        private ExerciseAnalysisVO exerciseAnalysis;
        
        // 生活方式分析
        private LifestyleAnalysisVO lifestyleAnalysis;
        
        // 身体成分分析
        private BodyCompositionAnalysisVO bodyCompositionAnalysis;
        
        // 代谢健康分析
        private MetabolicHealthAnalysisVO metabolicHealthAnalysis;
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NutritionAnalysisVO {
            private Map<String, Double> calorieDistribution;     // 热量分布
            private Map<String, Double> macronutrientBalance;    // 宏量营养素平衡
            private Map<String, Double> micronutrientStatus;     // 微量营养素状态
            private List<String> dietaryPatterns;                // 饮食模式
            private Map<String, Double> foodGroupConsumption;    // 食物组消费
            private String nutritionAssessment;                  // 营养评估
            private List<String> nutritionStrengths;             // 营养优势
            private List<String> nutritionWeaknesses;            // 营养不足
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ExerciseAnalysisVO {
            private Map<String, Integer> exerciseTypeDistribution; // 运动类型分布
            private Map<String, Double> intensityDistribution;     // 强度分布
            private Map<String, Integer> timeDistribution;         // 时间分布
            private Double exerciseEfficiency;                     // 运动效率
            private String exerciseConsistency;                    // 运动一致性
            private List<String> exerciseAchievements;             // 运动成就
            private List<String> exerciseChallenges;               // 运动挑战
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LifestyleAnalysisVO {
            private Map<String, Double> sleepPatterns;            // 睡眠模式
            private Map<String, Double> stressPatterns;           // 压力模式
            private Map<String, Double> hydrationPatterns;        // 水分摄入模式
            private Map<String, Double> mealPatterns;             // 进餐模式
            private String lifestyleBalance;                      // 生活方式平衡
            private List<String> healthyHabits;                   // 健康习惯
            private List<String> unhealthyHabits;                 // 不健康习惯
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BodyCompositionAnalysisVO {
            private Map<String, Double> compositionChanges;       // 成分变化
            private String muscleFatRatio;                        // 肌肉脂肪比
            private String visceralFatAssessment;                 // 内脏脂肪评估
            private String metabolicAgeComparison;                // 代谢年龄比较
            private List<String> compositionStrengths;            // 成分优势
            private List<String> compositionConcerns;             // 成分关注点
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MetabolicHealthAnalysisVO {
            private Map<String, Double> metabolicMarkers;         // 代谢标志物
            private String insulinSensitivity;                    // 胰岛素敏感性
            private String inflammationLevel;                     // 炎症水平
            private String oxidativeStress;                       // 氧化应激
            private List<String> metabolicStrengths;              // 代谢优势
            private List<String> metabolicRisks;                  // 代谢风险
        }
    }
    
    /**
     * 趋势分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendAnalysisVO {
        private List<TrendDataVO> weightTrend;                    // 体重趋势
        private List<TrendDataVO> bmiTrend;                       // BMI趋势
        private List<TrendDataVO> bodyFatTrend;                   // 体脂率趋势
        private List<TrendDataVO> healthScoreTrend;               // 健康评分趋势
        private List<TrendDataVO> calorieIntakeTrend;             // 热量摄入趋势
        private List<TrendDataVO> exerciseTrend;                  // 运动趋势
        private String overallTrendDirection;                     // 总体趋势方向
        private Double trendStrength;                             // 趋势强度
        private Map<String, String> significantTrends;            // 显著趋势
        private Map<String, String> trendPatterns;                // 趋势模式
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TrendDataVO {
            private LocalDate date;
            private Double value;
            private Double change;
            private String trend;
        }
    }
    
    /**
     * 风险评估
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessmentVO {
        private Map<String, Double> healthRisks;                  // 健康风险
        private String overallRiskLevel;                          // 总体风险等级
        private List<String> immediateRisks;                      // 即时风险
        private List<String> longTermRisks;                       // 长期风险
        private Map<String, String> riskFactors;                  // 风险因素
        private Map<String, String> protectiveFactors;            // 保护因素
        private String riskTrend;                                 // 风险趋势
        private List<String> riskRecommendations;                 // 风险建议
    }
    
    /**
     * 建议和行动计划
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationsVO {
        private List<RecommendationVO> priorityRecommendations;   // 优先建议
        private List<RecommendationVO> nutritionRecommendations;  // 营养建议
        private List<RecommendationVO> exerciseRecommendations;   // 运动建议
        private List<RecommendationVO> lifestyleRecommendations;  // 生活方式建议
        private ActionPlanVO actionPlan;                          // 行动计划
        private List<GoalVO> shortTermGoals;                      // 短期目标
        private List<GoalVO> longTermGoals;                       // 长期目标
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RecommendationVO {
            private String category;                              // 类别
            private String title;                                 // 标题
            private String description;                           // 描述
            private String rationale;                             // 原理
            private String priority;                              // 优先级
            private String expectedImpact;                        // 预期影响
            private String implementationTips;                    // 实施提示
            private List<String> resources;                       // 资源
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ActionPlanVO {
            private String planName;                              // 计划名称
            private LocalDate startDate;                          // 开始日期
            private LocalDate endDate;                            // 结束日期
            private List<ActionItemVO> weeklyActions;             // 每周行动
            private List<ActionItemVO> dailyHabits;               // 每日习惯
            private Map<String, String> milestones;               // 里程碑
            private String successMetrics;                        // 成功指标
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ActionItemVO {
            private String action;                                // 行动
            private String frequency;                             // 频率
            private String duration;                              // 时长
            private String intensity;                             // 强度
            private String trackingMethod;                        // 跟踪方法
            private String notes;                                 // 备注
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GoalVO {
            private String goal;                                  // 目标
            private String type;                                  // 类型
            private LocalDate targetDate;                         // 目标日期
            private Double targetValue;                           // 目标值
            private Double currentValue;                          // 当前值
            private Double progress;                              // 进度
            private List<String> actionSteps;                     // 行动步骤
        }
    }
    
    /**
     * 比较分析
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparativeAnalysisVO {
        private ComparisonVO previousPeriod;                      // 与上一期比较
        private ComparisonVO samePeriodLastYear;                  // 与去年同期比较
        private ComparisonVO peerGroup;                           // 与同龄人比较
        private ComparisonVO healthStandards;                     // 与健康标准比较
        private ComparisonVO personalBest;                        // 与个人最佳比较
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ComparisonVO {
            private Map<String, Double> metricComparisons;        // 指标比较
            private String overallComparison;                     // 总体比较
            private List<String> areasOfImprovement;              // 改进领域
            private List<String> areasOfDecline;                  // 下降领域
            private String comparativeAssessment;                 // 比较评估
        }
    }
    
    /**
     * 附件和参考资料
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentsVO {
        private List<ChartVO> charts;                             // 图表
        private List<TableVO> dataTables;                         // 数据表
        private List<ReferenceVO> references;                     // 参考资料
        private List<ResourceVO> additionalResources;             // 额外资源
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ChartVO {
            private String title;                                 // 标题
            private String type;                                  // 类型
            private String description;                           // 描述
            private String dataSource;                            // 数据源
            private String insights;                              // 洞察
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TableVO {
            private String title;                                 // 标题
            private List<String> headers;                         // 表头
            private List<List<String>> rows;                      // 行数据
            private String summary;                               // 摘要
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ReferenceVO {
            private String title;                                 // 标题
            private String source;                                // 来源
            private String url;                                   // URL
            private String relevance;                             // 相关性
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ResourceVO {
            private String type;                                  // 类型
            private String title;                                 // 标题
            private String description;                           // 描述
            private String link;                                  // 链接
        }
    }
    
    /**
     * 报告元数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetadataVO {
        private LocalDateTime generatedTime;                      // 生成时间
        private String generatedBy;                               // 生成者
        private String reportVersion;                             // 报告版本
        private String dataSources;                               // 数据源
        private String methodology;                               // 方法论
        private String limitations;                               // 局限性
        private String confidentiality;                           // 保密性
        private String nextReportDate;                            // 下次报告日期
        private String contactInformation;                        // 联系信息
    }
    
    /**
     * 获取报告摘要
     */
    public String getReportSummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append("健康报告: ").append(title).append("\n");
        summary.append("期间: ").append(period.getFormattedPeriod()).append("\n");
        
        if (executiveSummary != null) {
            summary.append("总体评估: ").append(executiveSummary.getOverallAssessment()).append("\n");
            summary.append("健康评分: ").append(keyMetrics != null ? keyMetrics.getHealthScore() : "N/A").append("\n");
        }
        
        if (keyMetrics != null && keyMetrics.getWeightChange() != null) {
            summary.append("体重变化: ").append(String.format("%+.1fkg", keyMetrics.getWeightChange())).append("\n");
        }
        
        if (recommendations != null && recommendations.getPriorityRecommendations() != null) {
            summary.append("优先建议: ").append(recommendations.getPriorityRecommendations().size()).append("条\n");
        }
        
        return summary.toString();
    }
    
    /**
     * 获取关键洞察
     */
    public List<String> getKeyInsights() {
        List<String> insights = new java.util.ArrayList<>();
        
        if (executiveSummary != null && executiveSummary.getKeyFindings() != null) {
            insights.add(executiveSummary.getKeyFindings());
        }
        
        if (trendAnalysis != null && trendAnalysis.getOverallTrendDirection() != null) {
            insights.add("总体趋势: " + trendAnalysis.getOverallTrendDirection());
        }
        
        if (riskAssessment != null && riskAssessment.getOverallRiskLevel() != null) {
            insights.add("风险等级: " + riskAssessment.getOverallRiskLevel());
        }
        
        if (keyMetrics != null) {
            if (keyMetrics.getHealthScore() != null) {
                insights.add("健康评分: " + String.format("%.1f/100", keyMetrics.getHealthScore()));
            }
            if (keyMetrics.getWeightChange() != null) {
                String change = keyMetrics.getWeightChange() > 0 ? 
                    "增加" + String.format("%.1fkg", keyMetrics.getWeightChange()) :
                    "减少" + String.format("%.1fkg", Math.abs(keyMetrics.getWeightChange()));
                insights.add("体重变化: " + change);
            }
        }
        
        return insights;
    }
    
    /**
     * 获取行动建议摘要
     */
    public List<String> getActionableRecommendations() {
        List<String> actions = new java.util.ArrayList<>();
        
        if (recommendations != null) {
            if (recommendations.getPriorityRecommendations() != null) {
                for (RecommendationsVO.RecommendationVO rec : recommendations.getPriorityRecommendations()) {
                    if (rec.getPriority().equals("高")) {
                        actions.add(rec.getTitle() + ": " + rec.getDescription());
                    }
                }
            }
            
            if (recommendations.getActionPlan() != null && recommendations.getActionPlan().getWeeklyActions() != null) {
                for (RecommendationsVO.ActionItemVO action : recommendations.getActionPlan().getWeeklyActions()) {
                    actions.add("每周行动: " + action.getAction());
                }
            }
        }
        
        return actions;
    }
    
    /**
     * 验证报告数据完整性
     */
    public boolean validate() {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        
        if (reportType == null) {
            return false;
        }
        
        if (period == null || period.getStartDate() == null || period.getEndDate() == null) {
            return false;
        }
        
        if (executiveSummary == null || executiveSummary.getOverallAssessment() == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 计算报告质量评分
     */
    public Double calculateReportQuality() {
        double score = 0.0;
        double maxScore = 100.0;
        
        // 基本完整性 (30分)
        if (title != null && !title.trim().isEmpty()) score += 10;
        if (period != null && period.getStartDate() != null && period.getEndDate() != null) score += 10;
        if (executiveSummary != null && executiveSummary.getOverallAssessment() != null) score += 10;
        
        // 数据丰富度 (40分)
        if (keyMetrics != null) score += 10;
        if (detailedAnalysis != null) score += 10;
        if (trendAnalysis != null) score += 10;
        if (recommendations != null) score += 10;
        
        // 分析深度 (30分)
        if (riskAssessment != null) score += 10;
        if (comparativeAnalysis != null) score += 10;
        if (attachments != null && attachments.getCharts() != null && !attachments.getCharts().isEmpty()) score += 10;
        
        return score;
    }
}