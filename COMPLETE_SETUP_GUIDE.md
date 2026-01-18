# Complete Setup & Deployment Guide

## Table of Contents
1. [System Requirements](#system-requirements)
2. [Local Development Setup](#local-development-setup)
3. [Database Configuration](#database-configuration)
4. [Backend Setup & Running](#backend-setup--running)
5. [Frontend Setup & Running](#frontend-setup--running)
6. [Testing](#testing)
7. [Docker Deployment](#docker-deployment)
8. [Production Deployment](#production-deployment)
9. [Troubleshooting](#troubleshooting)

---

## System Requirements

### Development Machine
- **OS**: Windows/macOS/Linux
- **RAM**: Minimum 4GB (8GB recommended)
- **Storage**: 5GB free space
- **Internet**: Required for Maven/npm downloads

### Software Required
- **Java**: JDK 17+ ([Download](https://adoptopenjdk.net/))
- **Maven**: 3.8+ ([Download](https://maven.apache.org/download.cgi))
- **Node.js**: 16+ ([Download](https://nodejs.org/))
- **MySQL**: 8.0+ ([Download](https://www.mysql.com/downloads/))
- **Git**: Latest version ([Download](https://git-scm.com/))
- **IDE**: VS Code or IntelliJ IDEA

---

## Local Development Setup

### Step 1: Clone Repository
```bash
git clone https://github.com/your-username/event_management_system_project.git
cd event_management_system_project
```

### Step 2: Verify Installations
```bash
# Check Java
java -version
# Output should show: openjdk version "17" or higher

# Check Maven
mvn -version
# Output should show: Apache Maven 3.8+ or higher

# Check Node.js
node -v
npm -v
# Output should show: Node v16+ and npm 7+

# Check MySQL
mysql --version
# Output should show: mysql Ver 8.0+ or higher
```

### Step 3: Clone All Files
Ensure the following structure exists:
```
event_management_system_project/
├── backend/
├── frontend/
├── docker-compose.yml
├── README.md
├── QUICKSTART.md
└── PROJECT_SUMMARY.md
```

---

## Database Configuration

### Step 1: Create MySQL Database

**Option A: Using MySQL CLI**
```bash
# Open MySQL
mysql -u root -p

# Create database
CREATE DATABASE event_management;

# Create user
CREATE USER 'event_user'@'localhost' IDENTIFIED BY 'password123';

# Grant privileges
GRANT ALL PRIVILEGES ON event_management.* TO 'event_user'@'localhost';
FLUSH PRIVILEGES;

# Verify
SHOW DATABASES;
\q  # Exit
```

**Option B: Using MySQL Workbench**
1. Open MySQL Workbench
2. Click "+" to create new connection
3. Enter credentials:
   - Connection Name: event_management
   - Hostname: localhost
   - Port: 3306
   - Username: root
   - Password: (your password)
4. Right-click "Databases" → "Create Schema"
5. Enter name: event_management

### Step 2: Update Backend Configuration

Edit `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/event_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: event_user
    password: password123
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Step 3: Verify Connection
```bash
mysql -u event_user -p -h localhost -D event_management -e "SELECT 1;"
# Should output: 1
```

---

## Backend Setup & Running

### Step 1: Navigate to Backend Directory
```bash
cd backend
```

### Step 2: Build the Project
```bash
# Download dependencies and compile
mvn clean install

# This may take 2-5 minutes on first run
```

### Step 3: Run the Application
```bash
# Option A: Using Maven
mvn spring-boot:run

# Option B: Using JAR (after build)
java -jar target/event-management-system-1.0.0.jar
```

### Step 4: Verify Backend is Running
```bash
# In another terminal, test the API
curl http://localhost:8080/api/events

# Should return JSON response (even if empty)
```

### Backend Logs
The application will log to console:
```
Starting EventManagementApplication
Hibernate: create table ... (table creation DDL)
Started EventManagementApplication in X.XXX seconds
```

**Port**: `http://localhost:8080`
**API Documentation**: Available at `http://localhost:8080/api/`

---

## Frontend Setup & Running

### Step 1: Navigate to Frontend Directory
```bash
cd ../frontend
```

### Step 2: Install Dependencies
```bash
npm install
# This downloads ~300+ npm packages (~200MB)
```

### Step 3: Configure Environment
Create/Update `.env`:
```env
REACT_APP_API_URL=http://localhost:8080/api
```

### Step 4: Start Development Server
```bash
npm start
```

The browser will automatically open to `http://localhost:3000`

### Frontend Console
Should show:
```
webpack compiled successfully
```

**Port**: `http://localhost:3000`

---

## Testing

### Backend Unit Tests

```bash
cd backend

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run with coverage
mvn test -Ddocker.skip=true

# Run tests without building JAR
mvn clean test
```

**Test Files**:
- `AuthServiceTest.java` - Authentication tests
- `EventServiceTest.java` - Event management tests

### Frontend Component Tests

```bash
cd frontend

# Run all tests in watch mode
npm test

# Run with coverage
npm test -- --coverage

# Run specific test
npm test -- --testNamePattern="Login"
```

### Manual Testing Checklist

**Authentication**:
- [ ] Signup new user
- [ ] Verify welcome email received
- [ ] Login with correct credentials
- [ ] Login fails with wrong password
- [ ] JWT token stored in localStorage

**Events**:
- [ ] View all events on dashboard
- [ ] Search events by keyword
- [ ] Filter events by category
- [ ] View event details
- [ ] Register for event
- [ ] Receive confirmation email
- [ ] Cancel registration

**Admin Features**:
- [ ] Login as admin
- [ ] Create new event
- [ ] Update event details
- [ ] Delete event
- [ ] View event registrations

---

## Docker Deployment

### Step 1: Install Docker
- [Windows](https://docs.docker.com/desktop/install/windows-install/)
- [macOS](https://docs.docker.com/desktop/install/mac-install/)
- [Linux](https://docs.docker.com/engine/install/ubuntu/)

### Step 2: Build Backend Docker Image
```bash
cd backend
mvn clean package -DskipTests
docker build -t event-management-backend:latest .
```

### Step 3: Build Frontend Docker Image
```bash
cd ../frontend
docker build -t event-management-frontend:latest .
```

### Step 4: Run with Docker Compose
```bash
cd ..
docker-compose up -d
```

### Verify Containers Running
```bash
docker ps

# Expected output shows 3 running containers:
# - event_management_mysql
# - event_management_backend
# - event_management_frontend
```

### Docker Compose Services
- **MySQL**: `localhost:3306`
- **Backend**: `localhost:8080`
- **Frontend**: `localhost:3000`

### Stop Containers
```bash
docker-compose down

# Remove volumes
docker-compose down -v
```

### View Logs
```bash
# Backend logs
docker logs event_management_backend -f

# Frontend logs
docker logs event_management_frontend -f

# MySQL logs
docker logs event_management_mysql -f
```

---

## Production Deployment

### Option 1: Deploy Backend to Render

1. **Create Render Account**
   - Visit [https://render.com](https://render.com)
   - Sign up with GitHub

2. **Connect GitHub Repository**
   - Create new Web Service
   - Select GitHub repository
   - Click Deploy

3. **Configure Environment**
   ```
   Build Command:  ./mvnw clean install
   Start Command:  java -jar target/event-management-system-1.0.0.jar
   ```

4. **Set Environment Variables**
   - `SPRING_DATASOURCE_URL`: Your database URL
   - `SPRING_DATASOURCE_USERNAME`: Database user
   - `SPRING_DATASOURCE_PASSWORD`: Database password
   - `JWT_SECRET`: Generate random 256+ char string
   - `MAIL_USERNAME`: Gmail address
   - `MAIL_PASSWORD`: Gmail app password

5. **Database Setup** (Render Postgres or AWS RDS)
   ```sql
   -- Run after deployment
   CREATE DATABASE event_management;
   -- Tables created automatically via Hibernate DDL
   ```

**Result**: Backend available at `https://your-app.onrender.com`

### Option 2: Deploy Frontend to Netlify

1. **Create Netlify Account**
   - Visit [https://app.netlify.com](https://app.netlify.com)
   - Sign up with GitHub

2. **Connect Repository**
   - New site from Git
   - Select GitHub repository

3. **Configure Build Settings**
   - Base directory: `frontend`
   - Build command: `npm run build`
   - Publish directory: `frontend/build`

4. **Set Environment Variables**
   - `REACT_APP_API_URL`: Your production backend API

5. **Deploy**
   - Site automatically deploys on git push

**Result**: Frontend available at `https://your-site.netlify.app`

### Option 3: AWS EC2 Deployment

1. **Launch EC2 Instance**
   ```bash
   # Ubuntu 20.04 LTS, t2.micro (free tier eligible)
   ```

2. **Connect to Instance**
   ```bash
   ssh -i key.pem ubuntu@your-instance-ip
   ```

3. **Install Java & Maven**
   ```bash
   sudo apt-get update
   sudo apt-get install openjdk-17-jdk-headless maven
   ```

4. **Install MySQL**
   ```bash
   sudo apt-get install mysql-server
   mysql_secure_installation
   ```

5. **Clone & Deploy**
   ```bash
   git clone https://github.com/your-repo.git
   cd event_management_system_project/backend
   mvn clean package -DskipTests
   
   java -jar target/event-management-system-1.0.0.jar &
   ```

6. **Setup Domain**
   - Purchase domain from Route53/GoDaddy
   - Point to EC2 Elastic IP
   - Use CloudFront for HTTPS

---

## Troubleshooting

### Backend Issues

**Port 8080 Already in Use**
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

**Database Connection Failed**
```
Error: Communications link failure
```
**Solution**:
```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1;"

# Check credentials in application.yml
# Verify database exists
mysql -u event_user -p -e "USE event_management; SHOW TABLES;"
```

**Build Fails with Maven Error**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

**Memory Issues**
```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx1024m -Xms512m"
mvn spring-boot:run
```

### Frontend Issues

**Module Not Found Error**
```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

**Port 3000 Already in Use**
```bash
# Kill process
lsof -i :3000 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

**CORS Error: "Access to XMLHttpRequest blocked"**
```javascript
// Verify .env has correct backend URL
REACT_APP_API_URL=http://localhost:8080/api
// Verify backend CORS is configured
// Check browser console for actual URL being called
```

**Blank White Screen**
```bash
# Check browser console (F12)
# Verify NetworkTab shows successful API responses
# Clear browser cache: Ctrl+Shift+Delete
npm start  # Restart dev server
```

### Database Issues

**Tables Not Created**
```bash
# Verify Hibernate DDL-auto is set to "update"
# spring.jpa.hibernate.ddl-auto: update

# Check Spring Boot logs for DDL statements
# Restart application to trigger table creation
```

**Lost Database Connection**
```bash
# Restart MySQL
sudo systemctl restart mysql

# Or (macOS)
brew services restart mysql

# Verify connection
mysql -u event_user -p event_management -e "SELECT 1;"
```

### Email Issues

**Emails Not Sending**
```
Error: Connection refused or Authentication failed
```
**Solution**:
1. Generate Gmail App Password:
   - Visit [myaccount.google.com](https://myaccount.google.com)
   - Security → App Passwords
   - Select Mail, Windows Computer
   - Generate password

2. Update `application.yml`:
   ```yaml
   mail:
     username: your-email@gmail.com
     password: xxxx xxxx xxxx xxxx
   ```

3. Allow Less Secure Apps (if not using App Password):
   - Visit [Google Account](https://myaccount.google.com/security)
   - Less secure app access → Enable

### Deployment Issues

**Render: Build Fails**
- Check build logs in Render dashboard
- Verify environment variables are set
- Ensure Java version is compatible

**Netlify: 404 Errors**
- Verify `netlify.toml` redirects rule exists
- Check `REACT_APP_API_URL` environment variable
- Clear Netlify cache and redeploy

**DNS Not Resolving**
- Wait 24-48 hours for DNS propagation
- Verify nameservers in domain registrar
- Test with `nslookup your-domain.com`

---

## Common Commands Reference

### Backend
```bash
# Start development server
mvn spring-boot:run

# Build JAR file
mvn clean package -DskipTests

# Run tests
mvn test

# Clean build
mvn clean install

# Run specific main class
mvn exec:java -Dexec.mainClass="com.eventmanagement.EventManagementApplication"
```

### Frontend
```bash
# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Install dependencies
npm install

# Update all packages
npm update

# Check for vulnerabilities
npm audit
```

### Docker
```bash
# Build image
docker build -t name:tag .

# Run container
docker run -p 8080:8080 name:tag

# View logs
docker logs container-name -f

# Stop container
docker stop container-name

# Remove container
docker rm container-name
```

### Git
```bash
# Clone repository
git clone url

# Create new branch
git checkout -b feature/name

# Commit changes
git commit -m "message"

# Push to remote
git push origin branch-name

# Create pull request
# (on GitHub)
```

---

## Performance Tuning

### Backend
```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
          fetch_size: 50
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      maximum-lifetime: 1200000
```

### Frontend
```bash
# Analyze bundle size
npm install -g webpack-bundle-analyzer
npm run build -- --analyze

# Enable service worker for caching
# (already included in CRA)
```

---

## Security Best Practices

1. **Never commit secrets**
   - Use `.env` files (add to .gitignore)
   - Use environment variables in production

2. **Update dependencies regularly**
   ```bash
   Backend: mvn dependency:update-properties
   Frontend: npm update
   ```

3. **Use HTTPS in production**
   - Enable automatic SSL on Render/Netlify
   - Use AWS Certificate Manager for EC2

4. **Secure password storage**
   - Always use BCrypt for password hashing
   - Never store plain text passwords

5. **API security**
   - Validate all inputs
   - Use prepared statements (JPA does this)
   - Implement rate limiting in production

---

## Monitoring

### Application Performance
- Backend: Monitor in Render/Railway dashboard
- Frontend: Monitor Netlify analytics
- Database: Enable slow query logs

### Error Tracking
- Install Sentry for error monitoring
- Configure email alerts for critical errors
- Review logs regularly

### User Analytics
- Add Google Analytics to frontend
- Track page views and user interactions
- Monitor conversion metrics

---

## Next Steps

1. ✅ Complete local setup
2. ✅ Run both applications successfully
3. ✅ Test all features manually
4. ✅ Implement any customizations
5. ✅ Deploy to staging environment
6. ✅ Run full regression testing
7. ✅ Deploy to production
8. ✅ Monitor and maintain

---

For questions or issues, refer to:
- [README.md](README.md)
- [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)
- [BACKEND_DEPLOYMENT.md](BACKEND_DEPLOYMENT.md)
- [FRONTEND_DEPLOYMENT.md](FRONTEND_DEPLOYMENT.md)

---

**Happy Deployment! 🚀**
