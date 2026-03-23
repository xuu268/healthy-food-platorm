package com.healthyfood.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "product")
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商家ID
     */
    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    /**
     * 商品名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 商品描述
     */
    @Column(columnDefinition = "text")
    private String description;

    /**
     * 商品图片
     */
    @Column(length = 500)
    private String image;

    /**
     * 分类
     */
    @Column(length = 50)
    private String category;

    /**
     * 价格
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 原价
     */
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 库存 -1表示无限
     */
    @Column(columnDefinition = "int default -1")
    private Integer stock = -1;

    /**
     * 销量
     */
    @Column(name = "sales_count", columnDefinition = "int default 0")
    private Integer salesCount = 0;

    /**
     * 热量(卡路里)
     */
    @Column
    private Integer calories;

    /**
     * 蛋白质(g)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal protein;

    /**
     * 碳水化合物(g)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal carbohydrates;

    /**
     * 脂肪(g)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal fat;

    /**
     * 膳食纤维(g)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal fiber;

    /**
     * 糖分(g)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal sugar;

    /**
     * 钠(mg)
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal sodium;

    /**
     * 健康评分
     */
    @Column(name = "health_score", precision = 3, scale = 2, columnDefinition = "decimal(3,2) default 0.00")
    private BigDecimal healthScore = BigDecimal.ZERO;

    /**
     * 标签
     */
    @Column(length = 500)
    private String tags;

    /**
     * 规格
     */
    @Column(columnDefinition = "json")
    private String specifications;

    /**
     * 排序
     */
    @Column(name = "sort_order", columnDefinition = "int default 0")
    private Integer sortOrder = 0;

    /**
     * 状态 0-下架 1-上架
     */
    @Column(columnDefinition = "tinyint default 1")
    private Integer status = 1;

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
     * 成分列表（与成分表系统关联）
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductIngredient> ingredients;
    
    /**
     * 成分分析结果（与成分表系统关联）
     */
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IngredientAnalysis ingredientAnalysis;
    
    /**
     * 检查商品是否上架
     */
    public boolean isOnSale() {
        return status != null && status == 1;
    }

    /**
     * 检查库存是否充足
     */
    public boolean isInStock(int quantity) {
        if (stock == -1) {
            return true; // 无限库存
        }
        return stock >= quantity;
    }

    /**
     * 减少库存
     */
    public boolean decreaseStock(int quantity) {
        if (stock == -1) {
            return true; // 无限库存，无需减少
        }
        if (stock < quantity) {
            return false; // 库存不足
        }
        stock -= quantity;
        salesCount += quantity;
        return true;
    }

    /**
     * 增加库存
     */
    public void increaseStock(int quantity) {
        if (stock != -1) {
            stock += quantity;
        }
    }

    /**
     * 获取折扣信息
     */
    public DiscountInfo getDiscountInfo() {
        if (originalPrice == null || originalPrice.compareTo(price) <= 0) {
            return new DiscountInfo(false, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        BigDecimal discountAmount = originalPrice.subtract(price);
        BigDecimal discountRate = discountAmount.divide(originalPrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        
        return new DiscountInfo(true, discountAmount, discountRate);
    }

    /**
     * 计算总营养值
     */
    public NutritionInfo calculateNutrition(int quantity) {
        NutritionInfo info = new NutritionInfo();
        
        if (calories != null) {
            info.setCalories(calories * quantity);
        }
        if (protein != null) {
            info.setProtein(protein.multiply(BigDecimal.valueOf(quantity)));
        }
        if (carbohydrates != null) {
            info.setCarbohydrates(carbohydrates.multiply(BigDecimal.valueOf(quantity)));
        }
        if (fat != null) {
            info.setFat(fat.multiply(BigDecimal.valueOf(quantity)));
        }
        if (fiber != null) {
            info.setFiber(fiber.multiply(BigDecimal.valueOf(quantity)));
        }
        if (sugar != null) {
            info.setSugar(sugar.multiply(BigDecimal.valueOf(quantity)));
        }
        if (sodium != null) {
            info.setSodium(sodium.multiply(BigDecimal.valueOf(quantity)));
        }
        
        return info;
    }

    /**
     * 获取健康等级
     */
    public String getHealthLevel() {
        if (healthScore == null) {
            return "未知";
        }
        
        double score = healthScore.doubleValue();
        if (score >= 4.5) {
            return "非常健康";
        } else if (score >= 4.0) {
            return "健康";
        } else if (score >= 3.0) {
            return "一般";
        } else if (score >= 2.0) {
            return "需注意";
        } else {
            return "不健康";
        }
    }

    /**
     * 检查是否适合特定健康目标
     */
    public boolean isSuitableForGoal(String healthGoal) {
        if (healthGoal == null || healthScore == null) {
            return true;
        }
        
        double score = healthScore.doubleValue();
        
        switch (healthGoal) {
            case "weight_loss":
                return score >= 4.0 && calories != null && calories <= 400;
            case "muscle_gain":
                return score >= 3.5 && protein != null && protein.doubleValue() >= 20;
            case "diabetes":
                return score >= 4.0 && sugar != null && sugar.doubleValue() <= 10;
            case "hypertension":
                return score >= 4.0 && sodium != null && sodium.doubleValue() <= 500;
            default:
                return score >= 3.0;
        }
    }

    /**
     * 获取商品完整信息
     */
    public String getFullInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" | ¥").append(price);
        
        if (originalPrice != null && originalPrice.compareTo(price) > 0) {
            DiscountInfo discount = getDiscountInfo();
            sb.append(" (原价¥").append(originalPrice)
              .append("，省¥").append(discount.getAmount())
              .append(")");
        }
        
        if (calories != null) {
            sb.append(" | ").append(calories).append("卡");
        }
        
        if (healthScore != null && healthScore.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" | 健康评分: ").append(healthScore);
        }
        
        return sb.toString();
    }

    /**
     * 折扣信息类
     */
    @Data
    public static class DiscountInfo {
        private boolean hasDiscount;
        private BigDecimal amount;
        private BigDecimal rate;
        
        public DiscountInfo(boolean hasDiscount, BigDecimal amount, BigDecimal rate) {
            this.hasDiscount = hasDiscount;
            this.amount = amount;
            this.rate = rate;
        }
    }

    /**
     * 营养信息类
     */
    @Data
    public static class NutritionInfo {
        private Integer calories;
        private BigDecimal protein;
        private BigDecimal carbohydrates;
        private BigDecimal fat;
        private BigDecimal fiber;
        private BigDecimal sugar;
        private BigDecimal sodium;
        
        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            if (calories != null) {
                sb.append("热量: ").append(calories).append("卡 ");
            }
            if (protein != null) {
                sb.append("蛋白质: ").append(protein).append("g ");
            }
            if (carbohydrates != null) {
                sb.append("碳水: ").append(carbohydrates).append("g ");
            }
            if (fat != null) {
                sb.append("脂肪: ").append(fat).append("g");
            }
            return sb.toString();
        }
    }

    /**
     * 获取标签数组
     */
    public String[] getTagArray() {
        if (tags == null || tags.isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }

    /**
     * 添加标签
     */
    public void addTag(String tag) {
        if (tags == null || tags.isEmpty()) {
            tags = tag;
        } else {
            tags += "," + tag;
        }
    }

    /**
     * 检查是否包含标签
     */
    public boolean hasTag(String tag) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }
        String[] tagArray = getTagArray();
        for (String t : tagArray) {
            if (t.trim().equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算商品总价
     */
    public BigDecimal calculateTotalPrice(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * 获取商品状态描述
     */
    public String getStatusDescription() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "已下架";
            case 1:
                return "销售中";
            default:
                return "未知";
        }
    }

    /**
     * 检查是否是热门商品
     */
    public boolean isHotProduct() {
        return salesCount != null && salesCount >= 100;
    }

    /**
     * 检查是否是新品
     */
    public boolean isNewProduct() {
        if (createdAt == null) {
            return false;
        }
        return createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }
}