FROM openjdk:24-jdk-slim

WORKDIR /app

# dos2unix 설치 (줄바꿈 문자 문제 해결)
RUN apt-get update && apt-get install -y dos2unix

# Gradle wrapper와 소스 코드 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# Windows 줄바꿈 문자를 Unix 형식으로 변환
RUN dos2unix gradlew

# 디버깅: 파일 상태 확인
RUN ls -la
RUN ls -la gradlew
RUN ls -la gradle/
RUN cat gradlew | head -5

# Gradle wrapper 권한 설정 및 빌드
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# JAR 파일을 실행 디렉토리로 복사
RUN mkdir -p /app/logs
RUN find build/libs -name "*.jar" -not -name "*plain*" -exec cp {} app.jar \;

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
