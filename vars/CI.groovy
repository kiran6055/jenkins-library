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
          dir('my-app') {
            sh 'mvn clean install'
            sh 'mvn package && cp target/my-app-0.0.1-SNAPSHOT.jar my-app.jar'
          }
          sh 'docker build -t 855602409808.dkr.ecr.us-east-1.amazonaws.com/deops-asswssment .'
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
//          sh "curl -v -u admin:admin123 --upload-file /path/to/my-app.jar http://172.31.83.87:8081/repository/deops-asswssment/my-app.jar"
         sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 855602409808.dkr.ecr.us-east-1.amazonaws.com"
         sh "docker push 855602409808.dkr.ecr.us-east-1.amazonaws.com/deops-asswssment"


        }
      }
    }
  }
}