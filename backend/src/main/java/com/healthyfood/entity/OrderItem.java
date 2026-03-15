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

/**
 * 订单项实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "order_item")
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单项ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @Column(name = "order_id", nullable = false, length = 32)
    private String orderId;

    /**
     * 商品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 商品名称
     */
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    /**
     * 商品图片
     */
    @Column(name = "product_image", length = 500)
    private String productImage;

    /**
     * 单价
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 数量
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 小计
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * 规格
     */
    @Column(columnDefinition = "json")
    private String specifications;

    /**
     * 备注
     */
    @Column(length = 200)
    private String note;

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
     * 商品信息（非数据库字段）
     */
    @Transient
    @TableField(exist = false)
    private Product product;

    /**
     * 计算小计金额
     */
    public void calculateTotal() {
        if (price != null && quantity != null) {
            total = price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * 检查数量是否有效
     */
    public boolean isValidQuantity() {
        return quantity != null && quantity > 0 && quantity <= 99; // 最大99份
    }

    /**
     * 获取订单项完整信息
     */
    public String getFullInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(productName)
          .append(" ×").append(quantity)
          .append(" = ¥").append(total);
        
        if (note != null && !note.trim().isEmpty()) {
            sb.append(" (").append(note).append(")");
        }
        
        return sb.toString();
    }

    /**
     * 获取规格信息
     */
    public String getSpecificationsDisplay() {
        if (specifications == null || specifications.trim().isEmpty() || "{}".equals(specifications)) {
            return "无规格";
        }
        
        try {
            // 简化处理，实际应该解析JSON
            return specifications.replace("{", "").replace("}", "").replace("\"", "");
        } catch (Exception e) {
            return "规格信息";
        }
    }

    /**
     * 检查是否有特殊要求
     */
    public boolean hasSpecialRequest() {
        return note != null && !note.trim().isEmpty();
    }

    /**
     * 获取商品分类（通过商品信息）
     */
    public String getProductCategory() {
        if (product != null && product.getCategory() != null) {
            return product.getCategory();
        }
        return "未知";
    }

    /**
     * 计算营养信息
     */
    public NutritionInfo calculateNutrition() {
        NutritionInfo info = new NutritionInfo();
        
        if (product != null) {
            if (product.getCalories() != null) {
                info.setCalories(product.getCalories() * quantity);
            }
            if (product.getProtein() != null) {
                info.setProtein(product.getProtein().multiply(BigDecimal.valueOf(quantity)));
            }
            if (product.getCarbohydrates() != null) {
                info.setCarbohydrates(product.getCarbohydrates().multiply(BigDecimal.valueOf(quantity)));
            }
            if (product.getFat() != null) {
                info.setFat(product.getFat().multiply(BigDecimal.valueOf(quantity)));
            }
            if (product.getFiber() != null) {
                info.setFiber(product.getFiber().multiply(BigDecimal.valueOf(quantity)));
            }
            if (product.getSugar() != null) {
                info.setSugar(product.getSugar().multiply(BigDecimal.valueOf(quantity)));
            }
            if (product.getSodium() != null) {
                info.setSodium(product.getSodium().multiply(BigDecimal.valueOf(quantity)));
            }
        }
        
        return info;
    }

    /**
     * 获取健康评分
     */
    public BigDecimal getHealthScore() {
        if (product != null && product.getHealthScore() != null) {
            return product.getHealthScore();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 检查是否适合特定健康目标
     */
    public boolean isSuitableForGoal(String healthGoal) {
        if (product == null) {
            return true;
        }
        return product.isSuitableForGoal(healthGoal);
    }

    /**
     * 获取折扣信息
     */
    public DiscountInfo getDiscountInfo() {
        if (product == null) {
            return new DiscountInfo(false, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return product.getDiscountInfo();
    }

    /**
     * 计算实际单价（考虑折扣）
     */
    public BigDecimal getActualUnitPrice() {
        DiscountInfo discount = getDiscountInfo();
        if (discount.isHasDiscount()) {
            return price.subtract(discount.getAmount().divide(BigDecimal.valueOf(quantity), 2, BigDecimal.ROUND_HALF_UP));
        }
        return price;
    }

    /**
     * 获取商品标签
     */
    public String[] getProductTags() {
        if (product == null || product.getTags() == null) {
            return new String[0];
        }
        return product.getTagArray();
    }

    /**
     * 检查商品是否包含特定标签
     */
    public boolean hasProductTag(String tag) {
        if (product == null) {
            return false;
        }
        return product.hasTag(tag);
    }

    /**
     * 获取商品库存状态
     */
    public String getStockStatus() {
        if (product == null) {
            return "未知";
        }
        
        if (product.getStock() == -1) {
            return "充足";
        } else if (product.getStock() >= quantity) {
            return "充足";
        } else if (product.getStock() > 0) {
            return "仅剩" + product.getStock() + "份";
        } else {
            return "缺货";
        }
    }

    /**
     * 检查库存是否充足
     */
    public boolean isStockSufficient() {
        if (product == null) {
            return true; // 未知情况下假设充足
        }
        return product.isInStock(quantity);
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
            return sb.toString().trim();
        }
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
     * 更新商品信息
     */
    public void updateProductInfo(Product product) {
        this.product = product;
        if (product != null) {
            if (productName == null) {
                productName = product.getName();
            }
            if (productImage == null && product.getImage() != null) {
                productImage = product.getImage();
            }
            if (price == null) {
                price = product.getPrice();
                calculateTotal();
            }
        }
    }

    /**
     * 验证订单项数据
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        if (productId == null) {
            result.addError("商品ID不能为空");
        }
        
        if (productName == null || productName.trim().isEmpty()) {
            result.addError("商品名称不能为空");
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("价格必须大于0");
        }
        
        if (!isValidQuantity()) {
            result.addError("数量必须在1-99之间");
        }
        
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("小计金额必须大于0");
        }
        
        // 验证计算是否正确
        if (price != null && quantity != null && total != null) {
            BigDecimal calculatedTotal = price.multiply(BigDecimal.valueOf(quantity));
            if (calculatedTotal.compareTo(total) != 0) {
                result.addError("小计金额计算错误");
            }
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
     * 复制订单项（用于修改订单时创建新项）
     */
    public OrderItem copy() {
        OrderItem copy = new OrderItem();
        copy.setProductId(this.productId);
        copy.setProductName(this.productName);
        copy.setProductImage(this.productImage);
        copy.setPrice(this.price);
        copy.setQuantity(this.quantity);
        copy.setTotal(this.total);
        copy.setSpecifications(this.specifications);
        copy.setNote(this.note);
        copy.setProduct(this.product);
        return copy;
    }

    /**
     * 比较订单项是否相同（忽略ID和时间）
     */
    public boolean isSameItem(OrderItem other) {
        if (other == null) {
            return false;
        }
        
        return productId.equals(other.getProductId()) &&
               price.compareTo(other.getPrice()) == 0 &&
               specificationsEquals(other.getSpecifications());
    }

    /**
     * 比较规格是否相同（简化比较）
     */
    private boolean specificationsEquals(String otherSpecs) {
        if (specifications == null && otherSpecs == null) {
            return true;
        }
        if (specifications == null || otherSpecs == null) {
            return false;
        }
        return specifications.equals(otherSpecs);
    }
}