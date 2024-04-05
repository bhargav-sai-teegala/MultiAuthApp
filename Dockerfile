FROM amazoncorretto:17.0.7-alpine
ARG JARFILE=target/*.jar
COPY ./target/multiauthapp-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]