FROM maven:3-jdk-11 as build
WORKDIR /app/
COPY . /app/
RUN mvn -Dmaven.test.skip=true -Duser.timezone=Europe/Paris clean package


FROM tomcat:9-jdk11 as back-server
COPY --from=build /app/web/target/*.war /tmp/app.war
CMD [ "bash", "-c", "rm -rf webapps/* && cp /tmp/app.war webapps/ROOT.war && catalina.sh run" ]

#FROM openjdk:11 as back-server
#COPY --from=build /app/web/target/*.jar /app/licences-nationales.jar
#ENTRYPOINT ["java","-jar","/app/licences-nationales.jar"]

