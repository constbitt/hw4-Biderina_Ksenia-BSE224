
FROM openjdk:17
WORKDIR /auth
COPY target/auth-0.0.1-SNAPSHOT.jar auth-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "auth-0.0.1-SNAPSHOT.jar"]
