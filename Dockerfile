# 1. 자바 실행 환경(JDK 21) 가져오기
FROM eclipse-temurin:21-jdk-jammy

# 2. 빌드된 jar 파일을 컨테이너 안으로 복사
ARG JAR_FILE=dalbit_bootstrap/build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 앱 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]