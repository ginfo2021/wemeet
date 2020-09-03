FROM java:openjdk-8-jre
VOLUME /tmp
RUN ls

ARG JARFILE
COPY ${JARFILE} build/libs/app.jar

EXPOSE 4050

RUN bash -c 'touch build/libs/app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","build/libs/app.jar"]

