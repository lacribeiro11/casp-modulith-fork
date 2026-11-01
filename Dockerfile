FROM eclipse-temurin:21-jre

USER casp:casp

WORKDIR /app

COPY --chown=casp:casp target/adminV2.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
