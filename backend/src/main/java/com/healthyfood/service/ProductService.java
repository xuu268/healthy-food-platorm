package com.healthyfood.service;

import com.healthyfood.common.ApiResult;
import com.healthyfood.common.ErrorCode;
import com.healthyfood.common.Constants;
import com.healthyfood.entity.Product;
import com.healthyfood.entity.Shop;
import com.healthyfood.repository.ProductRepository;
import com.healthyfood.repository.ShopRepository;
import com.healthyfood.vo.product.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务
 */
@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private RedisService redisService;
    
    /**
     * 创建商品
     */
    @Transactional
    public ApiResult<ProductVO> createProduct(Long shopId, ProductCreateRequest request) {
        try {
            // 1. 验证商家是否存在
            Shop shop = shopRepository.findById(shopId).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 2. 验证商家状态
            if (shop.getStatus() != Shop.ShopStatus.ACTIVE) {
                return ApiResult.error(ErrorCode.SHOP_NOT_ACTIVE, "商家未营业，无法创建商品");
            }
            
            // 3. 验证商品名称是否重复
            if (productRepository.existsByShopIdAndName(shopId, request.getName())) {
                return ApiResult.error(ErrorCode.PRODUCT_EXISTS, "商品名称已存在");
            }
            
            // 4. 验证请求参数
            if (!request.validate()) {
                return ApiResult.error(ErrorCode.PARAM_ERROR, "参数验证失败");
            }
            
            // 5. 计算健康评分
            Double healthScore = calculateHealthScore(request);
            
            // 6. 创建商品实体
            Product product = Product.builder()
                    .shopId(shopId)
                    .name(request.getName())
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .price(request.getPrice())
                    .originalPrice(request.getOriginalPrice())
                    .onSale(request.getOnSale())
                    .discount(request.getDiscount())
                    .stockQuantity(request.getStockQuantity())
                    .lowStockThreshold(request.getLowStockThreshold())
                    .unit(request.getUnit())
                    .weight(request.getWeight())
                    .calories(request.getCalories())
                    .protein(request.getProtein())
                    .carbohydrates(request.getCarbohydrates())
                    .fat(request.getFat())
                    .fiber(request.getFiber())
                    .sugar(request.getSugar())
                    .sodium(request.getSodium())
                    .cholesterol(request.getCholesterol())
                    .vitamins(request.getVitamins())
                    .minerals(request.getMinerals())
                    .healthScore(healthScore)
                    .vegetarian(request.getVegetarian())
                    .vegan(request.getVegan())
                    .glutenFree(request.getGlutenFree())
                    .dairyFree(request.getDairyFree())
                    .allergens(request.getAllergens())
                    .ingredients(request.getIngredients())
                    .cookingMethod(request.getCookingMethod())
                    .storageInstructions(request.getStorageInstructions())
                    .expiryDate(request.getExpiryDate())
                    .images(request.getImages())
                    .tags(request.getTags())
                    .recommended(request.getRecommended())
                    .hot(request.getHot())
                    .newArrival(request.getNewArrival())
                    .status(Product.ProductStatus.ACTIVE)
                    .createTime(LocalDateTime.now())
                    .lastUpdateTime(LocalDateTime.now())
                    .build();
            
            // 7. 保存商品
            product = productRepository.save(product);
            
            // 8. 更新商家商品数量缓存
            updateShopProductCountCache(shopId);
            
            // 9. 返回响应
            ProductVO productVO = convertToProductVO(product, shop);
            
            log.info("商品创建成功: productId={}, name={}, shopId={}", 
                    product.getId(), product.getName(), shopId);
            return ApiResult.success(productVO);
            
        } catch (Exception e) {
            log.error("创建商品失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "创建商品失败");
        }
    }
    
    /**
     * 更新商品信息
     */
    @Transactional
    public ApiResult<ProductVO> updateProduct(Long productId, ProductUpdateRequest request) {
        try {
            // 1. 获取商品
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return ApiResult.error(ErrorCode.PRODUCT_NOT_EXISTS, "商品不存在");
            }
            
            // 2. 获取商家
            Shop shop = shopRepository.findById(product.getShopId()).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 3. 验证权限（这里简化处理，实际应该检查商家权限）
            
            // 4. 更新商品信息
            if (StringUtils.hasText(request.getName())) {
                // 检查名称是否重复（排除当前商品）
                if (!product.getName().equals(request.getName()) && 
                    productRepository.existsByShopIdAndName(product.getShopId(), request.getName())) {
                    return ApiResult.error(ErrorCode.PRODUCT_EXISTS, "商品名称已存在");
                }
                product.setName(request.getName());
            }
            
            if (StringUtils.hasText(request.getDescription())) {
                product.setDescription(request.getDescription());
            }
            
            if (request.getCategory() != null) {
                product.setCategory(request.getCategory());
            }
            
            if (request.getPrice() != null) {
                product.setPrice(request.getPrice());
            }
            
            if (request.getOriginalPrice() != null) {
                product.setOriginalPrice(request.getOriginalPrice());
            }
            
            if (request.getOnSale() != null) {
                product.setOnSale(request.getOnSale());
            }
            
            if (request.getDiscount() != null) {
                product.setDiscount(request.getDiscount());
            }
            
            if (request.getStockQuantity() != null) {
                product.setStockQuantity(request.getStockQuantity());
            }
            
            if (request.getLowStockThreshold() != null) {
                product.setLowStockThreshold(request.getLowStockThreshold());
            }
            
            if (StringUtils.hasText(request.getUnit())) {
                product.setUnit(request.getUnit());
            }
            
            if (request.getWeight() != null) {
                product.setWeight(request.getWeight());
            }
            
            if (request.getCalories() != null) {
                product.setCalories(request.getCalories());
            }
            
            if (request.getProtein() != null) {
                product.setProtein(request.getProtein());
            }
            
            if (request.getCarbohydrates() != null) {
                product.setCarbohydrates(request.getCarbohydrates());
            }
            
            if (request.getFat() != null) {
                product.setFat(request.getFat());
            }
            
            if (request.getFiber() != null) {
                product.setFiber(request.getFiber());
            }
            
            if (request.getSugar() != null) {
                product.setSugar(request.getSugar());
            }
            
            if (request.getSodium() != null) {
                product.setSodium(request.getSodium());
            }
            
            if (request.getCholesterol() != null) {
                product.setCholesterol(request.getCholesterol());
            }
            
            if (request.getVitamins() != null) {
                product.setVitamins(request.getVitamins());
            }
            
            if (request.getMinerals() != null) {
                product.setMinerals(request.getMinerals());
            }
            
            // 重新计算健康评分
            if (request.getCalories() != null || request.getProtein() != null || 
                request.getCarbohydrates() != null || request.getFat() != null ||
                request.getFiber() != null || request.getSugar() != null ||
                request.getSodium() != null || request.getCholesterol() != null) {
                Double healthScore = calculateHealthScore(product);
                product.setHealthScore(healthScore);
            }
            
            if (request.getVegetarian() != null) {
                product.setVegetarian(request.getVegetarian());
            }
            
            if (request.getVegan() != null) {
                product.setVegan(request.getVegan());
            }
            
            if (request.getGlutenFree() != null) {
                product.setGlutenFree(request.getGlutenFree());
            }
            
            if (request.getDairyFree() != null) {
                product.setDairyFree(request.getDairyFree());
            }
            
            if (request.getAllergens() != null) {
                product.setAllergens(request.getAllergens());
            }
            
            if (request.getIngredients() != null) {
                product.setIngredients(request.getIngredients());
            }
            
            if (StringUtils.hasText(request.getCookingMethod())) {
                product.setCookingMethod(request.getCookingMethod());
            }
            
            if (StringUtils.hasText(request.getStorageInstructions())) {
                product.setStorageInstructions(request.getStorageInstructions());
            }
            
            if (request.getExpiryDate() != null) {
                product.setExpiryDate(request.getExpiryDate());
            }
            
            if (request.getImages() != null) {
                product.setImages(request.getImages());
            }
            
            if (request.getTags() != null) {
                product.setTags(request.getTags());
            }
            
            if (request.getRecommended() != null) {
                product.setRecommended(request.getRecommended());
            }
            
            if (request.getHot() != null) {
                product.setHot(request.getHot());
            }
            
            if (request.getNewArrival() != null) {
                product.setNewArrival(request.getNewArrival());
            }
            
            if (request.getStatus() != null) {
                product.setStatus(request.getStatus());
            }
            
            // 5. 保存更新
            product.setLastUpdateTime(LocalDateTime.now());
            product = productRepository.save(product);
            
            // 6. 清除商品缓存
            clearProductCache(productId);
            
            // 7. 返回响应
            ProductVO productVO = convertToProductVO(product, shop);
            
            log.info("商品更新成功: productId={}, name={}", productId, product.getName());
            return ApiResult.success(productVO);
            
        } catch (Exception e) {
            log.error("更新商品失败: productId={}", productId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "更新商品失败");
        }
    }
    
    /**
     * 获取商品详情
     */
    public ApiResult<ProductDetailVO> getProductDetail(Long productId, Long userId) {
        try {
            // 1. 获取商品
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return ApiResult.error(ErrorCode.PRODUCT_NOT_EXISTS, "商品不存在");
            }
            
            // 2. 检查商品状态
            if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                return ApiResult.error(ErrorCode.PRODUCT_NOT_ACTIVE, "商品已下架");
            }
            
            // 3. 获取商家信息
            Shop shop = shopRepository.findById(product.getShopId()).orElse(null);
            if (shop == null || shop.getStatus() != Shop.ShopStatus.ACTIVE) {
                return ApiResult.error(ErrorCode.SHOP_NOT_ACTIVE, "商家未营业");
            }
            
            // 4. 构建商品详情
            ProductDetailVO detail = buildProductDetailVO(product, shop, userId);
            
            // 5. 更新浏览数
            productRepository.incrementViewCount(productId);
            
            // 6. 记录用户浏览历史（如果有用户ID）
            if (userId != null) {
                recordUserViewHistory(userId, productId);
            }
            
            return ApiResult.success(detail);
            
        } catch (Exception e) {
            log.error("获取商品详情失败: productId={}", productId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取商品详情失败");
        }
    }
    
    /**
     * 搜索商品
     */
    public ApiResult<Page<ProductListVO>> searchProducts(ProductSearchRequest request) {
        try {
            // 1. 构建分页和排序
            Pageable pageable = PageRequest.of(
                    request.getPage() != null ? request.getPage() : 0,
                    request.getSize() != null ? request.getSize() : 20,
                    buildSort(request.getSortBy(), request.getSortOrder())
            );
            
            // 2. 构建查询条件
            Specification<Product> spec = buildSearchSpecification(request);
            
            // 3. 执行查询
            Page<Product> productPage = productRepository.findAll(spec, pageable);
            
            // 4. 获取商家信息
            Map<Long, Shop> shopMap = new HashMap<>();
            if (!productPage.isEmpty()) {
                List<Long> shopIds = productPage.getContent().stream()
                        .map(Product::getShopId)
                        .distinct()
                        .collect(Collectors.toList());
                
                List<Shop> shops = shopRepository.findByIdIn(shopIds);
                shopMap = shops.stream()
                        .collect(Collectors.toMap(Shop::getId, shop -> shop));
            }
            
            // 5. 转换为VO
            Page<ProductListVO> resultPage = productPage.map(product -> {
                Shop shop = shopMap.get(product.getShopId());
                return convertToProductListVO(product, shop);
            });
            
            return ApiResult.success(resultPage);
            
        } catch (Exception e) {
            log.error("搜索商品失败", e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "搜索商品失败");
        }
    }
    
    /**
     * 获取商家商品列表
     */
    public ApiResult<Page<ProductListVO>> getShopProducts(Long shopId, ProductSearchRequest request) {
        try {
            // 1. 验证商家是否存在
            Shop shop = shopRepository.findById(shopId).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 2. 构建分页和排序
            Pageable pageable = PageRequest.of(
                    request.getPage() != null ? request.getPage() : 0,
                    request.getSize() != null ? request.getSize() : 20,
                    buildSort(request.getSortBy(), request.getSortOrder())
            );
            
            // 3. 构建查询条件（限定商家）
            Specification<Product> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 限定商家
                predicates.add(cb.equal(root.get("shopId"), shopId));
                
                // 只查询活跃商品
                predicates.add(cb.equal(root.get("status"), Product.ProductStatus.ACTIVE));
                
                // 其他筛选条件
                if (StringUtils.hasText(request.getKeyword())) {
                    String keyword = "%" + request.getKeyword() + "%";
                    Predicate namePredicate = cb.like(root.get("name"), keyword);
                    Predicate descPredicate = cb.like(root.get("description"), keyword);
                    Predicate tagsPredicate = cb.like(root.get("tags"), keyword);
                    predicates.add(cb.or(namePredicate, descPredicate, tagsPredicate));
                }
                
                if (request.getCategory() != null) {
                    predicates.add(cb.equal(root.get("category"), request.getCategory()));
                }
                
                if (request.getMinPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
                }
                
                if (request.getMaxPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
                }
                
                if (request.getOnSale() != null) {
                    predicates.add(cb.equal(root.get("onSale"), request.getOnSale()));
                }
                
                if (request.getRecommended() != null) {
                    predicates.add(cb.equal(root.get("recommended"), request.getRecommended()));
                }
                
                if (request.getHot() != null) {
                    predicates.add(cb.equal(root.get("hot"), request.getHot()));
                }
                
                if (request.getNewArrival() != null) {
                    predicates.add(cb.equal(root.get("newArrival"), request.getNewArrival()));
                }
                
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            
            // 4. 执行查询
            Page<Product> productPage = productRepository.findAll(spec, pageable);
            
            // 5. 转换为VO
            Page<ProductListVO> resultPage = productPage.map(product -> 
                convertToProductListVO(product, shop));
            
            return ApiResult.success(resultPage);
            
        } catch (Exception e) {
            log.error("获取商家商品列表失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取商家商品列表失败");
        }
    }
    
    /**
     * 更新商品库存
     */
    @Transactional
    public ApiResult<Void> updateProductStock(Long productId, StockUpdateRequest request) {
        try {
            // 1. 获取商品
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return ApiResult.error(ErrorCode.PRODUCT_NOT_EXISTS, "商品不存在");
            }
            
            // 2. 验证操作类型
            Integer newStock = product.getStockQuantity();
            
            switch (request.getOperation()) {
                case INCREMENT:
                    newStock += request.getQuantity();
                    break;
                case DECREMENT:
                    newStock -= request.getQuantity();
                    if (newStock < 0) {
                        return ApiResult.error(ErrorCode.INSUFFICIENT_STOCK, "库存不足");
                    }
                    break;
                case SET:
                    newStock = request.getQuantity();
                    if (newStock < 0) {
                        return ApiResult.error(ErrorCode.PARAM_ERROR, "库存数量不能为负数");
                    }
                    break;
                default:
                    return ApiResult.error(ErrorCode.PARAM_ERROR, "无效的操作类型");
            }
            
            // 3. 更新库存
            product.setStockQuantity(newStock);
            product.setLastUpdateTime(LocalDateTime.now());
            productRepository.save(product);
            
            // 4. 清除商品缓存
            clearProductCache(productId);
            
            // 5. 记录库存变更日志
            logStockChange(productId, request.getOperation(), request.getQuantity(), 
                          request.getReason(), request.getOperator());
            
            log.info("商品库存更新成功: productId={}, operation={}, quantity={}, newStock={}", 
                    productId, request.getOperation(), request.getQuantity(), newStock);
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("更新商品库存失败: productId={}", productId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "更新商品库存失败");
        }
    }
    
    /**
     * 批量更新商品状态
     */
    @Transactional
    public ApiResult<Void> batchUpdateProductStatus(List<Long> productIds, Product.ProductStatus status, String reason) {
        try {
            // 1. 验证商品列表
            if (productIds == null || productIds.isEmpty()) {
                return ApiResult.error(ErrorCode.PARAM_ERROR, "商品ID列表不能为空");
            }
            
            // 2. 获取商品列表
            List<Product> products = productRepository.findByIdIn(productIds);
            if (products.isEmpty()) {
                return ApiResult.error(ErrorCode.PRODUCT_NOT_EXISTS, "未找到指定商品");
            }
            
            // 3. 更新状态
            for (Product product : products) {
                product.setStatus(status);
                product.setLastUpdateTime(LocalDateTime.now());
            }
            
            // 4. 批量保存
            productRepository.saveAll(products);
            
            // 5. 清除商品缓存
            for (Long productId : productIds) {
                clearProductCache(productId);
            }
            
            // 6. 记录操作日志
            logBatchStatusChange(productIds, status, reason);
            
            log.info("批量更新商品状态成功: count={}, status={}", productIds.size(), status);
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("批量更新商品状态失败", e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "批量更新商品状态失败");
        }
    }
    
    /**
     * 获取商品统计信息
     */
    public ApiResult<ProductStatisticsVO> getProductStatistics(Long productId, StatisticsPeriod period) {
        try {
            // 1. 获取商品
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return ApiResult.error(ErrorCode.PRODUCT_NOT_EXISTS, "商品不存在");
            }
            
            // 2. 构建统计信息（这里简化处理，实际应该从数据库统计）
            ProductStatisticsVO statistics = ProductStatisticsVO.builder()
                    .productId(productId)
                    .productName(product.getName())
                    .period(period)
                    .totalSales(product.getSalesCount())
                    .totalRevenue(product.getSalesCount() * product.getPrice())
                    .averageRating(product.getAverageRating())
                    .totalReviews(product.getTotalReviews())
                    .viewCount(product.getViewCount())
                    .favoriteCount(product.getFavoriteCount())
                    .currentStock(product.getStockQuantity())
                    .lowStockThreshold(product.getLowStockThreshold())
                    .build();
            
            // 3. 计算趋势（这里简化处理）
            statistics.setSalesTrend(calculateSalesTrend(productId, period));
            statistics.setViewTrend(calculateViewTrend(productId, period));
            statistics.setRatingTrend(calculateProductRatingTrend(productId, period));
            
            // 4. 计算库存预警
            if (product.getStockQuantity() <= product.getLowStockThreshold()) {
                statistics.setStockAlert("库存紧张，建议补货");
            } else if (product.getStockQuantity() == 0) {
                statistics.setStockAlert("已售罄，请及时补货");
            }
            
            return ApiResult.success(statistics);
            
        } catch (Exception e) {
            log.error("获取商品统计失败: productId={}", productId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取商品统计失败");
        }
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 计算商品健康评分
     */
    private Double calculateHealthScore(ProductCreateRequest request) {
        double score = 100.0;
        
        // 热量评分 (0-30分)
        if (request.getCalories() != null) {
            if (request.getCalories() < 200) {
                score += 10;
            } else if (request.getCalories() < 400) {
                score += 5;
            } else if (request.getCalories() < 600) {
                score += 0;
            } else {
                score -= 10;
            }
        }
        
        // 蛋白质评分 (0-20分)
        if (request.getProtein() != null) {
            if (request.getProtein() > 20) {
                score += 10;
            } else if (request.getProtein() > 10) {
                score += 5;
            } else {
                score += 0;
            }
        }
        
        // 脂肪评分 (0-15分)
        if (request.getFat() != null) {
            if (request.getFat() < 10) {
                score += 10;
            } else if (request.getFat() < 20) {
                score += 5;
            } else {
                score -= 5;
            }
        }
        
        // 糖分评分 (0-15分)
        if (request.getSugar() != null) {
            if (request.getSugar() < 10) {
                score += 10;
            } else if (request.getSugar() < 20) {
                score += 5;
            } else {
                score -= 10;
            }
        }
        
        // 纤维评分 (0-10分)
        if (request.getFiber() != null && request.getFiber() > 5) {
            score += 10;
        }
        
        // 钠评分 (0-10分)
        if (request.getSodium() != null && request.getSodium() < 500) {
            score += 10;
        } else if (request.getSodium() != null && request.getSodium() < 1000) {
            score += 5;
        }
        
        // 特殊饮食加分
        if (Boolean.TRUE.equals(request.getVegetarian())) score += 5;
        if (Boolean.TRUE.equals(request.getVegan())) score += 10;
        if (Boolean.TRUE.equals(request.getGlutenFree())) score += 5;
        if (Boolean.TRUE.equals(request.getDairyFree())) score += 5;
        
        // 确保分数在0-100之间
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * 计算商品健康评分（从Product实体）
     */
    private Double calculateHealthScore(Product product) {
        double score = 100.0;
        
        // 热量评分
        if (product.getCalories() != null) {
            if (product.getCalories() < 200) {
                score += 10;
            } else if (product.getCalories() < 400) {
                score += 5;
            } else if (product.getCalories() < 600) {
                score += 0;
            } else {
                score -= 10;
            }
        }
        
        // 蛋白质评分
        if (product.getProtein() != null && product.getProtein() > 20) {
            score += 10;
        }
        
        // 脂肪评分
        if (product.getFat() != null && product.getFat() < 10) {
            score += 10;
        }
        
        // 糖分评分
        if (product.getSugar() != null && product.getSugar() < 10) {
            score += 10;
        }
        
        // 特殊饮食加分
        if (Boolean.TRUE.equals(product.getVegetarian())) score += 5;
        if (Boolean.TRUE.equals(product.getVegan())) score += 10;
        if (Boolean.TRUE.equals(product.getGlutenFree())) score += 5;
        if (Boolean.TRUE.equals(product.getDairyFree())) score += 5;
        
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * 更新商家商品数量缓存
     */
    private void updateShopProductCountCache(Long shopId) {
        String cacheKey = Constants.REDIS_KEY_SHOP_PRODUCT_COUNT_PREFIX + shopId;
        Long productCount = productRepository.countByShopIdAndStatus(shopId, Product.ProductStatus.ACTIVE);
        redisService.set(cacheKey, productCount != null ? productCount.toString() : "0", 3600);
    }
    
    /**
     * 清除商品缓存
     */
    private void clearProductCache(Long productId) {
        String productKey = Constants.REDIS_KEY_PRODUCT_PREFIX + productId;
        redisService.delete(productKey);
    }
    
    /**
     * 构建商品详情VO
     */
    private ProductDetailVO buildProductDetailVO(Product product, Shop shop, Long userId) {
        // 计算折扣信息
        Double discount = null;
        if (Boolean.TRUE.equals(product.getOnSale()) && product.getDiscount() != null) {
            discount = product.getDiscount();
        }
        
        // 构建营养信息
        NutritionalInfoVO nutritionalInfo = NutritionalInfoVO.builder()
                .calories(product.getCalories())
                .protein(product.getProtein())
                .carbohydrates(product.getCarbohydrates())
                .fat(product.getFat())
                .fiber(product.getFiber())
                .sugar(product.getSugar())
                .sodium(product.getSodium())
                .cholesterol(product.getCholesterol())
                .vitamins(product.getVitamins())
                .minerals(product.getMinerals())
                .build();
        
        // 构建特殊饮食信息
        DietaryInfoVO dietaryInfo = DietaryInfoVO.builder()
                .vegetarian(product.getVegetarian())
                .vegan(product.getVegan())
                .glutenFree(product.getGlutenFree())
                .dairyFree(product.getDairyFree())
                .allergens(product.getAllergens())
                .build();
        
        return ProductDetailVO.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .shopId(product.getShopId())
                .shopName(shop.getShopName())
                .shopLogo(shop.getLogo())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .onSale(product.getOnSale())
                .discount(discount)
                .stockQuantity(product.getStockQuantity())
                .lowStockThreshold(product.getLowStockThreshold())
                .unit(product.getUnit())
                .weight(product.getWeight())
                .nutritionalInfo(nutritionalInfo)
                .dietaryInfo(dietaryInfo)
                .healthScore(product.getHealthScore())
                .ingredients(product.getIngredients())
                .cookingMethod(product.getCookingMethod())
                .storageInstructions(product.getStorageInstructions())
                .expiryDate(product.getExpiryDate())
                .images(product.getImages())
                .tags(product.getTags())
                .recommended(product.getRecommended())
                .hot(product.getHot())
                .newArrival(product.getNewArrival())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .salesCount(product.getSalesCount())
                .viewCount(product.getViewCount())
                .favoriteCount(product.getFavoriteCount())
                .status(product.getStatus().name())
                .createTime(product.getCreateTime())
                .lastUpdateTime(product.getLastUpdateTime())
                .build();
    }
    
    /**
     * 构建排序
     */
    private Sort buildSort(String sortBy, String sortOrder) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "salesCount");
        }
        
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        switch (sortBy) {
            case "price":
                return Sort.by(direction, "price");
            case "sales":
                return Sort.by(direction, "salesCount");
            case "rating":
                return Sort.by(direction, "averageRating");
            case "health":
                return Sort.by(direction, "healthScore");
            case "calories":
                return Sort.by(direction, "calories");
            case "protein":
                return Sort.by(direction, "protein");
            case "new":
                return Sort.by(Sort.Direction.DESC, "createTime");
            case "recommended":
                return Sort.by(Sort.Direction.DESC, "recommended");
            default:
                return Sort.by(Sort.Direction.DESC, "salesCount");
        }
    }
    
    /**
     * 构建搜索条件
     */
    private Specification<Product> buildSearchSpecification(ProductSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询活跃商品
            predicates.add(cb.equal(root.get("status"), Product.ProductStatus.ACTIVE));
            
            // 关键词搜索
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword() + "%";
                Predicate namePredicate = cb.like(root.get("name"), keyword);
                Predicate descPredicate = cb.like(root.get("description"), keyword);
                Predicate tagsPredicate = cb.like(root.get("tags"), keyword);
                predicates.add(cb.or(namePredicate, descPredicate, tagsPredicate));
            }
            
            // 分类筛选
            if (request.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), request.getCategory()));
            }
            
            // 价格范围
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }
            
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }
            
            // 热量范围
            if (request.getMaxCalories() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("calories"), request.getMaxCalories()));
            }
            
            // 健康评分
            if (request.getMinHealthScore() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("healthScore"), request.getMinHealthScore()));
            }
            
            // 特殊饮食要求
            if (Boolean.TRUE.equals(request.getVegetarian())) {
                predicates.add(cb.equal(root.get("vegetarian"), true));
            }
            
            if (Boolean.TRUE.equals(request.getVegan())) {
                predicates.add(cb.equal(root.get("vegan"), true));
            }
            
            if (Boolean.TRUE.equals(request.getGlutenFree())) {
                predicates.add(cb.equal(root.get("glutenFree"), true));
            }
            
            if (Boolean.TRUE.equals(request.getDairyFree())) {
                predicates.add(cb.equal(root.get("dairyFree"), true));
            }
            
            // 商家筛选
            if (request.getShopId() != null) {
                predicates.add(cb.equal(root.get("shopId"), request.getShopId()));
            }
            
            // 标签筛选
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                for (String tag : request.getTags()) {
                    predicates.add(cb.like(root.get("tags"), "%" + tag + "%"));
                }
            }
            
            // 促销筛选
            if (request.getOnSale() != null) {
                predicates.add(cb.equal(root.get("onSale"), request.getOnSale()));
            }
            
            // 推荐筛选
            if (request.getRecommended() != null) {
                predicates.add(cb.equal(root.get("recommended"), request.getRecommended()));
            }
            
            // 热门筛选
            if (request.getHot() != null) {
                predicates.add(cb.equal(root.get("hot"), request.getHot()));
            }
            
            // 新品筛选
            if (request.getNewArrival() != null) {
                predicates.add(cb.equal(root.get("newArrival"), request.getNewArrival()));
            }
            
            // 评分筛选
            if (request.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), request.getMinRating()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 转换为商品列表VO
     */
    private ProductListVO convertToProductListVO(Product product, Shop shop) {
        // 计算折扣信息
        Double discountPrice = null;
        if (Boolean.TRUE.equals(product.getOnSale()) && product.getDiscount() != null) {
            discountPrice = product.getPrice() * (1 - product.getDiscount() / 100);
        }
        
        return ProductListVO.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .shopId(product.getShopId())
                .shopName(shop != null ? shop.getShopName() : null)
                .shopLogo(shop != null ? shop.getLogo() : null)
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .onSale(product.getOnSale())
                .discount(product.getDiscount())
                .discountPrice(discountPrice)
                .stockQuantity(product.getStockQuantity())
                .unit(product.getUnit())
                .weight(product.getWeight())
                .calories(product.getCalories())
                .healthScore(product.getHealthScore())
                .images(product.getImages())
                .tags(product.getTags())
                .recommended(product.getRecommended())
                .hot(product.getHot())
                .newArrival(product.getNewArrival())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .salesCount(product.getSalesCount())
                .viewCount(product.getViewCount())
                .favoriteCount(product.getFavoriteCount())
                .build();
    }
    
    /**
     * 转换为商品VO
     */
    private ProductVO convertToProductVO(Product product, Shop shop) {
        return ProductVO.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .shopId(product.getShopId())
                .shopName(shop.getShopName())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .onSale(product.getOnSale())
                .discount(product.getDiscount())
                .stockQuantity(product.getStockQuantity())
                .unit(product.getUnit())
                .weight(product.getWeight())
                .calories(product.getCalories())
                .healthScore(product.getHealthScore())
                .images(product.getImages())
                .tags(product.getTags())
                .status(product.getStatus().name())
                .createTime(product.getCreateTime())
                .build();
    }
    
    /**
     * 记录用户浏览历史
     */
    private void recordUserViewHistory(Long userId, Long productId) {
        try {
            String historyKey = Constants.REDIS_KEY_USER_VIEW_HISTORY_PREFIX + userId;
            
            // 使用ZSet记录浏览历史，分数为时间戳
            double score = System.currentTimeMillis();
            redisService.zadd(historyKey, productId.toString(), score);
            
            // 只保留最近100条记录
            redisService.zremrangeByRank(historyKey, 0, -101);
            
            // 设置过期时间（30天）
            redisService.expire(historyKey, 30 * 24 * 3600);
            
        } catch (Exception e) {
            log.error("记录用户浏览历史失败: userId={}, productId={}", userId, productId, e);
        }
    }
    
    /**
     * 记录库存变更日志
     */
    private void logStockChange(Long productId, StockOperation operation, Integer quantity, 
                               String reason, String operator) {
        try {
            String logKey = Constants.REDIS_KEY_PRODUCT_STOCK_LOG_PREFIX + productId;
            
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("timestamp", LocalDateTime.now().toString());
            logEntry.put("operation", operation.name());
            logEntry.put("quantity", quantity);
            logEntry.put("reason", reason);
            logEntry.put("operator", operator);
            
            // 使用List记录日志
            redisService.lpush(logKey, logEntry.toString());
            
            // 只保留最近50条日志
            redisService.ltrim(logKey, 0, 49);
            
        } catch (Exception e) {
            log.error("记录库存变更日志失败: productId={}", productId, e);
        }
    }
    
    /**
     * 记录批量状态变更日志
     */
    private void logBatchStatusChange(List<Long> productIds, Product.ProductStatus status, String reason) {
        try {
            String logKey = Constants.REDIS_KEY_BATCH_STATUS_CHANGE_LOG;
            
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("timestamp", LocalDateTime.now().toString());
            logEntry.put("productIds", productIds);
            logEntry.put("status", status.name());
            logEntry.put("reason", reason);
            logEntry.put("count", productIds.size());
            
            redisService.lpush(logKey, logEntry.toString());
            redisService.ltrim(logKey, 0, 99);
            
        } catch (Exception e) {
            log.error("记录批量状态变更日志失败", e);
        }
    }
    
    /**
     * 计算销售趋势
     */
    private Map<String, Integer> calculateSalesTrend(Long productId, StatisticsPeriod period) {
        // 这里简化处理，实际应该从数据库查询
        Map<String, Integer> trend = new LinkedHashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(period.getDays() - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 模拟数据
            int sales = (int) (Math.random() * 20) + 1;
            trend.put(date.toString(), sales);
        }
        
        return trend;
    }
    
    /**
     * 计算浏览趋势
     */
    private Map<String, Integer> calculateViewTrend(Long productId, StatisticsPeriod period) {
        // 这里简化处理，实际应该从数据库查询
        Map<String, Integer> trend = new LinkedHashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(period.getDays() - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 模拟数据
            int views = (int) (Math.random() * 100) + 10;
            trend.put(date.toString(), views);
        }
        
        return trend;
    }
    
    /**
     * 计算评分趋势
     */
    private Map<String, Double> calculateProductRatingTrend(Long productId, StatisticsPeriod period) {
        // 这里简化处理，实际应该从数据库查询
        Map<String, Double> trend = new LinkedHashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(period.getDays() - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 模拟数据，评分在4.0-5.0之间波动
            double rating = 4.0 + Math.random() * 1.0;
            trend.put(date.toString(), rating);
        }
        
        return trend;
    }
    
    /**
     * 库存操作类型枚举
     */
    public enum StockOperation {
        INCREMENT,  // 增加
        DECREMENT,  // 减少
        SET         // 设置
    }
    
    /**
     * 统计周期枚举
     */
    public enum StatisticsPeriod {
        DAY(1),
        WEEK(7),
        MONTH(30),
        QUARTER(90),
        YEAR(365);
        
        private final int days;
        
        StatisticsPeriod(int days) {
            this.days = days;
        }
        
        public int getDays() {
            return days;
        }
    }
}
