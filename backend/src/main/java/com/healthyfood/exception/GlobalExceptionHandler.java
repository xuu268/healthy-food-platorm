package com.healthyfood.exception;

import com.healthyfood.common.ApiResult;
import com.healthyfood.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Object> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", e.getCode(), e.getMessage(), e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("code", e.getCode());
        errorData.put("message", e.getMessage());
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        
        if (e.getData() != null) {
            errorData.put("data", e.getData());
        }
        
        return ApiResult.failure(e.getCode(), e.getMessage(), errorData);
    }

    /**
     * 处理参数验证异常（@Validated）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("参数验证异常: {}", errorMessage, e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errors", e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? 
                                fieldError.getDefaultMessage() : "参数错误"
                )));
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        
        return ApiResult.failure(ErrorCode.PARAM_ERROR, errorMessage, errorData);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<Object> handleBindException(BindException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("参数绑定异常: {}", errorMessage, e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errors", e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? 
                                fieldError.getDefaultMessage() : "参数错误"
                )));
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        
        return ApiResult.failure(ErrorCode.PARAM_ERROR, errorMessage, errorData);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("约束违反异常: {}", errorMessage, e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("errors", e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                )));
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        
        return ApiResult.failure(ErrorCode.PARAM_ERROR, errorMessage, errorData);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<Object> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("404异常: {} {}", e.getHttpMethod(), e.getRequestURL(), e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("path", e.getRequestURL());
        errorData.put("method", e.getHttpMethod());
        errorData.put("message", "请求的资源不存在");
        
        return ApiResult.failure(ErrorCode.DATA_NOT_FOUND, "请求的资源不存在", errorData);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ApiResult<Object> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常", e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        errorData.put("exception", "NullPointerException");
        
        return ApiResult.failure(ErrorCode.SYSTEM_ERROR, "系统内部错误（空指针）", errorData);
    }

    /**
     * 处理类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public ApiResult<Object> handleClassCastException(ClassCastException e, HttpServletRequest request) {
        log.error("类型转换异常", e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        errorData.put("exception", "ClassCastException");
        
        return ApiResult.failure(ErrorCode.SYSTEM_ERROR, "系统内部错误（类型转换）", errorData);
    }

    /**
     * 处理数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public ApiResult<Object> handleNumberFormatException(NumberFormatException e, HttpServletRequest request) {
        log.warn("数字格式异常: {}", e.getMessage(), e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        errorData.put("exception", "NumberFormatException");
        errorData.put("message", e.getMessage());
        
        return ApiResult.failure(ErrorCode.PARAM_ERROR, "数字格式错误: " + e.getMessage(), errorData);
    }

    /**
     * 处理数组越界异常
     */
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ApiResult<Object> handleArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException e, HttpServletRequest request) {
        log.error("数组越界异常", e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        errorData.put("exception", "ArrayIndexOutOfBoundsException");
        
        return ApiResult.failure(ErrorCode.SYSTEM_ERROR, "系统内部错误（数组越界）", errorData);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常", e);
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        errorData.put("exception", e.getClass().getName());
        errorData.put("message", e.getMessage());
        
        // 生产环境隐藏详细错误信息
        String errorMessage = "系统内部错误";
        if (isDevelopmentEnvironment()) {
            errorMessage = e.getMessage();
        }
        
        return ApiResult.failure(ErrorCode.SYSTEM_ERROR, errorMessage, errorData);
    }

    /**
     * 判断是否是开发环境
     */
    private boolean isDevelopmentEnvironment() {
        String profile = System.getProperty("spring.profiles.active");
        return profile != null && profile.contains("dev");
    }

    /**
     * 记录异常日志
     */
    private void logException(Exception e, HttpServletRequest request) {
        String requestInfo = String.format("%s %s from %s", 
                request.getMethod(), 
                request.getRequestURI(), 
                request.getRemoteAddr());
        
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            log.warn("业务异常 [{}] - {}: {}", be.getCode(), be.getMessage(), requestInfo);
        } else if (e instanceof MethodArgumentNotValidException || 
                   e instanceof BindException || 
                   e instanceof ConstraintViolationException) {
            log.warn("参数验证异常: {} - {}", e.getMessage(), requestInfo);
        } else {
            log.error("系统异常: {} - {}", e.getClass().getSimpleName(), requestInfo, e);
        }
    }

    /**
     * 构建错误响应数据
     */
    private Map<String, Object> buildErrorData(Exception e, HttpServletRequest request) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("timestamp", System.currentTimeMillis());
        errorData.put("path", request.getRequestURI());
        errorData.put("method", request.getMethod());
        errorData.put("query", request.getQueryString());
        
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            errorData.put("code", be.getCode());
            errorData.put("module", be.getModuleCode());
            errorData.put("detail", be.getDetailCode());
            
            if (be.getData() != null) {
                errorData.put("data", be.getData());
            }
        }
        
        // 开发环境显示更多信息
        if (isDevelopmentEnvironment()) {
            errorData.put("exception", e.getClass().getName());
            errorData.put("message", e.getMessage());
            
            // 添加堆栈跟踪（限制深度）
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                int depth = Math.min(stackTrace.length, 5);
                String[] simplifiedTrace = new String[depth];
                for (int i = 0; i < depth; i++) {
                    simplifiedTrace[i] = stackTrace[i].toString();
                }
                errorData.put("stackTrace", simplifiedTrace);
            }
        }
        
        return errorData;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取用户代理信息
     */
    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 获取请求ID（从header或生成）
     */
    private String getRequestId(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isEmpty()) {
            requestId = java.util.UUID.randomUUID().toString();
        }
        return requestId;
    }
}