# licencesnationales-api

[![ci](https://github.com/abes-esr/licencesnationales-back/actions/workflows/build-test-pubtodockerhub.yml/badge.svg)](https://github.com/abes-esr/licencesnationales-back/actions/workflows/build-test-pubtodockerhub.yml) [![Docker Pulls](https://img.shields.io/docker/pulls/abesesr/licencesnationales.svg)](https://hub.docker.com/r/abesesr/licencesnationales/)

API REST de l'application de gestion des droits des Licences Nationales. Son [interface graphique](https://github.com/abes-esr/licencesnationales-front) repose sur cette API. URL de producation de l'application : https://acces.licencesnationales.fr


Les fonctionnalités prévues sont de permettre :
- aux institutions bénéficiaires des ressources numériques de :
  - déclarer les contacts pour l’administration technique des ressources,
  - déclarer les adresses IP.
- à l’Abes :
  - renseigner les contacts auprès des éditeurs de ces ressources
  - vérifier les informations saisies par les bénéficiaires, supprimer ou accepter les comptes, valider ou non les adresses IP
  - envoyer les informations de gestion renseignées par les institutions aux éditeurs présents dans la base, via un batch mensuel, non automatisé. Ce batch comprend un identifiant Abes ad hoc, utilisé souvent comme identifiant pivot sur lequel les éditeurs et l’Inist (pour les plates-formes ISTEX et PANIST) s’appuient pour gérer les droits d’accès.

Copie d'écran de la page d'accueil de la documentation Swagger (OpenAPI) de cette API :  
![image](https://user-images.githubusercontent.com/328244/159644528-57410331-3a78-4273-9660-c40303a05724.png)


Un client JavaScript VueJS pour cette API est développé par l'Abes, son code source est disponible sur le dépôt suivant :  
https://github.com/abes-esr/licencesnationales-front

## Architecture

Cette API est développée en Java, à l'aide du framework Spring Boot. La gestion des dépendances et du build se fait grâce à l'outil maven.

L'accès à la couche de données utilise l'ORM JPA. Dans sa configuration actuelle, le projet utilise des dépendances permettant de se connecter à une base de données Oracle, mais grâce à la couche d'abstraction fournie par JPA, il est assez aisé d'utiliser une autre base de données relationnelle.

Le projet est découpé en trois modules : 
* Core : regroupe l'ensemble des entités et des services utilisés par les autres modules
* Web : controllers web permettant de fournir une API REST
* Batch : plusieurs batch, lancés périodiquement, permettant la gestion de comptes utilisateurs

L'authentification et la sécurisation des points d'accès se fait via JWT, à l'aide du framework Spring Security.

L'API est documentée à l'aide d'OpenAPI, et le code grâce à JavaDoc.

Un fichier jenkinsfile est présent sur le dépôt, et nous permet d'utiliser pour le déploiement et l'intégration continue les pipelines jenkins.

Les tests unitaire utilisent JUnit et Mockito.

## Installation

### Copier le répertoire
```sh
git clone https://github.com/abes-esr/licencesnationales-back
```

### Installer les dépendances en local pour le développement

```
mvn clean install
```


## Utilisation

### Compilation et packaging
```
mvn package
```
Avec la configuration actuellement (modifiable via le fichier pom.xml), cela donne un fichier WAR dans le dossier /target/, a déployer ensuite dans un serveur tomcat par exemple.

Il est possible de modifier le pom.xml pour obtenir un jar executable (le serveur web sera alors inclus dans le jar).

### Tests Unitaire
```
mvn test
```
Les tests seront également executés lors de la phase de packaging.

### Configuration

Les fichiers de configuration nécessaires au bon fonctionnement de l'application suivent les conventions du framework Spring Boot.

Des fichiers application.properties et application-ENV.properties se trouvent à la racine des différents modules. Certaines valeurs contenant des données sensibles (adresse, login et mot de passe des bases de données par exemple) sont vides sur ce dépôt et donc à renseigner avant utilisation.

L'environnement souhaité devra être spécifié à l'aide des profiles spring. Il faut donc spécifier la valeur ``LOCAL``, ``DEV``, ``TEST``, ``PROD`` à la variable ``SPRING_PROFILES_ACTIVE``, que ce soit via une variable d'environnement sur le serveur, ou à l'aide d'un paramètre de la JVM au lancement du war/jar. Plus d'informations sont disponibles dans la documentation de Spring Boot.

## Utilisation avec docker

### Génération des images docker

Les images docker de licencesnationales-back sont générées automatiquement à chaque ``git push`` par la chaîne d'intégration continue [![ci](https://github.com/abes-esr/licencesnationales-back/actions/workflows/build-test-pubtodockerhub.yml/badge.svg)](https://github.com/abes-esr/licencesnationales-back/actions/workflows/build-test-pubtodockerhub.yml). Les images suivantes sont [disponibles sur dockerhub](https://hub.docker.com/r/abesesr/licencesnationales/tags) (idem pour ``batch``) :
- ``abesesr/licencesnationales:main-web`` : l'image du dernier git push sur la branche ``main``
- ``abesesr/licencesnationales:develop-web`` : l'image du dernier git push sur la branche ``develop``
- ``abesesr/licencesnationales:X.X.X-web`` : l'image dont le n° de version est ``X.X.X``
- ``abesesr/licencesnationales:latest-web`` : l'image pointant vers la dernière version X.X.X publiée

Il est aussi possible de générer ces images localement en tapant par exemple les commandes suivantes :
```bash
cd licencesnationales-back/
docker build . --target web-image -t abesesr/licencesnationales:latest-web
docker build . --target batch-image -t abesesr/licencesnationales:latest-batch
```

Remarque : utilisation de ``--target`` car le Dockerfile crée 3 images (multi-stage),
- une première (``build-image``) pour la compilation de tout licencesnationales-back (core, web et batch),
- une seconde (``batch-image``) pour l'image du batch qui condiendra la crontab avec le JAR du batch de licencesnationales,
- et une troisième (``web-image``) pour l'image du web qui condient tomcat9 avec le WAR de licencesnationales.

### Démarrage des conteneurs docker

Pour un déploiement dans le SI de l'Abes, il faut se référer aux configurations suivantes :
https://git.abes.fr/depots/licencesnationales-docker/

Pour le déployer en local sur sa machine, une fois la génération des images terminée (cf section au dessus), voici les commandes que l'on peut utiliser (TODO, cette partie pourrait être améliorée en proposant un ``docker-compose.yml``):
```bash
docker run -d \
  --name licencesnationales-web \
  -p 8080:8080 \
  abesesr/licencesnationales:latest-web

docker run -d \
  --name licencesnationales-batch \
  -e LN_BATCH_CRON="0 * * * *" \
  -e LN_BATCH_AT_STARTUP="1" \
  abesesr/licencesnationales:latest-batch
```

Pour consulter les logs des deux conteneurs :
```bash
docker logs -n 100 -f licencesnationales-web
docker logs -n 100 -f licencesnationales-batch
```


## Publier une nouvelle release de l'application

Se référer à la procédure commune à toutes les applications opensource de l'Abes ici :  
https://github.com/abes-esr/abes-politique-developpement/blob/main/01-Gestion%20du%20code%20source.md#publier-une-nouvelle-release-dune-application

## Licences

Tous les nouveaux projets créés par l'Abes depuis 2019 produisent du code opensource.

Nous appliquons la Licence CeCILL : c'est une licence équivalente à la GPL compatible avec le droit Français et préconisée par la loi pour une République numérique. Elle est donc "contaminante", c'est à dire qu'elle impose aux contributeurs de publier les modifications/améliorations réalisées sous la même licence. Les bibliothèques de logiciels ("librairies") développées seront elles publiées sous la licence MIT qui permet une réutilisation moins contraignante et donc plus adapté à la nature de ces codes.
