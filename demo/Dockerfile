# =========================
# 1. BUILD STAGE
# =========================
FROM maven:3.9-eclipse-temurin-25-alpine AS build

WORKDIR /app

# Copia apenas o pom primeiro (cache de dependências)
COPY pom.xml .

# Baixa dependências
RUN mvn dependency:go-offline

# Copia o código
COPY src ./src

# Gera sources + build
RUN mvn clean generate-sources package -DskipTests


# =========================
# 2. RUNTIME STAGE
# =========================
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Porta da aplicação
EXPOSE 8080

# Copia o jar do build
COPY --from=build /app/target/*.jar app.jar

# OpenTelemetry Java Agent (Tempo / tracing)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

# JVM + Agent
ENTRYPOINT ["sh", "-c", "\
java \
-Dnet.bytebuddy.experimental=true \
-javaagent:/opentelemetry-javaagent.jar \
-jar app.jar \
"]
