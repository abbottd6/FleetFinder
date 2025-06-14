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

    stage('Tag Commit') {
      when {
        branch 'dev_main'
      }

      steps {
        script {
          def tagName = "build-${env.BUILD_NUMBER}"
          sh """
            git config user.name "Jenkins CI"
            git config user.email "jenkins@scfleetfinder.com"
            git tag ${tagName}
            git push https://abbottd6:${env.GITHUB_TOKEN}@github.com/abbottd6/FleetFinder.git ${tagName}
          """
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