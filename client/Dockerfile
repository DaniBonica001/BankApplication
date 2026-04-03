FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=target/client-0.0.1.jar

WORKDIR /app

COPY ${JAR_FILE} app.jar

EXPOSE 8001

ENTRYPOINT ["java","-jar","/app/app.jar"]
