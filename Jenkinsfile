pipeline {
  agent any

  environment {
    MAVEN_OPTS = "-Dmaven.test.failure.ignore=false"
    GITHUB_TOKEN = credentials('GITHUB_TOKEN')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Test Backend') {
      steps {
        dir('backend') {
          sh './mvnw clean test | tee test-output.log'
          sh "grep 'Tests run' test-output.log || true"
        }
        echo 'Backend tests completed.'
      }
    }
  }

  post {
    failure {
      echo 'Tests failed. Pipeline stopped.'
    }
    success {
      echo 'Tests passed. Ready for next stage.'
    }
    always {
      junit 'backend/target/surefire-reports/*.xml'
    }
  }
}