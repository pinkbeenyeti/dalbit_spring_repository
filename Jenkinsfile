pipeline {
    agent any

    environment {
        DOCKER_HUB_ID = 'pinkbeenyeti' // 본인 도커 허브 ID
        APP_NAME = 'dalbit-app'
    }

    stages {
        stage('1. Checkout') {
            steps { checkout scm } // GitHub에서 코드 가져오기
        }

        stage('2. Build Gradle') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean bootJar' // Jar 파일 생성
            }
        }

        stage('3. Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry('', 'docker-access-token') {
                        def appImage = docker.build("${DOCKER_HUB_ID}/${APP_NAME}:${env.BUILD_NUMBER}")
                        appImage.push()
                        appImage.push('latest')
                    }
                }
            }
        }

        stage('4. Deploy to VM 2') {
            steps {
                sshagent(['spring-server-key']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ubuntu@10.0.0.183 "
                        docker pull ${DOCKER_HUB_ID}/${APP_NAME}:latest &&
                        docker stop ${APP_NAME} || true &&
                        docker rm ${APP_NAME} || true &&
                        docker run -d --name ${APP_NAME} -p 5000:5000 ${DOCKER_HUB_ID}/${APP_NAME}:latest
                    "
                    """
                }
            }
        }
    }
}