# 1. 자바 실행 환경(JDK 21) 가져오기
FROM openjdk:21-jdk-slim

# 2. 빌드된 jar 파일을 컨테이너 안으로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 앱 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]