# licencesnationales-back
## API Licences Nationales

Application sous forme d'API REST, permettant : 

- aux institutions bénéficiaires des ressources numériques de :
  - déclarer les contacts pour l’administration technique des ressources,
  - déclarer les adresses IP.
- à l’Abes :
  - renseigner les contacts auprès des éditeurs de ces ressources
  - vérifier les informations saisies par les bénéficiaires, supprimer ou accepter les comptes, valider ou non les adresses IP
  - envoyer les informations de gestion renseignées par les institutions aux éditeurs présents dans la base, via un batch mensuel, non automatisé. Ce batch comprend un identifiant Abes ad hoc, utilisé souvent comme identifiant pivot sur lequel les éditeurs et l’Inist (pour les plates-formes ISTEX et PANIST) s’appuient pour gérer les droits d’accès.


Un client JavaScript VueJS pour cette API est développé par l'Abes, visible sur le dépôt suivant : https://github.com/abes-esr/licencesnationales-front

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

L'environnement souhaité devra être spécifié à l'aide des profiles spring. Il faut donc spécifier la valeur LOCAL, DEV, TEST, PROD à la variable spring_profiles_active, que ce soit via une variable d'environnement sur le serveur, ou à l'aide d'un paramètre de la JVM au lancement du war/jar. Plus d'informations sont disponibles dans la documentation de Spring Boot.

## Licences

Tous les nouveaux projets créés par l'Abes depuis 2019 produisent du code opensource.

Nous appliquons la Licence CeCILL : c'est une licence équivalente à la GPL compatible avec le droit Français et préconisée par la loi pour une République numérique. Elle est donc "contaminante", c'est à dire qu'elle impose aux contributeurs de publier les modifications/améliorations réalisées sous la même licence. Les bibliothèques de logiciels ("librairies") développées seront elles publiées sous la licence MIT qui permet une réutilisation moins contraignante et donc plus adapté à la nature de ces codes.
