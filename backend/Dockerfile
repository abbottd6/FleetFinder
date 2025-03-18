FROM openjdk:22-jdk
LABEL authors="Dayton Abbott"
WORKDIR /app

#cache buster for for forcing rebuilds on layers
ARG CACHE_BUSTER=1
RUN echo "Cache bust: $CACHE_BUSTER"

COPY target/fleets-0.0.1-SNAPSHOT.jar app.jar

#Exposing the backend port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]