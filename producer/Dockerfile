FROM openjdk:17.0-jdk-slim

RUN apt-get update && \
    apt-get install -y maven

WORKDIR /app
COPY .  .
WORKDIR /

EXPOSE 5000