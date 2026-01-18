# Backend Deployment Guide

## Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+ (or managed database service)
- Git

## Local Development

```bash
# Clone repository
git clone <repository-url>
cd event_management_system_project/backend

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

## Production Deployment Options

### Option 1: Render (Recommended)

1. Push code to GitHub
2. Go to https://render.com and create account
3. Click "New Web Service"
4. Connect GitHub repository
5. Configure:
   - Name: `event-management-backend`
   - Environment: `Java`
   - Build Command: `./mvnw clean install`
   - Start Command: `java -jar target/event-management-system-1.0.0.jar`

6. Add Environment Variables:
```
SPRING_DATASOURCE_URL=<mysql-database-url>
SPRING_DATASOURCE_USERNAME=<database-username>
SPRING_DATASOURCE_PASSWORD=<database-password>
JWT_SECRET=<your-super-secret-key>
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<your-app-password>
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Option 2: Railway

1. Go to https://railway.app
2. Create new project
3. Connect GitHub repository
4. Railway auto-detects Java/Maven project
5. Add MySQL plugin from Railway dashboard
6. Set environment variables in Railway dashboard

### Option 3: AWS EC2

1. Launch EC2 instance (Ubuntu 20.04 LTS)
2. Install Java:
```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk-headless
```

3. Install MySQL:
```bash
sudo apt-get install mysql-server
```

4. Deploy JAR:
```bash
scp -i key.pem target/event-management-system-1.0.0.jar ec2-user@<instance-ip>:/home/ubuntu/

ssh -i key.pem ubuntu@<instance-ip>
java -jar event-management-system-1.0.0.jar
```

### Option 4: Docker Deployment

Create `Dockerfile`:
```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/event-management-system-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t event-management:latest .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=<db-url> \
  -e SPRING_DATASOURCE_USERNAME=<username> \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  event-management:latest
```

## Database Setup

### Cloud Database (Recommended)
- Use AWS RDS, Azure Database, or Google Cloud SQL
- More reliable and managed
- Automatic backups

### Local MySQL

```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE event_management;
CREATE USER 'event_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON event_management.* TO 'event_user'@'localhost';
FLUSH PRIVILEGES;
```

## Monitoring & Logs

### Render
- View logs in Render dashboard
- Set up alerts for errors

### Railway
- View logs in Railway dashboard
- Deploy notifications

### AWS EC2
```bash
# View application logs
tail -f /var/log/application.log

# Check Java processes
jps -lm
```

## Health Check

Test your deployment:
```bash
curl https://your-backend-url/api/events
```

## Performance Tuning

```yaml
# application.yml for production
spring:
  jpa:
    show-sql: false
    properties:
      hibernate:
        jdbc:
          batch_size: 20
          fetch_size: 50
        order_inserts: true
        order_updates: true
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

## Backup & Recovery

```bash
# Backup MySQL database
mysqldump -u root -p event_management > backup.sql

# Restore from backup
mysql -u root -p event_management < backup.sql
```

## SSL/TLS Certificate

For HTTPS deployment:
- Render: Automatic SSL
- Railway: Automatic SSL
- AWS: Use AWS Certificate Manager
- Self-hosted: Use Let's Encrypt

## Troubleshooting

1. **503 Service Unavailable**: 
   - Check database connection
   - Verify environment variables
   - Check logs for errors

2. **Database Connection Error**:
   - Verify database URL format
   - Check username and password
   - Ensure database is running
   - Check firewall rules

3. **JWT Token Issues**:
   - Verify JWT_SECRET is set
   - Check token expiration
   - Verify Authorization header format

4. **Email Not Sending**:
   - Verify MAIL_USERNAME and MAIL_PASSWORD
   - Check Gmail App Password (not account password)
   - Verify SMTP settings
   - Check email logs

## Continuous Deployment

### GitHub Actions

Create `.github/workflows/deploy.yml`:
```yaml
name: Deploy to Render
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Deploy to Render
        run: curl ${{ secrets.RENDER_DEPLOY_URL }}
```

---

For production issues, check logs first and then contact support.
