package com.healthyfood.common;

/**
 * 系统常量类
 */
public class Constants {

    private Constants() {
        // 防止实例化
    }

    // ==================== 系统配置常量 ====================
    
    /**
     * 系统名称
     */
    public static final String SYSTEM_NAME = "健康餐饮AI平台";
    
    /**
     * 系统版本
     */
    public static final String SYSTEM_VERSION = "1.0.0";
    
    /**
     * 默认字符编码
     */
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    /**
     * 默认时区
     */
    public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
    
    /**
     * 默认日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 默认日期时间格式
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 默认时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    // ==================== 业务常量 ====================
    
    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 最小分页大小
     */
    public static final int MIN_PAGE_SIZE = 1;
    
    /**
     * 默认缓存时间（秒）
     */
    public static final int DEFAULT_CACHE_TIME = 300; // 5分钟
    
    /**
     * 长缓存时间（秒）
     */
    public static final int LONG_CACHE_TIME = 3600; // 1小时
    
    /**
     * 超长缓存时间（秒）
     */
    public static final int VERY_LONG_CACHE_TIME = 86400; // 24小时
    
    // ==================== 用户相关常量 ====================
    
    /**
     * 默认用户头像
     */
    public static final String DEFAULT_USER_AVATAR = "/static/images/default-avatar.png";
    
    /**
     * 默认用户昵称前缀
     */
    public static final String DEFAULT_USER_NICKNAME_PREFIX = "用户";
    
    /**
     * 用户状态 - 正常
     */
    public static final int USER_STATUS_NORMAL = 1;
    
    /**
     * 用户状态 - 禁用
     */
    public static final int USER_STATUS_DISABLED = 0;
    
    /**
     * 用户性别 - 未知
     */
    public static final int USER_GENDER_UNKNOWN = 0;
    
    /**
     * 用户性别 - 男
     */
    public static final int USER_GENDER_MALE = 1;
    
    /**
     * 用户性别 - 女
     */
    public static final int USER_GENDER_FEMALE = 2;
    
    /**
     * 用户活动水平 - 久坐
     */
    public static final int USER_ACTIVITY_SEDENTARY = 1;
    
    /**
     * 用户活动水平 - 轻度
     */
    public static final int USER_ACTIVITY_LIGHT = 2;
    
    /**
     * 用户活动水平 - 中度
     */
    public static final int USER_ACTIVITY_MODERATE = 3;
    
    /**
     * 用户活动水平 - 重度
     */
    public static final int USER_ACTIVITY_HEAVY = 4;
    
    // ==================== 商家相关常量 ====================
    
    /**
     * 商家状态 - 营业
     */
    public static final int SHOP_STATUS_OPEN = 1;
    
    /**
     * 商家状态 - 关闭
     */
    public static final int SHOP_STATUS_CLOSED = 0;
    
    /**
     * 商家状态 - 休息
     */
    public static final int SHOP_STATUS_REST = 2;
    
    /**
     * 商家认证状态 - 未认证
     */
    public static final int SHOP_VERIFICATION_PENDING = 0;
    
    /**
     * 商家认证状态 - 已认证
     */
    public static final int SHOP_VERIFICATION_APPROVED = 1;
    
    /**
     * 商家订阅计划 - 免费
     */
    public static final String SHOP_SUBSCRIPTION_FREE = "free";
    
    /**
     * 商家订阅计划 - 基础版
     */
    public static final String SHOP_SUBSCRIPTION_BASIC = "basic";
    
    /**
     * 商家订阅计划 - 专业版
     */
    public static final String SHOP_SUBSCRIPTION_PRO = "pro";
    
    /**
     * 商家订阅计划 - 企业版
     */
    public static final String SHOP_SUBSCRIPTION_ENTERPRISE = "enterprise";
    
    // ==================== 商品相关常量 ====================
    
    /**
     * 商品状态 - 上架
     */
    public static final int PRODUCT_STATUS_ON_SALE = 1;
    
    /**
     * 商品状态 - 下架
     */
    public static final int PRODUCT_STATUS_OFF_SALE = 0;
    
    /**
     * 无限库存标识
     */
    public static final int PRODUCT_STOCK_UNLIMITED = -1;
    
    /**
     * 默认商品排序
     */
    public static final int PRODUCT_DEFAULT_SORT_ORDER = 0;
    
