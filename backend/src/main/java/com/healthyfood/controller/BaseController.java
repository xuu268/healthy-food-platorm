package com.healthyfood.controller;

import com.healthyfood.common.ApiResult;
import com.healthyfood.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制器基类和全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class BaseController {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
        
        log.warn("参数验证失败: {}", errorMessage);
        return ApiResult.error(ErrorCode.PARAM_ERROR, errorMessage);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("约束违反: {}", errorMessage);
        return ApiResult.error(ErrorCode.PARAM_ERROR, errorMessage);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(com.healthyfood.exception.BusinessException.class)
    public ApiResult<Void> handleBusinessException(com.healthyfood.exception.BusinessException ex, 
                                                  HttpServletRequest request) {
        log.error("业务异常 - URL: {}, ErrorCode: {}, Message: {}", 
                request.getRequestURI(), ex.getErrorCode(), ex.getMessage(), ex);
        
        return ApiResult.error(ex.getErrorCode(), ex.getMessage());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleException(Exception ex, HttpServletRequest request) {
        log.error("系统异常 - URL: {}, Method: {}, IP: {}", 
                request.getRequestURI(), request.getMethod(), getClientIp(request), ex);
        
        // 生产环境返回通用错误信息，开发环境返回详细错误
        String message = "系统内部错误，请稍后重试";
        if (isDevelopmentEnvironment()) {
            message = ex.getMessage();
        }
        
        return ApiResult.error(ErrorCode.SYSTEM_ERROR, message);
    }

    /**
     * 获取客户端IP地址
     */
    protected String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    /**
     * 检查是否为开发环境
     */
    protected boolean isDevelopmentEnvironment() {
        String env = System.getProperty("spring.profiles.active");
        return "dev".equals(env) || "development".equals(env);
    }

    /**
     * 记录API访问日志
     */
    protected void logApiAccess(HttpServletRequest request, Object requestBody, Object response) {
        if (log.isDebugEnabled()) {
            log.debug("API访问 - URL: {}, Method: {}, IP: {}, Request: {}, Response: {}", 
                    request.getRequestURI(), request.getMethod(), getClientIp(request), 
                    requestBody, response);
        }
    }

    /**
     * 构建成功响应
     */
    protected <T> ApiResult<T> success(T data) {
        return ApiResult.success(data);
    }

    /**
     * 构建成功响应（无数据）
     */
    protected ApiResult<Void> success() {
        return ApiResult.success();
    }

    /**
     * 构建错误响应
     */
    protected ApiResult<Void> error(String code, String message) {
        return ApiResult.error(code, message);
    }

    /**
     * 构建错误响应（使用ErrorCode）
     */
    protected ApiResult<Void> error(ErrorCode errorCode) {
        return ApiResult.error(errorCode);
    }

    /**
     * 构建错误响应（使用ErrorCode和自定义消息）
     */
    protected ApiResult<Void> error(ErrorCode errorCode, String message) {
        return ApiResult.error(errorCode, message);
    }

    /**
     * 验证分页参数
     */
    protected void validatePagination(Integer page, Integer size) {
        if (page != null && page < 0) {
            throw new IllegalArgumentException("页码不能为负数");
        }
        if (size != null && (size <= 0 || size > 100)) {
            throw new IllegalArgumentException("每页大小必须在1-100之间");
        }
    }

    /**
     * 获取分页参数
     */
    protected int getPage(Integer page) {
        return page != null ? page : 0;
    }

    /**
     * 获取每页大小
     */
    protected int getSize(Integer size) {
        return size != null ? size : 20;
    }

    /**
     * 验证排序参数
     */
    protected String validateSort(String sortBy, String sortOrder) {
        if (sortBy == null) {
            return null;
        }
        
        // 允许的排序字段
        String[] allowedSortFields = {"id", "createTime", "updateTime", "price", "sales", "rating"};
        boolean isValid = false;
        for (String field : allowedSortFields) {
            if (field.equals(sortBy)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new IllegalArgumentException("不支持的排序字段: " + sortBy);
        }
        
        if (sortOrder != null && !"asc".equalsIgnoreCase(sortOrder) && !"desc".equalsIgnoreCase(sortOrder)) {
            throw new IllegalArgumentException("排序方向必须是asc或desc");
        }
        
        return sortOrder != null ? sortOrder : "desc";
    }
}