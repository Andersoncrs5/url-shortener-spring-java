# =========================
# 1. BUILD STAGE
# =========================
FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean generate-sources package -DskipTests

# =========================
# 2. RUNTIME STAGE
# =========================
FROM eclipse-temurin:25-jre-alpine

# >>> ADICIONE ESTA LINHA AQUI PARA INSTALAR O CURL <<<
RUN apk add --no-cache curl

WORKDIR /app
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

ENTRYPOINT ["sh", "-c", "\
java \
-Dnet.bytebuddy.experimental=true \
-javaagent:/opentelemetry-javaagent.jar \
-jar app.jar \
"]
