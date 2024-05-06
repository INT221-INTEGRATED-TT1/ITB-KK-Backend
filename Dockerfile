FROM maven:3.8.3-openjdk-17
COPY . /backendAPI
WORKDIR /backendAPI
RUN mvn clean package
CMD java -jar $(find /backendAPI/target -name '*.jar')
EXPOSE 8080