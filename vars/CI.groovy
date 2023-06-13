def call() {
  pipeline {
    agent any

    stages {
      stage('Checkout') {
        steps {
          cleanWs()
          git branch: 'main', url: "https://github.com/kiran6055/deops-asswssment"
          sh 'env'
        }
      }

      stage('Compile/Build') {
        steps {
          sh '''
             cd my-app
             mvn clean install
          
             
         '''
        }

      }

      stage('Unit Tests') {
        steps {
          sh 'echo test'
        }
      }

      stage('Quality Control') {
        steps {
          sh "echo Sonar Scan"
        }
      }




      stage('Upload Code to Centralized Place') {
        steps {
          sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 855602409808.dkr.ecr.us-east-1.amazonaws.com"
          sh "docker push 855602409808.dkr.ecr.us-east-1.amazonaws.com/deops-asswssment:1.0.0"

        }
      }
    }
  }
}


