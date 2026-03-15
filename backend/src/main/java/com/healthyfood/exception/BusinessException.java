package com.healthyfood.exception;

import com.healthyfood.common.ErrorCode;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误数据
     */
    private final Object data;

    /**
     * 构造方法
     */
    public BusinessException() {
        super(ErrorCode.SYSTEM_ERROR.getMessage());
        this.code = ErrorCode.SYSTEM_ERROR.getCode();
        this.data = null;
    }

    /**
     * 构造方法
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.SYSTEM_ERROR.getCode();
        this.data = null;
    }

    /**
     * 构造方法
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.data = null;
    }

    /**
     * 构造方法
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.data = null;
    }

    /**
     * 构造方法
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.data = null;
    }

    /**
     * 构造方法
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.data = null;
    }

    /**
     * 构造方法（带数据）
     */
    public BusinessException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.data = data;
    }

    /**
     * 构造方法（带数据和消息）
     */
    public BusinessException(ErrorCode errorCode, String message, Object data) {
        super(message);
        this.code = errorCode.getCode();
        this.data = data;
    }

    /**
     * 构造方法（带数据、消息和原因）
     */
    public BusinessException(ErrorCode errorCode, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.data = data;
    }

    /**
     * 获取错误码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误数据
     */
    public Object getData() {
        return data;
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

    /**
     * 判断是否是系统错误
     */
    public boolean isSystemError() {
        return getModuleCode() == 1; // 1xxx 是系统错误
    }

    /**
     * 判断是否是业务错误
     */
    public boolean isBusinessError() {
        return getModuleCode() >= 10; // 10xxx 及以上是业务错误
    }

    /**
     * 获取异常简略信息
     */
    public String getSimpleInfo() {
        return String.format("BusinessException{code=%d, message='%s', module=%d, detail=%d}", 
                code, getMessage(), getModuleCode(), getDetailCode());
    }

    /**
     * 快速创建参数错误异常
     */
    public static BusinessException paramError() {
        return new BusinessException(ErrorCode.PARAM_ERROR);
    }

    /**
     * 快速创建参数错误异常（指定消息）
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 快速创建未授权异常
     */
    public static BusinessException unauthorized() {
        return new BusinessException(ErrorCode.USER_NOT_LOGIN);
    }

    /**
     * 快速创建未授权异常（指定消息）
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(ErrorCode.USER_NOT_LOGIN, message);
    }

    /**
     * 快速创建权限不足异常
     */
    public static BusinessException forbidden() {
        return new BusinessException(ErrorCode.PERMISSION_DENIED);
    }

    /**
     * 快速创建权限不足异常（指定消息）
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(ErrorCode.PERMISSION_DENIED, message);
    }

    /**
     * 快速创建数据不存在异常
     */
    public static BusinessException notFound() {
        return new BusinessException(ErrorCode.DATA_NOT_FOUND);
    }

    /**
     * 快速创建数据不存在异常（指定消息）
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.DATA_NOT_FOUND, message);
    }

    /**
     * 快速创建数据已存在异常
     */
    public static BusinessException alreadyExists() {
        return new BusinessException(ErrorCode.DATA_ALREADY_EXISTS);
    }

    /**
     * 快速创建数据已存在异常（指定消息）
     */
    public static BusinessException alreadyExists(String message) {
        return new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, message);
    }

    /**
     * 快速创建操作失败异常
     */
    public static BusinessException operationFailed() {
        return new BusinessException(ErrorCode.OPERATION_FAILED);
    }

    /**
     * 快速创建操作失败异常（指定消息）
     */
    public static BusinessException operationFailed(String message) {
        return new BusinessException(ErrorCode.OPERATION_FAILED, message);
    }

    /**
     * 快速创建用户不存在异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    /**
     * 快速创建用户不存在异常（指定消息）
     */
    public static BusinessException userNotFound(String message) {
        return new BusinessException(ErrorCode.USER_NOT_FOUND, message);
    }

    /**
     * 快速创建用户已存在异常
     */
    public static BusinessException userAlreadyExists() {
        return new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
    }

    /**
     * 快速创建用户已存在异常（指定消息）
     */
    public static BusinessException userAlreadyExists(String message) {
        return new BusinessException(ErrorCode.USER_ALREADY_EXISTS, message);
    }

    /**
     * 快速创建商家不存在异常
     */
    public static BusinessException shopNotFound() {
        return new BusinessException(ErrorCode.SHOP_NOT_FOUND);
    }

    /**
     * 快速创建商家不存在异常（指定消息）
     */
    public static BusinessException shopNotFound(String message) {
        return new BusinessException(ErrorCode.SHOP_NOT_FOUND, message);
    }

    /**
     * 快速创建商品不存在异常
     */
    public static BusinessException productNotFound() {
        return new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
    }

    /**
     * 快速创建商品不存在异常（指定消息）
     */
    public static BusinessException productNotFound(String message) {
        return new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, message);
    }

    /**
     * 快速创建订单不存在异常
     */
    public static BusinessException orderNotFound() {
        return new BusinessException(ErrorCode.ORDER_NOT_FOUND);
    }

    /**
     * 快速创建订单不存在异常（指定消息）
     */
    public static BusinessException orderNotFound(String message) {
        return new BusinessException(ErrorCode.ORDER_NOT_FOUND, message);
    }

    /**
     * 快速创建支付记录不存在异常
     */
    public static BusinessException paymentNotFound() {
        return new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    /**
     * 快速创建支付记录不存在异常（指定消息）
     */
    public static BusinessException paymentNotFound(String message) {
        return new BusinessException(ErrorCode.PAYMENT_NOT_FOUND, message);
    }

    /**
     * 快速创建创作者不存在异常
     */
    public static BusinessException creatorNotFound() {
        return new BusinessException(ErrorCode.CREATOR_NOT_FOUND);
    }

    /**
     * 快速创建创作者不存在异常（指定消息）
     */
    public static BusinessException creatorNotFound(String message) {
        return new BusinessException(ErrorCode.CREATOR_NOT_FOUND, message);
    }

    /**
     * 快速创建内容不存在异常
     */
    public static BusinessException contentNotFound() {
        return new BusinessException(ErrorCode.CONTENT_NOT_FOUND);
    }

    /**
     * 快速创建内容不存在异常（指定消息）
     */
    public static BusinessException contentNotFound(String message) {
        return new BusinessException(ErrorCode.CONTENT_NOT_FOUND, message);
    }

    /**
     * 快速创建健康记录不存在异常
     */
    public static BusinessException healthRecordNotFound() {
        return new BusinessException(ErrorCode.HEALTH_RECORD_NOT_FOUND);
    }

    /**
     * 快速创建健康记录不存在异常（指定消息）
     */
    public static BusinessException healthRecordNotFound(String message) {
        return new BusinessException(ErrorCode.HEALTH_RECORD_NOT_FOUND, message);
    }

    /**
     * 快速创建积分记录不存在异常
     */
    public static BusinessException pointsRecordNotFound() {
        return new BusinessException(ErrorCode.POINTS_RECORD_NOT_FOUND);
    }

    /**
     * 快速创建积分记录不存在异常（指定消息）
     */
    public static BusinessException pointsRecordNotFound(String message) {
        return new BusinessException(ErrorCode.POINTS_RECORD_NOT_FOUND, message);
    }

    /**
     * 快速创建文件不存在异常
     */
    public static BusinessException fileNotFound() {
        return new BusinessException(ErrorCode.FILE_NOT_FOUND);
    }

    /**
     * 快速创建文件不存在异常（指定消息）
     */
    public static BusinessException fileNotFound(String message) {
        return new BusinessException(ErrorCode.FILE_NOT_FOUND, message);
    }

    /**
     * 快速创建令牌无效异常
     */
    public static BusinessException tokenInvalid() {
        return new BusinessException(ErrorCode.TOKEN_INVALID);
    }

    /**
     * 快速创建令牌无效异常（指定消息）
     */
    public static BusinessException tokenInvalid(String message) {
        return new BusinessException(ErrorCode.TOKEN_INVALID, message);
    }

    /**
     * 快速创建令牌过期异常
     */
    public static BusinessException tokenExpired() {
        return new BusinessException(ErrorCode.TOKEN_EXPIRED);
    }

    /**
     * 快速创建令牌过期异常（指定消息）
     */
    public static BusinessException tokenExpired(String message) {
        return new BusinessException(ErrorCode.TOKEN_EXPIRED, message);
    }
}