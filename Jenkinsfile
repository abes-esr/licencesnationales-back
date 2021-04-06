//this is the scripted method with groovy engine
import hudson.model.Result

node {

    //Configuration
    def gitURL = "https://github.com/abes-esr/licencesnationales-back.git"
    def gitCredentials = ''
    def warDir = "target/"
    def warName = "lnevent"
    def tomcatWebappsDir = "/usr/local/tomcat9-licencesNationales/webapps/"
    def tomcatServiceName = "tomcat9-licencesNationales.service"
    def slackChannel = "#notif-licencesnationales"

    // Variables globales
    def maventool
    def rtMaven
    def buildInfo
    def server
    def ENV
    def serverHostnames = []
    def executeTests
    def mavenProfil

    // Configuration du job Jenkins
    // On garde les 5 derniers builds par branche
    // On scanne les branches et les tags du Git
    properties([
            buildDiscarder(
                    logRotator(
                            artifactDaysToKeepStr: '',
                            artifactNumToKeepStr: '',
                            daysToKeepStr: '',
                            numToKeepStr: '5')
            ),
            parameters([
                    gitParameter(
                            branch: '',
                            branchFilter: 'origin/(.*)',
                            defaultValue: 'develop',
                            description: 'Sélectionner la branche ou le tag à déployer',
                            name: 'BRANCH_TAG',
                            quickFilterEnabled: false,
                            selectedValue: 'NONE',
                            sortMode: 'DESCENDING_SMART',
                            tagFilter: '*',
                            type: 'PT_BRANCH_TAG'),
                    choice(choices: ['DEV', 'TEST', 'PROD'], description: 'Sélectionner l\'environnement cible', name: 'ENV'),
                    booleanParam(defaultValue: false, description: 'Voulez-vous exécuter les tests ?', name: 'executeTests')
            ])
    ])

    stage('Set environnement variables') {
        try {
            env.JAVA_HOME = "${tool 'Open JDK 11'}"
            env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"

            maventool = tool 'Maven 3.3.9'
            rtMaven = Artifactory.newMavenBuild()
            server = Artifactory.server '-1137809952@1458918089773'
            rtMaven.tool = 'Maven 3.3.9'
            rtMaven.opts = '-Xms1024m -Xmx4096m'

            if (params.BRANCH_TAG == null) {
                throw new Exception("Variable BRANCH_TAG is null")
            } else {
                echo "Branch to deploy =  ${params.BRANCH_TAG}"
            }

            if (params.ENV == null) {
                throw new Exception("Variable ENV is null")
            } else {
                ENV = params.ENV
                echo "Target environnement =  ${ENV}"
            }

             if (ENV == 'DEV') {
                mavenProfil = "dev"
                serverHostnames.add('hostname.server-back-1-dev')
                serverHostnames.add('hostname.server-back-2-dev')

            } else if (ENV == 'TEST') {
                mavenProfil = "test"
                serverHostnames.add('hostname.server-back-1-test')
                serverHostnames.add('hostname.server-back-2-test')

            } else if (ENV == 'PROD') {
                mavenProfil = "prod"
                serverHostnames.add('hostname.server-back-1-prod')
                serverHostnames.add('hostname.server-back-2-prod')
            }

            if (params.executeTests == null) {
                executeTests = false
            } else {
                executeTests = params.executeTests
            }

            echo "executeTests =  ${executeTests}"

        } catch (e) {
            currentBuild.result = hudson.model.Result.NOT_BUILT.toString()
            notifySlack(slackChannel,e.getLocalizedMessage())
            throw e
        }
    }

    stage('SCM checkout') {
        try {
            checkout([
                    $class                           : 'GitSCM',
                    branches                         : [[name: "${params.BRANCH_TAG}"]],
                    doGenerateSubmoduleConfigurations: false,
                    extensions                       : [],
                    submoduleCfg                     : [],
                    userRemoteConfigs                : [[credentialsId: "${gitCredentials}", url: "${gitURL}"]]
            ])

        } catch (e) {
            currentBuild.result = hudson.model.Result.FAILURE.toString()
            notifySlack(slackChannel,e.getLocalizedMessage())
            throw e
        }
    }

    if ("${executeTests}" == 'true') {
        stage('test') {
            try {

                rtMaven.run pom: 'pom.xml', goals: 'clean test'
                junit allowEmptyResults: true, testResults: '/target/surefire-reports/*.xml'

            } catch (e) {
                currentBuild.result = hudson.model.Result.UNSTABLE.toString()
                notifySlack(slackChannel,e.getLocalizedMessage())
                // Si les tests ne passent pas, on mets le build en UNSTABLE et on continue
                //throw e
            }
        }
    } else {
        echo "Tests are skipped"
    }


    stage("Edit properties files") {
      try {
        echo "Edition application-${mavenProfil}.properties"
        echo "--------------------------"
        original = readFile "src/main/resources/application-${mavenProfil}.properties"
        newconfig = original

        withCredentials([
          string(credentialsId: "url-orpins-${mavenProfil}", variable: 'url')
        ]) {
          newconfig = newconfig.replaceAll("spring.datasource.url=*", "spring.datasource.url=${url}")
        }
        withCredentials([
          string(credentialsId: "LN-username-orpins", variable: 'BDusername')
        ]) {
          newconfig = newconfig.replaceAll("spring.datasource.username=*", "spring.datasource.username=${BDusername}")
        }
        withCredentials([
          string(credentialsId: "LN-password-orpins", variable: 'BDpassword')
        ]) {
          newconfig = newconfig.replaceAll("spring.datasource.password=*", "spring.datasource.password=${BDpassword}")
        }
        withCredentials([
          string(credentialsId: "LN-jwt-token-secret", variable: 'jwtToken')
        ]) {
          newconfig = newconfig.replaceAll("jwt.token.secret=*", "jwt.token.secret=${jwtToken}")
        }
        withCredentials([
          string(credentialsId: "LN-jwt-token-expirationInMs", variable: 'tokenExpirationInMs')
        ]) {
          newconfig = newconfig.replaceAll("jwt.token.expirationInMs=*", "jwt.token.expirationInMs=${tokenExpirationInMs}")
        }
        withCredentials([
          string(credentialsId: "LN-server-port", variable: 'serverPort')
        ]) {
          newconfig = newconfig.replaceAll("server.port=*", "server.port=${serverPort}")
        }
        withCredentials([
          string(credentialsId: "smtp-host", variable: 'smtpHost')
        ]) {
          newconfig = newconfig.replaceAll("spring.mail.host=*", "spring.mail.host=${smtpHost}")
        }
        withCredentials([
          string(credentialsId: "smtp-port", variable: 'smtpPort')
        ]) {
          newconfig = newconfig.replaceAll("spring.mail.port=*", "spring.mail.port=${smtpPort}")
        }
        withCredentials([
          string(credentialsId: "smtp-username", variable: 'smtpUsername')
        ]) {
          newconfig = newconfig.replaceAll("spring.mail.username=*", "spring.mail.username=${smtpUsername}")
        }
        withCredentials([
          string(credentialsId: "smtp-password", variable: 'smtpPassword')
        ]) {
          newconfig = newconfig.replaceAll("spring.mail.password=*", "spring.mail.password=${smtpPassword}")
        }
        withCredentials([
          string(credentialsId: "LN-orpins-driver", variable: 'driver')
        ]) {
          newconfig = newconfig.replaceAll("spring.datasource.driver-class-name=*", "spring.datasource.driver-class-name=${driver}")
        }
        withCredentials([
          string(credentialsId: "LN-orpins-dialect", variable: 'dialect')
        ]) {
          newconfig = newconfig.replaceAll("spring.jpa.properties.hibernate.dialect=*", "spring.jpa.properties.hibernate.dialect=${dialect}")
        }
        withCredentials([
          string(credentialsId: "google-recaptcha-key-site", variable: 'googleRecaptchaKey')
        ]) {
          newconfig = newconfig.replaceAll("google.recaptcha.key.site=*", "google.recaptcha.key.site=${googleRecaptchaKey}")
        }
        withCredentials([
          string(credentialsId: "google-recaptcha-key-secret", variable: 'googleRecaptchaSecret')
        ]) {
          newconfig = newconfig.replaceAll("google.recaptcha.key.secret=*", "google.recaptcha.key.secret=${googleRecaptchaSecret}")
        }
        withCredentials([
          string(credentialsId: "google-recaptcha-key-threshold", variable: 'googleRecaptchaThreshold')
        ]) {
          newconfig = newconfig.replaceAll("google.recaptcha.key.threshold=*", "google.recaptcha.key.threshold=${googleRecaptchaThreshold}")
        }

        writeFile file: "src/main/resources/application.properties", text: "${newconfig}"

      } catch (e) {
        currentBuild.result = hudson.model.Result.FAILURE.toString()
        notifySlack(slackChannel, "Failed to edit properties files: " + e.getLocalizedMessage())
        throw e
      }
    }

    stage('compile-package') {
        try {
            if (ENV == 'DEV') {
                echo 'Compile for dev profile'
                echo "--------------------------"

                sh "'${maventool}/bin/mvn' -Dmaven.test.skip=true clean package -DfinalName='${warName}' -DbaseDir='${tomcatWebappsDir}${warName}' -Pdev"
            }

            if (ENV == 'TEST') {
                echo 'Compile for test profile'
                echo "--------------------------"

                sh "'${maventool}/bin/mvn' -Dmaven.test.skip=true clean package -DfinalName='${warName}' -DbaseDir='${tomcatWebappsDir}${warName}' -Ptest"
            }

            if (ENV == 'PROD') {
                echo 'Compile for prod profile'
                echo "--------------------------"

                sh "'${maventool}/bin/mvn' -Dmaven.test.skip=true clean package -DfinalName='${warName}' -DbaseDir='${tomcatWebappsDir}${warName}' -Pprod"
            }

        } catch(e) {
            currentBuild.result = hudson.model.Result.FAILURE.toString()
            notifySlack(slackChannel,e.getLocalizedMessage())
            throw e
        }
    }

    //stage('sonarqube analysis'){
    //   withSonarQubeEnv('SonarQube Server2'){ cf : jenkins/configuration/sonarQube servers ==> between the quotes put the name we gave to the server
    //      sh "${maventool}/bin/mvn sonar:sonar"
    //  }
    // }

    stage('artifact') {
        try {
            archive "${warDir}${warName}.war"

        } catch(e) {
            currentBuild.result = hudson.model.Result.FAILURE.toString()
            notifySlack(slackChannel,e.getLocalizedMessage())
            throw e
        }
    }

    stage ('stop tomcat'){
        for (int i = 0; i < serverHostnames.size(); i++) { //Pour chaque serveur
            try {
                    withCredentials([
                            usernamePassword(credentialsId: 'tomcatuser', passwordVariable: 'pass', usernameVariable: 'username'),
                            string(credentialsId: "${serverHostnames[i]}", variable: 'hostname'),
                            string(credentialsId: 'service.status', variable: 'status'),
                            string(credentialsId: 'service.stop', variable: 'stop'),
                            string(credentialsId: 'service.start', variable: 'start')
                    ]) {
                        echo "Stop service on ${serverHostnames[i]}"
                        echo "--------------------------"

                        try {

                            echo 'get service status'
                            sh "ssh -tt ${username}@${hostname} \"${status} ${tomcatServiceName}\""

                            echo 'stop the service'
                            sh "ssh -tt ${username}@${hostname} \"${stop} ${tomcatServiceName}\""

                        } catch(e) {
                            // Maybe the tomcat is not running
                            echo 'maybe the service is not running'

                            echo 'we try to start the service'
                            sh "ssh -tt ${username}@${hostname} \"${start} ${tomcatServiceName}\""

                            echo 'get service status'
                            sh "ssh -tt ${username}@${hostname} \"${status} ${tomcatServiceName}\""

                            echo 'stop the service'
                            sh "ssh -tt ${username}@${hostname} \"${stop} ${tomcatServiceName}\""
                        }
                    }

            } catch(e) {
                currentBuild.result = hudson.model.Result.FAILURE.toString()
                notifySlack(slackChannel,e.getLocalizedMessage())
                throw e
            }
        }
    }

    stage ('deploy to tomcat'){
        for (int i = 0; i < serverHostnames.size(); i++) { //Pour chaque serveur
            try {
                    withCredentials([
                            usernamePassword(credentialsId: 'tomcatuser', passwordVariable: 'pass', usernameVariable: 'username'),
                            string(credentialsId: "${serverHostnames[i]}", variable: 'hostname')
                    ]) {
                        echo "Deploy to ${serverHostnames[i]}"
                        echo "--------------------------"

                        sh "ssh -tt ${username}@${hostname} \"rm -fr ${tomcatWebappsDir}${warName} ${tomcatWebappsDir}${warName}.war\""
                        sh "scp ${warDir}${warName}.war ${username}@${hostname}:${tomcatWebappsDir}"
                    }

            } catch(e) {
                currentBuild.result = hudson.model.Result.FAILURE.toString()
                notifySlack(slackChannel,e.getLocalizedMessage())
                throw e
            }
        }
    }

    stage ('restart tomcat'){
        for (int i = 0; i < serverHostnames.size(); i++) { //Pour chaque serveur
            try {
                    withCredentials([
                            usernamePassword(credentialsId: 'tomcatuser', passwordVariable: 'pass', usernameVariable: 'username'),
                            string(credentialsId: "${serverHostnames[i]}", variable: 'hostname'),
                            string(credentialsId: 'service.status', variable: 'status'),
                            string(credentialsId: 'service.start', variable: 'start')
                    ]) {
                        echo "Restart service on ${serverHostnames[i]}"
                        echo "--------------------------"

                        echo 'start service'
                        sh "ssh -tt ${username}@${hostname} \"${start} ${tomcatServiceName}\""

                        echo 'get service status'
                        sh "ssh -tt ${username}@${hostname} \"${status} ${tomcatServiceName}\""
                    }

            } catch(e) {
                currentBuild.result = hudson.model.Result.FAILURE.toString()
                notifySlack(slackChannel,e.getLocalizedMessage())
                throw e
            }
        }
    }

    stage ('Artifactory configuration') {
        try {
            rtMaven.deployer server: server, releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local'
            buildInfo = Artifactory.newBuildInfo()
            buildInfo = rtMaven.run pom: 'pom.xml', goals: '-U clean install -Dmaven.test.skip=true '

            rtMaven.deployer.deployArtifacts buildInfo
            buildInfo = rtMaven.run pom: 'pom.xml', goals: 'clean install -Dmaven.repo.local=.m2 -Dmaven.test.skip=true'
            buildInfo.env.capture = true
            server.publishBuildInfo buildInfo

        } catch(e) {
            currentBuild.result = hudson.model.Result.FAILURE.toString()
            notifySlack(slackChannel,e.getLocalizedMessage())
            throw e
        }
    }

    currentBuild.result = hudson.model.Result.SUCCESS.toString()
    notifySlack(slackChannel,"Congratulation !")
}

def notifySlack(String slackChannel, String info = '') {
    def colorCode = '#848484' // Gray

    switch (currentBuild.result) {
        case 'NOT_BUILT':
            colorCode = '#FFA500' // Orange
            break
        case 'SUCCESS':
            colorCode = '#00FF00' // Green
            break
        case 'UNSTABLE':
            colorCode = '#FFFF00' // Yellow
            break
        case 'FAILURE':
            colorCode = '#FF0000' // Red
            break;
    }

    String message = """
        *Jenkins Build*
        Job name: `${env.JOB_NAME}`
        Build number: `#${env.BUILD_NUMBER}`
        Build status: `${currentBuild.result}`
        Branch or tag: `${params.BRANCH_TAG}`
        Target environment: `${params.ENV}`
        Message: `${info}`
        Build details: <${env.BUILD_URL}/console|See in web console>
    """.stripIndent()

    return slackSend(tokenCredentialId: "slack_token",
            channel: "${slackChannel}",
            color: colorCode,
            message: message)
}