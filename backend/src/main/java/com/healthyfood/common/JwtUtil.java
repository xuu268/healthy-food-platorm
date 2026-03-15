package com.healthyfood.common;

import com.healthyfood.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT密钥
     */
    private final SecretKey secretKey;

    /**
     * 令牌过期时间（毫秒）
     */
    @Value("${app.jwt.expiration:86400000}")
    private long expiration;

    /**
     * 刷新令牌过期时间（毫秒）
     */
    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    /**
     * 发行人
     */
    @Value("${app.jwt.issuer:healthy-food-platform}")
    private String issuer;

    /**
     * 构造方法
     */
    public JwtUtil(@Value("${app.jwt.secret:healthy-food-platform-secret-key-2026}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username, Map<String, Object> claims) {
        return generateToken(userId, username, claims, expiration);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return generateToken(userId, username, claims, refreshExpiration);
    }

    /**
     * 生成令牌
     */
    private String generateToken(Long userId, String username, Map<String, Object> claims, long expiration) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        
        // 设置标准声明
        claims.put("userId", userId);
        claims.put("username", username);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object userIdObj = claims.get("userId");
        
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        } else if (userIdObj instanceof String) {
            try {
                return Long.parseLong((String) userIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException(ErrorCode.TOKEN_INVALID, "令牌中的用户ID格式错误");
            }
        }
        
        throw new BusinessException(ErrorCode.TOKEN_INVALID, "令牌中未找到用户ID");
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从令牌中获取声明
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("令牌已过期: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, "令牌已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的令牌: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "不支持的令牌格式");
        } catch (MalformedJwtException e) {
            log.warn("令牌格式错误: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "令牌格式错误");
        } catch (SignatureException e) {
            log.warn("令牌签名错误: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "令牌签名错误");
        } catch (IllegalArgumentException e) {
            log.warn("令牌参数错误: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "令牌参数错误");
        } catch (Exception e) {
            log.error("解析令牌时发生错误", e);
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "解析令牌时发生错误");
        }
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * 获取令牌过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (BusinessException e) {
            return true;
        }
    }

    /**
     * 检查令牌是否即将过期（在指定时间内）
     */
    public boolean isTokenAboutToExpire(String token, long milliseconds) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            Date threshold = new Date(now.getTime() + milliseconds);
            return expiration.before(threshold);
        } catch (BusinessException e) {
            return true;
        }
    }

    /**
     * 获取令牌剩余有效时间（毫秒）
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (BusinessException e) {
            return 0;
        }
    }

    /**
     * 获取令牌类型
     */
    public String getTokenType(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object type = claims.get("type");
            return type != null ? type.toString() : "access";
        } catch (BusinessException e) {
            return "invalid";
        }
    }

    /**
     * 检查是否是访问令牌
     */
    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    /**
     * 检查是否是刷新令牌
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * 从令牌中获取所有声明
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return new HashMap<>(claims);
    }

    /**
     * 从令牌中获取指定声明
     */
    public Object getClaimFromToken(String token, String claimName) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(claimName);
    }

    /**
     * 刷新访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        if (!isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "不是有效的刷新令牌");
        }
        
        if (isTokenExpired(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, "刷新令牌已过期");
        }
        
        Claims claims = getClaimsFromToken(refreshToken);
        Long userId = getUserIdFromToken(refreshToken);
        String username = getUsernameFromToken(refreshToken);
        
        // 移除刷新令牌特有的声明
        Map<String, Object> newClaims = new HashMap<>(claims);
        newClaims.remove("type");
        newClaims.remove("exp");
        newClaims.remove("iat");
        
        return generateAccessToken(userId, username, newClaims);
    }

    /**
     * 生成双令牌（访问令牌 + 刷新令牌）
     */
    public Map<String, String> generateTokenPair(Long userId, String username, Map<String, Object> claims) {
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("accessToken", generateAccessToken(userId, username, claims));
        tokenPair.put("refreshToken", generateRefreshToken(userId, username));
        return tokenPair;
    }

    /**
     * 验证令牌并返回用户ID（不抛出异常）
     */
    public Long validateTokenSilently(String token) {
        try {
            if (validateToken(token)) {
                return getUserIdFromToken(token);
            }
        } catch (Exception e) {
            // 静默处理
        }
        return null;
    }

    /**
     * 获取令牌简略信息（用于日志）
     */
    public String getTokenBriefInfo(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Long userId = getUserIdFromToken(token);
            String username = getUsernameFromToken(token);
            Date expiration = claims.getExpiration();
            
            return String.format("Token{userId=%d, username='%s', expires=%s}", 
                    userId, username, expiration);
        } catch (Exception e) {
            return "Token{invalid}";
        }
    }

    /**
     * 检查令牌是否有效且未过期
     */
    public boolean isValidAndNotExpired(String token) {
        return validateToken(token) && !isTokenExpired(token);
    }

    /**
     * 从Authorization头中提取令牌
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(Constants.TOKEN_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(Constants.TOKEN_PREFIX.length()).trim();
    }

    /**
     * 构建完整的Authorization头
     */
    public String buildAuthorizationHeader(String token) {
        return Constants.TOKEN_PREFIX + token;
    }

    /**
     * 生成测试令牌（仅用于测试环境）
     */
    public String generateTestToken(Long userId, String username, long customExpiration) {
        if (!isTestEnvironment()) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "仅测试环境可用");
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("test", true);
        return generateToken(userId, username, claims, customExpiration);
    }

    /**
     * 判断是否是测试环境
     */
    private boolean isTestEnvironment() {
        String profile = System.getProperty("spring.profiles.active");
        return profile != null && (profile.contains("test") || profile.contains("dev"));
    }

    /**
     * 获取令牌签发时间
     */
    public Date getIssuedAtFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuedAt();
    }

    /**
     * 获取令牌签发者
     */
    public String getIssuerFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getIssuer();
    }

    /**
     * 检查令牌是否由本系统签发
     */
    public boolean isIssuedByThisSystem(String token) {
        try {
            String issuer = getIssuerFromToken(token);
            return this.issuer.equals(issuer);
        } catch (Exception e) {
            return false;
        }
    }
}