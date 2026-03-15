# 健康餐饮平台CI/CD配置指南

基于github-automation技能的最佳实践，我们为健康餐饮平台配置了完整的CI/CD流程。

## 🏗️ 架构概述

### CI/CD流水线设计
```
代码提交 → 代码质量检查 → 构建测试 → 安全扫描 → 容器构建 → 环境部署 → 监控通知
```

### 环境策略
- **开发环境 (dev)**: 用于功能开发和测试
- **测试环境 (staging)**: 用于集成测试和预发布
- **生产环境 (production)**: 用于正式用户服务

## 📋 GitHub Actions工作流

### 1. Java应用CI/CD流水线 (`java-ci-cd.yml`)
**触发条件**: 后端代码变更

**工作阶段**:
1. **代码质量检查**: SonarCloud代码分析
2. **构建和测试**: 单元测试、集成测试、覆盖率报告
3. **安全扫描**: OWASP依赖检查
4. **Docker构建**: 多阶段构建镜像
5. **数据库迁移**: Flyway数据库迁移
6. **环境部署**: 开发 → 测试 → 生产

**关键特性**:
- 智能缓存加速构建
- 并行执行提高效率
- 质量门禁确保代码质量
- 安全扫描防止漏洞

### 2. 前端CI/CD流水线 (`frontend-ci-cd.yml`)
**触发条件**: 前端代码变更

**工作阶段**:
1. **代码质量和安全检查**: ESLint、TypeScript、npm audit
2. **构建和测试**: 单元测试、覆盖率报告
3. **E2E测试**: Playwright端到端测试
4. **性能监控**: Lighthouse性能测试
5. **环境部署**: S3 + CloudFront部署

### 3. PR自动化工作流 (`pr-automation.yml`)
**触发条件**: PR创建和更新

**自动化功能**:
1. **智能标签分配**: 根据代码变更自动添加标签
2. **代码质量检查**: CodeQL、Trivy安全扫描
3. **测试覆盖率检查**: JaCoCo覆盖率验证
4. **依赖更新检查**: 自动检测过时依赖
5. **合并要求检查**: 确保PR满足合并条件
6. **Dependabot自动合并**: 安全依赖更新自动合并

## 🔧 配置要求

### GitHub Secrets配置
```yaml
# 必需配置
DOCKER_USERNAME: Docker Hub用户名
DOCKER_PASSWORD: Docker Hub密码
SONAR_TOKEN: SonarCloud令牌
SNYK_TOKEN: Snyk安全令牌

# 数据库配置
DB_URL: 数据库连接URL
DB_USERNAME: 数据库用户名
DB_PASSWORD: 数据库密码

# AWS部署配置
AWS_ACCESS_KEY_ID: AWS访问密钥
AWS_SECRET_ACCESS_KEY: AWS密钥
AWS_REGION: AWS区域

# 通知配置
SLACK_WEBHOOK_URL: Slack通知Webhook
```

### GitHub Variables配置
```yaml
# 环境变量
STAGING_S3_BUCKET: 测试环境S3桶
PRODUCTION_S3_BUCKET: 生产环境S3桶
STAGING_CLOUDFRONT_DISTRIBUTION_ID: 测试环境CDN
PRODUCTION_CLOUDFRONT_DISTRIBUTION_ID: 生产环境CDN
```

## 🚀 快速开始

### 1. 初始设置
```bash
# 克隆仓库
git clone https://github.com/your-org/healthy-food-platform.git
cd healthy-food-platform

# 配置GitHub Secrets和Variables
# 在Git仓库设置中配置上述所有Secrets和Variables
```

### 2. 首次部署
```bash
# 推送代码触发CI/CD
git push origin main

# 查看工作流状态
# 访问 https://github.com/your-org/healthy-food-platform/actions
```

### 3. 开发工作流
```bash
# 创建功能分支
git checkout -b feature/new-feature

# 开发并提交代码
git add .
git commit -m "feat: 添加新功能"

# 创建PR
git push origin feature/new-feature
# 在GitHub创建Pull Request

# PR将自动:
# 1. 添加标签 (backend, feature, size/m等)
# 2. 运行代码检查
# 3. 执行测试
# 4. 检查合并要求
```

## 📊 监控和告警

### 内置监控
1. **构建状态监控**: GitHub Actions状态页面
2. **测试覆盖率趋势**: Codecov报告
3. **代码质量趋势**: SonarCloud仪表板
4. **安全漏洞监控**: Snyk仪表板
5. **性能监控**: Lighthouse报告

