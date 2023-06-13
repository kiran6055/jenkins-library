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
          NEXUS_PASS = sh ( script: 'aws ssm get-parameters --region us-east-1 --names nexus.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
          NEXUS_USER = sh ( script: 'aws ssm get-parameters --region us-east-1 --names nexus.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
          wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${NEXUS_PASS}", var: 'SECRET']]]) {
            sh "curl -v -u admin:admin123 --upload-file pom.xml http://172.31.83.87:8081/repository/maven-releases/org/foo/1.0/foo-1.0.pom"

          }

        }
      }
    }
  }
}