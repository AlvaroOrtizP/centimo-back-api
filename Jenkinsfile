pipeline {
    agent any

    environment {
        // Formato de imagen para GHCR: ghcr.io/usuario/repositorio
        REGISTRY = 'ghcr.io'
        IMAGE_NAME = 'alorpes/deepdive-record-api' // Sustituye por tu usuario/repo
    }

    triggers {
        // Equivalente al "schedule: cron '20 9 * * *'"
        cron('20 9 * * *')
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Image') {
            steps {
                // Construcción local de la imagen
                sh "docker build -t ${REGISTRY}/${IMAGE_NAME}:latest ."
            }
        }

        stage('Push & Sign (GHCR)') {
            when {
                branch 'main'
            }
            steps {
                // Usa tu Personal Access Token de GitHub guardado en Jenkins
                withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GH_USER', passwordVariable: 'GH_PAT')]) {
                    // Login en GitHub Container Registry
                    sh 'echo $GH_PAT | docker login ghcr.io -u $GH_USER --password-stdin'

                    // Subida de la imagen
                    sh "docker push ${REGISTRY}/${IMAGE_NAME}:latest"

                    // Obtener el digest e intentar firmar con Cosign (si está instalado en la VM)
                    script {
                        def digest = sh(script: "docker inspect --format='{{index .RepoDigests 0}}' ${REGISTRY}/${IMAGE_NAME}:latest", returnStdout: true).trim()
                        sh "cosign sign --yes ${digest} || echo 'Cosign no instalado en la VM, omitiendo firma'"
                    }
                }
            }
        }

        stage('Deploy to Render') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([string(credentialsId: 'render-hook-app2', variable: 'RENDER_HOOK')]) {
                    sh 'curl -X POST $RENDER_HOOK'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
            sh 'docker logout ghcr.io || true'
        }
    }
}