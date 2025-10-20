# ========= Build stage =========
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
# Download dependencies (better Docker cache)
RUN mvn -q -e -B -DskipTests dependency:go-offline
# Copy source and build
COPY src ./src
RUN mvn -q -e -B clean package -DskipTests

# ========= Run stage =========
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the fat jar
COPY --from=build /app/target/app-*.jar /app/app.jar
# Expose default port (Render sets PORT env var; Spring reads server.port)
EXPOSE 8080
# JVM opts can be passed via JAVA_OPTS env var
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
# Start the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
