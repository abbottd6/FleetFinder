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

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Test Backend') {
      when {
        expression {
          return env.BRANCH_NAME != 'dev_main'
          && env.BRANCH_NAME != 'prod_main'
          && env.CHANGE_TARGET == 'dev_main'
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

    stage('Tag dev_main Merge') {
      when {
        expression {
          return env.BRANCH_NAME == 'dev_main'
        }
      }

      environment {
          GITHUB_TOKEN = credentials('github-tag-version-token')
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
            git checkout dev_main
            git pull origin dev_main
            git config user.name "Jenkins CI"
            git config user.email "jenkins@scfleetfinder.com"
            git tag ${newTag}
            git push https://$GITHUB_TOKEN@github.com/abbottd6/FleetFinder.git ${newTag}
          """
        }
      }
    }

    stage('PR dev_main into prod_main') {
      when {
        expression {
          return env.BRANCH_NAME == 'dev_main'
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

    stage('Tag prod_main Release Version') {
      when {
        expression {
          return env.BRANCH_NAME == 'prod_main'
        }
      }

      environment {
        GITHUB_TOKEN = credentials('github-tag-version-token')
      }

      steps {
        script {
          sh 'git checkout dev_main'


          lastTag = sh(
            script: "git tag | grep '^release-v' | sort -V | tail -n 1",
            returnStdout: true
          ).trim()

          newTag = 'release-v1.4'

          if(lastTag) {
            newTag = lastTag
          }

          if(!lastTag) {
            error "Unable to access previous tag from dev_main"
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
        
          dir('frontend') {
            sh 'docker build --no-cache -t ${frontendTag} .'
          }

          dir('backend') {
            sh './mvnw clean package -DskipTests'
            sh 'docker build --no-cache -t ${backendTag} .'
          }

          env.FRONTEND_IMAGE_TAG = frontendTag
          env.BACKEND_IMAGE_TAG = backendTag
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
              aws configure set aws_access_key_id ${AWS_CREDS_USR}
              aws configure set aws_secret_access_key ${AWS_CREDS_PSW}
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
        if (env.TEST_STAGE_EXECUTED == 'true') {
          dir('backend') {
            junit 'target/surefire-reports/*.xml'
          }
        } else {
          echo "Skip JUnit parsing: Test stage was not run in this execution of the pipeline."
        }
      }
    }
  }
}