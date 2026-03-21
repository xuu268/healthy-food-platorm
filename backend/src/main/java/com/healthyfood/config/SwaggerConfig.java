package com.healthyfood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Swagger API文档配置
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 创建API文档
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * API基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("健康餐饮平台 API文档")
                .description("健康餐饮平台后端API接口文档")
                .version("1.0.0")
                .contact(new Contact("健康餐饮团队", "https://healthyfood.ai", "contact@healthyfood.ai"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("https://healthyfood.ai/terms")
                .build();
    }

    /**
     * 安全认证方案
     */
    private List<SecurityScheme> securitySchemes() {
        return Arrays.asList(
                new ApiKey("Authorization", "Authorization", "header"),
                new ApiKey("X-API-Key", "X-API-Key", "header")
        );
    }

    /**
     * 安全上下文配置
     */
    private List<SecurityContext> securityContexts() {
        return Arrays.asList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("/api/.*"))
                        .build()
        );
    }

    /**
     * 默认的认证引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        
        return Arrays.asList(
                new SecurityReference("Authorization", authorizationScopes),
                new SecurityReference("X-API-Key", authorizationScopes)
        );
    }

    /**
     * 用户API分组
     */
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户API")
                .apiInfo(userApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.regex("/api/users.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 用户API信息
     */
    private ApiInfo userApiInfo() {
        return new ApiInfoBuilder()
                .title("用户管理 API")
                .description("用户注册、登录、个人信息管理等接口")
                .version("1.0.0")
                .build();
    }

    /**
     * 商家API分组
     */
    @Bean
    public Docket shopApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商家API")
                .apiInfo(shopApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.regex("/api/shops.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 商家API信息
     */
    private ApiInfo shopApiInfo() {
        return new ApiInfoBuilder()
                .title("商家管理 API")
                .description("商家注册、商品管理、订单处理等接口")
                .version("1.0.0")
                .build();
    }

    /**
     * 商品API分组
     */
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商品API")
                .apiInfo(productApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.regex("/api/products.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 商品API信息
     */
    private ApiInfo productApiInfo() {
        return new ApiInfoBuilder()
                .title("商品管理 API")
                .description("商品CRUD、搜索、分类管理等接口")
                .version("1.0.0")
                .build();
    }

    /**
     * 订单API分组
     */
    @Bean
    public Docket orderApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("订单API")
                .apiInfo(orderApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.regex("/api/orders.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 订单API信息
     */
    private ApiInfo orderApiInfo() {
        return new ApiInfoBuilder()
                .title("订单管理 API")
                .description("订单创建、状态管理、支付配送等接口")
                .version("1.0.0")
                .build();
    }

    /**
     * 推荐API分组
     */
    @Bean
    public Docket recommendationApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("推荐API")
                .apiInfo(recommendationApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.regex("/api/recommendations.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 推荐API信息
     */
    private ApiInfo recommendationApiInfo() {
        return new ApiInfoBuilder()
                .title("推荐系统 API")
                .description("个性化推荐、健康饮食推荐等接口")
                .version("1.0.0")
                .build();
    }

    /**
     * 管理后台API分组
     */
    @Bean
    public Docket adminApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理后台API")
                .apiInfo(adminApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.regex("/api/admin.*"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 管理后台API信息
     */
    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("管理后台 API")
                .description("系统管理、数据统计、权限管理等接口")
                .version("1.0.0")
                .build();
    }

    /**
     * 公共API分组（无需认证）
     */
    @Bean
    public Docket publicApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("公共API")
                .apiInfo(publicApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.healthyfood.controller"))
                .paths(PathSelectors.regex("(/api/users/(register|login))|(/api/shops/(register|login))"))
                .build();
    }

    /**
     * 公共API信息
     */
    private ApiInfo publicApiInfo() {
        return new ApiInfoBuilder()
                .title("公共 API")
                .description("无需认证的公共接口")
                .version("1.0.0")
                .build();
    }
}