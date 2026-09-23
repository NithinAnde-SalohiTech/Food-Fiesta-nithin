pipeline {
    agent any

    environment {
        APP_EC2_IP = '13.201.98.44'

        DOCKER_IMAGE = 'nithinandedocker/food:latest'

        SONAR_PROJECT_KEY = 'nithinande-salohitech'
        SONAR_ORG = 'nithinande-salohitech'

        DOCKER_CREDENTIALS = 'dockerhub-credentials'
        SONAR_CREDENTIALS = 'SONAR_ID'
    }

    tools {
        jdk 'JAVA-21'
        maven 'MAVEN'
    }

    stages {

        stage('Git Checkout') {
            steps {
                git(
                    url: 'https://github.com/NithinAnde-SalohiTech/Food-Fiesta-nithin.git',
                    branch: 'main'
                )
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withCredentials([
                    string(
                        credentialsId: "${SONAR_CREDENTIALS}",
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh '''
                        mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.6.0.6792:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.organization=${SONAR_ORG} \
                        -Dsonar.host.url=https://sonarcloud.io \
                        -Dsonar.token=${SONAR_TOKEN}
                    '''
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${DOCKER_IMAGE} .
                """
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKER_CREDENTIALS}",
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login \
                        -u "$DOCKER_USERNAME" \
                        --password-stdin
                    '''
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                sh """
                    docker push ${DOCKER_IMAGE}
                """
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(['EC2-SSH-CREDENTIAL']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${APP_EC2_IP} '
                            docker pull ${DOCKER_IMAGE}

                            docker stop myapp || true
                            docker rm myapp || true

                            docker run -d \
                                --name myapp \
                                -p 8085:8085 \
                                ${DOCKER_IMAGE}

                            docker ps
                        '
                    """
                }
            }
        }
    }

    post {

        success {
            echo 'Pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the Jenkins console output.'
        }

        always {
            sh 'docker logout || true'
        }
    }
}
