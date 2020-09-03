FROM java:openjdk-8-jre
VOLUME /tmp

ARG JARFILE
COPY ${JARFILE} wemeet.jar

RUN ls

EXPOSE 4050

RUN sh -c 'touch /wemeet.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/wemeet.jar"]

