FROM java:openjdk-8-jre
VOLUME /tmp

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app.jar

EXPOSE 4050

RUN bash -c 'touch /wemeet-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

