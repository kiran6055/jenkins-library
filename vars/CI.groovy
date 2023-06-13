def call() {

  pipeline {

    stage('Checkout') {
      cleanWs()
      git branch: 'main', url: "https://github.com/kiran6055/deops-asswssment"
      sh 'env'
    }

    stage('Compile/Build') {
      sh "mvn clean compile "
    }

    stage('Unit Tests') {
      sh 'mvn test'
    }

    stage('Quality Control') {
      SONAR_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.password  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
      SONAR_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
      print SONAR_PASS
      print SONAR_USER
      wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
        // sh "sonar-scanner -Dsonar.host.url=http://172.31.29.123:9000 -Dsonar.login=${SONAR_USER} -Dsonar.password=${SONAR_PASS} -Dsonar.projectKey=${COMPONENT} -Dsonar.qualitygate.wait=true ${SONAR_EXTRA_OPTS}"
        sh "echo Sonar Scan"
      }
    }

    stage('Build Package') {
      sh "cd my-app"
      sh "mvn clean install"
      sh "cd target"
      sh "java -jar my-app-0.0.1-SNAPSHOT.jar"
    }


    stage('Upload Code to Centralized Place') {
      sh "echo ${TAG_NAME} >VERSION"
      sh "zip -r deops-asswssment-${TAG_NAME}.zip * deops-asswssment.jar VERSION ${extraFiles}"
      NEXUS_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names nexus.password  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
      NEXUS_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names nexus.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
      wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${NEXUS_PASS}", var: 'SECRET']]]) {
        sh "curl -v -u ${NEXUS_USER}:${NEXUS_PASS} --upload-file ${COMPONENT}-${TAG_NAME}.zip http://172.31.83.87:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip"
      }
    }



  }
}

