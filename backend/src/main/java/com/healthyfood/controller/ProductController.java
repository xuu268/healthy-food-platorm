package com.healthyfood.controller;

import com.healthyfood.common.ApiResult;
import com.healthyfood.service.ProductService;
import com.healthyfood.vo.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {

    private final ProductService productService;

    /**
     * 创建商品
     */
    @PostMapping
    public ApiResult<ProductVO> createProduct(@Valid @RequestBody ProductCreateRequest request,
                                            HttpServletRequest httpRequest) {
        log.info("创建商品: {}", request.getProductName());
        ApiResult<ProductVO> result = productService.createProduct(request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 更新商品
     */
    @PutMapping("/{productId}")
    public ApiResult<ProductVO> updateProduct(@PathVariable Long productId,
                                            @Valid @RequestBody ProductUpdateRequest request,
                                            HttpServletRequest httpRequest) {
        log.info("更新商品: productId={}", productId);
        ApiResult<ProductVO> result = productService.updateProduct(productId, request);
        logApiAccess(httpRequest, request, result);
        return result;
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    public ApiResult<ProductDetailVO> getProductDetail(@PathVariable Long productId,
                                                     HttpServletRequest httpRequest) {
        log.info("获取商品详情: productId={}", productId);
        ApiResult<ProductDetailVO> result = productService.getProductDetail(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{productId}")
    public ApiResult<Void> deleteProduct(@PathVariable Long productId,
                                       HttpServletRequest httpRequest) {
        log.info("删除商品: productId={}", productId);
        ApiResult<Void> result = productService.deleteProduct(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商品列表
     */
    @GetMapping
    public ApiResult<Object> getProductList(@RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size,
                                          @RequestParam(required = false) Long shopId,
                                          @RequestParam(required = false) String category,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sortBy,
                                          @RequestParam(required = false) String sortOrder,
                                          HttpServletRequest httpRequest) {
        log.info("获取商品列表: page={}, size={}, shopId={}, category={}, status={}, keyword={}, sortBy={}, sortOrder={}",
                page, size, shopId, category, status, keyword, sortBy, sortOrder);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        String validatedSortOrder = validateSort(sortBy, sortOrder);
        
        ApiResult<Object> result = productService.getProductList(pageNum, pageSize, shopId, 
                category, status, keyword, sortBy, validatedSortOrder);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ApiResult<Object> searchProducts(@RequestParam String keyword,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size,
                                          @RequestParam(required = false) Long shopId,
                                          @RequestParam(required = false) String category,
                                          HttpServletRequest httpRequest) {
        log.info("搜索商品: keyword={}, page={}, size={}, shopId={}, category={}",
                keyword, page, size, shopId, category);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = productService.searchProducts(keyword, pageNum, pageSize, shopId, category);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取热门商品
     */
    @GetMapping("/popular")
    public ApiResult<Object> getPopularProducts(@RequestParam(required = false) Integer limit,
                                              @RequestParam(required = false) Long shopId,
                                              @RequestParam(required = false) String category,
                                              HttpServletRequest httpRequest) {
        log.info("获取热门商品: limit={}, shopId={}, category={}", limit, shopId, category);
        
        int productLimit = limit != null ? limit : 10;
        ApiResult<Object> result = productService.getPopularProducts(productLimit, shopId, category);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取推荐商品
     */
    @GetMapping("/recommended")
    public ApiResult<Object> getRecommendedProducts(@RequestParam(required = false) Long userId,
                                                  @RequestParam(required = false) Integer limit,
                                                  HttpServletRequest httpRequest) {
        log.info("获取推荐商品: userId={}, limit={}", userId, limit);
        
        int productLimit = limit != null ? limit : 10;
        ApiResult<Object> result = productService.getRecommendedProducts(userId, productLimit);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取分类商品
     */
    @GetMapping("/category/{category}")
    public ApiResult<Object> getProductsByCategory(@PathVariable String category,
                                                 @RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer size,
                                                 @RequestParam(required = false) Long shopId,
                                                 HttpServletRequest httpRequest) {
        log.info("获取分类商品: category={}, page={}, size={}, shopId={}", 
                category, page, size, shopId);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = productService.getProductsByCategory(category, pageNum, pageSize, shopId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商家商品分类
     */
    @GetMapping("/shop/{shopId}/categories")
    public ApiResult<Object> getShopCategories(@PathVariable Long shopId,
                                             HttpServletRequest httpRequest) {
        log.info("获取商家商品分类: shopId={}", shopId);
        ApiResult<Object> result = productService.getShopCategories(shopId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 上架商品
     */
    @PutMapping("/{productId}/publish")
    public ApiResult<Void> publishProduct(@PathVariable Long productId,
                                        HttpServletRequest httpRequest) {
        log.info("上架商品: productId={}", productId);
        ApiResult<Void> result = productService.publishProduct(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 下架商品
     */
    @PutMapping("/{productId}/unpublish")
    public ApiResult<Void> unpublishProduct(@PathVariable Long productId,
                                          HttpServletRequest httpRequest) {
        log.info("下架商品: productId={}", productId);
        ApiResult<Void> result = productService.unpublishProduct(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 设置商品库存
     */
    @PutMapping("/{productId}/stock")
    public ApiResult<Void> updateProductStock(@PathVariable Long productId,
                                            @RequestParam Integer stock,
                                            HttpServletRequest httpRequest) {
        log.info("设置商品库存: productId={}, stock={}", productId, stock);
        ApiResult<Void> result = productService.updateProductStock(productId, stock);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 调整商品价格
     */
    @PutMapping("/{productId}/price")
    public ApiResult<Void> updateProductPrice(@PathVariable Long productId,
                                            @RequestParam Double price,
                                            HttpServletRequest httpRequest) {
        log.info("调整商品价格: productId={}, price={}", productId, price);
        ApiResult<Void> result = productService.updateProductPrice(productId, price);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 设置商品折扣
     */
    @PutMapping("/{productId}/discount")
    public ApiResult<Void> setProductDiscount(@PathVariable Long productId,
                                            @RequestParam Double discount,
                                            @RequestParam(required = false) String discountType,
                                            HttpServletRequest httpRequest) {
        log.info("设置商品折扣: productId={}, discount={}, discountType={}", 
                productId, discount, discountType);
        ApiResult<Void> result = productService.setProductDiscount(productId, discount, discountType);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 取消商品折扣
     */
    @PutMapping("/{productId}/discount/cancel")
    public ApiResult<Void> cancelProductDiscount(@PathVariable Long productId,
                                               HttpServletRequest httpRequest) {
        log.info("取消商品折扣: productId={}", productId);
        ApiResult<Void> result = productService.cancelProductDiscount(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商品评价
     */
    @GetMapping("/{productId}/reviews")
    public ApiResult<Object> getProductReviews(@PathVariable Long productId,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size,
                                             @RequestParam(required = false) Integer rating,
                                             HttpServletRequest httpRequest) {
        log.info("获取商品评价: productId={}, page={}, size={}, rating={}", 
                productId, page, size, rating);
        
        validatePagination(page, size);
        int pageNum = getPage(page);
        int pageSize = getSize(size);
        
        ApiResult<Object> result = productService.getProductReviews(productId, pageNum, pageSize, rating);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商品销售统计
     */
    @GetMapping("/{productId}/sales-statistics")
    public ApiResult<Object> getProductSalesStatistics(@PathVariable Long productId,
                                                     @RequestParam(required = false) String period,
                                                     HttpServletRequest httpRequest) {
        log.info("获取商品销售统计: productId={}, period={}", productId, period);
        ApiResult<Object> result = productService.getProductSalesStatistics(productId, period);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取相似商品
     */
    @GetMapping("/{productId}/similar")
    public ApiResult<Object> getSimilarProducts(@PathVariable Long productId,
                                              @RequestParam(required = false) Integer limit,
                                              HttpServletRequest httpRequest) {
        log.info("获取相似商品: productId={}, limit={}", productId, limit);
        
        int productLimit = limit != null ? limit : 5;
        ApiResult<Object> result = productService.getSimilarProducts(productId, productLimit);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 批量更新商品状态
     */
    @PutMapping("/batch/status")
    public ApiResult<Void> batchUpdateProductStatus(@RequestParam String status,
                                                  @RequestParam Long[] productIds,
                                                  HttpServletRequest httpRequest) {
        log.info("批量更新商品状态: status={}, productIds count={}", status, productIds.length);
        ApiResult<Void> result = productService.batchUpdateProductStatus(status, productIds);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 批量删除商品
     */
    @DeleteMapping("/batch")
    public ApiResult<Void> batchDeleteProducts(@RequestParam Long[] productIds,
                                             HttpServletRequest httpRequest) {
        log.info("批量删除商品: productIds count={}", productIds.length);
        ApiResult<Void> result = productService.batchDeleteProducts(productIds);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 导入商品数据
     */
    @PostMapping("/import")
    public ApiResult<Object> importProducts(@Valid @RequestBody Object importRequest,
                                          HttpServletRequest httpRequest) {
        log.info("导入商品数据");
        ApiResult<Object> result = productService.importProducts(importRequest);
        logApiAccess(httpRequest, importRequest, result);
        return result;
    }

    /**
     * 导出商品数据
     */
    @GetMapping("/export")
    public ApiResult<Object> exportProducts(@RequestParam(required = false) Long shopId,
                                          @RequestParam(required = false) String format,
                                          HttpServletRequest httpRequest) {
        log.info("导出商品数据: shopId={}, format={}", shopId, format);
        ApiResult<Object> result = productService.exportProducts(shopId, format);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商品标签
     */
    @GetMapping("/{productId}/tags")
    public ApiResult<Object> getProductTags(@PathVariable Long productId,
                                          HttpServletRequest httpRequest) {
        log.info("获取商品标签: productId={}", productId);
        ApiResult<Object> result = productService.getProductTags(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新商品标签
     */
    @PutMapping("/{productId}/tags")
    public ApiResult<Void> updateProductTags(@PathVariable Long productId,
                                           @RequestParam String[] tags,
                                           HttpServletRequest httpRequest) {
        log.info("更新商品标签: productId={}, tags count={}", productId, tags.length);
        ApiResult<Void> result = productService.updateProductTags(productId, tags);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商品营养信息
     */
    @GetMapping("/{productId}/nutrition")
    public ApiResult<Object> getProductNutrition(@PathVariable Long productId,
                                               HttpServletRequest httpRequest) {
        log.info("获取商品营养信息: productId={}", productId);
        ApiResult<Object> result = productService.getProductNutrition(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 更新商品营养信息
     */
    @PutMapping("/{productId}/nutrition")
    public ApiResult<Void> updateProductNutrition(@PathVariable Long productId,
                                                @Valid @RequestBody Object nutritionRequest,
                                                HttpServletRequest httpRequest) {
        log.info("更新商品营养信息: productId={}", productId);
        ApiResult<Void> result = productService.updateProductNutrition(productId, nutritionRequest);
        logApiAccess(httpRequest, nutritionRequest, result);
        return result;
    }

    /**
     * 获取商品图片
     */
    @GetMapping("/{productId}/images")
    public ApiResult<Object> getProductImages(@PathVariable Long productId,
                                            HttpServletRequest httpRequest) {
        log.info("获取商品图片: productId={}", productId);
        ApiResult<Object> result = productService.getProductImages(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 上传商品图片
     */
    @PostMapping("/{productId}/images")
    public ApiResult<Object> uploadProductImage(@PathVariable Long productId,
                                              @Valid @RequestBody Object imageRequest,
                                              HttpServletRequest httpRequest) {
        log.info("上传商品图片: productId={}", productId);
        ApiResult<Object> result = productService.uploadProductImage(productId, imageRequest);
        logApiAccess(httpRequest, imageRequest, result);
        return result;
    }

    /**
     * 删除商品图片
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    public ApiResult<Void> deleteProductImage(@PathVariable Long productId,
                                            @PathVariable Long imageId,
                                            HttpServletRequest httpRequest) {
        log.info("删除商品图片: productId={}, imageId={}", productId, imageId);
        ApiResult<Void> result = productService.deleteProductImage(productId, imageId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 设置商品主图
     */
    @PutMapping("/{productId}/images/{imageId}/set-main")
    public ApiResult<Void> setMainProductImage(@PathVariable Long productId,
                                             @PathVariable Long imageId,
                                             HttpServletRequest httpRequest) {
        log.info("设置商品主图: productId={}, imageId={}", productId, imageId);
        ApiResult<Void> result = productService.setMainProductImage(productId, imageId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 获取商品变体
     */
    @GetMapping("/{productId}/variants")
    public ApiResult<Object> getProductVariants(@PathVariable Long productId,
                                              HttpServletRequest httpRequest) {
        log.info("获取商品变体: productId={}", productId);
        ApiResult<Object> result = productService.getProductVariants(productId);
        logApiAccess(httpRequest, null, result);
        return result;
    }

    /**
     * 创建商品变体
     */
    @PostMapping("/{productId}/variants")
    public ApiResult<Object> createProduct