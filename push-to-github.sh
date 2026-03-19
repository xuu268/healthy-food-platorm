#!/bin/bash

# 健康餐饮平台 - GitHub推送脚本
# 使用方法: ./push-to-github.sh YOUR_GITHUB_USERNAME

set -e

echo "🚀 健康餐饮平台 GitHub推送脚本"
echo "========================================"

# 检查参数
if [ $# -eq 0 ]; then
    echo "❌ 错误: 请提供GitHub用户名"
    echo "使用方法: $0 YOUR_GITHUB_USERNAME"
    echo "示例: $0 xboss"
    exit 1
fi

GITHUB_USERNAME=$1
REPO_NAME="healthy-food-platform"
REPO_URL="https://github.com/${GITHUB_USERNAME}/${REPO_NAME}.git"
SSH_URL="git@github.com:${GITHUB_USERNAME}/${REPO_NAME}.git"

echo "📋 项目信息:"
echo "  - GitHub用户名: ${GITHUB_USERNAME}"
echo "  - 仓库名称: ${REPO_NAME}"
echo "  - HTTPS URL: ${REPO_URL}"
echo "  - SSH URL: ${SSH_URL}"
echo ""

# 检查当前目录
if [ ! -f "pom.xml" ] && [ ! -d "backend" ]; then
    echo "❌ 错误: 请在项目根目录运行此脚本"
    echo "当前目录: $(pwd)"
    exit 1
fi

echo "📊 检查Git状态..."
git status

echo ""
echo "📦 检查提交历史..."
git log --oneline -5

echo ""
read -p "是否继续推送? (y/n): " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ 操作已取消"
    exit 0
fi

echo ""
echo "🔗 配置远程仓库..."
echo "使用HTTPS URL: ${REPO_URL}"

# 检查是否已有远程仓库
if git remote | grep -q origin; then
    echo "⚠️  已存在远程仓库 'origin'，更新URL..."
    git remote set-url origin ${REPO_URL}
else
    echo "➕ 添加远程仓库 'origin'..."
    git remote add origin ${REPO_URL}
fi

echo ""
echo "✅ 远程仓库配置完成:"
git remote -v

echo ""
echo "🚀 开始推送代码到GitHub..."
echo "分支: master"

# 尝试推送
if git push -u origin master; then
    echo ""
    echo "🎉 推送成功!"
    echo ""
    echo "📋 项目已推送到:"
    echo "  - GitHub仓库: https://github.com/${GITHUB_USERNAME}/${REPO_NAME}"
    echo "  - 提交数量: $(git rev-list --count HEAD)"
    echo "  - 最新提交: $(git log --oneline -1)"
    echo ""
    echo "🔍 查看项目:"
    echo "  - 打开浏览器访问: https://github.com/${GITHUB_USERNAME}/${REPO_NAME}"
    echo ""
    echo "📝 后续步骤:"
    echo "  1. 在GitHub仓库设置中启用GitHub Actions"
    echo "  2. 配置必要的Secrets (数据库密码、JWT密钥等)"
    echo "  3. 添加README.md项目说明"
    echo "  4. 设置分支保护规则"
else
    echo ""
    echo "❌ 推送失败!"
    echo ""
    echo "🔧 可能的原因和解决方案:"
    echo "  1. 仓库不存在 - 请在GitHub上创建仓库: ${REPO_NAME}"
    echo "  2. 认证失败 - 使用SSH密钥或访问令牌"
    echo "  3. 权限不足 - 确保你有仓库的写入权限"
    echo ""
    echo "💡 使用SSH推送 (如果配置了SSH密钥):"
    echo "  git remote set-url origin ${SSH_URL}"
    echo "  git push -u origin master"
    echo ""
    echo "📚 详细指南请查看: PUSH_TO_GITHUB_GUIDE.md"
fi

echo ""
echo "========================================"
echo "脚本执行完成"