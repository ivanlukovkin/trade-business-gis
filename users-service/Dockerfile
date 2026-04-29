FROM gradle:9.4.0-jdk21 AS builder
WORKDIR /app
USER root
RUN chown -R gradle:gradle /app
USER gradle
RUN printf '%s\n' \
    'rootProject.name = "trade-business-gis"' \
    'include("users-service", "library")' \
    > settings.gradle.kts
COPY --chown=gradle:gradle users-service/build.gradle.kts ./users-service/
COPY --chown=gradle:gradle library/build.gradle.kts ./library/
COPY --chown=gradle:gradle gradle/libs.versions.toml ./gradle/libs.versions.toml
RUN gradle :users-service:dependencies --no-daemon
COPY --chown=gradle:gradle users-service/src ./users-service/src
COPY --chown=gradle:gradle library/src ./library/src
RUN gradle :users-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/users-service/build/libs/*.jar app.jar
EXPOSE 18081
ENTRYPOINT ["java", "-jar", "app.jar"]
