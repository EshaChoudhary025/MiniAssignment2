
FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/MiniAssignment2.jar /app/MiniAssignment2.jar
EXPOSE 8080
CMD ["java", "-jar", "MiniAssignment2.jar"]
