#Build
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

#Runtime
FROM amazoncorretto:17
ARG APP_VERSION=0.0.1-SNAPSHOT
WORKDIR /app
COPY --from=build /build/target/ConfigServer-*.jar /app/
EXPOSE 8888
ENV JAR_VERSION=${APP_VERSION}
CMD java -jar ConfigServer-${JAR_VERSION}.jar --spring.profiles.active=docker