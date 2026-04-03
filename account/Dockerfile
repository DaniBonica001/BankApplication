FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=target/account-0.0.1.jar

WORKDIR /app

COPY ${JAR_FILE} app.jar

EXPOSE 8000

ENTRYPOINT ["java","-jar","/app/app.jar"]
