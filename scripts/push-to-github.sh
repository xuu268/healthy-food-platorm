#!/bin/bash

# 健康餐饮平台代码推送到GitHub脚本
# 请根据你的GitHub信息修改以下配置

set -e

echo "🚀 开始推送健康餐饮平台代码到GitHub..."
echo "=========================================="

# GitHub配置 (请修改这些变量)
GITHUB_USERNAME="YOUR_GITHUB_USERNAME"      # 修改为你的GitHub用户名
GITHUB_REPO_NAME="healthy-food-platform"    # 修改为你的仓库名称
GITHUB_TOKEN="YOUR_GITHUB_TOKEN"            # 修改为你的GitHub Personal Access Token

# 检查配置
if [ "$GITHUB_USERNAME" = "YOUR_GITHUB_USERNAME" ] || [ "$GITHUB_TOKEN" = "YOUR_GITHUB_TOKEN" ]; then
    echo "❌ 请先修改脚本中的GitHub配置!"
    echo "需要修改的配置:"
    echo "1. GITHUB_USERNAME: 你的GitHub用户名"
    echo "2. GITHUB_REPO_NAME: 你的仓库名称"
    echo "3. GITHUB_TOKEN: 你的GitHub Personal Access Token"
    echo ""
    echo "如何获取GitHub Token:"
    echo "1. 访问 https://github.com/settings/tokens"
    echo "2. 点击 'Generate new token'"
    echo "3. 选择 'repo' 权限"
    echo "4. 复制生成的token"
    exit 1
fi

# 设置Git用户信息
echo "🔧 配置Git用户信息..."
git config --local user.name "HealthyFood AI Assistant"
git config --local user.email "openclaw@healthyfood.ai"

# 添加远程仓库
echo "🌐 配置远程仓库..."
GITHUB_URL="https://${GITHUB_TOKEN}@github.com/${GITHUB_USERNAME}/${GITHUB_REPO_NAME}.git"

# 检查是否已配置远程仓库
if git remote | grep -q "origin"; then
    echo "📦 更新现有远程仓库..."
    git remote set-url origin "$GITHUB_URL"
else
    echo "📦 添加新的远程仓库..."
    git remote add origin "$GITHUB_URL"
fi

# 推送代码
echo "📤 推送代码到GitHub..."
echo "分支: master"
echo "仓库: https://github.com/${GITHUB_USERNAME}/${GITHUB_REPO_NAME}"

# 强制推送（因为是新仓库）
git push -u origin master --force

echo ""
echo "🎉 代码推送成功!"
echo "=========================================="
echo "📊 项目统计:"
echo "总提交数: $(git rev-list --count HEAD)"
echo "最近提交: $(git log -1 --format=%s)"
echo "代码行数: $(find . -name "*.java" -o -name "*.md" -o -name "*.yml" -o -name "*.yaml" | xargs wc -l | tail -1 | awk '{print $1}')"
echo ""
echo "🌐 访问你的仓库:"
echo "https://github.com/${GITHUB_USERNAME}/${GITHUB_REPO_NAME}"
echo ""
echo "📁 项目结构:"
echo "├── backend/                    # Spring Boot后端"
echo "│   ├── src/main/java/com/healthyfood/"
echo "│   │   ├── entity/            # 10个实体类"
echo "│   │   ├── common/            # 通用工具类"
echo "│   │   ├── exception/         # 异常处理"
echo "│   │   ├── service/ai/        # AI推荐服务"
echo "│   │   └── vo/recommendation/ # 推荐数据模型"
echo "│   └── src/main/resources/    # 配置文件"
echo "├── docs/                      # 项目文档"
echo "│   ├── AI_RECOMMENDATION_INNOVATION.md"
echo "│   ├── AI_RECOMMENDATION_IMPLEMENTATION.md"
echo "│   ├── CI_CD_SETUP.md"
echo "│   └── DEVELOPMENT_PROGRESS.md"
echo "├── .github/workflows/         # CI/CD工作流"
echo "│   ├── java-ci-cd.yml"
echo "│   ├── frontend-ci-cd.yml"
echo "│   └── pr-automation.yml"
echo "├── k8s/                       # Kubernetes配置"
echo "├── scripts/                   # 自动化脚本"
echo "└── README.md                  # 项目说明"
echo ""
echo "🚀 下一步:"
echo "1. 访问 https://github.com/${GITHUB_USERNAME}/${GITHUB_REPO_NAME}"
echo "2. 配置GitHub Secrets (数据库、Docker、AWS等)"
echo "3. 触发CI/CD流水线测试"
echo "4. 开始团队协作开发"
echo ""
echo "💡 提示: 你可以运行 'bash scripts/push-to-github.sh' 再次推送更新"