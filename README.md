
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