package com.healthyfood.service;

import com.healthyfood.common.ApiResult;
import com.healthyfood.common.ErrorCode;
import com.healthyfood.common.Constants;
import com.healthyfood.entity.Shop;
import com.healthyfood.entity.User;
import com.healthyfood.repository.ShopRepository;
import com.healthyfood.repository.UserRepository;
import com.healthyfood.vo.shop.*;
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
 * 商家服务
 */
@Slf4j
@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RedisService redisService;
    
    /**
     * 商家注册
     */
    @Transactional
    public ApiResult<ShopRegisterResponse> register(ShopRegisterRequest request) {
        try {
            // 1. 验证请求参数
            if (!request.validate()) {
                return ApiResult.error(ErrorCode.PARAM_ERROR, "参数验证失败");
            }
            
            // 2. 验证手机号是否已注册
            if (shopRepository.existsByPhone(request.getPhone())) {
                return ApiResult.error(ErrorCode.USER_EXISTS, "手机号已注册");
            }
            
            // 3. 验证邮箱是否已注册
            if (shopRepository.existsByEmail(request.getEmail())) {
                return ApiResult.error(ErrorCode.USER_EXISTS, "邮箱已注册");
            }
            
            // 4. 验证验证码
            String verifyKey = Constants.REDIS_KEY_SMS_PREFIX + request.getPhone();
            String cachedCode = redisService.getString(verifyKey);
            
            if (cachedCode == null || !cachedCode.equals(request.getVerifyCode())) {
                return ApiResult.error(ErrorCode.VERIFY_CODE_ERROR, "验证码错误或已过期");
            }
            
            // 5. 创建商家实体
            Shop shop = Shop.builder()
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .password(encryptPassword(request.getPassword()))
                    .shopName(request.getShopName())
                    .shopType(request.getShopType())
                    .description(request.getDescription())
                    .address(request.getAddress())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .contactPerson(request.getContactPerson())
                    .contactPhone(request.getContactPhone())
                    .businessLicense(request.getBusinessLicense())
                    .healthPermit(request.getHealthPermit())
                    .openingHours(request.getOpeningHours())
                    .deliveryRange(request.getDeliveryRange())
                    .minOrderAmount(request.getMinOrderAmount())
                    .deliveryFee(request.getDeliveryFee())
                    .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
                    .paymentMethods(request.getPaymentMethods())
                    .cuisineTypes(request.getCuisineTypes())
                    .specialties(request.getSpecialties())
                    .tags(request.getTags())
                    .status(Shop.ShopStatus.PENDING)  // 初始状态为待审核
                    .registrationTime(LocalDateTime.now())
                    .lastUpdateTime(LocalDateTime.now())
                    .build();
            
            // 6. 保存商家
            shop = shopRepository.save(shop);
            
            // 7. 清除验证码
            redisService.delete(verifyKey);
            
            // 8. 返回响应
            ShopRegisterResponse response = ShopRegisterResponse.builder()
                    .shopId(shop.getId())
                    .shopName(shop.getShopName())
                    .phone(shop.getPhone())
                    .email(shop.getEmail())
                    .status(shop.getStatus().name())
                    .registrationTime(shop.getRegistrationTime())
                    .message("商家注册成功，请等待审核")
                    .build();
            
            log.info("商家注册成功: shopId={}, shopName={}", shop.getId(), shop.getShopName());
            return ApiResult.success(response);
            
        } catch (Exception e) {
            log.error("商家注册失败", e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "商家注册失败");
        }
    }
    
    /**
     * 商家登录
     */
    @Transactional
    public ApiResult<ShopLoginResponse> login(ShopLoginRequest request) {
        try {
            // 1. 根据登录方式查找商家
            Shop shop = null;
            switch (request.getLoginType()) {
                case PHONE_PASSWORD:
                    shop = shopRepository.findByPhone(request.getPhone());
                    break;
                case EMAIL_PASSWORD:
                    shop = shopRepository.findByEmail(request.getEmail());
                    break;
                case PHONE_VERIFY_CODE:
                    shop = shopRepository.findByPhone(request.getPhone());
                    // 验证验证码
                    String verifyKey = Constants.REDIS_KEY_SMS_PREFIX + request.getPhone();
                    String cachedCode = redisService.getString(verifyKey);
                    if (cachedCode == null || !cachedCode.equals(request.getVerifyCode())) {
                        return ApiResult.error(ErrorCode.VERIFY_CODE_ERROR, "验证码错误或已过期");
                    }
                    break;
                default:
                    return ApiResult.error(ErrorCode.PARAM_ERROR, "不支持的登录方式");
            }
            
            // 2. 验证商家是否存在
            if (shop == null) {
                return ApiResult.error(ErrorCode.USER_NOT_EXISTS, "商家不存在");
            }
            
            // 3. 验证密码（如果是密码登录）
            if (request.requiresPassword()) {
                String encryptedPassword = encryptPassword(request.getPassword());
                if (!shop.getPassword().equals(encryptedPassword)) {
                    return ApiResult.error(ErrorCode.PASSWORD_ERROR, "密码错误");
                }
            }
            
            // 4. 验证商家状态
            if (shop.getStatus() == Shop.ShopStatus.DISABLED) {
                return ApiResult.error(ErrorCode.USER_DISABLED, "商家账号已禁用");
            }
            
            if (shop.getStatus() == Shop.ShopStatus.PENDING) {
                return ApiResult.error(ErrorCode.SHOP_PENDING, "商家审核中，请等待审核通过");
            }
            
            if (shop.getStatus() == Shop.ShopStatus.REJECTED) {
                return ApiResult.error(ErrorCode.SHOP_REJECTED, "商家审核未通过");
            }
            
            // 5. 生成token
            String token = generateToken(shop.getId(), shop.getPhone());
            
            // 6. 更新登录信息
            shop.setLastLoginTime(LocalDateTime.now());
            shop.setLastLoginIp(request.getIpAddress());
            shopRepository.save(shop);
            
            // 7. 缓存token和商家信息
            cacheShopToken(token, shop);
            
            // 8. 清除验证码（如果是验证码登录）
            if (request.requiresVerifyCode()) {
                String verifyKey = Constants.REDIS_KEY_SMS_PREFIX + request.getPhone();
                redisService.delete(verifyKey);
            }
            
            // 9. 返回响应
            ShopLoginResponse response = ShopLoginResponse.builder()
                    .shopId(shop.getId())
                    .shopName(shop.getShopName())
                    .phone(shop.getPhone())
                    .email(shop.getEmail())
                    .token(token)
                    .tokenExpiry(LocalDateTime.now().plusHours(Constants.TOKEN_EXPIRY_HOURS))
                    .status(shop.getStatus().name())
                    .lastLoginTime(shop.getLastLoginTime())
                    .build();
            
            log.info("商家登录成功: shopId={}, shopName={}", shop.getId(), shop.getShopName());
            return ApiResult.success(response);
            
        } catch (Exception e) {
            log.error("商家登录失败", e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "商家登录失败");
        }
    }
    
    /**
     * 获取商家详情
     */
    public ApiResult<ShopDetailVO> getShopDetail(Long shopId, Long userId) {
        try {
            // 1. 获取商家
            Shop shop = shopRepository.findById(shopId).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 2. 检查商家状态
            if (shop.getStatus() != Shop.ShopStatus.ACTIVE) {
                return ApiResult.error(ErrorCode.SHOP_NOT_ACTIVE, "商家未营业");
            }
            
            // 3. 构建响应
            ShopDetailVO detail = buildShopDetailVO(shop, userId);
            
            // 4. 更新浏览量
            shop.setViewCount(shop.getViewCount() + 1);
            shopRepository.save(shop);
            
            return ApiResult.success(detail);
            
        } catch (Exception e) {
            log.error("获取商家详情失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取商家详情失败");
        }
    }
    
    /**
     * 搜索商家
     */
    public ApiResult<Page<ShopListVO>> searchShops(ShopSearchRequest request) {
        try {
            // 1. 构建分页和排序
            Pageable pageable = PageRequest.of(
                    request.getPage() != null ? request.getPage() : 0,
                    request.getSize() != null ? request.getSize() : 10,
                    buildSort(request.getSortBy(), request.getSortOrder())
            );
            
            // 2. 构建查询条件
            Specification<Shop> spec = buildSearchSpecification(request);
            
            // 3. 执行查询
            Page<Shop> shopPage = shopRepository.findAll(spec, pageable);
            
            // 4. 转换为VO
            Page<ShopListVO> resultPage = shopPage.map(shop -> convertToShopListVO(shop, request.getUserId()));
            
            return ApiResult.success(resultPage);
            
        } catch (Exception e) {
            log.error("搜索商家失败", e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "搜索商家失败");
        }
    }
    
    /**
     * 更新商家信息
     */
    @Transactional
    public ApiResult<ShopDetailVO> updateShopInfo(Long shopId, ShopUpdateRequest request) {
        try {
            // 1. 获取商家
            Shop shop = shopRepository.findById(shopId).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 2. 验证权限（这里简化处理，实际应该检查token）
            
            // 3. 更新商家信息
            if (StringUtils.hasText(request.getShopName())) {
                shop.setShopName(request.getShopName());
            }
            
            if (StringUtils.hasText(request.getDescription())) {
                shop.setDescription(request.getDescription());
            }
            
            if (StringUtils.hasText(request.getAddress())) {
                shop.setAddress(request.getAddress());
            }
            
            if (request.getLatitude() != null) {
                shop.setLatitude(request.getLatitude());
            }
            
            if (request.getLongitude() != null) {
                shop.setLongitude(request.getLongitude());
            }
            
            if (StringUtils.hasText(request.getContactPerson())) {
                shop.setContactPerson(request.getContactPerson());
            }
            
            if (StringUtils.hasText(request.getContactPhone())) {
                shop.setContactPhone(request.getContactPhone());
            }
            
            if (request.getOpeningHours() != null) {
                shop.setOpeningHours(request.getOpeningHours());
            }
            
            if (request.getDeliveryRange() != null) {
                shop.setDeliveryRange(request.getDeliveryRange());
            }
            
            if (request.getMinOrderAmount() != null) {
                shop.setMinOrderAmount(request.getMinOrderAmount());
            }
            
            if (request.getDeliveryFee() != null) {
                shop.setDeliveryFee(request.getDeliveryFee());
            }
            
            if (request.getEstimatedDeliveryTime() != null) {
                shop.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
            }
            
            if (request.getPaymentMethods() != null) {
                shop.setPaymentMethods(request.getPaymentMethods());
            }
            
            if (request.getCuisineTypes() != null) {
                shop.setCuisineTypes(request.getCuisineTypes());
            }
            
            if (request.getSpecialties() != null) {
                shop.setSpecialties(request.getSpecialties());
            }
            
            if (request.getTags() != null) {
                shop.setTags(request.getTags());
            }
            
            if (request.getLogo() != null) {
                shop.setLogo(request.getLogo());
            }
            
            if (request.getCoverImage() != null) {
                shop.setCoverImage(request.getCoverImage());
            }
            
            if (request.getImages() != null) {
                shop.setImages(request.getImages());
            }
            
            // 4. 保存更新
            shop.setLastUpdateTime(LocalDateTime.now());
            shop = shopRepository.save(shop);
            
            // 5. 返回更新后的详情
            ShopDetailVO detail = buildShopDetailVO(shop, null);
            
            log.info("商家信息更新成功: shopId={}", shopId);
            return ApiResult.success(detail);
            
        } catch (Exception e) {
            log.error("更新商家信息失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "更新商家信息失败");
        }
    }
    
    /**
     * 商家审核
     */
    @Transactional
    public ApiResult<Void> reviewShop(Long shopId, ShopReviewRequest request) {
        try {
            // 1. 获取商家
            Shop shop = shopRepository.findById(shopId).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 2. 检查当前状态
            if (shop.getStatus() != Shop.ShopStatus.PENDING) {
                return ApiResult.error(ErrorCode.SHOP_NOT_PENDING, "商家不在待审核状态");
            }
            
            // 3. 更新状态
            if (request.getApproved()) {
                shop.setStatus(Shop.ShopStatus.ACTIVE);
                shop.setApprovalTime(LocalDateTime.now());
                shop.setApprovedBy(request.getReviewer());
                shop.setApprovalNotes(request.getNotes());
            } else {
                shop.setStatus(Shop.ShopStatus.REJECTED);
                shop.setRejectionTime(LocalDateTime.now());
                shop.setRejectedBy(request.getReviewer());
                shop.setRejectionReason(request.getNotes());
            }
            
            // 4. 保存
            shopRepository.save(shop);
            
            log.info("商家审核完成: shopId={}, approved={}, reviewer={}", 
                    shopId, request.getApproved(), request.getReviewer());
            return ApiResult.success();
            
        } catch (Exception e) {
            log.error("商家审核失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "商家审核失败");
        }
    }
    
    /**
     * 获取商家统计
     */
    public ApiResult<ShopStatisticsVO> getShopStatistics(Long shopId, StatisticsPeriod period) {
        try {
            // 1. 获取商家
            Shop shop = shopRepository.findById(shopId).orElse(null);
            if (shop == null) {
                return ApiResult.error(ErrorCode.SHOP_NOT_EXISTS, "商家不存在");
            }
            
            // 2. 构建统计信息（这里简化处理，实际应该从数据库统计）
            ShopStatisticsVO statistics = ShopStatisticsVO.builder()
                    .shopId(shopId)
                    .period(period)
                    .totalOrders(shop.getTotalOrders())
                    .totalRevenue(shop.getTotalRevenue())
                    .averageRating(shop.getAverageRating())
                    .totalReviews(shop.getTotalReviews())
                    .viewCount(shop.getViewCount())
                    .favoriteCount(shop.getFavoriteCount())
                    .shareCount(shop.getShareCount())
                    .build();
            
            // 3. 计算趋势（这里简化处理）
            statistics.setOrderTrend(calculateOrderTrend(shopId, period));
            statistics.setRevenueTrend(calculateRevenueTrend(shopId, period));
            statistics.setRatingTrend(calculateRatingTrend(shopId, period));
            
            return ApiResult.success(statistics);
            
        } catch (Exception e) {
            log.error("获取商家统计失败: shopId={}", shopId, e);
            return ApiResult.error(ErrorCode.SYSTEM_ERROR, "获取商家统计失败");
        }
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 加密密码
     */
    private String encryptPassword(String password) {
        // 这里使用MD5加盐加密，实际生产环境应该使用更安全的加密方式
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(password + Constants.PASSWORD_SALT);
    }
    
    /**
     * 生成token
     */
    private String generateToken(Long shopId, String phone) {
        // 这里简化处理，实际应该使用JWT
        String rawToken = shopId + "_" + phone + "_" + System.currentTimeMillis();
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(rawToken);
    }
    
    /**
     * 缓存商家token
     */
    private void cacheShopToken(String token, Shop shop) {
        String tokenKey = Constants.REDIS_KEY_TOKEN_PREFIX + token;
        String shopKey = Constants.REDIS_KEY_SHOP_PREFIX + shop.getId();
        
        // 缓存token -> 商家ID
        redisService.set(tokenKey, shop.getId().toString(), Constants.TOKEN_EXPIRY_HOURS * 3600);
        
        // 缓存商家基本信息
        Map<String, Object> shopInfo = new HashMap<>();
        shopInfo.put("id", shop.getId());
        shopInfo.put("shopName", shop.getShopName());
        shopInfo.put("phone", shop.getPhone());
        shopInfo.put("email", shop.getEmail());
        shopInfo.put("status", shop.getStatus().name());
        
        redisService.hset(shopKey, shopInfo);
        redisService.expire(shopKey, Constants.TOKEN_EXPIRY_HOURS * 3600);
    }
    
    /**
     * 构建商家详情VO
     */
    private ShopDetailVO buildShopDetailVO(Shop shop, Long userId) {
        // 计算距离（如果有用户位置）
        Double distance = null;
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
                distance = calculateDistance(
                        user.getLatitude(), user.getLongitude(),
                        shop.getLatitude(), shop.getLongitude()
                );
            }
        }
        
        // 构建营业状态
        boolean isOpen = checkIfShopIsOpen(shop);
        
        return ShopDetailVO.builder()
                .shopId(shop.getId())
                .shopName(shop.getShopName())
                .shopType(shop.getShopType())
                .description(shop.getDescription())
                .address(shop.getAddress())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .distance(distance)
                .contactPerson(shop.getContactPerson())
                .contactPhone(shop.getContactPhone())
                .openingHours(shop.getOpeningHours())
                .isOpen(isOpen)
                .deliveryRange(shop.getDeliveryRange())
                .minOrderAmount(shop.getMinOrderAmount())
                .deliveryFee(shop.getDeliveryFee())
                .estimatedDeliveryTime(shop.getEstimatedDeliveryTime())
                .paymentMethods(shop.getPaymentMethods())
                .cuisineTypes(shop.getCuisineTypes())
                .specialties(shop.getSpecialties())
                .tags(shop.getTags())
                .logo(shop.getLogo())
                .coverImage(shop.getCoverImage())
                .images(shop.getImages())
                .averageRating(shop.getAverageRating())
                .totalReviews(shop.getTotalReviews())
                .totalOrders(shop.getTotalOrders())
                .totalRevenue(shop.getTotalRevenue())
                .viewCount(shop.getViewCount())
                .favoriteCount(shop.getFavoriteCount())
                .shareCount(shop.getShareCount())
                .status(shop.getStatus().name())
                .registrationTime(shop.getRegistrationTime())
                .lastUpdateTime(shop.getLastUpdateTime())
                .build();
    }
    
    /**
     * 构建排序
     */
    private Sort buildSort(String sortBy, String sortOrder) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "averageRating");
        }
        
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        switch (sortBy) {
            case "rating":
                return Sort.by(direction, "averageRating");
            case "orders":
                return Sort.by(direction, "totalOrders");
            case "revenue":
                return Sort.by(direction, "totalRevenue");
            case "distance":
                return Sort.by(direction, "distance");
            case "deliveryTime":
                return Sort.by(direction, "estimatedDeliveryTime");
            case "minOrder":
                return Sort.by(direction, "minOrderAmount");
            case "deliveryFee":
                return Sort.by(direction, "deliveryFee");
            default:
                return Sort.by(Sort.Direction.DESC, "averageRating");
        }
    }
    
    /**
     * 构建搜索条件
     */
    private Specification<Shop> buildSearchSpecification(ShopSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询活跃商家
            predicates.add(cb.equal(root.get("status"), Shop.ShopStatus.ACTIVE));
            
            // 关键词搜索
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword() + "%";
                Predicate namePredicate = cb.like(root.get("shopName"), keyword);
                Predicate descPredicate = cb.like(root.get("description"), keyword);
                Predicate tagsPredicate = cb.like(root.get("tags"), keyword);
                predicates.add(cb.or(namePredicate, descPredicate, tagsPredicate));
            }
            
            // 商家类型
            if (request.getShopType() != null) {
                predicates.add(cb.equal(root.get("shopType"), request.getShopType()));
            }
            
            // 菜系类型
            if (request.getCuisineTypes() != null && !request.getCuisineTypes().isEmpty()) {
                predicates.add(root.get("cuisineTypes").in(request.getCuisineTypes()));
            }
            
            // 标签
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                for (String tag : request.getTags()) {
                    predicates.add(cb.like(root.get("tags"), "%" + tag + "%"));
                }
            }
            
            // 最小订单金额
            if (request.getMinOrderAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minOrderAmount"), request.getMinOrderAmount()));
            }
            
            // 配送费
            if (request.getMaxDeliveryFee() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("deliveryFee"), request.getMaxDeliveryFee()));
            }
            
            // 配送时间
            if (request.getMaxDeliveryTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("estimatedDeliveryTime"), request.getMaxDeliveryTime()));
            }
            
            // 评分
            if (request.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), request.getMinRating()));
            }
            
            // 地理位置筛选（如果有用户位置）
            if (request.getUserId() != null && request.getLatitude() != null && request.getLongitude() != null) {
                User user = userRepository.findById(request.getUserId()).orElse(null);
                if (user != null) {
                    // 这里简化处理，实际应该使用空间查询
                    // 计算距离并筛选在配送范围内的商家
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 转换为商家列表VO
     */
    private ShopListVO convertToShopListVO(Shop shop, Long userId) {
        // 计算距离
        Double distance = null;
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
                distance = calculateDistance(
                        user.getLatitude(), user.getLongitude(),
                        shop.getLatitude(), shop.getLongitude()
                );
            }
        }
        
        // 检查营业状态
        boolean isOpen = checkIfShopIsOpen(shop);
        
        return ShopListVO.builder()
                .shopId(shop.getId())
                .shopName(shop.getShopName())
                .shopType(shop.getShopType())
                .description(shop.getDescription())
                .address(shop.getAddress())
                .distance(distance)
                .logo(shop.getLogo())
                .averageRating(shop.getAverageRating())
                .totalReviews(shop.getTotalReviews())
                .minOrderAmount(shop.getMinOrderAmount())
                .deliveryFee(shop.getDeliveryFee())
                .estimatedDeliveryTime(shop.getEstimatedDeliveryTime())
                .isOpen(isOpen)
                .tags(shop.getTags())
                .build();
    }
    
    /**
     * 计算两点间距离（公里）
     */
    private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return null;
        }
        
        final int R = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 检查商家是否营业
     */
    private boolean checkIfShopIsOpen(Shop shop) {
        if (shop.getOpeningHours() == null || shop.getOpeningHours().isEmpty()) {
            return true; // 如果没有设置营业时间，默认营业
        }
        
        try {
            // 这里简化处理，实际应该解析营业时间字符串
            // 格式如: "09:00-22:00" 或 "周一至周五 09:00-22:00, 周末 10:00-23:00"
            LocalDateTime now = LocalDateTime.now();
            int currentHour = now.getHour();
            int currentMinute = now.getMinute();
            
            // 简单判断：假设营业时间为 8:00-22:00
            int openHour = 8;
            int closeHour = 22;
            
            return currentHour >= openHour && currentHour < closeHour;
        } catch (Exception e) {
            log.error("检查营业状态失败: shopId={}", shop.getId(), e);
            return true;
        }
    }
    
    /**
     * 计算订单趋势
     */
    private Map<String, Integer> calculateOrderTrend(Long shopId, StatisticsPeriod period) {
        // 这里简化处理，实际应该从数据库查询
        Map<String, Integer> trend = new LinkedHashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(period.getDays() - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 模拟数据
            int orders = (int) (Math.random() * 50) + 10;
            trend.put(date.toString(), orders);
        }
        
        return trend;
    }
    
    /**
     * 计算收入趋势
     */
    private Map<String, Double> calculateRevenueTrend(Long shopId, StatisticsPeriod period) {
        // 这里简化处理，实际应该从数据库查询
        Map<String, Double> trend = new LinkedHashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(period.getDays() - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 模拟数据
            double revenue = Math.random() * 5000 + 1000;
            trend.put(date.toString(), revenue);
        }
        
        return trend;
    }
    
    /**
     * 计算评分趋势
     */
    private Map<String, Double> calculateRatingTrend(Long shopId, StatisticsPeriod period) {
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