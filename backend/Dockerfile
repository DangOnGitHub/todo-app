FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace/backend

COPY backend/gradle gradle
COPY backend/gradlew backend/build.gradle.kts backend/settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY openapi.yaml /workspace/openapi.yaml
COPY backend/src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
