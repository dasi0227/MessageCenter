FROM gcr.io/distroless/java17

LABEL maintainer="dasi"

WORKDIR /app

COPY target/message-center.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -t dasi0227/message-center:1.0 .
# docker logs -f mc