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
                serverHostnames.add('hostname.server-back-1-dev')
                serverHostnames.add('hostname.server-back-2-dev')

            } else if (ENV == 'TEST') {
                serverHostnames.add('hostname.server-back-1-test')
                serverHostnames.add('hostname.server-back-2-test')

            } else if (ENV == 'PROD') {
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

    // stage('edit-properties') {
        // try {
            // if (ENV == 'DEV') {
                // withCredentials([
                        // usernamePassword(credentialsId: 'tomcatuser', passwordVariable: 'pass', usernameVariable: 'username'),
                        // string(credentialsId: 'periscope.oracle', variable: 'url')
                // ]) {
                    // echo 'Edition application-dev.properties'
                    // echo "--------------------------"

                    // original = readFile "web/src/main/resources/application-dev.properties"
                    // newconfig = original

                    // newconfig = newconfig.replaceAll("spring.main.allow-bean-definition-overriding=true", "")
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.url=${url}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.username=${username}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.password=${pass}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.driver-class-name=oracle.jdbc.OracleDrive"

                    // writeFile file: "web/src/main/resources/application-dev.properties", text: "${newconfig}"
                // }
            // }

            // if (ENV == 'TEST') {
                // withCredentials([
                        // usernamePassword(credentialsId: 'tomcatuser', passwordVariable: 'pass', usernameVariable: 'username'),
                        // string(credentialsId: 'periscope.oracle', variable: 'url')
                // ]) {
                    // echo 'Edition application-test.properties'
                    // echo "--------------------------"

                    // original = readFile "web/src/main/resources/application-test.properties"
                    // newconfig = original

                    // newconfig = newconfig.replaceAll("spring.main.allow-bean-definition-overriding=true", "")
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.url=${url}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.username=${username}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.password=${pass}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.driver-class-name=oracle.jdbc.OracleDrive"

                    // writeFile file: "web/src/main/resources/application-test.properties", text: "${newconfig}"
                // }
            // }

            // if (ENV == 'PROD') {
                // withCredentials([
                        // usernamePassword(credentialsId: 'tomcatuser', passwordVariable: 'pass', usernameVariable: 'username'),
                        // string(credentialsId: 'periscope.oracle', variable: 'url')
                // ]) {
                    // echo 'Edition application-prod.properties'
                    // echo "--------------------------"

                    // original = readFile "web/src/main/resources/application-prod.properties"
                    // newconfig = original

                    // newconfig = newconfig.replaceAll("spring.main.allow-bean-definition-overriding=true", "")
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.url=${url}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.username=${username}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.password=${pass}"
                    // newconfig = newconfig + System.getProperty("line.separator") + "sujets.datasource.driver-class-name=oracle.jdbc.OracleDrive"

                    // writeFile file: "web/src/main/resources/application-prod.properties", text: "${newconfig}"
                // }
            // }

        // } catch(e) {
            // currentBuild.result = hudson.model.Result.FAILURE.toString()
            // notifySlack(slackChannel,e.getLocalizedMessage())
            // throw e
        // }
    // }

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