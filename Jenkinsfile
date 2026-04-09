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
                withCredentials([
                    string(credentialsId: 'JWT_SECRET_KEY', variable: 'JWT_SECRET_KEY'),
                    string(credentialsId: 'DB_URL', variable: 'DB_URL'),
                    string(credentialsId: 'DB_USERNAME', variable: 'DB_USERNAME'),
                    string(credentialsId: 'DB_PASSWORD', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'REDIS_PASSWORD', variable: 'REDIS_PASSWORD'),
                    string(credentialsId: 'R2_STORAGE_ACCESS_KEY', variable: 'R2_STORAGE_ACCESS_KEY'),
                    string(credentialsId: 'R2_STORAGE_SECRET_KEY', variable: 'R2_STORAGE_SECRET_KEY'),
                    string(credentialsId: 'RABBITMQ_USERNAME', variable: 'RABBITMQ_USERNAME'),
                    string(credentialsId: 'RABBITMQ_PASSWORD', variable: 'RABBITMQ_PASSWORD'),
                    string(credentialsId: 'FIREBASE_CONFIG_JSON', variable: 'FIREBASE_CONFIG_JSON')
                ]) {
                        sshagent(['spring-server-key']) {
                            sh """
                            ssh -o StrictHostKeyChecking=no ubuntu@10.0.0.183 "
                                docker pull ${DOCKER_HUB_ID}/${APP_NAME}:latest &&
                                docker stop ${APP_NAME} || true &&
                                docker rm ${APP_NAME} || true &&

                                docker run -d --name ${APP_NAME} \
                                    -p 5000:5000 \
                                    -e JWT_SECRET_KEY='${JWT_SECRET_KEY}' \
                                    -e DB_URL='${DB_URL}' \
                                    -e DB_USERNAME='${DB_USERNAME}' \
                                    -e DB_PASSWORD='${DB_PASSWORD}' \
                                    -e REDIS_PASSWORD='${REDIS_PASSWORD}' \
                                    -e R2_STORAGE_ACCESS_KEY='${R2_STORAGE_ACCESS_KEY}' \
                                    -e R2_STORAGE_SECRET_KEY='${R2_STORAGE_SECRET_KEY}' \
                                    -e RABBITMQ_USERNAME='${RABBITMQ_USERNAME}' \
                                    -e RABBITMQ_PASSWORD='${RABBITMQ_PASSWORD}' \
                                    -e FIREBASE_CONFIG_JSON='${FIREBASE_CONFIG_JSON}' \
                                    ${DOCKER_HUB_ID}/${APP_NAME}:latest
                            "
                            """
                        }
                    }
                }
            }
        }
}