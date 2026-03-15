package com.healthyfood.common;

/**
 * 错误码枚举类
 * 错误码格式：模块代码 + 具体错误码
 * 例如：10001 = 10(用户模块) + 001(具体错误)
 */
public enum ErrorCode {

    // ==================== 通用错误码 (0xxx) ====================
    
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    
    /**
     * 系统内部错误
     */
    SYSTEM_ERROR(1000, "系统内部错误"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(1001, "参数错误"),
    
    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(1002, "数据不存在"),
    
    /**
     * 数据已存在
     */
    DATA_ALREADY_EXISTS(1003, "数据已存在"),
    
    /**
     * 操作失败
     */
    OPERATION_FAILED(1004, "操作失败"),
    
    /**
     * 权限不足
     */
    PERMISSION_DENIED(1005, "权限不足"),
    
    /**
     * 请求过于频繁
     */
    REQUEST_TOO_FREQUENT(1006, "请求过于频繁"),
    
    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(1007, "服务不可用"),
    
    /**
     * 网络错误
     */
    NETWORK_ERROR(1008, "网络错误"),
    
    /**
     * 数据库错误
     */
    DATABASE_ERROR(1009, "数据库错误"),
    
    /**
     * 缓存错误
     */
    CACHE_ERROR(1010, "缓存错误"),
    
    /**
     * 文件操作错误
     */
    FILE_OPERATION_ERROR(1011, "文件操作错误"),
    
    /**
     * 数据格式错误
     */
    DATA_FORMAT_ERROR(1012, "数据格式错误"),
    
    /**
     * 数据验证失败
     */
    DATA_VALIDATION_FAILED(1013, "数据验证失败"),
    
    /**
     * 业务逻辑错误
     */
    BUSINESS_LOGIC_ERROR(1014, "业务逻辑错误"),
    
    /**
     * 配置错误
     */
    CONFIG_ERROR(1015, "配置错误"),
    
    /**
     * 版本不兼容
     */
    VERSION_INCOMPATIBLE(1016, "版本不兼容"),
    
    /**
     * 资源不足
     */
    RESOURCE_INSUFFICIENT(1017, "资源不足"),
    
    /**
     * 超时
     */
    TIMEOUT(1018, "操作超时"),
    
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(1999, "未知错误"),
    
    // ==================== 用户模块错误码 (10xxx) ====================
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(10001, "用户不存在"),
    
    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS(10002, "用户已存在"),
    
    /**
     * 用户名或密码错误
     */
    USER_PASSWORD_ERROR(10003, "用户名或密码错误"),
    
    /**
     * 用户被禁用
     */
    USER_DISABLED(10004, "用户已被禁用"),
    
    /**
     * 用户未登录
     */
    USER_NOT_LOGIN(10005, "用户未登录"),
    
    /**
     * 用户登录过期
     */
    USER_LOGIN_EXPIRED(10006, "用户登录已过期"),
    
    /**
     * 用户权限不足
     */
    USER_PERMISSION_DENIED(10007, "用户权限不足"),
    
    /**
     * 用户手机号已存在
     */
    USER_PHONE_EXISTS(10008, "手机号已存在"),
    
    /**
     * 用户邮箱已存在
     */
    USER_EMAIL_EXISTS(10009, "邮箱已存在"),
    
    /**
     * 用户验证码错误
     */
    USER_CAPTCHA_ERROR(10010, "验证码错误"),
    
    /**
     * 用户验证码过期
     */
    USER_CAPTCHA_EXPIRED(10011, "验证码已过期"),
    
    /**
     * 用户密码强度不足
     */
    USER_PASSWORD_WEAK(10012, "密码强度不足"),
    
    /**
     * 用户操作过于频繁
     */
    USER_OPERATION_FREQUENT(10013, "操作过于频繁，请稍后再试"),
    
    /**
     * 用户信息不完整
     */
    USER_INFO_INCOMPLETE(10014, "用户信息不完整"),
    
    /**
     * 用户账户异常
     */
    USER_ACCOUNT_ABNORMAL(10015, "用户账户异常"),
    
    // ==================== 商家模块错误码 (11xxx) ====================
    
    /**
     * 商家不存在
     */
    SHOP_NOT_FOUND(11001, "商家不存在"),
    
    /**
     * 商家已存在
     */
    SHOP_ALREADY_EXISTS(11002, "商家已存在"),
    
    /**
     * 商家被禁用
     */
    SHOP_DISABLED(11003, "商家已被禁用"),
    
    /**
     * 商家未营业
     */
    SHOP_NOT_OPEN(11004, "商家未营业"),
    
    /**
     * 商家认证失败
     */
    SHOP_VERIFICATION_FAILED(11005, "商家认证失败"),
    
    /**
     * 商家订阅过期
     */
    SHOP_SUBSCRIPTION_EXPIRED(11006, "商家订阅已过期"),
    
    /**
     * 商家信息不完整
     */
    SHOP_INFO_INCOMPLETE(11007, "商家信息不完整"),
    
    /**
     * 商家位置信息错误
     */
    SHOP_LOCATION_ERROR(11008, "商家位置信息错误"),
    
    /**
     * 商家营业时间错误
     */
    SHOP_BUSINESS_HOURS_ERROR(11009, "商家营业时间错误"),
    
    /**
     * 商家联系方式错误
     */
    SHOP_CONTACT_ERROR(11010, "商家联系方式错误"),
    
    // ==================== 商品模块错误码 (12xxx) ====================
    
    /**
     * 商品不存在
     */
    PRODUCT_NOT_FOUND(12001, "商品不存在"),
    
    /**
     * 商品已下架
     */
    PRODUCT_OFF_SHELF(12002, "商品已下架"),
    
    /**
     * 商品库存不足
     */
    PRODUCT_STOCK_INSUFFICIENT(12003, "商品库存不足"),
    
    /**
     * 商品价格错误
     */
    PRODUCT_PRICE_ERROR(12004, "商品价格错误"),
    
    /**
     * 商品信息不完整
     */
    PRODUCT_INFO_INCOMPLETE(12005, "商品信息不完整"),
    
    /**
     * 商品分类错误
     */
    PRODUCT_CATEGORY_ERROR(12006, "商品分类错误"),
    
    /**
     * 商品营养信息错误
     */
    PRODUCT_NUTRITION_ERROR(12007, "商品营养信息错误"),
    
    /**
     * 商品图片错误
     */
    PRODUCT_IMAGE_ERROR(12008, "商品图片错误"),
    
    // ==================== 订单模块错误码 (13xxx) ====================
    
    /**
     * 订单不存在
     */
    ORDER_NOT_FOUND(13001, "订单不存在"),
    
    /**
     * 订单状态错误
     */
    ORDER_STATUS_ERROR(13002, "订单状态错误"),
    
    /**
     * 订单已取消
     */
    ORDER_CANCELLED(13003, "订单已取消"),
    
    /**
     * 订单已完成
     */
    ORDER_COMPLETED(13004, "订单已完成"),
    
    /**
     * 订单超时
     */
    ORDER_TIMEOUT(13005, "订单已超时"),
    
    /**
     * 订单金额错误
     */
    ORDER_AMOUNT_ERROR(13006, "订单金额错误"),
    
    /**
     * 订单商品错误
     */
    ORDER_PRODUCT_ERROR(13007, "订单商品错误"),
    
    /**
     * 订单支付错误
     */
    ORDER_PAYMENT_ERROR(13008, "订单支付错误"),
    
    /**
     * 订单退款错误
     */
    ORDER_REFUND_ERROR(13009, "订单退款错误"),
    
    /**
     * 订单评价错误
     */
    ORDER_REVIEW_ERROR(13010, "订单评价错误"),
    
    // ==================== 支付模块错误码 (14xxx) ====================
    
    /**
     * 支付记录不存在
     */
    PAYMENT_NOT_FOUND(14001, "支付记录不存在"),
    
    /**
     * 支付状态错误
     */
    PAYMENT_STATUS_ERROR(14002, "支付状态错误"),
    
    /**
     * 支付金额错误
     */
    PAYMENT_AMOUNT_ERROR(14003, "支付金额错误"),
    
    /**
     * 支付方式不支持
     */
    PAYMENT_METHOD_UNSUPPORTED(14004, "支付方式不支持"),
    
    /**
     * 支付超时
     */
    PAYMENT_TIMEOUT(14005, "支付超时"),
    
    /**
     * 支付失败
     */
    PAYMENT_FAILED(14006, "支付失败"),
    
    /**
     * 支付回调错误
     */
    PAYMENT_CALLBACK_ERROR(14007, "支付回调错误"),
    
    /**
     * 退款失败
     */
    REFUND_FAILED(14008, "退款失败"),
    
    /**
     * 退款金额错误
     */
    REFUND_AMOUNT_ERROR(14009, "退款金额错误"),
    
    /**
     * 退款状态错误
     */
    REFUND_STATUS_ERROR(14010, "退款状态错误"),
    
    // ==================== 创作者模块错误码 (15xxx) ====================
    
    /**
     * 创作者不存在
     */
    CREATOR_NOT_FOUND(15001, "创作者不存在"),
    
    /**
     * 创作者已存在
     */
    CREATOR_ALREADY_EXISTS(15002, "创作者已存在"),
    
    /**
     * 创作者被禁用
     */
    CREATOR_DISABLED(15003, "创作者已被禁用"),
    
    /**
     * 创作者未认证
     */
    CREATOR_NOT_VERIFIED(15004, "创作者未认证"),
    
    /**
     * 创作者等级不足
     */
    CREATOR_LEVEL_INSUFFICIENT(15005, "创作者等级不足"),
    
    /**
     * 创作者积分不足
     */
    CREATOR_POINTS_INSUFFICIENT(15006, "创作者积分不足"),
    
    /**
     * 创作者余额不足
     */
    CREATOR_BALANCE_INSUFFICIENT(15007, "创作者余额不足"),
    
    /**
     * 创作者内容错误
     */
    CREATOR_CONTENT_ERROR(15008, "创作者内容错误"),
    
    /**
     * 创作者收益错误
     */
    CREATOR_EARNINGS_ERROR(15009, "创作者收益错误"),
    
    // ==================== 内容模块错误码 (16xxx) ====================
    
    /**
     * 内容不存在
     */
    CONTENT_NOT_FOUND(16001, "内容不存在"),
    
    /**
     * 内容状态错误
     */
    CONTENT_STATUS_ERROR(16002, "内容状态错误"),
    
    /**
     * 内容审核失败
     */
    CONTENT_REVIEW_FAILED(16003, "内容审核失败"),
    
    /**
     * 内容类型不支持
     */
    CONTENT_TYPE_UNSUPPORTED(16004, "内容类型不支持"),
    
    /**
     * 内容平台不支持
     */
    CONTENT_PLATFORM_UNSUPPORTED(16005, "内容平台不支持"),
    
    /**
     * 内容标签错误
     */
    CONTENT_TAG_ERROR(16006, "内容标签错误"),
    
    /**
     * 内容媒体错误
     */
    CONTENT_MEDIA_ERROR(16007, "内容媒体错误"),
    
    /**
     * 内容评分错误
     */
    CONTENT_SCORE_ERROR(16008, "内容评分错误"),
    
    /**
     * 内容收益错误
     */
    CONTENT_EARNINGS_ERROR(16009, "内容收益错误"),
    
    // ==================== 健康记录模块错误码 (17xxx) ====================
    
    /**
     * 健康记录不存在
     */
    HEALTH_RECORD_NOT_FOUND(17001, "健康记录不存在"),
    
    /**
     * 健康记录数据错误
     */
    HEALTH_RECORD_DATA_ERROR(17002, "健康记录数据错误"),
    
    /**
     * 健康记录日期错误
     */
    HEALTH_RECORD_DATE_ERROR(17003, "健康记录日期错误"),
    
    /**
     * 健康记录餐次错误
     */
    HEALTH_RECORD_MEAL_ERROR(17004, "健康记录餐次错误"),
    
    /**
     * 健康记录营养错误
     */
    HEALTH_RECORD_NUTRITION_ERROR(17005, "健康记录营养错误"),
    
    /**
     * 健康记录设备错误
     */
    HEALTH_RECORD_DEVICE_ERROR(17006, "健康记录设备错误"),
    
    /**
     * 健康记录评分错误
     */
    HEALTH_RECORD_SCORE_ERROR(17007, "健康记录评分错误"),
    
    // ==================== 积分模块错误码 (18xxx) ====================
    
    /**
     * 积分记录不存在
     */
    POINTS_RECORD_NOT_FOUND(18001, "积分记录不存在"),
    
    /**
     * 积分不足
     */
    POINTS_INSUFFICIENT(18002, "积分不足"),
    
    /**
     * 积分类型错误
     */
    POINTS_TYPE_ERROR(18003, "积分类型错误"),
    
    /**
     * 积分数量错误
     */
    POINTS_AMOUNT_ERROR(18004, "积分数量错误"),
    
    /**
     * 积分余额错误
     */
    POINTS_BALANCE_ERROR(18005, "积分余额错误"),
    
    /**
     * 积分兑换错误
     */
    POINTS_EXCHANGE_ERROR(18006, "积分兑换错误"),
    
    /**
     * 积分扣除错误
     */
    POINTS_DEDUCTION_ERROR(18007, "积分扣除错误"),
    
    // ==================== 文件模块错误码 (19xxx) ====================
    
    /**
     * 文件不存在
     */
    FILE_NOT_FOUND(19001, "文件不存在"),
    
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED(19002, "文件上传失败"),
    
    /**
     * 文件下载失败
     */
    FILE_DOWNLOAD_FAILED(19003, "文件下载失败"),
    
    /**
     * 文件删除失败
     */
    FILE_DELETE_FAILED(19004, "文件删除失败"),
    
    /**
     * 文件大小超限
     */
    FILE_SIZE_EXCEEDED(19005, "文件大小超限"),
    
    /**
     * 文件类型不支持
     */
    FILE_TYPE_UNSUPPORTED(19006, "文件类型不支持"),
    
    /**
     * 文件格式错误
     */
    FILE_FORMAT_ERROR(19007, "文件格式错误"),
    
    /**
     * 文件路径错误
     */
    FILE_PATH_ERROR(19008, "文件路径错误"),
    
    // ==================== 认证授权模块错误码 (20xxx) ====================
    
    /**
     * 令牌无效
     */
    TOKEN_INVALID(20001, "令牌无效"),
    
    /**
     * 令牌过期
     */
    TOKEN_EXPIRED(20002, "令牌已过期"),
    
    /**
     * 令牌缺失
     */
    TOKEN_MISSING(20003, "令牌缺失"),
    
    /**
     * 刷新令牌无效
     */
    REFRESH_TOKEN_INVALID(20004, "刷新令牌无效"),
    
    /**
     * 刷新令牌过期
     */
    REFRESH_TOKEN_EXPIRED(20005, "刷新令牌已过期"),
    
    /**
     * 访问被拒绝
     */
    ACCESS_DENIED(20006, "访问被拒绝"),
    
    /**
     * 需要登录
     */
    LOGIN_REQUIRED(20007, "需要登录"),
    
    /**
     * 需要验证
     */
    VERIFICATION_REQUIRED(20008, "需要验证"),
    
    /**
     * 验证失败
     */
    VERIFICATION_FAILED(20009, "验证失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据错误码获取错误信息
     */
    public static String getMessageByCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode.getMessage();
            }
        }
        return "未知错误";
    }

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return UNKNOWN_ERROR;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * 判断是否失败
     */
    public boolean isFailure() {
        return this != SUCCESS;
    }

    /**
     * 获取模块代码
     */
    public int getModuleCode() {
        return code / 1000;
    }

    /**
     * 获取具体错误码
     */
    public int getDetailCode() {
        return code % 1000;
    }
}