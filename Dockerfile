###
# Image pour la compilation de licencesnationales
FROM maven:3-jdk-11 as ln-build
WORKDIR /build/

# On lance la compilation
COPY ./pom.xml /build/pom.xml
COPY ./core/   /build/core/
COPY ./batch/  /build/batch/
COPY ./web/    /build/web/
RUN mvn -Dmaven.test.skip=true -Duser.timezone=Europe/Paris package



###
# Image pour le module batch de licencesnationales
FROM openjdk:11 as batch-image
WORKDIR /scripts/
# cron: for running periodicaly scanning
# gettext-base: for having envsubst command
ARG DEBIAN_FRONTEND=noninteractive
RUN apt update && apt install -y cron gettext-base
RUN rm -rf /etc/cron.*/*
COPY ./docker/batch/tasks.tmpl /etc/cron.d/tasks.tmpl

# Le JAR et le script pour le batch de LN
COPY ./docker/batch/licencesnationales-batch1.sh /scripts/licencesnationales-batch1.sh
COPY --from=ln-build /build/batch/target/*.jar /scripts/licencesnationales-batch1.jar

COPY ./docker/batch/docker-entrypoint.sh /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["cron", "-f"]



###
# Image pour le module web de licencesnationales
FROM tomcat:9-jdk11 as web-image
COPY --from=ln-build /build/web/target/*.war /usr/local/tomcat/webapps/ROOT.war
CMD [ "catalina.sh", "run" ]

#FROM openjdk:11 as back-server
#COPY --from=web-build /app/web/target/*.jar /app/licences-nationales.jar
#ENTRYPOINT ["java","-jar","/app/licences-nationales.jar"]


