pipeline {
    agent any

    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['staging', 'production', 'local-tomcat'],
            description: 'Deployment target environment for Telemedicine Queue Management System'
        )
        string(
            name: 'TOMCAT_PORT',
            defaultValue: '8082',
            description: 'Target Application / Tomcat port for deployment verification'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Check to skip automated JUnit unit and integration tests (emergency hotfix)'
        )
    }

    environment {
        APP_NAME = 'tqms'
        WAR_FILE = "target/${APP_NAME}.war"
        DEPLOY_PATH = "deployments\\${params.DEPLOY_ENV}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "=========================================================="
                echo "Stage 1: Checking out source code from Git repository"
                echo "Target Environment: ${params.DEPLOY_ENV}"
                echo "Target Port: ${params.TOMCAT_PORT}"
                echo "=========================================================="
                checkout scm
            }
        }

        stage('Compile & Test') {
            steps {
                echo "=========================================================="
                echo "Stage 2: Compiling Java 17 sources and running test suite"
                echo "=========================================================="
                script {
                    if (params.SKIP_TESTS) {
                        echo "[WARN] Automated tests skipped per build parameter."
                        bat 'mvn clean compile'
                    } else {
                        bat 'mvn clean test'
                    }
                }
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: '**/target/selenium-screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('Package') {
            steps {
                echo "=========================================================="
                echo "Stage 3: Packaging production WAR artifact"
                echo "=========================================================="
                bat 'mvn package -DskipTests=true'
            }
        }

        stage('Archive Artifact') {
            steps {
                echo "=========================================================="
                echo "Stage 4: Archiving deployable WAR package"
                echo "=========================================================="
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo "=========================================================="
                echo "Stage 5: Deploying ${APP_NAME}.war to ${params.DEPLOY_ENV}"
                echo "=========================================================="
                script {
                    bat """
                        @echo off
                        echo [DEPLOY] Creating deployment destination directory: ${DEPLOY_PATH}
                        if not exist "${DEPLOY_PATH}" mkdir "${DEPLOY_PATH}"
                        echo [DEPLOY] Copying target\\${APP_NAME}.war to ${DEPLOY_PATH}\\${APP_NAME}.war
                        copy /Y "target\\${APP_NAME}.war" "${DEPLOY_PATH}\\${APP_NAME}.war"
                        echo [DEPLOY] Verifying deployed artifact:
                        dir "${DEPLOY_PATH}\\${APP_NAME}.war"
                        echo [DEPLOY] Deployment to ${params.DEPLOY_ENV} completed successfully!
                    """
                }
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo "CI/CD Pipeline Succeeded!"
            echo "Application ${APP_NAME}.war successfully built and deployed to [${params.DEPLOY_ENV}]."
            echo "Target Port / Service: ${params.TOMCAT_PORT}"
            echo "=========================================================="
        }
        failure {
            echo "[ERROR] Pipeline failed! Inspect stage console logs for root cause."
        }
    }
}
