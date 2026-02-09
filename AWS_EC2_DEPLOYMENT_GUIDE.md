# AWS EC2 Deployment Guide - Event Management System

## Overview
This document provides a comprehensive step-by-step guide for deploying the Event Management System on AWS EC2 instances. The backend uses Java Spring Boot with MySQL database hosted on Railway, and the frontend is a React application.

---

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Changes Made for EC2 Deployment](#changes-made-for-ec2-deployment)
3. [Backend Deployment (Java Spring Boot)](#backend-deployment-java-spring-boot)
4. [Frontend Deployment (React)](#frontend-deployment-react)
5. [Environment Variables Configuration](#environment-variables-configuration)
6. [Security & Access](#security--access)
7. [Verification & Testing](#verification--testing)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### AWS Setup (Free Tier Eligible)
- AWS Account with EC2 free tier access
- 2 EC2 instances recommended:
  - **Instance 1**: t3.micro (1 vCPU, 1GB RAM) - Amazon Linux 2023 AMI
  - **Instance 2**: t3.micro (1 vCPU, 1GB RAM) - Amazon Linux 2023 AMI
- Security Groups configured with appropriate inbound/outbound rules
- Elastic IPs assigned (optional but recommended for static IP)

**Free Tier Benefits**:
- 750 hours/month of t3.micro instances (combined)
- 30GB of EBS storage (combined across both instances)
- No charges if usage stays within free tier limits

### Local Requirements
- Git installed
- SSH client installed
- Access to Railway database credentials

### Required Access
- SSH key pair (.pem file) downloaded from AWS
- Railway MySQL database credentials (hostname, username, password)
- GitHub repository access (if using Git)

---

## Changes Made for EC2 Deployment

### 1. **Application Configuration** 
**File**: `backend/src/main/resources/application.yml`

**Changes**:
- Removed hardcoded database credentials
- Updated to use environment variables for all sensitive data
- Configuration now reads from:
  - `DATABASE_URL`: Full JDBC connection string to Railway MySQL
  - `DATABASE_USER`: Database username
  - `DATABASE_PASSWORD`: Database password
  - `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`: Email configuration

**Before**:
```yaml
datasource:
  url: jdbc:mysql://turntable.proxy.rlwy.net:48482/railway?useSSL=false&...
  username: root
  password: UOUrchBLOLddRwwRybIrVuHvgCAqwTDZ
```

**After**:
```yaml
datasource:
  url: ${DATABASE_URL}
  username: ${DATABASE_USER}
  password: ${DATABASE_PASSWORD}
```

### 2. **Removed Files**
The following files specific to other deployment platforms were deleted:
- `DEPLOY_NOW.md` - Railway deployment guide
- `FINAL_STEPS.md` - Previous deployment notes
- `QUICK_DEPLOY_CHECKLIST.md` - Railway-specific checklist
- `RAILWAY_DEPLOY_STEPS.md` - Railway deployment steps
- `Dockerfile` - Root-level Docker configuration
- `backend/RAILWAY_DEPLOYMENT.md` - Railway-specific backend guide
- `backend/render.yaml` - Render platform configuration
- `backend/setup-render.sh` - Render setup script
- `backend/railway-env-variables.json` - Railway environment template
- `backend/docker-compose.yml` - Local development Docker Compose

---

## Backend Deployment (Java Spring Boot - JAR Method)

### IMPORTANT: Code Fixes Applied

Before deployment, the following code issues were fixed:
1. **Resolved ambiguous ApiResponse imports** in EventController and RegistrationController
2. All DTOs and entities have Lombok annotations (@Data, @Builder)
3. All service classes have proper logging (@Slf4j)
4. Maven pom.xml is correctly configured with Lombok support

See **BUILD_AND_FIX_SUMMARY.md** for details on all fixes.

### Step 1: Build JAR File on Local Machine

```bash
# Navigate to backend directory
cd backend

# Build JAR (skip tests for faster build)
mvn clean package -DskipTests

# Output: target/event-management-system-1.0.0.jar (~80-100MB)
```

**Alternative with Full Tests:**
```bash
mvn clean package

# This will run unit tests (takes longer)
```

### Step 2: Launch EC2 Instance for Backend

1. Go to AWS EC2 Console
2. Click "Launch Instance"
3. **Configuration**:
   - **AMI**: Amazon Linux 2023 kernel-6.1 AMI (Free Tier Eligible)
   - **Instance Type**: t3.micro (1 vCPU, 1GB RAM - Free Tier)
   - **Storage**: 15GB SSD
   - **Security Group**: Create new with rules:
     - SSH (22): Your IP
     - HTTP (80): 0.0.0.0/0
     - HTTPS (443): 0.0.0.0/0
     - Custom TCP (8080): 0.0.0.0/0 (backend port)

4. Download key pair (.pem file) and save securely
5. Launch instance and note the **Public IP**

### Step 3: Connect to EC2 Instance

```bash
# Change permissions on key file
chmod 400 your-key.pem

# SSH into instance (default user for Amazon Linux 2023 is 'ec2-user')
ssh -i your-key.pem ec2-user@<PUBLIC_IP>
```

### Step 4: Install Java 17

```bash
# Update system packages
sudo dnf update -y

# Install Java 17
sudo dnf install -y java-17-amazon-corretto-headless

# Verify Java installation
java -version

# Output should show: "openjdk version "17.x.x""
```

### Step 5: Transfer JAR to EC2

**From your local machine:**

```bash
# Option 1: Using SCP (Secure Copy)
scp -i your-key.pem target/event-management-system-1.0.0.jar ec2-user@<PUBLIC_IP>:/home/ec2-user/app/

# Option 2: Using SFTP GUI (WinSCP on Windows)
# Open WinSCP → New Site → Protocol: SCP → Host: <IP> → Key: your-key.pem
# Navigate to /home/ec2-user/app/ and drag-drop the JAR file

# Option 3: Verify transfer was successful
ssh -i your-key.pem ec2-user@<PUBLIC_IP> "ls -lh /home/ec2-user/app/"
```

### Step 6: Create Environment Variables File

```bash
# SSH into EC2
ssh -i your-key.pem ec2-user@<PUBLIC_IP>

# Create application directory
mkdir -p /home/ec2-user/app
cd /home/ec2-user/app

# Create environment variables file
nano env-vars.sh
```

Add the following content (replace with actual values):

```bash
#!/bin/bash

# Railway Database Configuration
export DATABASE_URL="jdbc:mysql://turntable.proxy.rlwy.net:48482/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DATABASE_USER="root"
export DATABASE_PASSWORD="<YOUR_RAILWAY_DB_PASSWORD>"

# Mail Configuration
export MAIL_HOST="smtp.gmail.com"
export MAIL_PORT="587"
export MAIL_USERNAME="<YOUR_GMAIL_ADDRESS>"
export MAIL_PASSWORD="<YOUR_APP_PASSWORD>"

# Spring Profile for Production
export SPRING_PROFILES_ACTIVE="production"

# Server Port
export SERVER_PORT="8080"

# Java Memory Settings (optimized for t3.micro 1GB RAM)
export JAVA_OPTS="-Xms128m -Xmx384m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

Save file (Ctrl+X, then Y, then Enter)

```bash
# Make script executable
chmod +x env-vars.sh

# Verify environment variables load correctly
source env-vars.sh
echo $DATABASE_URL
```

### Step 7: Create Systemd Service for JAR Application

```bash
# Create service file
sudo nano /etc/systemd/system/event-app.service
```

Add the following content:

```ini
[Unit]
Description=Event Management System - Spring Boot JAR
After=network.target
StartLimitIntervalSec=0

[Service]
Type=simple
Restart=always
RestartSec=10
User=ec2-user
WorkingDirectory=/home/ec2-user/app
EnvironmentFile=/home/ec2-user/app/env-vars.sh
Environment="JAVA_OPTS=-Xms128m -Xmx384m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ExecStart=/usr/bin/java $JAVA_OPTS -jar event-management-system-1.0.0.jar
StandardOutput=journal
StandardError=journal
SyslogIdentifier=event-app

[Install]
WantedBy=multi-user.target
```

Save and exit (Ctrl+X, Y, Enter)

### Step 8: Start the Application Service

```bash
# Reload systemd daemon
sudo systemctl daemon-reload

# Enable service to start on boot
sudo systemctl enable event-app.service

# Start the service
sudo systemctl start event-app.service

# Check service status
sudo systemctl status event-app.service

# View logs (Ctrl+C to exit)
sudo journalctl -u event-app.service -f

# View last 50 lines of logs
sudo journalctl -u event-app.service -n 50 --no-pager

# View logs from last 30 minutes
sudo journalctl -u event-app.service --since "30 minutes ago"
```

**Wait 10-15 seconds for application to start**

### Step 9: Test Backend API

```bash
# Test health endpoint
curl http://localhost:8080/actuator/health

# Should return:
# {"status":"UP"}

# Test API availability
curl http://localhost:8080/events

# Should return events list (may be empty if no events created)
```

---

## Frontend Deployment (React)

### Step 1: Launch EC2 Instance for Frontend

1. Go to AWS EC2 Console
2. Click "Launch Instance"
3. **Configuration**:
   - **AMI**: Amazon Linux 2023 kernel-6.1 AMI (Free Tier Eligible)
   - **Instance Type**: t3.micro (1 vCPU, 1GB RAM - Free Tier)
   - **Storage**: 15GB SSD (stays within 30GB free tier limit)
   - **Security Group**: Create new with rules:
     - SSH (22): Your IP
     - HTTP (80): 0.0.0.0/0
     - HTTPS (443): 0.0.0.0/0

4. Download key pair and save securely
5. Launch and note the **Public IP**

### Step 2: Connect to Frontend EC2 Instance

```bash
chmod 400 your-key.pem
ssh -i your-key.pem ec2-user@<FRONTEND_PUBLIC_IP>
```

### Step 3: Install Node.js and Nginx

```bash
# Update packages
sudo dnf update -y

# Install Node.js 18
sudo dnf install -y nodejs

# Verify installation
node --version
npm --version

# Install Nginx
sudo dnf install -y nginx

# Start Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

### Step 4: Clone and Build Frontend Application

```bash
# Navigate to app directory
cd ~
mkdir -p applications
cd applications

# Clone repository
git clone <YOUR_GITHUB_REPO_URL> event-management-system
cd event-management-system/frontend

# Install dependencies (may take time on t3.micro)
npm install

# Create .env file with backend API URL
nano .env
```

Add the following:

```
REACT_APP_API_URL=http://<BACKEND_EC2_PUBLIC_IP>:8080
```

Save and exit.

### Step 5: Build React Application

```bash
# Build for production
npm run build

# This creates a 'build' folder with optimized production build
```

### Step 6: Configure Nginx as Reverse Proxy

```bash
# Create new Nginx config
sudo nano /etc/nginx/conf.d/event-frontend.conf
```

Add the following configuration:

```nginx
server {
    listen 80;
    server_name _;

    # Frontend React App
    location / {
        root /home/ec2-user/applications/event-management-system/frontend/build;
        try_files $uri /index.html;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Serve static assets with caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        root /home/ec2-user/applications/event-management-system/frontend/build;
        expires 365d;
        add_header Cache-Control "public, immutable";
    }

    # Proxy API requests to backend
    location /api {
        proxy_pass http://<BACKEND_EC2_PUBLIC_IP>:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

Replace `<BACKEND_EC2_PUBLIC_IP>` with your backend EC2 public IP.

### Step 7: Enable and Test Nginx Configuration

```bash
# Test Nginx configuration
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx

# Verify Nginx is running
sudo systemctl status nginx
```

---

## Environment Variables Configuration

### Backend Environment Variables

Create `/home/ec2-user/env-vars.sh` with the following variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | Railway MySQL JDBC connection string | `jdbc:mysql://turntable.proxy.rlwy.net:48482/railway?...` |
| `DATABASE_USER` | Database username | `root` |
| `DATABASE_PASSWORD` | Database password | `YourSecurePassword` |
| `MAIL_HOST` | Email SMTP server | `smtp.gmail.com` |
| `MAIL_PORT` | Email SMTP port | `587` |
| `MAIL_USERNAME` | Email sender address | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Email app password | `xxxx xxxx xxxx xxxx` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `production` |
| `SERVER_PORT` | Application port | `8080` |
| `JAVA_OPTS` | Java heap settings for t3.micro | `-Xms256m -Xmx512m` |

### Free Tier Memory Management

For t3.micro instances (1GB RAM):

```bash
# Backend: Allocate 512MB max heap for Java application
JAVA_OPTS="-Xms256m -Xmx512m"

# Frontend: Node.js should have enough memory for npm build
# If build fails due to memory, consider:
# 1. Increasing Node memory: NODE_OPTIONS="--max-old-space-size=768"
# 2. Adding swap space (see Troubleshooting)
```

### Security Considerations

⚠️ **IMPORTANT**: Never hardcode credentials in files that will be committed to Git.

**Best Practices**:
1. Store sensitive data in AWS Systems Manager Parameter Store or Secrets Manager (free tier eligible)
2. Use IAM roles for EC2 instances instead of credentials
3. Rotate database passwords regularly
4. Use application passwords for Gmail instead of main password
5. Restrict Security Group rules to known IPs when possible
6. Never commit `.env` files or `env-vars.sh` to Git

---

## Security & Access

### Configure AWS Security Groups

**Backend Security Group**:
```
Inbound Rules:
- Type: SSH, Protocol: TCP, Port: 22, Source: Your-IP/32
- Type: HTTP, Protocol: TCP, Port: 80, Source: 0.0.0.0/0
- Type: Custom TCP, Protocol: TCP, Port: 8080, Source: 0.0.0.0/0
- Type: HTTPS, Protocol: TCP, Port: 443, Source: 0.0.0.0/0

Outbound Rules:
- All traffic allowed
```

**Frontend Security Group**:
```
Inbound Rules:
- Type: SSH, Protocol: TCP, Port: 22, Source: Your-IP/32
- Type: HTTP, Protocol: TCP, Port: 80, Source: 0.0.0.0/0
- Type: HTTPS, Protocol: TCP, Port: 443, Source: 0.0.0.0/0

Outbound Rules:
- All traffic allowed
```

### SSH Security

```bash
# Ensure proper key permissions
chmod 400 your-key.pem

# Add key to SSH agent (optional)
ssh-add your-key.pem

# Use SSH config for easier access
nano ~/.ssh/config
```

Add:
```
Host backend-ec2
    HostName <BACKEND_PUBLIC_IP>
    User ec2-user
    IdentityFile ~/.ssh/your-key.pem

Host frontend-ec2
    HostName <FRONTEND_PUBLIC_IP>
    User ec2-user
    IdentityFile ~/.ssh/your-key.pem
```

Then connect with: `ssh backend-ec2`

---

## Verification & Testing

### Test Backend API

```bash
# From your local machine or EC2 instance
curl http://<BACKEND_EC2_PUBLIC_IP>:8080/health

# Or test with specific endpoint
curl -X POST http://<BACKEND_EC2_PUBLIC_IP>:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

### Test Frontend Application

1. Open browser and navigate to: `http://<FRONTEND_EC2_PUBLIC_IP>`
2. Verify all pages load correctly
3. Test login/signup functionality
4. Verify API calls reach the backend

### Check Service Logs

```bash
# Backend logs
ssh -i your-key.pem ubuntu@<BACKEND_IP>
sudo journalctl -u event-backend.service -n 100 -f

# Frontend logs
ssh -i your-key.pem ubuntu@<FRONTEND_IP>
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### Verify Database Connection

```bash
# From backend EC2 instance
mysql -h turntable.proxy.rlwy.net -u root -p

# Then in MySQL prompt
use railway;
show tables;
```

---

## Troubleshooting

### Backend Service Not Starting

**Problem**: `sudo systemctl status event-backend.service` shows failed status

**Solutions**:
```bash
# Check logs
sudo journalctl -u event-backend.service -n 50

# Verify JAR was built
ls -la ~/applications/event-management-system/backend/target/

# Verify Java is installed
java -version

# Check if port 8080 is in use
sudo netstat -tlpn | grep 8080

# Manually test startup
source ~/env-vars.sh
cd ~/applications/event-management-system/backend
java -jar target/event-management-system-1.0.0.jar
```

### Out of Memory Issues (t3.micro 1GB RAM)

**Problem**: "OutOfMemoryError" in backend or npm build fails

**Solutions for Backend**:
```bash
# Adjust Java heap in service file
sudo nano /etc/systemd/system/event-backend.service
# Change: Environment="JAVA_OPTS=-Xms128m -Xmx384m"

# Reload and restart
sudo systemctl daemon-reload
sudo systemctl restart event-backend.service
```

**Solutions for Frontend (npm build)**:
```bash
# Option 1: Increase Node.js memory allocation
export NODE_OPTIONS="--max-old-space-size=512"
npm run build

# Option 2: Add swap space (1GB)
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Verify swap
free -h

# Make swap permanent
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### Database Connection Issues

**Problem**: "Connection refused" or "Unknown host" errors

**Check**:
```bash
# Test connectivity to Railway database
telnet turntable.proxy.rlwy.net 48482

# Or use curl
curl -v telnet://turntable.proxy.rlwy.net:48482

# Verify environment variables are set
echo $DATABASE_URL
echo $DATABASE_USER

# Check database credentials are correct
mysql -h turntable.proxy.rlwy.net -u root -p -e "SHOW DATABASES;"
```

**Solution**:
- Verify Railway MySQL is running and accessible
- Check DATABASE_URL format in env-vars.sh
- Ensure network connectivity from EC2 to Railway
- Verify credentials in ~/env-vars.sh

### Frontend Not Loading

**Problem**: Blank page or 404 errors

**Solutions**:
```bash
# Check if Nginx is running
sudo systemctl status nginx

# Verify Nginx configuration
sudo nginx -t

# Check Nginx logs
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/nginx/access.log

# Verify build files exist
ls -la ~/applications/event-management-system/frontend/build

# Rebuild if needed
cd ~/applications/event-management-system/frontend
export NODE_OPTIONS="--max-old-space-size=512"
npm install
npm run build
```

### API Requests Failing from Frontend

**Problem**: Frontend can't communicate with backend

**Check**:
```bash
# Verify backend is running
ssh -i your-key.pem ec2-user@<BACKEND_IP>
sudo systemctl status event-backend.service

# Test backend from frontend EC2
curl http://<BACKEND_IP>:8080/health

# Verify API URL in frontend .env
cat ~/applications/event-management-system/frontend/.env

# Update Nginx proxy_pass if needed
sudo nano /etc/nginx/conf.d/event-frontend.conf
# Ensure proxy_pass points to correct backend IP
sudo nginx -t && sudo systemctl reload nginx
```

### Port Already in Use

**Problem**: Port 8080 or 80 already in use

```bash
# Find process using port
sudo ss -tlpn | grep ':8080'
sudo ss -tlpn | grep ':80'

# Kill process if needed (get PID from output)
sudo kill -9 <PID>

# Or change port in service file
sudo nano /etc/systemd/system/event-backend.service
# Change ExecStart to use different port
```

### Git Clone Issues

**Problem**: "permission denied" or "command not found"

```bash
# Install git on Amazon Linux
sudo dnf install -y git

# Or use HTTPS instead of SSH
git clone https://github.com/your-username/repo.git
# Instead of: git clone git@github.com:your-username/repo.git
```

### Systemd Service Issues (Amazon Linux specific)

**Problem**: Service fails to start with "User not found" errors

```bash
# Verify correct username for Amazon Linux 2023
whoami  # Should output: ec2-user

# Update service file with correct user
sudo nano /etc/systemd/system/event-backend.service
# Change: User=ec2-user (NOT ubuntu)
# Change: WorkingDirectory=/home/ec2-user/...
# Change: EnvironmentFile=/home/ec2-user/...

# Reload and restart
sudo systemctl daemon-reload
sudo systemctl restart event-backend.service
```

---

## Monitoring & Maintenance

### Regular Backups

```bash
# Backup database from EC2
mysqldump -h turntable.proxy.rlwy.net -u root -p railway > ~/backups/railway_$(date +%Y%m%d).sql

# Or setup automated backup script
mkdir -p ~/backups
nano ~/backup.sh
```

Add to `~/backup.sh`:
```bash
#!/bin/bash
BACKUP_DIR="/home/ec2-user/backups"
DB_HOST="turntable.proxy.rlwy.net"
DB_USER="root"
DB_PASS="your_password"
DB_NAME="railway"

mkdir -p $BACKUP_DIR
mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/railway_$(date +\%Y\%m\%d_\%H\%M\%S).sql

# Keep only last 7 days of backups
find $BACKUP_DIR -name "railway_*.sql" -mtime +7 -delete
```

Make executable and add to crontab:
```bash
chmod +x ~/backup.sh
crontab -e
# Add: 0 2 * * * /home/ec2-user/backup.sh (runs daily at 2 AM)
```

### Monitor Application Health

```bash
# Check backend service status
sudo systemctl status event-backend.service

# Monitor resource usage
top

# Check memory usage
free -h

# Check disk space
df -h

# Check application directory size
du -sh ~/applications/event-management-system/*
```

### Monitor with Free Tier CloudWatch

```bash
# View CloudWatch metrics (free tier eligible)
# Go to AWS CloudWatch console to monitor:
# - CPU Utilization
# - Network In/Out
# - Disk Space (via CloudWatch agent)
# - Memory Usage (via CloudWatch agent)

# For memory monitoring, install CloudWatch agent:
wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
sudo rpm -U ./amazon-cloudwatch-agent.rpm
```

### Update Application

```bash
# Pull latest changes
cd ~/applications/event-management-system
git pull origin main

# Rebuild backend
cd backend
source ~/env-vars.sh
mvn clean package -DskipTests

# Restart service
sudo systemctl restart event-backend.service

# For frontend
cd ../frontend
export NODE_OPTIONS="--max-old-space-size=512"
npm install
npm run build
sudo systemctl reload nginx
```

### Log Rotation (Amazon Linux)

Logs are automatically rotated by systemd. To view logs:

```bash
# View backend logs
sudo journalctl -u event-backend.service -n 100

# Follow backend logs in real-time
sudo journalctl -u event-backend.service -f

# View logs from specific time
sudo journalctl -u event-backend.service --since "2024-01-01"
```

---

## Summary of Deployed Architecture (Free Tier)

```
┌─────────────────────────────────────────────────────┐
│   AWS EC2 Free Tier Instances (750 hrs/month)      │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────┐  ┌────────────────────┐  │
│  │  Frontend EC2        │  │  Backend EC2       │  │
│  │  (t3.micro, 1GB)     │  │  (t3.micro, 1GB)   │  │
│  │  Amazon Linux 2023   │  │  Amazon Linux 2023 │  │
│  │                      │  │                    │  │
│  │  ┌────────────────┐  │  │  ┌──────────────┐  │  │
│  │  │  Nginx + React │  │  │  │  Java 17 +   │  │  │
│  │  │  Build         │  │  │  │  Spring Boot │  │  │
│  │  │  (static)      │  │  │  │  (8080)      │  │  │
│  │  └────────────────┘  │  │  └──────────────┘  │  │
│  │        (15GB SSD)     │  │       (15GB SSD)   │  │
│  └──────────────────────┘  └────────────────────┘  │
│        (30GB EBS total - within free tier)         │
│                                                     │
└─────────────────────────────────────────────────────┘
        │                        │
        │    API Requests        │
        └────────┬───────────────┘
                 │
                 ▼
    ┌──────────────────────────────────┐
    │   Railway MySQL Database         │
    │   (Separate Service)             │
    │   (Persistent Connection)        │
    └──────────────────────────────────┘
```

## Free Tier Cost Estimate

| Resource | Free Tier Limit | Your Usage |
|----------|-----------------|-----------|
| EC2 t3.micro hours | 750 hrs/month | ~2 instances × 30 days = ~1440 hours (exceeds by ~690 hours) |
| EBS Storage | 30 GB | 30 GB (15GB + 15GB) ✓ |
| Data Transfer | 100 GB/month | Depends on usage |
| Railway MySQL | Varies | Check Railway pricing separately |

**Note**: Two t3.micro instances running 24/7 will exceed free tier. Options:
1. Run only during business hours
2. Use on-demand pricing ($0.0104/hour per instance = ~$15/month for both)
3. Schedule auto-stop/start using AWS Lambda (free tier eligible)

---

**Last Updated**: February 2026
**Deployment Type**: AWS EC2 (Free Tier) with Railway Database
**Operating System**: Amazon Linux 2023 kernel-6.1 (Free Tier Eligible)
**Instance Type**: t3.micro (1 vCPU, 1GB RAM) × 2
**Java Version**: 17 (Amazon Corretto)
**Spring Boot Version**: 3.2.0
**Node.js Version**: 18 (from dnf package)
**React Version**: Latest (from package.json)
**Database**: Railway MySQL (External)

## Free Tier Optimization Tips

1. **Manage Instance Running Time**
   - Use AWS Scheduled Scaling or EC2 Instance Scheduler
   - Stop instances when not in use
   - Set up CloudWatch alarms for billing alerts

2. **Optimize Memory Usage**
   - Set proper Java heap sizes (`-Xms128m -Xmx384m`)
   - Use swap space for temporary memory needs
   - Monitor with `free -h` command regularly

3. **Monitor Costs**
   - Enable AWS Cost Explorer
   - Set up billing alerts
   - Use AWS Pricing Calculator before deployment

4. **Database Optimization**
   - Check Railway's free tier offerings
   - Consider downtime for backups during low-traffic hours
   - Monitor connection pools to prevent resource exhaustion
