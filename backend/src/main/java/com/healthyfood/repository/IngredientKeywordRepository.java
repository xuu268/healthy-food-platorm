package com.healthyfood.repository;

import com.healthyfood.entity.IngredientKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 成分关键词Repository接口
 */
@Repository
public interface IngredientKeywordRepository extends JpaRepository<IngredientKeyword, Long> {
    
    /**
     * 根据关键词查找
     * @param keyword 关键词
     * @return 关键词实体
     */
    Optional<IngredientKeyword> findByKeyword(String keyword);
    
    /**
     * 根据分类查找
     * @param category 分类
     * @return 关键词列表
     */
    List<IngredientKeyword> findByCategory(IngredientKeyword.KeywordCategory category);
    
    /**
     * 根据关键词模糊搜索
     * @param keyword 关键词
     * @return 关键词列表
     */
    List<IngredientKeyword> findByKeywordContaining(String keyword);
    
    /**
     * 根据流行度排序查找
     * @param limit 限制数量
     * @return 关键词列表
     */
    @Query("SELECT k FROM IngredientKeyword k ORDER BY k.popularityScore DESC, k.usageCount DESC")
    List<IngredientKeyword> findPopularKeywords(int limit);
    
    /**
     * 根据使用次数排序查找
     * @param limit 限制数量
     * @return 关键词列表
     */
    @Query("SELECT k FROM IngredientKeyword k ORDER BY k.usageCount DESC")
    List<IngredientKeyword> findMostUsedKeywords(int limit);
    
    /**
     * 查找AI生成的关键词
     * @param aiGenerated AI生成标志
     * @return 关键词列表
     */
    List<IngredientKeyword> findByAiGenerated(Boolean aiGenerated);
    
    /**
     * 根据关键词列表查找
     * @param keywords 关键词列表
     * @return 关键词列表
     */
    List<IngredientKeyword> findByKeywordIn(List<String> keywords);
    
    /**
     * 统计分类数量
     * @param category 分类
     * @return 数量
     */
    Long countByCategory(IngredientKeyword.KeywordCategory category);
    
    /**
     * 增加关键词使用次数
     * @param keywordId 关键词ID
     * @return 更新后的使用次数
     */
    @Query("UPDATE IngredientKeyword k SET k.usageCount = k.usageCount + 1 WHERE k.id = :keywordId")
    int incrementUsageCount(Long keywordId);
}