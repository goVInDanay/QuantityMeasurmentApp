FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/history-service.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]