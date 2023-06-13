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
             mvn package && cp target/my-app-0.0.1-SNAPSHOT.jar my-app.jar
          
             
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
          sh "curl -v -u admin:admin123 --upload-file /path/to/my-app.jar http://172.31.83.87:8081/repository/deops-asswssment/my-app.jar"

        }
      }
    }
  }
}