    // ==================== 订单相关常量 ====================
    
    /**
     * 订单状态 - 待确认
     */
    public static final int ORDER_STATUS_PENDING = 0;
    
    /**
     * 订单状态 - 已确认
     */
    public static final int ORDER_STATUS_CONFIRMED = 1;
    
    /**
     * 订单状态 - 制作中
     */
    public static final int ORDER_STATUS_PREPARING = 2;
    
    /**
     * 订单状态 - 已完成
     */
    public static final int ORDER_STATUS_COMPLETED = 3;
    
    /**
     * 订单状态 - 已取消
     */
    public static final int ORDER_STATUS_CANCELLED = 4;
    
    /**
     * 支付状态 - 待支付
     */
    public static final int PAYMENT_STATUS_PENDING = 0;
    
    /**
     * 支付状态 - 已支付
     */
    public static final int PAYMENT_STATUS_PAID = 1;
    
    /**
     * 支付状态 - 支付失败
     */
    public static final int PAYMENT_STATUS_FAILED = 2;
    
    /**
     * 支付方式 - 微信支付
     */
    public static final String PAYMENT_METHOD_WECHAT = "wechat";
    
    /**
     * 支付方式 - 支付宝
     */
    public static final String PAYMENT_METHOD_ALIPAY = "alipay";
    
    /**
     * 支付方式 - 现金
     */
    public static final String PAYMENT_METHOD_CASH = "cash";
    
    /**
     * 支付方式 - 余额支付
     */
    public static final String PAYMENT_METHOD_BALANCE = "balance";
    
    /**
     * 支付方式 - 积分支付
     */
    public static final String PAYMENT_METHOD_POINTS = "points";
    
    /**
     * 默认订单超时时间（分钟）
     */
    public static final int ORDER_DEFAULT_TIMEOUT_MINUTES = 30;
    
    // ==================== 创作者相关常量 ====================
    
    /**
     * 创作者等级 - 青铜
     */
    public static final String CREATOR_LEVEL_BRONZE = "bronze";
    
    /**
     * 创作者等级 - 白银
     */
    public static final String CREATOR_LEVEL_SILVER = "silver";
    
    /**
     * 创作者等级 - 黄金
     */
    public static final String CREATOR_LEVEL_GOLD = "gold";
    
    /**
     * 创作者等级 - 钻石
     */
    public static final String CREATOR_LEVEL_DIAMOND = "diamond";
    
    /**
     * 创作者状态 - 正常
     */
    public static final int CREATOR_STATUS_ACTIVE = 1;
    
    /**
     * 创作者状态 - 禁用
     */
    public static final int CREATOR_STATUS_DISABLED = 0;
    
    /**
     * 创作者认证状态 - 未认证
     */
    public static final int CREATOR_VERIFICATION_PENDING = 0;
    
    /**
     * 创作者认证状态 - 已认证
     */
    public static final int CREATOR_VERIFICATION_APPROVED = 1;
    
    // ==================== 内容相关常量 ====================
    
    /**
     * 内容类型 - 文章
     */
    public static final String CONTENT_TYPE_ARTICLE = "article";
    
    /**
     * 内容类型 - 视频
     */
    public static final String CONTENT_TYPE_VIDEO = "video";
    
    /**
     * 内容类型 - 图片
     */
    public static final String CONTENT_TYPE_IMAGE = "image";
    
    /**
     * 发布平台 - 抖音
     */
    public static final String CONTENT_PLATFORM_DOUYIN = "douyin";
    
    /**
     * 发布平台 - 小红书
     */
    public static final String CONTENT_PLATFORM_XIAOHONGSHU = "xiaohongshu";
    
    /**
     * 发布平台 - 微信
     */
    public static final String CONTENT_PLATFORM_WECHAT = "wechat";
    
    /**
     * 发布平台 - 微博
     */
    public static final String CONTENT_PLATFORM_WEIBO = "weibo";
    
    /**
     * 发布平台 - B站
     */
    public static final String CONTENT_PLATFORM_BILIBILI = "bilibili";
    
    /**
     * 内容状态 - 草稿
     */
    public static final int CONTENT_STATUS_DRAFT = 0;
    
    /**
     * 内容状态 - 已发布
     */
    public static final int CONTENT_STATUS_PUBLISHED = 1;
    
