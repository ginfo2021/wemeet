FROM java:openjdk-8-jre
RUN mkdir /app

RUN ls /app

ARG JARFILE
COPY ${JARFILE} /app/app.jar

EXPOSE 4050

RUN sh -c 'touch /app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]

