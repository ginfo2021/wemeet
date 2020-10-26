FROM java:openjdk-8-jre
VOLUME /tmp
RUN ls
# ARG JAR_FILE
ADD build/libs/wemeetbackend.jar app.jar
RUN ls
EXPOSE 4050
# RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=$PORT", "-jar","/app.jar"]

