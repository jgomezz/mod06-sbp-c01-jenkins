# Chapter I : Basic CI/CD

## PART 1: Launch EC2 Instance

### Step 1: Create EC2
```
- AWS Console → EC2 → Launch Instance
- Configure:
    Name: jenkins-docker
    AMI: Ubuntu Server 22.04 LTS
    Instance type: t2.small (or t2.micro)
    Key pair: Create new jenkins-key
    Security group rules:
        SSH (22) → My IP
        Custom TCP (8080) → Anywhere
- Storage: 20 GB
- Launch
- Get IP address (example: 54.123.45.67)
```

## PART 2: Install Docker & Run Jenkins

### Step 2: Connect to EC2

#### Mac/Linux
```
chmod 400 jenkins-key.pem
ssh -i jenkins-key.pem ubuntu@54.123.45.67
```

### Step 3: Install Docker 

```
# Update system
sudo apt update

# Install Docker
sudo apt install -y docker.io

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Allow ubuntu user to use Docker (no need for sudo)
sudo usermod -aG docker ubuntu

# Apply group changes
newgrp docker

# Verify Docker works
docker --version
```

### Step 4: Run Jenkins Container
```
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --restart unless-stopped \
  jenkins/jenkins:lts-jdk17
```

### Step 5: Get Jenkins Password

#### Get initial password
```
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Step 6: Open Jenkins
```
http://54.123.45.67:8080
(use YOUR EC2 IP)
```
```
- Paste password → Continue
- Install suggested plugins → Wait
- Create admin user:
    Username: admin
    Password: admin123
- Start using Jenkins
```

## PART 3: Install Maven & Git in Jenkins Container

### Step 7: Access Jenkins Container
```
docker exec -u root -it jenkins bash
```

### Step 8: Install Tools Inside Container
```
# Update package list
apt-get update

# Install Maven
apt-get install -y maven

# Install Git
apt-get install -y git

# Verify installations
mvn -version
git --version

# Exit container
exit
```

### Step 9: Configure Tools in Jenkins
```
- Manage Jenkins → Tools
- JDK:
    Add JDK
    Name: Java
- Maven:
  Add Maven
  Name: Maven
  Uncheck "Install automatically"
  MAVEN_HOME: /usr/share/maven
- Git:
  Should auto-detect
  Path: /usr/bin/git
- Save
```
<img src="images/JDK_installations.png">

<img src="images/Git_installations.png">

<img src="images/Maven_installations.png">

## PART 4: Create Simple Spring Boot App (Same as Before)

### Step 10: Create Project

#### File 4: Jenkinsfile
```
pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'Java'
        // git 'Git'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Getting code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building...'
                dir('user-service') {  // Change to user-service directory
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Testing...'
                dir('user-service') {  // Change to user-service directory
                    sh 'mvn test'
                }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Creating JAR...'
                dir('user-service') {  // Change to user-service directory
                    sh 'mvn package -DskipTests'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Success in Docker!'
            archiveArtifacts 'user-service/target/*.jar'
        }
        failure {
            echo '❌ Failed!'
        }
    }
}
```

## PART 5: Push to GitHub & Setup Pipeline

### Step 11: Push to GitHub
```

```
### Step 12: Create Pipeline in Jenkins
```
- Jenkins → New Item
- Name: docker-pipeline
- Type: Pipeline
- Configure:
    - Pipeline → Definition: Pipeline script from SCM
    - SCM: Git
    - Repository URL: https://github.com/jgomezz/mod06-sbp-c01-jenkins.git
    - Branch: */main
    - Script Path: Jenkinsfile
- Save
```
<img src="images/Pipeline.png">

### Step 13: Run Build
```
- Click "Build Now"
- Click #1 → Console Output
- Wait for "✅ Success in Docker!"
```

## PART 6: Setup Webhook (Automatic Builds)

### Step 14: GitHub Webhook
```
- GitHub repo → Settings → Webhooks → Add webhook
- Payload URL: http://IP:8080/github-webhook/
- Content type: application/json
- Add webhook
```
<img src="images/GitHub_Webhook.png">


### Step 15: Enable in Jenkins
```
- Pipeline → Configure
- Build Triggers:
    ✅ GitHub hook trigger for GITScm polling
- Save
```
<img src="images/Pipeline_Webhook.png">

### Step 16: Do change, push and build starts automatically!

_

# Chapter II : GitHub Credentials in Jenkins

### Step 1: Create GitHub Personal Access Token:
- Go to: GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
- Click "Generate new token (classic)"
- Name: Jenkins 
- Select scopes:
  - ✅ repo (all)
  - ✅ admin:repo_hook
- Click "Generate token"
- Copy the token (you won't see it again!)

<img src="images/GitHub_Token.png">

### Step 2: Add GitHub Credentials

- Go to: Manage Jenkins → Credentials → System → Global credentials → Add Credentials

<img src="images/Jenkins_add_credential.png">

- Configure:
  - Kind: Username with password
  - Username: your-github-username
  - Password: your-github-personal-access-token
  - ID: github-credentials
  - Description: GitHub Access Token
- Click: "Create"

<img src="images/Jenkins_create_credential.png">

### Step 3: Add GitHub Credential to Pipeline

<img src="images/Pipeline_add_GitHub_credential.png">


# Chapter III : Hide Credentials in Jenkins Pipeline

### Step 1: Setting Variables in Jenkins Pipeline

- Create credentials in Jenkins:
  - Manage Jenkins → Manage Credentials 
  - Add three "Secret text" credentials:
    - ID: db-url, Secret: jdbc:postgresql://postgres-user:5432/userdb
    - ID: db-username, Secret: postgres
    - ID: db-password, Secret: postgres

### Step 2: Update Jenkinsfile to Use Credentials

```
    environment {
        // Database credentials from Jenkins
        DB_URL = credentials('db-url')
        DB_USERNAME = credentials('db-username')
        DB_PASSWORD = credentials('db-password')
    }
    .
    .
    .
    stage('Docker Build') {
        steps {
            echo '🐳 Building Docker image...'
            dir('user-service') {
                script {
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        # docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
    }
```

### Step 3: Docker Socket Mounting

```declarative
    # Stop Jenkins
    docker stop jenkins

    # REMOVE the old container (can't add volumes to existing container!)
    docker rm jenkins

    # Start with Docker socket mounted
    docker run -d \
        --name jenkins \
        -p 8080:8080 \
        -p 50000:50000 \
        -v jenkins_home:/var/jenkins_home \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v /usr/bin/docker:/usr/bin/docker \
        --restart unless-stopped \
        jenkins/jenkins:lts-jdk17

    # Fix permissions
    docker exec -u root jenkins chmod 666 /var/run/docker.sock

```
### Step 4: Install Maven in Jenkins Container
```declarative
docker exec -u root -it jenkins bash

# Update package list
apt-get update

# Install Maven
apt-get install -y maven

```

### Step 5: Install ONLY Docker Compose Plugin in EC2
```
# 1. Add Docker's GPG key
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# 2. Add Docker repository (to get docker-compose-plugin)
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 3. Update package list
sudo apt update

# 4. Install ONLY Docker Compose plugin
sudo apt install docker-compose-plugin -y
```