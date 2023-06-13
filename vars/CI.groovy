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
          sh "mvn clean compile"
        }
      }

      stage('Unit Tests') {
        steps {
          sh 'mvn test'
        }
      }

      stage('Quality Control') {
        steps {
          sh "echo Sonar Scan"
        }
      }

      stage('Build Package') {
        steps {
          sh """
                        cd my-app
                        mvn clean install
                        cd target
                        java -jar my-app-0.0.1-SNAPSHOT.jar
                    """
        }
      }

      stage('Upload Code to Centralized Place') {
        steps {
          echo "Pushing code to artifact repository"
        }
      }
    }
  }
}
