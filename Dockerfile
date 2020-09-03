FROM java:openjdk-8-jre
VOLUME /tmp

ADD build/libs/*.jar app.jar
EXPOSE 4050
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

