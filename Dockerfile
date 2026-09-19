FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./pom.xml
RUN mvn -B dependency:go-offline
COPY backend/src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S kodrotasi && adduser -S kodrotasi -G kodrotasi
COPY --from=backend-build /app/target/platform-0.0.1-SNAPSHOT.jar app.jar
USER kodrotasi
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]

