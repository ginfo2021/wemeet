FROM java:openjdk-8-jre
VOLUME /tmp

ARG JAR_FILE=build/libs/wemeet-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar

EXPOSE 4050
# RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

