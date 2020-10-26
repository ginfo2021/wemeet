FROM java:openjdk-8-jre
ARG JAR_FILE=build/libs/wemeetbackend.jar
ADD ${JAR_FILE} app.jar
RUN ls
EXPOSE 4050
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=$PORT", "-jar","/app.jar"]

