FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get install -y maven

WORKDIR /app
COPY .  .
RUN sleep 10

EXPOSE 8091