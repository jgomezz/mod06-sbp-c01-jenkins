
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

### Step 2: Connect to EC2

#### Mac/Linux
```
chmod 400 jenkins-key.pem
ssh -i jenkins-key.pem ubuntu@54.123.45.67
```

### Step 3: Install Docker 

#### Update system
```
sudo apt update
```
#### Install Docker
```
sudo apt install -y docker.io
```
#### Start Docker
```
sudo systemctl start docker
sudo systemctl enable docker
```
#### Allow ubuntu user to use Docker (no need for sudo)
```
sudo usermod -aG docker ubuntu
```
#### Apply group changes
```
newgrp docker
```
#### Verify Docker works
```
docker --version

```

### Step 4: Run Jenkins Container (ONE COMMAND!)
