#FROM scratch
#MAINTAINER Scott Davis <scottleedavis@gmail.com>

FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
#ENV REMIND_WEBHOOK_URL=http://127.0.0.1:8065/hooks/jqg3bkxbip8hzgsrsnzep5jqyc
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
