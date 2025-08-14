# 빌드 환경
FROM gradle:8.14-jdk21-alpine AS builder

WORKDIR /build

COPY build.gradle settings.gradle gradle.properties /build/
COPY src /build/src

RUN gradle bootJar --no-daemon


# 실행 환경
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]