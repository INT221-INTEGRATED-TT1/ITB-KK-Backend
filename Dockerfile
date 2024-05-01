FROM maven:3.8.3-openjdk-17
COPY . /backendAPI
WORKDIR /backendAPI
RUN mvn clean package
ENTRYPOINT ["java","-jar","./target/demoBackend-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080