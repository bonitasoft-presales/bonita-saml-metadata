#!/usr/bin/env groovy
import static groovy.json.JsonOutput.toJson

ansiColor('xterm') {
    node('bcd-7100') {

        stage("Checkout") {
             checkout scm
        }

        stage('Build') {
            sh './gradlew clean build --info'
        }

        stage('End to End ') {
            sh './e2e.sh'
        }

        stage('Archive') {
             archiveArtifacts   artifacts: "build/distributions/*.zip, build/reports/tests/test/index.html, build/distributions/**/*.xml",
                                allowEmptyArchive: true,
                                flatten:true

        }
    }
}
