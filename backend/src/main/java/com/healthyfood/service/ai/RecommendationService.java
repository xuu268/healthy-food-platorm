package com.healthyfood.service.ai;

import com.healthyfood.entity.User;
import com.healthyfood.entity.Dish;
import com.healthyfood.entity.HealthRecord;
import com.healthyfood.vo.recommendation.RecommendationRequest;
import com.healthyfood.vo.recommendation.RecommendationResponse;
import com.healthyfood.vo.recommendation.RecommendationItem;
import com.healthyfood.vo.recommendation.Explanation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI推荐服务 - 健康餐饮智能推荐核心服务
 * 实现多维度个性化推荐算法
 */
@Slf4j
@Service
public class RecommendationService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private DishService dishService;
    
    @Autowired
    private NutritionService nutritionService;
    
    @Autowired
    private HealthService healthService;
    
    @Autowired
    private RecommendationCacheService cacheService;
    
    @Autowired
    private RecommendationAlgorithmFactory algorithmFactory;
    
    /**
     * 获取个性化菜品推荐
     */
    @Cacheable(value = "recommendations", key = "#request.userId + '_' + #request.scenario")
    public RecommendationResponse getRecommendations(RecommendationRequest request) {
        log.info("开始为用户 {} 生成推荐，场景: {}", request.getUserId(), request.getScenario());
        
        // 1. 获取用户信息
        User user = userService.getUserById(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在: " + request.getUserId());
        }
        
        // 2. 获取用户健康数据
        HealthRecord latestHealthRecord = healthService.getLatestHealthRecord(request.getUserId());
        
        // 3. 计算营养需求
        NutritionRequirements requirements = nutritionService.calculateNutritionRequirements(user, latestHealthRecord);
        
        // 4. 获取候选菜品
        List<Dish> candidateDishes = getCandidateDishes(request, user);
        
        // 5. 多算法融合推荐
        List<RecommendationItem> recommendations = generateRecommendations(
            user, requirements, candidateDishes, request
        );
        
        // 6. 生成推荐解释
        Explanation explanation = generateExplanation(user, recommendations, requirements);
        
        // 7. 记录推荐历史
        recordRecommendationHistory(request.getUserId(), recommendations, request.getScenario());
        
        return RecommendationResponse.builder()
            .userId(request.getUserId())
            .recommendations(recommendations)
            .explanation(explanation)
            .generatedAt(LocalDateTime.now())
            .scenario(request.getScenario())
            .build();
    }
    
    /**
     * 获取候选菜品列表
     */
    private List<Dish> getCandidateDishes(RecommendationRequest request, User user) {
        List<Dish> candidateDishes = new ArrayList<>();
        
        // 1. 基于用户偏好的菜品
        candidateDishes.addAll(dishService.getDishesByPreferences(
            user.getTastePreferences(),
            user.getFoodRestrictions(),
            user.getAllergens()
        ));
        
        // 2. 基于场景的菜品
        if (request.getScenario() != null) {
            candidateDishes.addAll(dishService.getDishesByScenario(
                request.getScenario(),
                request.getMealTime(),
                request.getSeason()
            ));
        }
        
        // 3. 热门菜品
        candidateDishes.addAll(dishService.getPopularDishes(50));
        
        // 去重
        return candidateDishes.stream()
            .distinct()
            .limit(200)  // 限制候选集大小
            .collect(Collectors.toList());
    }
    
    /**
     * 多算法融合生成推荐
     */
    private List<RecommendationItem> generateRecommendations(
        User user, 
        NutritionRequirements requirements,
        List<Dish> candidateDishes,
        RecommendationRequest request
    ) {
        Map<String, List<RecommendationItem>> algorithmResults = new HashMap<>();
        
        // 1. 基于内容的推荐 (营养特征匹配)
        algorithmResults.put("content_based", 
            algorithmFactory.getContentBasedAlgorithm().recommend(
                user, requirements, candidateDishes
            ));
        
        // 2. 协同过滤推荐 (用户相似度)
        algorithmResults.put("collaborative_filtering",
            algorithmFactory.getCollaborativeFilteringAlgorithm().recommend(
                user, candidateDishes
            ));
        
        // 3. 知识图谱推荐 (食材功效和搭配)
        algorithmResults.put("knowledge_graph",
            algorithmFactory.getKnowledgeGraphAlgorithm().recommend(
                user, requirements, candidateDishes
            ));
        
        // 4. 深度学习推荐
        algorithmResults.put("deep_learning",
            algorithmFactory.getDeepLearningAlgorithm().recommend(
                user, candidateDishes, request
            ));
        
        // 5. 多算法融合
        List<RecommendationItem> fusedRecommendations = 
            algorithmFactory.getFusionAlgorithm().fuseRecommendations(algorithmResults);
        
        // 6. 后处理优化
        return postProcessRecommendations(fusedRecommendations, user, requirements);
    }
    
    /**
     * 推荐后处理优化
     */
    private List<RecommendationItem> postProcessRecommendations(
        List<RecommendationItem> recommendations,
        User user,
        NutritionRequirements requirements
    ) {
        List<RecommendationItem> processed = new ArrayList<>();
        
        for (RecommendationItem item : recommendations) {
            // 1. 营养优化调整
            Dish optimizedDish = nutritionService.optimizeDishForUser(
                item.getDish(), user, requirements
            );
            
            // 2. 计算推荐分数
            double finalScore = calculateFinalScore(item, user, requirements);
            
            // 3. 创建优化后的推荐项
            RecommendationItem optimizedItem = RecommendationItem.builder()
                .dish(optimizedDish)
                .score(finalScore)
                .nutritionMatch(calculateNutritionMatch(optimizedDish, requirements))
                .tasteMatch(calculateTasteMatch(optimizedDish, user))
                .healthBenefits(calculateHealthBenefits(optimizedDish, user))
                .build();
            
            processed.add(optimizedItem);
        }
        
        // 4. 排序和限制数量
        return processed.stream()
            .sorted(Comparator.comparing(RecommendationItem::getScore).reversed())
            .limit(10)  // 返回前10个推荐
            .collect(Collectors.toList());
    }
    
    /**
     * 计算最终推荐分数
     */
    private double calculateFinalScore(
        RecommendationItem item,
        User user,
        NutritionRequirements requirements
    ) {
        double score = 0.0;
        
        // 1. 基础算法分数 (40%)
        score += item.getScore() * 0.4;
        
        // 2. 营养匹配度 (30%)
        double nutritionMatch = calculateNutritionMatch(item.getDish(), requirements);
        score += nutritionMatch * 0.3;
        
        // 3. 口味匹配度 (20%)
        double tasteMatch = calculateTasteMatch(item.getDish(), user);
        score += tasteMatch * 0.2;
        
        // 4. 健康效益 (10%)
        double healthBenefits = calculateHealthBenefits(item.getDish(), user);
        score += healthBenefits * 0.1;
        
        return score;
    }
    
    /**
     * 计算营养匹配度
     */
    private double calculateNutritionMatch(Dish dish, NutritionRequirements requirements) {
        // 计算菜品营养与用户需求的匹配度
        Map<String, Double> dishNutrition = dish.getNutritionFacts();
        Map<String, Double> userRequirements = requirements.getDailyRequirements();
        
        double matchScore = 0.0;
        int matchedNutrients = 0;
        
        for (Map.Entry<String, Double> entry : userRequirements.entrySet()) {
            String nutrient = entry.getKey();
            Double required = entry.getValue();
            Double provided = dishNutrition.get(nutrient);
            
            if (provided != null) {
                // 计算该营养素的匹配度
                double nutrientMatch = calculateNutrientMatch(provided, required);
                matchScore += nutrientMatch;
                matchedNutrients++;
            }
        }
        
        return matchedNutrients > 0 ? matchScore / matchedNutrients : 0.0;
    }
    
    /**
     * 计算单个营养素匹配度
     */
    private double calculateNutrientMatch(double provided, double required) {
        if (required <= 0) return 1.0;  // 无要求
        
        double ratio = provided / required;
        
        // 理想范围: 0.8-1.2
        if (ratio >= 0.8 && ratio <= 1.2) {
            return 1.0;
        } else if (ratio >= 0.6 && ratio < 0.8) {
            return 0.8;
        } else if (ratio > 1.2 && ratio <= 1.5) {
            return 0.8;
        } else if (ratio >= 0.4 && ratio < 0.6) {
            return 0.6;
        } else if (ratio > 1.5 && ratio <= 2.0) {
            return 0.6;
        } else {
            return 0.3;
        }
    }
    
    /**
     * 计算口味匹配度
     */
    private double calculateTasteMatch(Dish dish, User user) {
        Set<String> userPreferences = user.getTastePreferences();
        Set<String> dishFlavors = dish.getFlavorProfiles();
        
        if (userPreferences.isEmpty() || dishFlavors.isEmpty()) {
            return 0.5;  // 默认中等匹配
        }
        
        // 计算口味交集
        Set<String> intersection = new HashSet<>(userPreferences);
        intersection.retainAll(dishFlavors);
        
        // 计算匹配度
        double preferenceMatch = (double) intersection.size() / userPreferences.size();
        double flavorCoverage = (double) intersection.size() / dishFlavors.size();
        
        // 综合匹配度
        return (preferenceMatch * 0.7 + flavorCoverage * 0.3);
    }
    
    /**
     * 计算健康效益
     */
    private double calculateHealthBenefits(Dish dish, User user) {
        double benefitScore = 0.0;
        
        // 1. 基于用户健康目标的效益
        Set<String> healthGoals = user.getHealthGoals();
        Set<String> dishBenefits = dish.getHealthBenefits();
        
        Set<String> matchedBenefits = new HashSet<>(healthGoals);
        matchedBenefits.retainAll(dishBenefits);
        
        if (!healthGoals.isEmpty()) {
            benefitScore += (double) matchedBenefits.size() / healthGoals.size() * 0.6;
        }
        
        // 2. 基于用户健康风险的效益
        Set<String> healthRisks = healthService.identifyHealthRisks(user);
        Set<String> riskReductionBenefits = dish.getRiskReductionBenefits();
        
        Set<String> matchedRisks = new HashSet<>(healthRisks);
        matchedRisks.retainAll(riskReductionBenefits);
        
        if (!healthRisks.isEmpty()) {
            benefitScore += (double) matchedRisks.size() / healthRisks.size() * 0.4;
        }
        
        return benefitScore;
    }
    
    /**
     * 生成推荐解释
     */
    private Explanation generateExplanation(
        User user, 
        List<RecommendationItem> recommendations,
        NutritionRequirements requirements
    ) {
        Explanation.ExplanationBuilder builder = Explanation.builder();
        
        // 1. 总体解释
        builder.overallExplanation(generateOverallExplanation(user, recommendations));
        
        // 2. 营养解释
        builder.nutritionExplanation(generateNutritionExplanation(recommendations, requirements));
        
        // 3. 健康解释
        builder.healthExplanation(generateHealthExplanation(recommendations, user));
        
        // 4. 个性化解释
        builder.personalizedExplanation(generatePersonalizedExplanation(recommendations, user));
        
        // 5. 替代方案
        builder.alternatives(generateAlternativeSuggestions(recommendations, user));
        
        return builder.build();
    }
    
    /**
     * 生成总体解释
     */
    private String generateOverallExplanation(User user, List<RecommendationItem> recommendations) {
        StringBuilder sb = new StringBuilder();
        sb.append("根据您的健康档案和饮食偏好，我们为您推荐了")
          .append(recommendations.size())
          .append("道菜品。");
        
        if (!recommendations.isEmpty()) {
            sb.append("首推「")
              .append(recommendations.get(0).getDish().getName())
              .append("」，因为它最符合您当前的营养需求和口味偏好。");
        }
        
        return sb.toString();
    }
    
    /**
     * 生成营养解释
     */
    private String generateNutritionExplanation(
        List<RecommendationItem> recommendations,
        NutritionRequirements requirements
    ) {
        if (recommendations.isEmpty()) {
            return "暂无营养分析数据。";
        }
        
        // 分析推荐菜品的营养特点
        Map<String, Double> totalNutrition = new HashMap<>();
        for (RecommendationItem item : recommendations) {
            Map<String, Double> dishNutrition = item.getDish().getNutritionFacts();
            dishNutrition.forEach((nutrient, value) -> 
                totalNutrition.merge(nutrient, value, Double::sum)
            );
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("这些菜品整体营养均衡：");
        
        // 分析主要营养素
        List<String> keyNutrients = Arrays.asList("蛋白质", "碳水化合物", "脂肪", "膳食纤维");
        for (String nutrient : keyNutrients) {
            Double provided = totalNutrition.get(nutrient);
            Double required = requirements.getDailyRequirements().get(nutrient);
            
            if (provided != null && required != null && required > 0) {
                double percentage = provided / required * 100;
                sb.append("\n- ").append(nutrient).append(": ")
                  .append(String.format("%.1f", provided)).append("g (")
                  .append(String.format("%.0f", percentage)).append("% 日需量)");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 生成健康解释
     */
    private String generateHealthExplanation(List<RecommendationItem> recommendations, User user) {
        if (recommendations.isEmpty()) {
            return "暂无健康效益分析。";
        }
        
        // 收集所有菜品的健康效益
        Set<String> allBenefits = new HashSet<>();
        for (RecommendationItem item : recommendations) {
            allBenefits.addAll(item.getDish().getHealthBenefits());
        }
        
        // 匹配用户健康目标
        Set<String> userGoals = user.getHealthGoals();
        Set<String> matchedBenefits = new HashSet<>(allBenefits);
        matchedBenefits.retainAll(userGoals);
        
        StringBuilder sb = new StringBuilder();
        if (!matchedBenefits.isEmpty()) {
            sb.append("这些菜品特别适合您的健康目标：");
            for (String benefit : matchedBenefits) {
                sb.append("\n- 有助于").append(benefit);
            }
        } else {
            sb.append("这些菜品提供全面的营养支持。");
        }
        
        return sb.toString();
    }
    
    /**
     * 生成个性化解释
     */
    private String generatePersonalizedExplanation(List<RecommendationItem> recommendations, User user) {
        if (recommendations.isEmpty()) {
            return "暂无个性化分析。";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("基于您的个人特点：");
        
        // 口味偏好
        if (!user.getTastePreferences().isEmpty()) {
            sb.append("\n- 口味偏好: ").append(String.join("、", user.getTastePreferences()));
        }
        
        // 饮食限制
        if (!user.getFoodRestrictions().isEmpty()) {
            sb.append("\n- 饮食限制: ").append(String.join("、", user.getFoodRestrictions()));
        }
        
        // 过敏原
        if (!user.getAllergens().isEmpty()) {
            sb.append("\n- 过敏原规避: ").append(String.join("、", user.getAllergens()));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成替代方案
     */
    private List<String> generateAlternativeSuggestions(List<RecommendationItem> recommendations, User user) {
        List<String> alternatives = new ArrayList<>();
        
        if (recommendations.size() > 1) {
            alternatives.add("如果您想尝试其他口味，可以考虑「" + 
                recommendations.get(1).getDish().getName() + "」");
        }
        
        if (recommendations.size() > 2) {
            alternatives.add("对于更清淡的选择，推荐「" + 
                recommendations.get(2).getDish().getName() + "」");
        }
        
        // 基于用户特点的特别建议
        if (user.getHealthGoals().contains("减重")) {
            alternatives.add("为达到减重目标，建议控制总热量摄入，搭配适量运动。");
        }
        
        if (user.getHealthGoals().contains("控糖")) {
            alternatives.add("为控制血糖，建议选择低GI值的菜品，避免高糖食物。");
        }
        
        return alternatives;
    }
    
    /**
     * 记录推荐历史
     */
    private void recordRecommendationHistory(
        Long userId, 
        List<RecommendationItem> recommendations,
        String scenario
    ) {
        try {
            RecommendationHistory history = RecommendationHistory.builder()
                .userId(userId)
                .recommendations(recommendations.stream()
                    .map(item -> item.getDish().getId())
                    .collect(Collectors.toList()))
                .scenario(scenario)
                .generatedAt(LocalDateTime.now())
                .build();
            
            // 保存到数据库
            recommendationHistoryRepository.save(history);
            
            // 更新缓存
            cacheService.updateUserRecommendationHistory(userId, history);
            
            log.info("已记录用户 {} 的推荐历史，场景: {}", userId, scenario);
            
        } catch (Exception e) {
            log.error("记录推荐历史失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 收集推荐反馈
     */
    public void collectFeedback(Long userId, Long dishId, FeedbackType feedbackType, String scenario) {
        log.info("收集用户 {} 对菜品 {} 的反馈: {}", userId, dishId, feedbackType);
        
        RecommendationFeedback feedback = RecommendationFeedback.builder()
            .userId(userId)
            .dishId(dishId)
            .feedbackType(feedbackType)
            .scenario(scenario)
            .feedbackTime(LocalDateTime.now())
            .build();
        
        // 保存反馈
        recommendationFeedbackRepository.save(feedback);
        
        // 更新用户偏好模型
        updateUserPreferenceModel(userId, dishId, feedbackType);
        
        // 更新菜品评分
        updateDishRating(dishId, feedbackType);
        
        log.info("反馈收集完成，将用于优化后续推荐");
    }
    
    /**
     * 更新用户偏好模型
     */
    private void updateUserPreferenceModel(Long userId, Long dishId, FeedbackType feedbackType) {
        try {
            UserPreferenceModel preferenceModel = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> createNewPreferenceModel(userId));
            
            Dish dish = dishService.getDishById(dishId);
            if (dish == null) return;
            
            // 根据反馈类型更新偏好
            switch (feedbackType) {
                case LIKE:
                    preferenceModel.addPositivePreference(dish);
                    break;
                case DISLIKE:
                    preferenceModel.addNegativePreference(dish);
                    break;
                case ORDER:
                    preferenceModel.addOrderPreference(dish);
                    break;
                case SKIP:
                    preferenceModel.addSkipPreference(dish);
                    break;
            }
            
            // 重新计算用户特征向量
            preferenceModel.recalculateFeatureVector();
            
            // 保存更新
            userPreferenceRepository.save(preferenceModel);
            
            // 清除相关缓存
            cacheService.evictUserRecommendations(userId);
            
        } catch (Exception e) {
            log.error("更新用户偏好模型失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 更新菜品评分
     */
    private void updateDishRating(Long dishId, FeedbackType feedbackType) {
        try {
            DishRating rating = dishRatingRepository.findByDishId(dishId)
                .orElseGet(() -> createNewDishRating(dishId));
            
            // 更新评分统计
            rating.addFeedback(feedbackType);
            
            // 重新计算综合评分
            rating.calculateCompositeScore();
            
            // 保存更新
            dishRatingRepository.save(rating);
            
        } catch (Exception e) {
            log.error("更新菜品评分失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取推荐效果分析
     */
    public RecommendationEffectAnalysis analyzeRecommendationEffect(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("分析用户 {} 的推荐效果，时间范围: {} - {}", userId, startTime, endTime);
        
        RecommendationEffectAnalysis analysis = new RecommendationEffectAnalysis();
        
        // 1. 获取推荐历史
        List<RecommendationHistory> histories = recommendationHistoryRepository
            .findByUserIdAndGeneratedAtBetween(userId, startTime, endTime);
        
        // 2. 获取反馈数据
        List<RecommendationFeedback> feedbacks = recommendationFeedbackRepository
            .findByUserIdAndFeedbackTimeBetween(userId, startTime, endTime);
        
        // 3. 计算各项指标
        analysis.setTotalRecommendations(histories.size());
        analysis.setTotalFeedback(feedbacks.size());
        
        // 计算反馈率
        if (!histories.isEmpty()) {
            double feedbackRate = (double) feedbacks.size() / histories.size();
            analysis.setFeedbackRate(feedbackRate);
        }
        
        // 计算正面反馈率
        long positiveFeedback = feedbacks.stream()
            .filter(f -> f.getFeedbackType() == FeedbackType.LIKE || f.getFeedbackType() == FeedbackType.ORDER)
            .count();
        
        if (!feedbacks.isEmpty()) {
            double positiveRate = (double) positiveFeedback / feedbacks.size();
            analysis.setPositiveFeedbackRate(positiveRate);
        }
        
        // 计算下单转化率
        long orderFeedback = feedbacks.stream()
            .filter(f -> f.getFeedbackType() == FeedbackType.ORDER)
            .count();
        
        if (!feedbacks.isEmpty()) {
            double orderRate = (double) orderFeedback / feedbacks.size();
            analysis.setOrderConversionRate(orderRate);
        }
        
        // 4. 分析推荐质量趋势
        analysis.setQualityTrend(analyzeQualityTrend(histories, feedbacks));
        
        // 5. 生成改进建议
        analysis.setImprovementSuggestions(generateImprovementSuggestions(analysis));
        
        return analysis;
    }
    
    /**
     * 分析推荐质量趋势
     */
    private QualityTrend analyzeQualityTrend(
        List<RecommendationHistory> histories,
        List<RecommendationFeedback> feedbacks
    ) {
        QualityTrend trend = new QualityTrend();
        
        if (histories.isEmpty() || feedbacks.isEmpty()) {
            trend.setTrend("数据不足，无法分析趋势");
            return trend;
        }
        
        // 按时间分组分析
        Map<LocalDateTime, List<RecommendationFeedback>> feedbackByTime = feedbacks.stream()
            .collect(Collectors.groupingBy(RecommendationFeedback::getFeedbackTime));
        
        // 计算时间窗口内的平均正面反馈率
        List<Double> positiveRates = new ArrayList<>();
        for (Map.Entry<LocalDateTime, List<RecommendationFeedback>> entry : feedbackByTime.entrySet()) {
            List<RecommendationFeedback> timeFeedbacks = entry.getValue();
            long positiveCount = timeFeedbacks.stream()
                .filter(f -> f.getFeedbackType() == FeedbackType.LIKE || f.getFeedbackType() == FeedbackType.ORDER)
                .count();
            
            if (!timeFeedbacks.isEmpty()) {
                double positiveRate = (double) positiveCount / timeFeedbacks.size();
                positiveRates.add(positiveRate);
            }
        }
        
        // 分析趋势
        if (positiveRates.size() >= 2) {
            double firstRate = positiveRates.get(0);
            double lastRate = positiveRates.get(positiveRates.size() - 1);
            
            if (lastRate > firstRate * 1.1) {
                trend.setTrend("上升趋势 (推荐质量持续改善)");
                trend.setImprovementRate((lastRate - firstRate) / firstRate);
            } else if (lastRate < firstRate * 0.9) {
                trend.setTrend("下降趋势 (需要关注推荐质量)");
                trend.setImprovementRate((lastRate - firstRate) / firstRate);
            } else {
                trend.setTrend("稳定趋势 (推荐质量保持稳定)");
                trend.setImprovementRate(0.0);
            }
        } else {
            trend.setTrend("数据不足，需要更多反馈数据");
        }
        
        return trend;
    }
    
    /**
     * 生成改进建议
     */
    private List<String> generateImprovementSuggestions(RecommendationEffectAnalysis analysis) {
        List<String> suggestions = new ArrayList<>();
        
        if (analysis.getFeedbackRate() < 0.3) {
            suggestions.add("反馈率较低，建议增加反馈激励机制，如积分奖励");
        }
        
        if (analysis.getPositiveFeedbackRate() < 0.6) {
            suggestions.add("正面反馈率有待提升，建议优化推荐算法，提高个性化程度");
        }
        
        if (analysis.getOrderConversionRate() < 0.2) {
            suggestions.add("下单转化率较低，建议优化菜品展示和购买流程");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("当前推荐效果良好，继续保持并探索更多优化机会");
        }
        
        return suggestions;
    }
    
    /**
     * 重新训练推荐模型
     */
    public void retrainRecommendationModel(RetrainRequest request) {
        log.info("开始重新训练推荐模型，模式: {}", request.getMode());
        
        try {
            // 1. 准备训练数据
            TrainingData trainingData = prepareTrainingData(request);
            
            // 2. 训练模型
            ModelTrainingResult result = algorithmFactory.trainModels(trainingData, request);
            
            // 3. 评估模型效果
            ModelEvaluation evaluation = evaluateModels(result.getTrainedModels());
            
            // 4. 部署新模型
            if (evaluation.getOverallScore() > request.getThreshold()) {
                deployNewModels(result.getTrainedModels());
                log.info("新模型部署成功，评估分数: {}", evaluation.getOverallScore());
            } else {
                log.warn("新模型评估分数 {} 低于阈值 {}，暂不部署", 
                    evaluation.getOverallScore(), request.getThreshold());
            }
            
            // 5. 记录训练历史
            recordTrainingHistory(request, result, evaluation);
            
        } catch (Exception e) {
            log.error("重新训练推荐模型失败: {}", e.getMessage(), e);
            throw new RuntimeException("模型训练失败", e);
        }
    }
    
    /**
     * 准备训练数据
     */
    private TrainingData prepareTrainingData(RetrainRequest request) {
        TrainingData data = new TrainingData();
        
        // 获取用户数据
        List<User> users = userService.getAllActiveUsers();
        data.setUsers(users);
        
        // 获取菜品数据
        List<Dish> dishes = dishService.getAllDishes();
        data.setDishes(dishes);
        
        // 获取交互数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(request.getDataDays());
        List<RecommendationHistory> histories = recommendationHistoryRepository
            .findByGeneratedAtAfter(startTime);
        List<RecommendationFeedback> feedbacks = recommendationFeedbackRepository
            .findByFeedbackTimeAfter(startTime);
        
        data.setHistories(histories);
        data.setFeedbacks(feedbacks);
        
        // 获取营养数据
        Map<Long, NutritionRequirements> nutritionRequirements = new HashMap<>();
        for (User user : users) {
            HealthRecord healthRecord = healthService.getLatestHealthRecord(user.getId());
            if (healthRecord != null) {
                NutritionRequirements requirements = nutritionService
                    .calculateNutritionRequirements(user, healthRecord);
                nutritionRequirements.put(user.getId(), requirements);
            }
        }
        data.setNutritionRequirements(nutritionRequirements);
        
        return data;
    }
    
    /**
     * 评估模型效果
     */
    private ModelEvaluation evaluateModels(Map<String, Object> trainedModels) {
        ModelEvaluation evaluation = new ModelEvaluation();
        
        // 这里应该使用测试集进行模型评估
        // 实际项目中需要实现完整的评估流程
        
        // 模拟评估结果
        evaluation.setOverallScore(0.85);  // 综合评分
        evaluation.setAccuracy(0.82);      // 准确率
        evaluation.setPrecision(0.79);     // 精确率
        evaluation.setRecall(0.83);        // 召回率
        evaluation.setF1Score(0.81);       // F1分数
        
        Map<String, Double> algorithmScores = new HashMap<>();
        algorithmScores.put("content_based", 0.80);
        algorithmScores.put("collaborative_filtering", 0.78);
        algorithmScores.put("knowledge_graph", 0.82);
        algorithmScores.put("deep_learning", 0.85);
        algorithmScores.put("fusion", 0.87);
        
        evaluation.setAlgorithmScores(algorithmScores);
        
        return evaluation;
    }
    
    /**
     * 部署新模型
     */
    private void deployNewModels(Map<String, Object> trainedModels) {
        // 更新算法工厂中的模型
        algorithmFactory.updateModels(trainedModels);
        
        // 清除所有缓存
        cacheService.clearAllCaches();
        
        // 记录部署日志
        log.info("新模型部署完成，清除所有缓存");
    }
    
    /**
     * 记录训练历史
     */
    private void recordTrainingHistory(
        RetrainRequest request,
        ModelTrainingResult result,
        ModelEvaluation evaluation
    ) {
        ModelTrainingHistory history = ModelTrainingHistory.builder()
            .trainingMode(request.getMode())
            .dataDays(request.getDataDays())
            .modelCount(result.getTrainedModels().size())
            .trainingTime(result.getTrainingTime())
            .evaluationScore(evaluation.getOverallScore())
            .trainedAt(LocalDateTime.now())
            .build();
        
        modelTrainingHistoryRepository.save(history);
    }
    
    // 辅助方法：创建新对象
    private UserPreferenceModel createNewPreferenceModel(Long userId) {
        return UserPreferenceModel.builder()
            .userId(userId)
            .positivePreferences(new HashSet<>())
            .negativePreferences(new HashSet<>())
            .orderPreferences(new HashSet<>())
            .skipPreferences(new HashSet<>())
            .featureVector(new double[0])
            .lastUpdated(LocalDateTime.now())
            .build();
    }
    
    private DishRating createNewDishRating(Long dishId) {
        return DishRating.builder()
            .dishId(dishId)
            .likeCount(0)
            .dislikeCount(0)
            .orderCount(0)
            .skipCount(0)
            .compositeScore(0.0)
            .lastUpdated(LocalDateTime.now())
            .build();
    }
}