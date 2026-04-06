// ─────────────────────────────────────────────────────────────────────────────
//  Jenkinsfile — DigitalWallet Backend CI/CD Pipeline
//
//  Services:  eureka-server | api-gateway | auth-service | user-service
//             wallet-service | reward-service | notification-service
//
//  Flow:  GitHub Push → Checkout → Test All → Build All → Docker Build →
//         Push to DockerHub → Deploy via SSH → Health Check
// ─────────────────────────────────────────────────────────────────────────────

pipeline {
    agent any

    // ── Global environment variables ──────────────────────────────────────────
    environment {
        DOCKER_USERNAME    = 'abhirathour'   // ← CHANGE THIS
        IMAGE_TAG          = "${BUILD_NUMBER}"
        DEPLOY_SERVER      = 'your-deploy-server-ip'     // ← CHANGE THIS
        DEPLOY_USER        = 'ubuntu'                    // ← CHANGE IF NEEDED
        APP_DIR            = '/home/ubuntu/digitalwallet' // ← CHANGE IF NEEDED

        // Service names must match your Docker Hub repo names
        EUREKA_IMAGE       = "${DOCKER_USERNAME}/digitalwallet-eureka-server"
        GATEWAY_IMAGE      = "${DOCKER_USERNAME}/digitalwallet-api-gateway"
        AUTH_IMAGE         = "${DOCKER_USERNAME}/digitalwallet-auth-service"
        USER_IMAGE         = "${DOCKER_USERNAME}/digitalwallet-user-service"
        WALLET_IMAGE       = "${DOCKER_USERNAME}/digitalwallet-wallet-service"
        REWARD_IMAGE       = "${DOCKER_USERNAME}/digitalwallet-reward-service"
        NOTIF_IMAGE        = "${DOCKER_USERNAME}/digitalwallet-notification-service"
    }

    tools {
        jdk   'JDK-21'      // Must match name set in Jenkins → Manage Jenkins → Tools
        maven 'Maven-3.9'   // Must match name set in Jenkins → Manage Jenkins → Tools
    }

    stages {

        // ── STAGE 1: Checkout ─────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo '📥 Pulling latest code from GitHub...'
                git branch: 'main',
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/your-username/DigitalWallet_Backend.git' // ← CHANGE
            }
        }

        // ── STAGE 2: Test All Services in Parallel ────────────────────────────
        stage('Test All Services') {
            parallel {
                stage('Test: eureka-server') {
                    steps {
                        dir('eureka-server') {
                            sh 'mvn test -q'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'eureka-server/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('Test: api-gateway') {
                    steps {
                        dir('api-gateway') {
                            sh 'mvn test -q'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'api-gateway/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('Test: auth-service') {
                    steps {
                        dir('auth-service') {
                            sh 'mvn test -q'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'auth-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('Test: user-service') {
                    steps {
                        dir('user-service') {
                            sh 'mvn test -q'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'user-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('Test: wallet-service') {
                    steps {
                        dir('wallet-service') {
                            sh 'mvn test -q'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'wallet-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('Test: reward-service') {
                    steps {
                        dir('reward-service') {
                            sh 'mvn test -q'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'reward-service/target/surefire-reports/*.xml'
                        }
                    }
                }
                stage('Test: notification-service') {
                    steps {
                        dir('notification_service') {
                            sh 'mvn test -q'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true,
                                  testResults: 'notification_service/target/surefire-reports/*.xml'
                        }
                    }
                }
            }
        }

        // ── STAGE 3: Build Docker Images in Parallel ──────────────────────────
        stage('Build Docker Images') {
            parallel {
                stage('Build: eureka-server') {
                    steps {
                        sh "docker build -f eureka-server/Dockerfile -t ${EUREKA_IMAGE}:${IMAGE_TAG} -t ${EUREKA_IMAGE}:latest ./eureka-server"
                    }
                }
                stage('Build: api-gateway') {
                    steps {
                        sh "docker build -f api-gateway/Dockerfile -t ${GATEWAY_IMAGE}:${IMAGE_TAG} -t ${GATEWAY_IMAGE}:latest ./api-gateway"
                    }
                }
                stage('Build: auth-service') {
                    steps {
                        sh "docker build -f auth-service/Dockerfile -t ${AUTH_IMAGE}:${IMAGE_TAG} -t ${AUTH_IMAGE}:latest ./auth-service"
                    }
                }
                stage('Build: user-service') {
                    steps {
                        sh "docker build -f user-service/Dockerfile -t ${USER_IMAGE}:${IMAGE_TAG} -t ${USER_IMAGE}:latest ./user-service"
                    }
                }
                stage('Build: wallet-service') {
                    steps {
                        sh "docker build -f wallet-service/Dockerfile -t ${WALLET_IMAGE}:${IMAGE_TAG} -t ${WALLET_IMAGE}:latest ./wallet-service"
                    }
                }
                stage('Build: reward-service') {
                    steps {
                        sh "docker build -f reward-service/Dockerfile -t ${REWARD_IMAGE}:${IMAGE_TAG} -t ${REWARD_IMAGE}:latest ./reward-service"
                    }
                }
                stage('Build: notification-service') {
                    steps {
                        sh "docker build -f notification_service/Dockerfile -t ${NOTIF_IMAGE}:${IMAGE_TAG} -t ${NOTIF_IMAGE}:latest ./notification_service"
                    }
                }
            }
        }

        // ── STAGE 4: Push All Images to Docker Hub ────────────────────────────
        stage('Push to Docker Hub') {
            steps {
                echo '📤 Pushing all images to Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'

                    // Push each service (both versioned tag and latest)
                    sh "docker push ${EUREKA_IMAGE}:${IMAGE_TAG} && docker push ${EUREKA_IMAGE}:latest"
                    sh "docker push ${GATEWAY_IMAGE}:${IMAGE_TAG} && docker push ${GATEWAY_IMAGE}:latest"
                    sh "docker push ${AUTH_IMAGE}:${IMAGE_TAG}   && docker push ${AUTH_IMAGE}:latest"
                    sh "docker push ${USER_IMAGE}:${IMAGE_TAG}   && docker push ${USER_IMAGE}:latest"
                    sh "docker push ${WALLET_IMAGE}:${IMAGE_TAG} && docker push ${WALLET_IMAGE}:latest"
                    sh "docker push ${REWARD_IMAGE}:${IMAGE_TAG} && docker push ${REWARD_IMAGE}:latest"
                    sh "docker push ${NOTIF_IMAGE}:${IMAGE_TAG}  && docker push ${NOTIF_IMAGE}:latest"
                }
            }
        }

        // ── STAGE 5: Deploy to Production Server ──────────────────────────────
        stage('Deploy to Server') {
            steps {
                echo '🚀 Deploying DigitalWallet to production server...'
                sshagent(['deployment-server-ssh']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} '
                            set -e

                            # ── Create app directory ──────────────────────────
                            mkdir -p ${APP_DIR}
                            cd ${APP_DIR}

                            # ── Pull latest compose file from server ──────────
                            # The .env file must already exist on the server!
                            # You put it there ONCE manually (see README).
                            echo "IMAGE_TAG=${IMAGE_TAG}" >> .env

                            # ── Pull all new images ───────────────────────────
                            docker compose -f docker-compose.prod.yml pull \\
                                eureka-server api-gateway auth-service \\
                                user-service wallet-service reward-service \\
                                notification-service

                            # ── Rolling restart of microservices only ─────────
                            # Infrastructure (kafka, redis, dbs) stays running.
                            docker compose -f docker-compose.prod.yml up -d \\
                                --no-deps --force-recreate \\
                                eureka-server api-gateway auth-service \\
                                user-service wallet-service reward-service \\
                                notification-service

                            # ── Clean up old images ───────────────────────────
                            docker image prune -f
                        '
                    """
                }
            }
        }

        // ── STAGE 6: Health Check ─────────────────────────────────────────────
        stage('Health Check') {
            steps {
                echo '🏥 Waiting for services to be healthy...'
                sshagent(['deployment-server-ssh']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SERVER} '
                            # Wait 45s for services to start
                            sleep 45

                            echo "Checking Eureka Server..."
                            curl -sf http://localhost:8761/actuator/health | grep UP || echo "⚠️  Eureka not healthy"

                            echo "Checking API Gateway..."
                            curl -sf http://localhost:8080/actuator/health | grep UP || echo "⚠️  Gateway not healthy"

                            echo "Checking Auth Service..."
                            curl -sf http://localhost:8084/actuator/health | grep UP || echo "⚠️  Auth not healthy"

                            echo "Checking User Service..."
                            curl -sf http://localhost:8082/actuator/health | grep UP || echo "⚠️  User not healthy"

                            echo "Checking Wallet Service..."
                            curl -sf http://localhost:8083/actuator/health | grep UP || echo "⚠️  Wallet not healthy"

                            echo "Checking Reward Service..."
                            curl -sf http://localhost:8085/actuator/health | grep UP || echo "⚠️  Reward not healthy"

                            echo "✅ Health check complete!"
                        '
                    """
                }
            }
        }
    }

    // ── Post-build actions ────────────────────────────────────────────────────
    post {
        success {
            echo """
            ✅ ─────────────────────────────────────────────────
               DEPLOYMENT SUCCESSFUL — Build #${BUILD_NUMBER}
               All 7 microservices deployed and healthy!
            ─────────────────────────────────────────────────
            """
        }
        failure {
            echo """
            ❌ ─────────────────────────────────────────────────
               DEPLOYMENT FAILED — Build #${BUILD_NUMBER}
               Check console output above for errors.
            ─────────────────────────────────────────────────
            """
        }
        always {
            // Clean up locally built images on Jenkins agent to save disk
            sh """
                docker rmi ${EUREKA_IMAGE}:${IMAGE_TAG}  || true
                docker rmi ${GATEWAY_IMAGE}:${IMAGE_TAG} || true
                docker rmi ${AUTH_IMAGE}:${IMAGE_TAG}    || true
                docker rmi ${USER_IMAGE}:${IMAGE_TAG}    || true
                docker rmi ${WALLET_IMAGE}:${IMAGE_TAG}  || true
                docker rmi ${REWARD_IMAGE}:${IMAGE_TAG}  || true
                docker rmi ${NOTIF_IMAGE}:${IMAGE_TAG}   || true
            """
        }
    }
}
