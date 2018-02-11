FROM maven:3.3.3-jdk-8 as build

WORKDIR /work
ADD pom.xml /work/
RUN mvn dependency:go-offline

ADD / /work

RUN mvn -q -Prelease package


### ---

FROM openjdk:8-jdk-alpine
COPY --from=build /work/target/call-for-paper.jar /app.jar
LABEL maintainer "team@breizhcamp.org"
EXPOSE 8080
CMD [ "java", "-jar", "app.jar" ]




