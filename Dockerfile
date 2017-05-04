FROM maven:3.3.3-jdk-8 as build

WORKDIR /work
ADD pom.xml /work/
RUN mvn dependency:go-offline

ADD / /work

RUN mvn -q -Prelease package


### ---

FROM openjdk:8-jdk-alpine
LABEL maintainer "team@breizhcamp.org"
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app.jar" ]

COPY --from=build target/call-for-paper.jar /app.jar



