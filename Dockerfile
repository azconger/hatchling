FROM openjdk:21-jdk-slim

RUN mkdir /hatchling /app
COPY . /hatchling/

RUN cd /hatchling \
&& ./gradlew --no-daemon build \
&& cp build/libs/hatchling-0.0.1-SNAPSHOT.jar /app/ \
&& cd / \
&& rm -Rf /hatchling /root/.gradle/

WORKDIR /app

ENV PWD=/app
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/hatchling-0.0.1-SNAPSHOT.jar"]
