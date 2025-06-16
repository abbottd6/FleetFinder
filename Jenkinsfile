pipeline {
  agent any

  environment {
    MAVEN_OPTS = "-Dmaven.test.failure.ignore=false"
  }

  stages {
    stage('Debug Env') {
      steps {
        script {
          sh 'printenv | sort'
        }
      }
    }

    stage('Skip Redundant Build') {
      when {
        expression {
            return env.GIT_COMMIT != null && env.GIT_PREVIOUS_SUCCESSFUL_COMMIT != null
        }
      }
      steps {
        script {
          def current_commit = env.GIT_COMMIT
          def previous_commit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT

          if (current_commit == previous_commit) {
            currentBuild.result = 'NOT_BUILT'
            error("This commit (${current_commit}) has already been successfully built. Skipping.")
          }
        }
      }
    }

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Test Backend') {
      when {
        expression {
          return env.BRANCH_NAME != 'dev_main' && env.BRANCH_NAME != 'prod_main' && env.CHANGE_TARGET == 'dev_main'
        }
      }
      steps {
        dir('backend') {
          sh './mvnw clean test | tee test-output.log'
          sh "grep 'Tests run' test-output.log || true"
        }
        echo 'Backend tests completed.'
        script {
         env.TEST_STAGE_EXECUTED = 'true'
        }
      }
      post {
        unsuccessful {
          error('Tests failed. Marking pipeline as failed.')
        }
      }
    }

    stage('PR dev_main into prod_main') {
      when {
        expression {
          return !env.CHANGE_ID && env.CHANGE_TARGET != 'prod_main' && env.BRANCH_NAME == 'dev_main'
        }
      }

      steps {
        script {
            withCredentials([string(credentialsId: 'github-pr-token', variable: 'GITHUB_TOKEN')]) {
              sh '''
                curl -X POST -H "Authorization: token ${GITHUB_TOKEN}" \
                       -H "Accept: application/vnd.github.v3+json" \
                       https://api.github.com/repos/abbottd6/FleetFinder/pulls \
                       -d '{
                         "title": "CI: Merge dev_main into prod_main",
                         "head": "dev_main",
                         "base": "prod_main",
                         "body": "Automated PR created by Jenkins pipeline."
                       }'
              '''
            }
        }
      }
    }

    stage('Tag PR merge into prod_main') {
      when {
        expression {
          return env.BRANCH_NAME == 'prod_main'
        }
      }

      environment {
        GITHUB_TOKEN = credentials('github-pr-token')
      }

      steps {
        script {
          def lastTag = sh(
            script: "git tag | grep '^release-v' | sort -V | tail -n 1",
            returnStdout: true
          ).trim()

          def newTag = "release-v1.1"

          if (lastTag) {
            def versionParts = lastTag.replace('release-v', '').tokenize('.')
            def major = versionParts[0].toInteger()
            def minor = versionParts[1].toInteger() + 1
            newTag = "release-v${major}.${minor}"
          }

          sh """
            git checkout prod_main
            git config user.name "Jenkins CI"
            git config user.email "jenkins@scfleetfinder.com"
            git fetch origin
            git tag -f ${newTag}
            git push https://${GITHUB_TOKEN}@github.com/abbottd6/FleetFinder.git refs/tags/${newTag} --force
          """
        }
      }
    }

    stage('Build Docker Images') {
      when {
        expression {
          return env.BRANCH_NAME == 'prod_main'
        }
      }

      environment {
        FRONTEND_REPO = credentials('frontend-ecr-repo')
        BACKEND_REPO = credentials('backend-ecr-repo')
      }

      steps {
        script {
          def timestamp = System.currentTimeMillis().intdiv(1000)
          def frontendRepo = env.FRONTEND_REPO
          def backendRepo = env.BACKEND_REPO
          def frontendTag = "${frontendRepo}:cache_${timestamp}"
          def backendTag = "${backendRepo}:cache_${timestamp}"

          env.FRONTEND_IMAGE_TAG = frontendTag
          env.BACKEND_IMAGE_TAG = backendTag

          dir('frontend/angular-fleets') {
            sh 'docker build --no-cache -t $FRONTEND_IMAGE_TAG .'
          }

          dir('backend') {
            sh './mvnw clean package -DskipTests'
            sh 'docker build --no-cache -t $BACKEND_IMAGE_TAG .'
          }
        }
      }

    }

    stage('Push Images to ECR') {
      when {
        expression {
          return env.BRANCH_NAME == 'prod_main'
        }
      }

      environment {
        ECR_REGISTRY = credentials('ecr-registry-url')
        AWS_REGION = 'us-west-2'
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'aws-ecr-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
          script {
            sh '''
              aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}

              docker push ${FRONTEND_IMAGE_TAG}
              docker push ${BACKEND_IMAGE_TAG}
            '''
          }
        }
      }
    }

    stage('Deploy to EC2') {
      when {
        expression {
          return env.BRANCH_NAME == 'prod_main'
        }
      }

      environment {
        REMOTE_USER = 'ec2-user'
        REMOTE_HOST = credentials('EC2_HOST')
        REMOTE_DIR = '/home/ec2-user/FleetFinder/deployment'
      }

      steps {
        sshagent(credentials: ['ec2-ssh-key']) {
          script {
            sh '''
              ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} '
                cd ${REMOTE_DIR} &&
                sed -i "s|image:.*fleetfinder-backend:.*|image: ${BACKEND_IMAGE_TAG}|" docker-compose.yml &&
                sed -i "s|image:.*fleetfinder-frontend:.*|image: ${FRONTEND_IMAGE_TAG}|" docker-compose.yml &&
                docker-compose --env-file .env.prod pull &&
                docker-compose --env-file .env.prod down &&
                docker-compose --env-file .env.prod up -d
              '
            '''
          }
        }
      }
    }
  }

  post {
    failure {
      echo 'Pipeline failed. Attempting to print last llines of test output:'
      script {
        def logFile = 'backend/test-output.log'
        if (fileExists(logFile)) {
          def tail = sh(script: "tail -n 25 ${logFile}", returnStdout: true).trim()
          echo "Last 25 lines of test-output.log:\n${tail}"
        } else {
          echo "No test-output.log found."
        }
      }
    }
    success {
      echo 'Pipeline run successfully.'
    }
    always {
      script {
        def logDir = 'backend/target/surefire-reports'
        if (env.TEST_STAGE_EXECUTED == 'true' && (fileExists(logFile))) {
          dir('backend') {
            junit 'target/surefire-reports/*.xml'
            publishChecks name: 'JUnit Test Results', conclusion: testResults.failCount == 0 ? 'success' : 'failure', output: [
              title: 'JUnit Summary',
              summary: "${testResults.totalCount} test(s) run, ${testResults.failCount} failed, ${testResults.skipCount} skipped."
            ]
          }
        } else {
          echo "Skip JUnit parsing: Test stage was not run in this execution of the pipeline."
        }
      }
    }
  }
}