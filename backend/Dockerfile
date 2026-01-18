FROM openjdk:17-slim

WORKDIR /app

COPY target/event-management-system-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
