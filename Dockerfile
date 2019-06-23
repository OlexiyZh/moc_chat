FROM openjdk:8-jre-alpine

VOLUME /tmp
ADD target/chat-*.jar /usr/src/myapp/chat.jar
WORKDIR /usr/src/myapp
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "chat.jar"]
