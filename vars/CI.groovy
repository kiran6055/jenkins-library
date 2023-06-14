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
            sh "curl -v -u ${NEXUS_USER}:${NEXUS_PASS} --upload-file ${component}-${TAG_NAME}.zip http://172.31.1.57:8081/repository/${component}/${component}-${TAG_NAME}.zip"


        }
      }
    }
  }
}