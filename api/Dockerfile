FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

COPY . .

RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests


FROM eclipse-temurin:25-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-jar", "app.jar"]
