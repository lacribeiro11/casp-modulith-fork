FROM eclipse-temurin:21

WORKDIR /app

COPY --chown=10001:10001 target/adminV2.jar app.jar

USER 10001:10001

ENTRYPOINT ["java", "-jar", "app.jar"]
