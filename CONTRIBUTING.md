# 🤝 贡献指南 - 健康餐饮平台

欢迎 `traesolo` 加入开发！本指南将帮助您快速上手项目。

## 🎯 项目概述
**健康餐饮AI平台** - 基于Spring Boot的完整电商系统，专注校园和工厂区市场。

## 📊 当前进度
- **总体进度**: 87%完成
- **Java文件**: 44个
- **代码行数**: 约20,000行
- **GitHub**: https://github.com/xuu268/healthy-food-platorm

## 🏗️ 技术栈
- **后端**: Spring Boot 3.x + MySQL + Redis
- **安全**: JWT + Spring Security
- **API文档**: Swagger/OpenAPI
- **CI/CD**: GitHub Actions

## 📁 项目结构
```
healthy-food-platform/
├── backend/                    # Spring Boot后端
│   ├── src/main/java/com/healthyfood/
│   │   ├── entity/            # 实体层 (10个实体，100%完成)
│   │   ├── repository/        # 数据访问层 (5个接口，100%完成)
│   │   ├── service/           # 服务层 (6个服务，80%完成)
│   │   ├── vo/                # 视图对象层 (16个VO，70%完成)
│   │   ├── controller/        # 控制器层 (0%完成，待开发)
│   │   ├── common/            # 通用工具 (100%完成)
│   │   └── exception/         # 异常处理 (100%完成)
│   └── pom.xml               # Maven配置
├── docs/                      # 项目文档
├── .github/workflows/         # GitHub Actions CI/CD
└── README.md                  # 项目说明
```

## 🎯 分工建议

### 我 (当前开发者) 负责:
1. **核心服务层完善**
   - 完成OrderService剩余功能
   - 优化Redis缓存策略
   - 完善异常处理机制

2. **VO对象层完成**
   - 剩余Order相关VO (4个)
   - Shop相关VO补充 (3个)

3. **架构优化**
   - 数据库性能优化
   - 安全加固
   - 代码质量检查

### traesolo 负责 (建议):
1. **控制器层开发** (优先级最高)
   - UserController (用户API)
   - ShopController (商家API)
   - ProductController (商品API)
   - OrderController (订单API)
   - RecommendationController (推荐API)

2. **API文档和测试**
   - Swagger/OpenAPI配置
   - 单元测试编写
   - API接口测试

3. **前端对接准备**
   - 统一响应格式
   - 错误处理规范
   - 接口文档生成

## 🔧 开发环境

### 环境要求
- **Java**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 7.0+

### 本地运行
```bash
# 克隆项目
git clone https://github.com/xuu268/healthy-food-platorm.git
cd healthy-food-platform

# 配置数据库
# 创建数据库: healthy_food
# 编辑: backend/src/main/resources/application.yml

# 构建运行
cd backend
mvn spring-boot:run
```

## 📝 代码规范

### 1. 命名规范
- **类名**: 大驼峰，如 `UserController`
- **方法名**: 小驼峰，如 `getUserById`
- **变量名**: 小驼峰，如 `userName`
- **常量**: 全大写加下划线，如 `MAX_RETRY_COUNT`

### 2. 包结构
```
com.healthyfood.
├── controller      # 控制器层
├── service         # 服务层  
├── repository      # 数据访问层
├── entity          # 实体层
├── vo              # 视图对象层
├── common          # 通用工具
└── exception       # 异常处理
```

### 3. 提交规范 (Conventional Commits)
```
feat:    新功能
fix:     修复bug
docs:    文档更新
style:   代码格式
refactor:代码重构
test:    测试相关
chore:   构建过程或辅助工具
```

示例:
```bash
git commit -m "feat: 添加用户注册API接口"
git commit -m "fix: 修复订单状态更新bug"
```

## 🔄 协作流程

### 1. 分支策略
- `master`: 生产就绪代码
- `dev`: 开发集成分支
- `feature/*`: 功能开发分支
- `fix/*`: bug修复分支

### 2. 开发流程
```bash
# 1. 同步最新代码
git checkout dev
git pull origin dev

# 2. 创建功能分支
git checkout -b feature/user-controller

# 3. 开发并提交
git add .
git commit -m "feat: 添加用户控制器"

# 4. 推送到远程
git push origin feature/user-controller

# 5. 创建Pull Request
# 在GitHub上创建PR: feature/user-controller → dev
```

### 3. 代码评审
- 所有PR需要至少1人评审
- 评审重点: 代码质量、安全性、性能
- 通过后由仓库管理员合并

## 🚀 开发任务清单

### 优先级1: 控制器层 (预计3-4小时)
- [ ] **UserController** - 用户相关API
  - POST /api/users/register - 用户注册
  - POST /api/users/login - 用户登录
  - GET /api/users/{id} - 获取用户信息
  - PUT /api/users/{id} - 更新用户信息

- [ ] **ShopController** - 商家相关API
  - POST /api/shops/register - 商家注册
  - POST /api/shops/login - 商家登录
  - GET /api/shops/{id} - 获取商家信息
  - PUT /api/shops/{id} - 更新商家信息

- [ ] **ProductController** - 商品相关API
  - GET /api/products - 商品列表
  - GET /api/products/{id} - 商品详情
  - POST /api/products - 创建商品
  - PUT /api/products/{id} - 更新商品

- [ ] **OrderController** - 订单相关API
  - POST /api/orders - 创建订单
  - GET /api/orders/{id} - 订单详情
  - GET /api/users/{userId}/orders - 用户订单列表
  - PUT /api/orders/{id}/status - 更新订单状态

- [ ] **RecommendationController** - 推荐API
  - GET /api/recommendations - 获取推荐
  - GET /api/recommendations/personal - 个性化推荐

### 优先级2: API文档和测试 (预计1-2小时)
- [ ] 配置Swagger/OpenAPI
- [ ] 编写基础单元测试
- [ ] 创建API测试用例
- [ ] 生成接口文档

### 优先级3: 前端对接准备 (预计1小时)
- [ ] 统一响应格式规范
- [ ] 错误码定义文档
- [ ] 接口调用示例
- [ ] 跨域配置

## 📞 沟通协作

### 问题反馈
1. **GitHub Issues**: 功能请求、bug报告
2. **Pull Requests**: 代码变更、功能开发
3. **项目文档**: 更新开发进度和设计决策

### 进度同步
- 每日/每周进度更新
- 重大变更提前沟通
- 技术难题协作解决

## 🎯 成功标准

### 短期目标 (1-2天)
- 完成所有控制器层开发
- 实现基本API功能
- 通过基础测试

### 中期目标 (3-5天)
- 完成前后端对接
- 实现核心业务流程
- 部署测试环境

### 长期目标 (1-2周)
- 完善所有功能模块
- 性能优化和安全加固
- 准备生产部署

---

**开始协作吧！** 🚀

如果有任何问题，请随时在GitHub Issues中提出，或通过其他沟通渠道联系。

祝合作愉快！