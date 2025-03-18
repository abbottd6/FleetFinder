FROM openjdk:22-jdk
LABEL authors="Dayton Abbott"
WORKDIR /app
COPY target/fleets-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]