### 告警配置
- **构建失败**: Slack通知
- **安全漏洞**: 安全团队通知
- **性能下降**: 开发团队通知
- **部署成功**: 团队庆祝通知

## 🔒 安全最佳实践

### 代码安全
1. **依赖扫描**: 每次构建扫描依赖漏洞
2. **代码扫描**: CodeQL静态分析
3. **容器扫描**: Trivy容器漏洞扫描
4. **密钥检测**: 防止硬编码密钥

### 部署安全
1. **最小权限原则**: 每个环境独立权限
2. **密钥管理**: GitHub Secrets安全存储
3. **网络隔离**: 环境间网络隔离
4. **审计日志**: 所有操作记录审计

## 📈 性能优化

### 构建性能
1. **依赖缓存**: Maven和npm依赖缓存
2. **并行执行**: 工作流阶段并行
3. **增量构建**: 只构建变更部分
4. **镜像缓存**: Docker构建缓存

### 部署性能
1. **蓝绿部署**: 零停机部署
2. **回滚机制**: 快速故障恢复
3. **健康检查**: 自动健康监控
4. **自动扩缩**: 基于负载自动扩缩

## 🛠️ 故障排除

### 常见问题

#### 构建失败
```bash
# 检查日志
查看GitHub Actions详细日志

# 本地复现
mvn clean test  # 后端
npm test        # 前端

# 依赖问题
mvn dependency:resolve  # 后端
npm ci                 # 前端
```

#### 部署失败
```bash
# 检查Kubernetes状态
kubectl get pods -n healthy-food-dev
kubectl describe pod <pod-name>
kubectl logs <pod-name>

# 检查网络
kubectl get ingress -n healthy-food-dev
curl -I https://dev.healthy-food.ai
```

#### 测试失败
```bash
# 运行单个测试
mvn test -Dtest=SpecificTestClass
npm test -- --testNamePattern="specific test"

# 调试模式
mvn test -Dmaven.surefire.debug
npm test -- --inspect-brk
```

### 性能问题
1. **构建时间过长**: 检查缓存配置
2. **测试时间过长**: 优化测试用例
3. **部署时间过长**: 检查镜像大小
4. **资源不足**: 调整Kubernetes资源限制

## 🔄 维护和更新

### 定期维护任务
1. **依赖更新**: 每月检查并更新依赖
2. **安全补丁**: 及时应用安全更新
3. **配置优化**: 根据使用情况优化配置
4. **文档更新**: 保持文档最新

### 版本升级
```bash
# 升级Spring Boot
mvn versions:set -DnewVersion=2.7.x

# 升级Node.js
更新.github/workflows中的node-version

# 升级Kubernetes配置
更新apiVersion和资源配置
```

## 🎯 最佳实践

### 开发实践
1. **小步提交**: 频繁提交小变更
2. **描述性提交**: 清晰的提交消息
3. **测试驱动**: 先写测试再写代码
4. **代码审查**: 所有代码必须经过审查

### 部署实践
1. **渐进式部署**: 先小范围再全量
2. **监控先行**: 部署前确保监控就绪
3. **回滚计划**: 总是有回滚方案
4. **文档更新**: 部署后更新文档

### 运维实践
1. **自动化一切**: 减少手动操作
2. **监控告警**: 及时发现问题
3. **容量规划**: 提前规划资源
4. **灾难恢复**: 定期测试恢复流程

## 📚 相关资源

### 文档链接
- [GitHub Actions文档](https://docs.github.com/en/actions)
- [SonarCloud文档](https://docs.sonarcloud.io/)
- [Snyk文档](https://docs.snyk.io/)
- [Kubernetes文档](https://kubernetes.io/docs/)

### 工具链接
- [Docker Hub](https://hub.docker.com/)
- [AWS Console](https://aws.amazon.com/console/)
- [Slack API](https://api.slack.com/)
- [Let's Encrypt](https://letsencrypt.org/)

### 社区支持
- [GitHub Discussions](https://github.com/your-org/healthy-food-platform/discussions)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/healthy-food-platform)
- [Discord社区](https://discord.gg/your-community)

---

**最后更新**: 2026-03-15  
**维护团队**: 健康餐饮平台开发团队  
**状态**: ✅ 生产就绪