    /**
     * 内容状态 - 审核中
     */
    public static final int CONTENT_STATUS_REVIEWING = 2;
    
    /**
     * 内容状态 - 审核失败
     */
    public static final int CONTENT_STATUS_REJECTED = 3;
    
    // ==================== 健康记录相关常量 ====================
    
    /**
     * 餐次 - 早餐
     */
    public static final String MEAL_TYPE_BREAKFAST = "breakfast";
    
    /**
     * 餐次 - 午餐
     */
    public static final String MEAL_TYPE_LUNCH = "lunch";
    
    /**
     * 餐次 - 晚餐
     */
    public static final String MEAL_TYPE_DINNER = "dinner";
    
    /**
     * 餐次 - 零食
     */
    public static final String MEAL_TYPE_SNACK = "snack";
    
    // ==================== 积分相关常量 ====================
    
    /**
     * 积分类型 - 发布内容
     */
    public static final String POINTS_TYPE_CONTENT_PUBLISH = "content_publish";
    
    /**
     * 积分类型 - 优质内容
     */
    public static final String POINTS_TYPE_CONTENT_QUALITY = "content_quality";
    
    /**
     * 积分类型 - 爆款内容
     */
    public static final String POINTS_TYPE_CONTENT_VIRAL = "content_viral";
    
    /**
     * 积分类型 - 商家对接
     */
    public static final String POINTS_TYPE_SHOP_CONNECTION = "shop_connection";
    
    /**
     * 积分类型 - 商家评价
     */
    public static final String POINTS_TYPE_SHOP_REVIEW = "shop_review";
    
    /**
     * 积分类型 - 商家复购
     */
    public static final String POINTS_TYPE_SHOP_REPURCHASE = "shop_repurchase";
    
    /**
     * 积分类型 - 邀请创作者
     */
    public static final String POINTS_TYPE_INVITE_CREATOR = "invite_creator";
    
    /**
     * 积分类型 - 每日签到
     */
    public static final String POINTS_TYPE_DAILY_CHECKIN = "daily_checkin";
    
    /**
     * 积分类型 - 健康记录
     */
    public static final String POINTS_TYPE_HEALTH_RECORD = "health_record";
    
    /**
     * 积分类型 - 订单完成
     */
    public static final String POINTS_TYPE_ORDER_COMPLETE = "order_complete";
    
    /**
     * 积分类型 - 分享应用
     */
    public static final String POINTS_TYPE_SHARE_APP = "share_app";
    
    /**
     * 积分类型 - 反馈建议
     */
    public static final String POINTS_TYPE_FEEDBACK = "feedback";
    
    /**
     * 积分类型 - 活动参与
     */
    public static final String POINTS_TYPE_ACTIVITY_PARTICIPATION = "activity_participation";
    
    /**
     * 积分类型 - 系统奖励
     */
    public static final String POINTS_TYPE_SYSTEM_REWARD = "system_reward";
    
    /**
     * 积分类型 - 积分兑换
     */
    public static final String POINTS_TYPE_POINTS_EXCHANGE = "points_exchange";
    
    /**
     * 积分类型 - 积分扣除
     */
    public static final String POINTS_TYPE_POINTS_DEDUCTION = "points_deduction";
    
    // ==================== 支付记录相关常量 ====================
    
    /**
     * 支付状态 - 待支付
     */
    public static final int PAYMENT_RECORD_STATUS_PENDING = 0;
    
    /**
     * 支付状态 - 支付成功
     */
    public static final int PAYMENT_RECORD_STATUS_SUCCESS = 1;
    
    /**
     * 支付状态 - 支付失败
     */
    public static final int PAYMENT_RECORD_STATUS_FAILED = 2;
    
    /**
     * 支付状态 - 已退款
     */
    public static final int PAYMENT_RECORD_STATUS_REFUNDED = 3;
    
    /**
     * 支付状态 - 已关闭
     */
    public static final int PAYMENT_RECORD_STATUS_CLOSED = 4;
    
    /**
     * 货币 - 人民币
     */
    public static final String CURRENCY_CNY = "CNY";
    
    /**
     * 货币 - 美元
     */
    public static final String CURRENCY_USD = "USD";
    
    // ==================== 缓存键常量 ====================
    
