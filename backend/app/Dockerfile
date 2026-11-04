# JDK 17 기준 애플리케이션 이미지 (Week 6 이후 컨테이너 실행 실습)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
