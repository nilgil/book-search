# 빌드 환경
FROM gradle:8.14-jdk21-alpine AS builder

WORKDIR /build

COPY . .

RUN gradle :core:api:bootJar --no-daemon


# 실행 환경
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /build/core/api/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]