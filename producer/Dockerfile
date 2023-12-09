FROM openjdk:17.0-jdk-slim

RUN apt-get update && \
    apt-get install -y maven

WORKDIR /app
COPY .  .
RUN sleep 30
CMD java -jar target/producer-0.0.1-SNAPSHOT.jar  
WORKDIR /

EXPOSE 5000