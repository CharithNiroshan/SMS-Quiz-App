FROM openjdk:17
WORKDIR /app
COPY /target/test-0.0.1-SNAPSHOT.jar .
CMD ["java","-jar","test-0.0.1-SNAPSHOT.jar"]