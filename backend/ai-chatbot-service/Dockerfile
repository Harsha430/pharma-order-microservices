FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-Xmx256m", "-Deureka.client.serviceUrl.defaultZone=http://eureka-server:8761/eureka/", "-jar", "app.jar"]
