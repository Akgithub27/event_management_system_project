# Railway Deployment Guide

## Overview
This guide walks through deploying the Event Management System backend to Railway.app

## Prerequisites
- Railway account (https://railway.app)
- GitHub repository with the code
- Environment variables configured

## Deployment Steps

### Step 1: Connect GitHub to Railway
1. Go to [railway.app](https://railway.app)
2. Sign in with GitHub
3. Click "Create a new project"
4. Select "Deploy from GitHub repo"
5. Connect your GitHub account and select the repository

### Step 2: Configure Environment Variables
In Railway dashboard, set the following environment variables:

#### Database Configuration
```
DATABASE_URL=jdbc:mysql://[mysql-host]:[mysql-port]/event_management_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DATABASE_USER=root
DATABASE_PASSWORD=[your-secure-password]
```

#### JWT Configuration
```
JWT_SECRET=[generate-a-strong-32-character-secret]
JWT_EXPIRATION=86400000
```

#### Email Configuration (Optional)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=[your-email@gmail.com]
MAIL_PASSWORD=[your-app-specific-password]
```

#### Application Configuration
```
SPRING_PROFILES_ACTIVE=production
PORT=8080
```

### Step 3: Set up MySQL Database on Railway
1. In the same Railway project, click "+ New"
2. Select "Database"
3. Choose "MySQL"
4. Configure the database with credentials matching your environment variables
5. Copy the generated connection URL to `DATABASE_URL`

### Step 4: Deploy the Application
1. Railway will automatically detect the Dockerfile
2. Click the service
3. Go to "Deployments" tab
4. Watch the build and deployment logs
5. Once deployed, you'll get a public URL

### Step 5: Test the Deployment
```bash
# Test health endpoint
curl https://[your-railway-url]/api/health

# Test API
curl https://[your-railway-url]/api/events
```

## Important Notes

### Port Configuration
- Railway dynamically assigns a PORT environment variable
- The application automatically uses PORT=8080 internally
- Railway's load balancer handles external routing

### Database Connection
- If using Railway's MySQL service, the connection URL will be auto-injected
- Ensure `allowPublicKeyRetrieval=true` is set for MySQL 8.0
- Use SSL in production with proper certificates

### Health Check
- The Dockerfile includes a health check endpoint: `/api/health`
- Railway monitors this for application availability
- Ensure this endpoint is accessible without authentication

### Logs
- View logs in Railway dashboard under "Logs" tab
- Use the Metrics tab to monitor CPU, Memory, and Network

### Scaling
- Configure auto-scaling in Railway settings if needed
- Adjust resource limits based on traffic

## Troubleshooting

### Build Fails
- Check Maven dependencies in pom.xml
- Verify Dockerfile can access all source files
- Check Java version compatibility (using Java 17)

### Database Connection Fails
- Verify DATABASE_URL format
- Check DATABASE_USER and DATABASE_PASSWORD
- Ensure MySQL service is running
- Check firewall/network settings

### Application Won't Start
- Check logs for specific error messages
- Verify all required environment variables are set
- Ensure JWT_SECRET is set to a strong value
- Check port configuration matches

### Memory Issues
- Monitor memory usage in Metrics
- Increase Java heap size if needed with JVM_OPTS
- Check for database connection pool issues

## Configuration Files Modified

1. **application-production.yml** - Updated to use environment variables
2. **Dockerfile** - Optimized for Railway deployment
3. **railway.json** - Railway deployment configuration
4. **railway.yaml** - Docker Compose for local Railway testing

## Using Railway CLI (Optional)

Install Railway CLI and deploy locally first:
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Link project
railway link

# Deploy
railway up
```

## Next Steps

After deployment to Railway:
1. Update frontend API endpoints to use Railway URL
2. Configure CORS if frontend is on different domain
3. Set up custom domain in Railway settings
4. Configure SSL/TLS certificates
5. Set up monitoring and alerts here

## Database Backup & Recovery

Enable automatic backups in Railway MySQL service settings to prevent data loss.

---

**Last Updated:** February 2026
**Tested with:** Spring Boot 3.2.0, Java 17, MySQL 8.0
