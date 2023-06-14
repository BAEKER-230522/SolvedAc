FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${JAVA_TIMEZ} ${JAVA_ACTIVE} -jar /app.jar"]