    /**
     * 用户信息缓存键前缀
     */
    public static final String CACHE_KEY_USER_PREFIX = "user:";
    
    /**
     * 商家信息缓存键前缀
     */
    public static final String CACHE_KEY_SHOP_PREFIX = "shop:";
    
    /**
     * 商品信息缓存键前缀
     */
    public static final String CACHE_KEY_PRODUCT_PREFIX = "product:";
    
    /**
     * 订单信息缓存键前缀
     */
    public static final String CACHE_KEY_ORDER_PREFIX = "order:";
    
    /**
     * 验证码缓存键前缀
     */
    public static final String CACHE_KEY_CAPTCHA_PREFIX = "captcha:";
    
    /**
     * 访问令牌缓存键前缀
     */
    public static final String CACHE_KEY_TOKEN_PREFIX = "token:";
    
    /**
     * 限流缓存键前缀
     */
    public static final String CACHE_KEY_RATE_LIMIT_PREFIX = "rate_limit:";
    
    // ==================== 安全相关常量 ====================
    
    /**
     * JWT令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * JWT令牌头
     */
    public static final String TOKEN_HEADER = "Authorization";
    
    /**
     * 刷新令牌头
     */
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    
    /**
     * 默认令牌过期时间（毫秒）
     */
    public static final long TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L; // 24小时
    
    /**
     * 刷新令牌过期时间（毫秒）
     */
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7天
    
    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;
    
    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 20;
    
    /**
     * 手机号验证正则
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    
    /**
     * 邮箱验证正则
     */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    
    // ==================== 文件相关常量 ====================
    
    /**
     * 默认文件上传大小限制（MB）
     */
    public static final long DEFAULT_MAX_UPLOAD_SIZE = 10 * 1024 * 1024L; // 10MB
    
    /**
     * 图片文件类型
     */
    public static final String[] IMAGE_FILE_TYPES = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    
    /**
     * 视频文件类型
     */
    public static final String[] VIDEO_FILE_TYPES = {".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv"};
    
    /**
     * 文档文件类型
     */
    public static final String[] DOCUMENT_FILE_TYPES = {".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt"};
    
    /**
     * 默认文件存储路径
     */
    public static final String DEFAULT_UPLOAD_PATH = "/uploads";
    
    // ==================== 业务配置常量 ====================
    
    /**
     * 创作者分成比例 - 青铜
     */
    public static final double CREATOR_COMMISSION_RATE_BRONZE = 0.15;
    
    /**
     * 创作者分成比例 - 白银
     */
    public static final double CREATOR_COMMISSION_RATE_SILVER = 0.18;
    
    /**
     * 创作者分成比例 - 黄金
     */
    public static final double CREATOR_COMMISSION_RATE_GOLD = 0.20;
    
    /**
     * 创作者分成比例 - 钻石
     */
    public static final double CREATOR_COMMISSION_RATE_DIAMOND = 0.25;
    
    /**
     * 默认每日热量目标（卡路里）
     */
    public static final int DEFAULT_DAILY_CALORIE_TARGET = 2000;
    
    /**
     * 默认蛋白质目标（克）
     */
    public static final double DEFAULT_PROTEIN_TARGET = 60.0;
    
    /**
     * 默认碳水目标（克）
     */
    public static final double DEFAULT_CARB_TARGET = 300.0;
    
    /**
     * 默认脂肪目标（克）
     */
    public static final double DEFAULT_FAT_TARGET = 65.0;
    
    /**
     * 商家认证费用（元）
     */
    public static final double SHOP_VERIFICATION_FEE = 99.0;
    
    /**
     * 免费试用天数
     */
    public static final int FREE_TRIAL_DAYS = 14;
    
    /**
     * 基础版月费（元）
     */
    public static final double BASIC_MONTHLY_PRICE = 199.0;
    
    /**
     * 专业版月费（元）
     */
    public static final double PRO_MONTHLY_PRICE = 499.0;
    
    /**
     * 企业版月费（元）
     */
    public static final double ENTERPRISE_MONTHLY_PRICE = 999.0;
    
    // ==================== 积分规则常量 ====================
    
    /**
     * 发布内容积分
     */
    public static final int POINTS_CONTENT_PUBLISH = 10;
    
