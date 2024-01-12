FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY build/libs/app.jar app.jar
EXPOSE 8080
CMD java -jar -Dspring.config.location=classpath:/application.properties,file:/app/application.properties app.jar

