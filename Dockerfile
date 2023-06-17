FROM gradle:7.6.1-jdk17 AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
ARG JAR_FILE=build/libs/*.jar

FROM openjdk:jre-alpine
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${JAVA_ACTIVE} -jar /app.jar"]