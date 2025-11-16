FROM gcr.io/distroless/java17

LABEL maintainer="dasi"

WORKDIR /app

COPY target/message-center.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]