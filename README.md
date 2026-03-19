# 🍎 健康餐饮AI平台 - Spring Boot后端项目

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 项目概述
**AI驱动的健康餐饮SaaS平台** - 基于Spring Boot的完整电商系统，专注校园和工厂区市场，提供智能健康餐饮解决方案。

## 🎯 核心特性

### 🧠 AI智能系统
- **健康评分算法**: 基于营养成分的智能评分系统
- **个性化推荐**: AI驱动的健康食品推荐引擎
- **营养分析**: 实时营养计算和健康建议
- **趋势预测**: 基于用户行为的智能预测

### 🏪 完整电商功能
- **多商家管理**: 商家入驻、审核、管理平台
- **商品管理**: 全生命周期商品管理系统
- **订单系统**: 完整的订单创建、支付、配送流程
- **库存管理**: 实时库存跟踪和预警系统

### 🚀 高性能架构
- **Redis缓存**: 多级缓存策略，提升系统性能
- **数据库优化**: 索引优化和查询性能调优
- **异步处理**: 批量操作和异步任务处理
- **分布式锁**: 保证数据一致性和并发安全

### 🔒 安全可靠
- **JWT认证**: 安全的身份认证和授权机制
- **数据加密**: 敏感数据加密和脱敏处理
- **权限控制**: 细粒度的角色和权限管理
- **审计日志**: 完整的操作日志和审计跟踪

## 📊 项目进度: 85%完成

### ✅ 已完成功能
- **实体层**: 10个核心实体类 (100%)
- **数据访问层**: 5个Repository接口 (100%)
- **服务层**: 6个核心服务类 (80%)
- **通用工具层**: 完整的工具类和异常处理 (100%)

### 📈 项目统计
- **Java文件**: 38个
- **代码行数**: 18,338行
- **提交次数**: 6次
- **开发时间**: 约6小时

## 🏗️ 技术架构

### 后端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| **Spring Boot** | 3.x | 后端框架 |
| **Spring Data JPA** | 3.x | 数据访问 |
| **Spring Security** | 6.x | 安全认证 |
| **MySQL** | 8.0 | 主数据库 |
| **Redis** | 7.0 | 缓存和会话 |
| **JWT** | 0.11.5 | 身份认证 |
| **Lombok** | 1.18 | 代码简化 |
| **Swagger** | 3.0 | API文档 |

### 项目结构
```
healthy-food-platform/
├── backend/                          # Spring Boot后端
│   ├── src/main/java/com/healthyfood/
│   │   ├── entity/                  # 实体层 (10个实体)
│   │   │   ├── User.java           # 用户实体
│   │   │   ├── Shop.java           # 商家实体
│   │   │   ├── Product.java        # 商品实体
│   │   │   ├── Order.java          # 订单实体
│   │   │   └── ... (共10个)
│   │   ├── repository/             # 数据访问层 (5个Repository)
│   │   ├── service/                # 服务层 (6个Service)
│   │   │   ├── UserService.java    # 用户服务
│   │   │   ├── ShopService.java    # 商家服务
│   │   │   ├── ProductService.java # 商品服务
│   │   │   ├── OrderService.java   # 订单服务
│   │   │   ├── RedisService.java   # 缓存服务
│   │   │   └── RecommendationService.java # AI推荐服务
│   │   ├── vo/                     # 视图对象层 (10个VO)
│   │   ├── common/                 # 通用工具
│   │   └── exception/              # 异常处理
│   └── pom.xml                     # Maven配置
├── docs/                           # 项目文档
├── .github/workflows/              # GitHub Actions CI/CD
├── scripts/                        # 自动化脚本
└── README.md                       # 项目说明
```

## 🚀 快速开始

### 环境要求
- **Java**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 7.0+

### 安装步骤
1. **克隆项目**
   ```bash
   git clone https://github.com/YOUR_USERNAME/healthy-food-platform.git
   cd healthy-food-platform
   ```

2. **配置数据库**
   ```bash
   # 创建数据库
   mysql -u root -p -e "CREATE DATABASE healthy_food;"
   
   # 导入初始数据 (如果需要)
   # mysql -u root -p healthy_food < backend/src/main/resources/schema.sql
   ```

3. **配置应用**
   ```bash
   cd backend
   cp src/main/resources/application.example.yml src/main/resources/application.yml
   # 编辑application.yml，配置数据库和Redis连接
   ```

4. **构建项目**
   ```bash
   mvn clean package
   ```

5. **运行应用**
   ```bash
   java -jar target/healthy-food-backend-1.0.0.jar
   ```

6. **访问应用**
   - API文档: http://localhost:8080/swagger-ui.html
   - 健康检查: http://localhost:8080/actuator/health

## 📚 API文档

### 主要API端点
| 模块 | 端点 | 方法 | 描述 |
|------|------|------|------|
| **用户** | `/api/users/register` | POST | 用户注册 |
| **用户** | `/api/users/login` | POST | 用户登录 |
| **商家** | `/api/shops/register` | POST | 商家注册 |
| **商品** | `/api/products` | GET | 商品列表 |
| **商品** | `/api/products/{id}` | GET | 商品详情 |
| **订单** | `/api/orders` | POST | 创建订单 |
| **订单** | `/api/orders/{id}` | GET | 订单详情 |
| **推荐** | `/api/recommendations` | GET | 获取推荐 |

## 🔧 开发指南

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用Lombok减少样板代码
- 统一的异常处理机制
- 完整的日志记录

### 提交规范
使用Conventional Commits格式:
- `feat:` 新功能
- `fix:` 修复bug
- `docs:` 文档更新
- `style:` 代码格式
- `refactor:` 代码重构
- `test:` 测试相关
- `chore:` 构建过程或辅助工具

### 测试要求
- 单元测试覆盖率 > 80%
- 集成测试覆盖主要业务流程
- 性能测试关键接口

## 📈 性能指标

### 缓存策略
- **一级缓存**: Redis分布式缓存
- **二级缓存**: 本地缓存 (Caffeine)
- **缓存穿透**: 布隆过滤器防护
- **缓存雪崩**: 随机过期时间

### 数据库优化
- 合理的索引设计
- 分页查询优化
- 批量操作减少IO
- 读写分离 (规划中)

## 🤝 贡献指南

1. Fork本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 📞 联系方式

- **项目负责人**: 斌哥
- **问题反馈**: 通过GitHub Issues
- **技术讨论**: GitHub Discussions

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

---
**开始时间**: 2026-03-13  
**当前状态**: 🚀 开发进行中 (85%完成)  
**最新更新**: 2026-03-19