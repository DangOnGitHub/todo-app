FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace/backend

COPY backend/gradle gradle
COPY backend/gradlew backend/build.gradle.kts backend/settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY openapi.yaml /workspace/openapi.yaml
COPY backend/src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine AS extract
WORKDIR /builder
COPY --from=build /workspace/backend/build/libs/*.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jre-alpine
WORKDIR /application
COPY --from=extract /builder/extracted/dependencies/ ./
COPY --from=extract /builder/extracted/spring-boot-loader/ ./
COPY --from=extract /builder/extracted/snapshot-dependencies/ ./
COPY --from=extract /builder/extracted/application/ ./
ENTRYPOINT ["java", "-jar", "application.jar"]
