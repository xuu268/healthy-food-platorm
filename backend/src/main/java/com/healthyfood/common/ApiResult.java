package com.healthyfood.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一API响应结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 请求ID（用于追踪）
     */
    private String requestId;

    /**
     * 分页信息
     */
    private PageInfo page;

    /**
     * 私有构造方法
     */
    private ApiResult() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 私有构造方法
     */
    private ApiResult(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    /**
     * 私有构造方法
     */
    private ApiResult(int code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage());
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（有数据和消息）
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(ErrorCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResult<T> failure() {
        return new ApiResult<>(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 失败响应（指定错误码）
     */
    public static <T> ApiResult<T> failure(ErrorCode errorCode) {
        return new ApiResult<>(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 失败响应（指定错误码和消息）
     */
    public static <T> ApiResult<T> failure(ErrorCode errorCode, String message) {
        return new ApiResult<>(errorCode.getCode(), message);
    }

    /**
     * 失败响应（指定错误码、消息和数据）
     */
    public static <T> ApiResult<T> failure(ErrorCode errorCode, String message, T data) {
        return new ApiResult<>(errorCode.getCode(), message, data);
    }

    /**
     * 失败响应（指定错误码枚举）
     */
    public static <T> ApiResult<T> failure(int code, String message) {
        return new ApiResult<>(code, message);
    }

    /**
     * 失败响应（指定错误码枚举和数据）
     */
    public static <T> ApiResult<T> failure(int code, String message, T data) {
        return new ApiResult<>(code, message, data);
    }

    /**
     * 参数错误响应
     */
    public static <T> ApiResult<T> paramError() {
        return failure(ErrorCode.PARAM_ERROR);
    }

    /**
     * 参数错误响应（指定消息）
     */
    public static <T> ApiResult<T> paramError(String message) {
        return failure(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 未授权响应
     */
    public static <T> ApiResult<T> unauthorized() {
        return failure(ErrorCode.USER_NOT_LOGIN);
    }

    /**
     * 未授权响应（指定消息）
     */
    public static <T> ApiResult<T> unauthorized(String message) {
        return failure(ErrorCode.USER_NOT_LOGIN, message);
    }

    /**
     * 权限不足响应
     */
    public static <T> ApiResult<T> forbidden() {
        return failure(ErrorCode.PERMISSION_DENIED);
    }

    /**
     * 权限不足响应（指定消息）
     */
    public static <T> ApiResult<T> forbidden(String message) {
        return failure(ErrorCode.PERMISSION_DENIED, message);
    }

    /**
     * 数据不存在响应
     */
    public static <T> ApiResult<T> notFound() {
        return failure(ErrorCode.DATA_NOT_FOUND);
    }

    /**
     * 数据不存在响应（指定消息）
     */
    public static <T> ApiResult<T> notFound(String message) {
        return failure(ErrorCode.DATA_NOT_FOUND, message);
    }

    /**
     * 数据已存在响应
     */
    public static <T> ApiResult<T> alreadyExists() {
        return failure(ErrorCode.DATA_ALREADY_EXISTS);
    }

    /**
     * 数据已存在响应（指定消息）
     */
    public static <T> ApiResult<T> alreadyExists(String message) {
        return failure(ErrorCode.DATA_ALREADY_EXISTS, message);
    }

    /**
     * 操作失败响应
     */
    public static <T> ApiResult<T> operationFailed() {
        return failure(ErrorCode.OPERATION_FAILED);
    }

    /**
     * 操作失败响应（指定消息）
     */
    public static <T> ApiResult<T> operationFailed(String message) {
        return failure(ErrorCode.OPERATION_FAILED, message);
    }

    /**
     * 设置分页信息
     */
    public ApiResult<T> page(PageInfo page) {
        this.page = page;
        return this;
    }

    /**
     * 设置请求ID
     */
    public ApiResult<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == ErrorCode.SUCCESS.getCode();
    }

    /**
     * 判断是否失败
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * 获取简化的响应信息
     */
    public String getSimpleInfo() {
        return String.format("ApiResult{code=%d, message='%s', success=%s}", 
                code, message, isSuccess());
    }

    /**
     * 分页信息类
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 当前页码
         */
        private int pageNum;

        /**
         * 每页大小
         */
        private int pageSize;

        /**
         * 总记录数
         */
        private long total;

        /**
         * 总页数
         */
        private int pages;

        /**
         * 是否有上一页
         */
        private boolean hasPrevious;

        /**
         * 是否有下一页
         */
        private boolean hasNext;

        /**
         * 是否是第一页
         */
        private boolean isFirst;

        /**
         * 是否是最后一页
         */
        private boolean isLast;

        /**
         * 创建分页信息
         */
        public static PageInfo of(int pageNum, int pageSize, long total) {
            PageInfo pageInfo = new PageInfo();
            pageInfo.pageNum = pageNum;
            pageInfo.pageSize = pageSize;
            pageInfo.total = total;
            
            if (pageSize > 0) {
                pageInfo.pages = (int) Math.ceil((double) total / pageSize);
            } else {
                pageInfo.pages = 0;
            }
            
            pageInfo.hasPrevious = pageNum > 1;
            pageInfo.hasNext = pageNum < pageInfo.pages;
            pageInfo.isFirst = pageNum == 1;
            pageInfo.isLast = pageNum == pageInfo.pages || pageInfo.pages == 0;
            
            return pageInfo;
        }

        /**
         * 创建空分页信息
         */
        public static PageInfo empty() {
            return of(1, Constants.DEFAULT_PAGE_SIZE, 0);
        }

        /**
         * 获取分页描述
         */
        public String getPageDescription() {
            return String.format("第%d页/共%d页，每页%d条，共%d条", 
                    pageNum, pages, pageSize, total);
        }
    }

    /**
     * 构建器模式（链式调用）
     */
    public static class Builder<T> {
        private final ApiResult<T> result;

        public Builder() {
            this.result = new ApiResult<>();
        }

        public Builder<T> code(int code) {
            result.code = code;
            return this;
        }

        public Builder<T> message(String message) {
            result.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            result.data = data;
            return this;
        }

        public Builder<T> requestId(String requestId) {
            result.requestId = requestId;
            return this;
        }

        public Builder<T> page(PageInfo page) {
            result.page = page;
            return this;
        }

        public ApiResult<T> build() {
            return result;
        }
    }

    /**
     * 使用构建器创建响应
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * 创建成功响应的构建器
     */
    public static <T> Builder<T> successBuilder() {
        return new Builder<T>()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage());
    }

    /**
     * 创建失败响应的构建器
     */
    public static <T> Builder<T> failureBuilder(ErrorCode errorCode) {
        return new Builder<T>()
                .code(errorCode.getCode())
                .message(errorCode.getMessage());
    }

    /**
     * 创建失败响应的构建器（指定消息）
     */
    public static <T> Builder<T> failureBuilder(ErrorCode errorCode, String message) {
        return new Builder<T>()
                .code(errorCode.getCode())
                .message(message);
    }
}