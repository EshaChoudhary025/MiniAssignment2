
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/MiniAssignment2-0.0.1-SNAPSHOT.jar /app/MiniAssignment2.jar
EXPOSE 8080
CMD ["java", "-jar", "MiniAssignment2.jar"]
