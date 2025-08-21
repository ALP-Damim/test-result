# --- Build stage ---
FROM gradle:8.9-jdk17-alpine AS build
WORKDIR /src
COPY . .
RUN gradle clean bootJar -x test --no-daemon

# --- Runtime stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# app.jar 로 산출되도록(아래 build.gradle 한 줄 참고)
COPY --from=build /src/build/libs/app.jar /app/app.jar
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
