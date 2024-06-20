FROM openjdk:17-alpine

WORKDIR /app

COPY target/main-service-0.0.1-SNAPSHOT.jar /app/wigell-travels.jar

EXPOSE 6060

CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "wigell-travels.jar"]