FROM openjdk:17

ARG APP_NAME
COPY target/${APP_NAME} /opt/
WORKDIR /opt
RUN mv ${APP_NAME} app.jar
CMD ["java", "-jar", "app.jar"]
