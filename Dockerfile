FROM scratch
MAINTAINER Scott Davis <scottleedavis@gmail.com>

#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]


#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#ARG REMIND_SLASH_TOKEN
#ARG REMIND_WEBHOOK_URL

#RUN echo "$REMIND_SLASH_TOKEN"
#RUN echo "$REMIND_WEBHOOK_URL"

#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java", "-c", "java -Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
