FROM openjdk:11
EXPOSE 8081
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar CloudStorage.jar
ENTRYPOINT ["java", "-jar", "CloudStorage.jar"]