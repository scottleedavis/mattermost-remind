FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ARG REMIND_WEBHOOK
ARG REMIND_SLASH

RUN echo "remind.webhook: $REMIND_WEBHOOK"
RUN echo "remind.slash: $REMIND_SLASH"

COPY ${JAR_FILE} app.jar
# ENV JAVA_OPTS="remind.webhookUrl=${REMIND_WEBHOOK} remind.SlashCommandToken=${REMIND_SLASH}"
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
