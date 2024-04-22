FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR usr/src/springboot
COPY . ./
#RUN mvn install
RUN mvn clean package -DskipTests
#
# Package stage
#
FROM openjdk:21
ARG JAR_NAME="messageflinkconsumer-0.0.1-SNAPSHOT"
WORKDIR /usr/src/springboot
EXPOSE 8080
COPY --from=build /usr/src/springboot/target/${JAR_NAME}.jar ./springboot.jar
CMD ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.nio=ALL-UNNAMED" , "-jar", "springboot.jar"]