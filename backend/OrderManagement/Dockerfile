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
COPY --from=build /build/target/ordermanagement-*.jar /app/
COPY wait-for-it.sh /app/
RUN chmod +x /app/wait-for-it.sh
EXPOSE 8083
#ENV DB_URL=jdbc:mysql://localhost:3306/ordermanagement
ENV JAR_VERSION=${APP_VERSION}
#CMD java -jar ordermanagement-${JAR_VERSION}.jar
CMD sh -c "./wait-for-it.sh menumanagement:8081 --timeout=60 --strict -- \
          ./wait-for-it.sh eureka:8761 --timeout=60 --strict -- \
          java -jar ordermanagement-${JAR_VERSION}.jar --spring.profiles.active=docker"