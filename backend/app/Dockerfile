# Week 6+ 에서 컨테이너로 앱을 띄울 때 사용 (./gradlew bootJar 선행)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
