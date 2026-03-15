# 多阶段构建Dockerfile
# 阶段1: 构建阶段
FROM maven:3.8.7-eclipse-temurin-11 AS builder

WORKDIR /app

# 复制pom.xml并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 阶段2: 运行时阶段
FROM eclipse-temurin:11-jre-alpine

# 安装必要的工具
RUN apk add --no-cache tzdata curl bash \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

# 创建非root用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/target/*.jar app.jar

# 复制配置文件
COPY --from=builder /app/src/main/resources/application.yml ./config/
COPY --from=builder /app/src/main/resources/application-dev.yml ./config/

# 设置权限
RUN chown -R appuser:appgroup /app
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]

# 默认配置
CMD ["--spring.profiles.active=prod", "--spring.config.location=classpath:/,file:./config/"]