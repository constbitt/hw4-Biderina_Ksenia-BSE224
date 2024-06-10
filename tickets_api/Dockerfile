
FROM openjdk:17
WORKDIR /tickets
COPY target/tickets-0.0.1-SNAPSHOT.jar tickets-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "tickets-0.0.1-SNAPSHOT.jar"]
