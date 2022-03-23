###
# Image pour la compilation de licencesnationales
FROM maven:3-jdk-11 as build-image
WORKDIR /build/

# On lance la compilation
# si on a un .m2 local on peut décommenter la ligne suivante pour 
# éviter à maven de retélécharger toutes les dépendances
#COPY ./.m2/    /root/.m2/
RUN export LANG=fr_FR.UTF-8
RUN locale
COPY ./pom.xml /build/pom.xml
COPY ./core/   /build/core/
COPY ./batch/  /build/batch/
COPY ./web/    /build/web/
RUN mvn -Duser.timezone=Europe/Paris -Duser.language=fr package



###
# Image pour le module batch de licencesnationales
# Remarque: l'image openjdk:11 n'est pas utilisée car nous avons besoin de cronie
#           qui n'est que disponible sous centos/rockylinux.
FROM rockylinux:8 as batch-image
WORKDIR /scripts/
# systeme pour les crontab
# cronie: remplacant de crond qui support le CTRL+C dans docker (sans ce système c'est compliqué de stopper le conteneur)
# gettext: pour avoir envsubst qui permet de gérer le template tasks.tmpl
RUN dnf install -y cronie gettext && \
    crond -V && rm -rf /etc/cron.*/*
COPY ./docker/batch/tasks.tmpl /etc/cron.d/tasks.tmpl
# Le JAR et le script pour le batch de LN
RUN dnf install -y java-11-openjdk
COPY ./docker/batch/licencesnationales-batch1.sh /scripts/licencesnationales-batch1.sh
COPY --from=build-image /build/batch/target/*.jar /scripts/licencesnationales-batch1.jar

COPY ./docker/batch/docker-entrypoint.sh /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["crond", "-n"]



###
# Image pour le module web de licencesnationales
FROM tomcat:9-jdk11 as web-image
COPY --from=build-image /build/web/target/*.war /usr/local/tomcat/webapps/ROOT.war
CMD [ "catalina.sh", "run" ]

#FROM openjdk:11 as back-server
#COPY --from=web-build /app/web/target/*.jar /app/licences-nationales.jar
#CMD ["java", "-jar", "/app/licences-nationales.jar"]


