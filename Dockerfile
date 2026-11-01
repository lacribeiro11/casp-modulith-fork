FROM eclipse-temurin:25

COPY ./target/adminV2.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
