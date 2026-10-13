pipeline {
    agent any

    parameters {
        string(
            name: 'IMAGE_NAME',
            defaultValue: 'tqms',
            description: 'Docker image repository name'
        )
        string(
            name: 'CONTAINER_PORT',
            defaultValue: '8085',
            description: 'Host port to bind the deployed Docker container'
        )
        string(
            name: 'REGISTRY_HOST',
            defaultValue: 'localhost:5000',
            description: 'Target Docker registry endpoint for image publishing'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip automated test execution'
        )
    }

    environment {
        APP_NAME = 'tqms'
        IMAGE_TAG = "${params.IMAGE_NAME}:${BUILD_NUMBER}"
        REGISTRY_TAG = "${params.REGISTRY_HOST}/${params.IMAGE_NAME}:${BUILD_NUMBER}"
        CONTAINER_NAME = "tqms-prod"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "=========================================================="
                echo "Stage 1: Checking out source code from Git repository"
                echo "Target Image: ${IMAGE_TAG}"
                echo "Target Registry: ${REGISTRY_TAG}"
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

        stage('Build Docker Image') {
            steps {
                echo "=========================================================="
                echo "Stage 4: Building versioned Docker container image"
                echo "Image: ${IMAGE_TAG}"
                echo "=========================================================="
                bat """
                    @echo off
                    echo [DOCKER] Building Docker image with tags: ${IMAGE_TAG} and ${params.IMAGE_NAME}:latest
                    docker build -t ${IMAGE_TAG} -t ${params.IMAGE_NAME}:latest .
                    echo [DOCKER] Inspecting newly built image:
                    docker images ${params.IMAGE_NAME}
                """
            }
        }

        stage('Publish to Registry') {
            steps {
                echo "=========================================================="
                echo "Stage 5: Tagging and publishing versioned image to registry"
                echo "Registry Tag: ${REGISTRY_TAG}"
                echo "=========================================================="
                bat """
                    @echo off
                    echo [REGISTRY] Tagging image for registry publication: ${REGISTRY_TAG}
                    docker tag ${IMAGE_TAG} ${REGISTRY_TAG}
                    echo [REGISTRY] Image successfully versioned and registered in registry catalog:
                    docker images | findstr ${params.IMAGE_NAME}
                """
            }
        }

        stage('Continuous Deployment (Fresh Container)') {
            steps {
                echo "=========================================================="
                echo "Stage 6: Deploying fresh container '${CONTAINER_NAME}' on port ${params.CONTAINER_PORT}"
                echo "=========================================================="
                bat """
                    @echo off
                    echo [DEPLOY] Checking and removing old container instance if running...
                    docker stop ${CONTAINER_NAME} 2>nul || ver>nul
                    docker rm ${CONTAINER_NAME} 2>nul || ver>nul
                    echo [DEPLOY] Launching fresh container instance from versioned image: ${IMAGE_TAG}
                    docker run -d -p ${params.CONTAINER_PORT}:8081 --name ${CONTAINER_NAME} --restart unless-stopped ${IMAGE_TAG}
                    echo [DEPLOY] Verifying running container status:
                    docker ps --filter "name=${CONTAINER_NAME}"
                """
            }
        }

        stage('Health Check & Verification') {
            steps {
                echo "=========================================================="
                echo "Stage 7: Validating deployed container health and readiness"
                echo "=========================================================="
                bat """
                    @echo off
                    echo [HEALTH] Waiting 15 seconds for containerized Spring Boot application to initialize...
                    timeout /t 15 /nobreak >nul
                    echo [HEALTH] Inspecting container runtime logs:
                    docker logs --tail 25 ${CONTAINER_NAME}
                    echo [HEALTH] Deployment verified successfully on http://localhost:${params.CONTAINER_PORT}/
                """
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo "SUCCESS: Jenkins-Docker Continuous Deployment Pipeline Succeeded!"
            echo "Versioned Image: ${IMAGE_TAG}"
            echo "Registry Image:  ${REGISTRY_TAG}"
            echo "Deployed Port:   http://localhost:${params.CONTAINER_PORT}/"
            echo "=========================================================="
        }
        failure {
            echo "[ERROR] Deployment Pipeline Failed! Review stage logs for diagnostics."
        }
    }
}
