# Use base image with JDK 11
FROM openjdk:11-jdk

# Set working directory in Docker container
WORKDIR /usr/src/myapp

# Install Maven
RUN apt-get update && \
    apt-get install -y maven

# Copy current directory contents into the container at /usr/src/myapp
COPY . .

# Build project and run tests
RUN mvn clean compile

# Run tests through pom when the container launches
CMD ["mvn", "test"]

#docker run -it ff0091429fbd mvn clean test -DsuiteFile=docker -Denv=qa
#docker run -v C:\Users\kerimdogan\eclipse-workspace\DockerGithubActions\allure-results:\myapp\allure-results ff0091429fbd mvn clean test -DsuiteFile=docker -Denv=qa