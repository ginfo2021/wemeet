FROM java:openjdk-8-jre
VOLUME /tmp
RUN ls
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
RUN ls
EXPOSE 4050
# RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

