# Building WAR file
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY hostedftp-auth/pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY hostedftp-auth/src ./src
RUN mvn -B -DskipTests package

# Tomcat 
FROM tomcat:11-jdk21
ENV CATALINA_OPTS="-Djava.security.egd=file:/dev/./urandom -XX:MaxRAMPercentage=75"
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /src/target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh","run"]
