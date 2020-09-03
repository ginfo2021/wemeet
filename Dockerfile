FROM java:openjdk-8-jre
VOLUME /tmp
RUN ls

COPY /build/libs/wemeet.jar /app.jar

EXPOSE 4050

RUN bash -c 'touch /app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

