#!/usr/bin/env groovy
import static groovy.json.JsonOutput.toJson

ansiColor('xterm') {
    node() {

        stage("Checkout") {
             checkout scm
        }

        stage('Build') {
            sh './gradlew clean build '
        }

        stage('Archive') {
             archiveArtifacts artifacts: "build/distribution/*.zip", fingerprint: true, flatten:true

        }
    }
}

// wrap a stage in try/catch and notify team by slack in case of failure
def slackStage(def name, boolean performSlackNotification, Closure body) {
    try {
        stage(name) {
            body()
        }
    } catch (e) {
        if (performSlackNotification) {
            def attachment = [
                    title: "${env.BRANCH_NAME} build is failing!",
                    title_link: env.BUILD_URL,
                    text: "Stage ${name} has failed"
            ]
            // use the bici channel id as it cannot change (channel name can)
            //slackSend(color: 'danger', channel: 'C33EA345S', attachments: toJson([attachment]))
        }
        throw e
    }
}