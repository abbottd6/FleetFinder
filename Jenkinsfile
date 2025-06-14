pipeline {
  agent any

  environment {
    MAVEN_OPTS = "-Dmaven.test.failure.ignore=false"
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
          sh './mvnw clean test'
        }
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
  }
}