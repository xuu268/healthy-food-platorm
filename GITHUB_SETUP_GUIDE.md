# 🚀 健康餐饮平台GitHub仓库配置指南

## 📋 准备工作

### 1. 创建GitHub仓库
1. 登录 [GitHub](https://github.com)
2. 点击右上角 "+" → "New repository"
3. 填写仓库信息:
   - **Repository name**: `healthy-food-platform` (或你喜欢的名称)
   - **Description**: `AI驱动的健康餐饮SaaS平台 - 路边摊点餐系统升级版`
   - **Visibility**: Public (推荐) 或 Private
   - 勾选 "Add a README file" (可选)
   - 点击 "Create repository"

### 2. 获取GitHub Token
1. 访问 [GitHub Token设置](https://github.com/settings/tokens)
2. 点击 "Generate new token"
3. 选择权限:
   - `repo` (完全控制仓库)
   - `workflow` (如果需要CI/CD)
4. 点击 "Generate token"
5. **复制并保存token** (只显示一次!)

## 🔧 本地配置

### 方法1: 使用自动化脚本 (推荐)
```bash
# 1. 编辑推送脚本
nano scripts/push-to-github.sh

# 2. 修改以下配置:
# GITHUB_USERNAME="你的GitHub用户名"
# GITHUB_REPO_NAME="你的仓库名称" 
# GITHUB_TOKEN="你的GitHub Token"

# 3. 运行脚本
bash scripts/push-to-github.sh
```

### 方法2: 手动配置
```bash
# 1. 进入项目目录
cd /home/node/.openclaw/workspace/healthy-food-platform

# 2. 配置Git用户信息
git config --local user.name "你的名字"
git config --local user.email "你的邮箱"

# 3. 添加远程仓库
git remote add origin https://github.com/你的用户名/你的仓库名.git

# 4. 推送代码
git push -u origin master
```

## 📊 项目内容概览

### 已完成的代码
```
✅ 后端实体类 (10个)
   - User.java (用户实体，包含健康计算)
   - Shop.java (商家实体，评分系统)
   - Product.java (商品实体，营养分析)
   - Order.java (订单实体，状态管理)
   - OrderItem.java (订单项实体)
   - Creator.java (创作者实体)
   - Content.java (内容实体)
   - HealthRecord.java (健康记录实体)
   - PointsRecord.java (积分记录实体)
   - PaymentRecord.java (支付记录实体)

✅ 通用工具类
   - Constants.java (常量定义)
   - ErrorCode.java (错误码)
   - ApiResult.java (API统一响应)
   - JwtUtil.java (JWT工具)
   - BusinessException.java (业务异常)
   - GlobalExceptionHandler.java (全局异常处理)

✅ AI推荐系统
   - RecommendationService.java (核心推荐服务)
   - RecommendationRequest.java (推荐请求模型)
   - RecommendationResponse.java (推荐响应模型)
   - RecommendationItem.java (推荐项模型)

✅ 项目文档
   - AI_RECOMMENDATION_INNOVATION.md (AI推荐创新方案)
   - AI_RECOMMENDATION_IMPLEMENTATION.md (实施计划)
   - CI_CD_SETUP.md (CI/CD配置指南)
   - DEVELOPMENT_PROGRESS.md (开发进度)

✅ CI/CD配置
   - java-ci-cd.yml (Java应用流水线)
   - frontend-ci-cd.yml (前端流水线)
   - pr-automation.yml (PR自动化)

✅ 基础设施
   - Dockerfile (多阶段构建)
   - k8s/development/deployment.yaml (K8s配置)
   - pom.xml (Maven配置)
   - application.yml (Spring Boot配置)
```

### 提交历史
```
1. ec012dd - 添加健康餐饮AI推荐系统核心功能
2. 5441c4c - 添加CI/CD配置文档
3. f70bfff - 添加CI/CD配置和自动化工作流
4. 932da46 - 初始提交: 健康餐饮AI平台基础架构
```

## 🌐 仓库设置建议

### 1. 分支保护规则
1. 进入仓库 Settings → Branches
2. 点击 "Add branch protection rule"
3. 设置规则:
   - Branch name pattern: `main` 或 `master`
   - Require pull request reviews before merging
   - Require status checks to pass
   - Include administrators

### 2. GitHub Actions设置
1. 进入仓库 Settings → Actions → General
2. 配置:
   - Actions permissions: "Allow all actions"
   - Workflow permissions: "Read and write permissions"
   - Fork pull request workflows: "Require approval"

### 3. 配置Secrets (用于CI/CD)
进入仓库 Settings → Secrets and variables → Actions
添加以下Secrets:
- `DOCKER_USERNAME`: Docker Hub用户名
- `DOCKER_PASSWORD`: Docker Hub密码
- `SONAR_TOKEN`: SonarCloud令牌
- `AWS_ACCESS_KEY_ID`: AWS访问密钥
- `AWS_SECRET_ACCESS_KEY`: AWS密钥
- `SLACK_WEBHOOK_URL`: Slack通知Webhook

## 🚀 首次推送后操作

### 1. 验证推送成功
```bash
# 查看远程仓库
git remote -v

# 拉取最新代码
git pull origin master

# 查看提交历史
git log --oneline --graph
```

### 2. 触发CI/CD测试
1. 访问仓库 Actions 页面
2. 查看工作流运行状态
3. 确保所有检查通过

### 3. 邀请团队成员
1. 进入仓库 Settings → Collaborators
2. 添加团队成员GitHub用户名
3. 设置权限级别 (Write/Admin)

## 📈 项目发展路线

### 短期计划 (1个月内)
1. **完善后端服务层**
   - 实现用户服务API
   - 实现菜品服务API
   - 实现订单服务API
   - 实现支付服务API

2. **前端开发**
   - 商户管理后台
   - 用户点餐小程序
   - 运营管理平台

3. **AI算法优化**
   - 推荐算法训练和优化
   - 营养计算准确性提升
   - 健康评估模型完善

### 中期计划 (3个月内)
1. **系统集成**
   - 第三方支付集成
   - 地图服务集成
   - 消息推送服务

2. **性能优化**
   - 数据库优化
   - 缓存策略优化
   - 负载均衡配置

3. **功能扩展**
   - 社交功能
   - 营销功能
   - 数据分析功能

### 长期计划 (6个月内)
1. **生态建设**
   - 开放平台API
   - 第三方应用市场
   - 合作伙伴生态

2. **商业化**
   - 收费模式验证
   - 市场推广
   - 用户增长

## 🔧 开发环境配置

### 后端开发环境
```bash
# 1. 安装JDK 11
sudo apt install openjdk-11-jdk

# 2. 安装Maven
sudo apt install maven

# 3. 安装MySQL
sudo apt install mysql-server

# 4. 安装Redis
sudo apt install redis-server

# 5. 运行项目
cd backend
mvn spring-boot:run
```

### 前端开发环境
```bash
# 1. 安装Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs

# 2. 安装依赖
cd frontend
npm install

# 3. 运行开发服务器
npm run dev
```

## 🆘 常见问题解决

### 问题1: 推送被拒绝
```
error: failed to push some refs to 'github.com:xxx/xxx.git'
```
**解决方案**:
```bash
# 强制推送 (首次推送)
git push -u origin master --force

# 或先拉取再推送
git pull origin master --rebase
git push origin master
```

### 问题2: 认证失败
```
remote: Invalid username or password.
```
**解决方案**:
1. 使用Token代替密码
2. 更新远程仓库URL:
```bash
git remote set-url origin https://你的token@github.com/用户名/仓库名.git
```

### 问题3: 大文件推送失败
```
remote: error: File xxx is 105.00 MB; this exceeds GitHub's file size limit of 100.00 MB
```
**解决方案**:
1. 使用Git LFS管理大文件
2. 或从仓库中移除大文件

## 📞 支持与帮助

### 技术问题
- **Git相关问题**: 查看 [Git官方文档](https://git-scm.com/doc)
- **GitHub问题**: 查看 [GitHub帮助](https://docs.github.com)
- **项目问题**: 查看项目文档或创建Issue

### 联系开发者
如果你需要进一步的技术支持或定制开发，可以通过以下方式联系:

**项目维护者**: 进化之魂 🌀  
**项目状态**: 🚀 活跃开发中  
**最后更新**: 2026-03-15

---

**重要提示**: 在推送代码前，请确保:
1. ✅ 已创建GitHub仓库
2. ✅ 已获取GitHub Token
3. ✅ 已备份重要数据
4. ✅ 已测试本地代码运行正常

**祝你项目顺利上线!** 🎉