    /**
     * 优质内容积分
     */
    public static final int POINTS_CONTENT_QUALITY = 50;
    
    /**
     * 爆款内容积分
     */
    public static final int POINTS_CONTENT_VIRAL = 200;
    
    /**
     * 商家对接积分
     */
    public static final int POINTS_SHOP_CONNECTION = 100;
    
    /**
     * 商家评价积分
     */
    public static final int POINTS_SHOP_REVIEW = 50;
    
    /**
     * 商家复购积分
     */
    public static final int POINTS_SHOP_REPURCHASE = 200;
    
    /**
     * 邀请创作者积分
     */
    public static final int POINTS_INVITE_CREATOR = 500;
    
    /**
     * 每日签到积分
     */
    public static final int POINTS_DAILY_CHECKIN = 5;
    
    /**
     * 健康记录积分
     */
    public static final int POINTS_HEALTH_RECORD = 10;
    
    /**
     * 订单完成积分
     */
    public static final int POINTS_ORDER_COMPLETE = 20;
    
    /**
     * 分享应用积分
     */
    public static final int POINTS_SHARE_APP = 30;
    
    /**
     * 反馈建议积分
     */
    public static final int POINTS_FEEDBACK = 15;
    
    /**
     * 活动参与积分
     */
    public static final int POINTS_ACTIVITY_PARTICIPATION = 50;
    
    /**
     * 系统奖励积分
     */
    public static final int POINTS_SYSTEM_REWARD = 100;
    
    // ==================== 健康目标常量 ====================
    
    /**
     * 健康目标 - 减重
     */
    public static final String HEALTH_GOAL_WEIGHT_LOSS = "weight_loss";
    
    /**
     * 健康目标 - 增重
     */
    public static final String HEALTH_GOAL_WEIGHT_GAIN = "weight_gain";
    
    /**
     * 健康目标 - 增肌
     */
    public static final String HEALTH_GOAL_MUSCLE_GAIN = "muscle_gain";
    
    /**
     * 健康目标 - 维持
     */
    public static final String HEALTH_GOAL_MAINTENANCE = "maintenance";
    
    /**
     * 健康目标 - 糖尿病管理
     */
    public static final String HEALTH_GOAL_DIABETES = "diabetes";
    
    /**
     * 健康目标 - 高血压管理
     */
    public static final String HEALTH_GOAL_HYPERTENSION = "hypertension";
    
    // ==================== 错误码常量 ====================
    
    /**
     * 成功
     */
    public static final int CODE_SUCCESS = 200;
    
    /**
     * 失败
     */
    public static final int CODE_FAILURE = 500;
    
    /**
     * 参数错误
     */
    public static final int CODE_PARAM_ERROR = 400;
    
    /**
     * 未授权
     */
    public static final int CODE_UNAUTHORIZED = 401;
    
    /**
     * 禁止访问
     */
    public static final int CODE_FORBIDDEN = 403;
    
    /**
     * 资源不存在
     */
    public static final int CODE_NOT_FOUND = 404;
    
    /**
     * 请求超时
     */
    public static final int CODE_TIMEOUT = 408;
    
    /**
     * 系统繁忙
     */
    public static final int CODE_SYSTEM_BUSY = 429;
    
    /**
     * 服务不可用
     */
    public static final int CODE_SERVICE_UNAVAILABLE = 503;
    
    // ==================== 其他常量 ====================
    
    /**
     * 是/否 - 是
     */
    public static final int YES = 1;
    
    /**
     * 是/否 - 否
     */
    public static final int NO = 0;
    
    /**
     * 启用/禁用 - 启用
     */
    public static final int ENABLED = 1;
    
    /**
     * 启用/禁用 - 禁用
     */
    public static final int DISABLED = 0;
    
    /**
     * 删除标记 - 未删除
     */
    public static final int NOT_DELETED = 0;
    
    /**
     * 删除标记 - 已删除
     */
    public static final int DELETED = 1;
    
    /**
     * 默认排序字段
     */
    public static final String DEFAULT_SORT_FIELD = "id";
    
    /**
     * 默认排序方向
     */
    public static final String DEFAULT_SORT_DIRECTION = "desc";
    
    /**
     * 升序
     */
    public static final String SORT_ASC = "asc";
    
    /**
     * 降序
     */
    public static final String SORT_DESC = "desc";
}