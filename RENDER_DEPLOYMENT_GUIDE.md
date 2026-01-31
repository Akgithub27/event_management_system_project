# Event Management System - Render Deployment Guide

## Prerequisites
- Railway MySQL Database: `mysql://root:UOUrchBLOLddRwwRybIrVuHvgCAqwTDZ@turntable.proxy.rlwy.net:48482/railway`
- Render Account
- GitHub Repository (code pushed)

---

## Step 1: Push Code to GitHub

```bash
cd c:\Users\aakas\OneDrive\Desktop\GUVI Projects\event_management_system_project

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit changes
git commit -m "Configure Railway MySQL integration and Render deployment"

# Add remote (replace with your repo)
git remote add origin https://github.com/YOUR_USERNAME/event_management_system.git

# Push to GitHub
git branch -M main
git push -u origin main
```

---

## Step 2: Deploy Backend on Render

### 2.1: Create a New Web Service
1. Go to [Render Dashboard](https://render.com/dashboard)
2. Click **New +** → **Web Service**
3. Select your GitHub repository
4. Click **Connect**

### 2.2: Configure Service
**Basic Configuration:**
- **Name:** `event-management-backend` (or your preference)
- **Root Directory:** `backend` (since this is in a monorepo)
- **Runtime:** `Java`
- **Build Command:** `cd backend && mvn clean package -DskipTests`
- **Start Command:** `java -jar target/*.jar`
- **Plan:** (Select based on your needs; Free tier works for testing)

### 2.3: Set Environment Variables
Click **Environment** and add these variables:

```
# Database Configuration (Railway MySQL)
DATABASE_URL=jdbc:mysql://turntable.proxy.rlwy.net:48482/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&connectTimeout=10000&socketTimeout=30000
DB_USERNAME=root
DB_PASSWORD=UOUrchBLOLddRwwRybIrVuHvgCAqwTDZ

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# JWT Configuration (CHANGE IN PRODUCTION)
JWT_SECRET=your-super-secret-key-that-is-at-least-256-bits-long-please-change-in-production-now
JWT_EXPIRATION=86400000

# Mail Configuration (Optional, update with your Gmail)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587

# Connection Pool Configuration
DB_HIKARI_MAX_POOL_SIZE=10
DB_HIKARI_MIN_IDLE=5
DB_HIKARI_CONNECTION_TIMEOUT=30000
DB_HIKARI_IDLE_TIMEOUT=600000
DB_HIKARI_MAX_LIFETIME=1800000

# Spring Boot Configuration
PORT=8080
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_EVENTMANAGEMENT=DEBUG
```

**⚠️ IMPORTANT - Security Notes:**
- Replace `JWT_SECRET` with a strong, unique value (use a password generator)
- Update `MAIL_USERNAME` and `MAIL_PASSWORD` with your Gmail credentials
- For Gmail, use an [App Password](https://myaccount.google.com/apppasswords), not your main password
- Never commit these values to GitHub; always use environment variables

### 2.4: Create Service
Click **Create Web Service** and wait for deployment to complete.

---

## Step 3: Verify Deployment

### 3.1: Check Logs
1. Go to your service on Render
2. Check the **Logs** tab for any errors
3. Look for: `Started EventManagementApplication in X seconds`

### 3.2: Test Health
```bash
# Replace YOUR_RENDER_URL with your actual Render service URL
curl https://YOUR_RENDER_URL.onrender.com/api/swagger-ui.html

# Or test API health
curl https://YOUR_RENDER_URL.onrender.com/api/health
```

### 3.3: Verify Database Connection
The logs should show:
```
HikariPool-1 - Starting...
HikariPool-1 - Added connection conn#X: url=jdbc:mysql://turntable...
```

---

## Step 4: Deploy Frontend on Render or Netlify

### Option A: Deploy on Render
1. Create another Web Service for frontend
2. Connect your GitHub repo
3. **Configuration:**
   - **Root Directory:** `frontend`
   - **Build Command:** `npm install && npm run build`
   - **Start Command:** `npx serve -s build -l 3000`
   - **Plan:** Free tier

4. **Environment Variables:**
   ```
   REACT_APP_API_URL=https://YOUR_BACKEND_URL.onrender.com/api
   ```

### Option B: Deploy on Netlify (Recommended for Frontend)
1. Go to [Netlify](https://netlify.com)
2. Click **New site from Git**
3. Connect your GitHub repository
4. **Build settings:**
   - **Base directory:** `frontend`
   - **Build command:** `npm run build`
   - **Publish directory:** `build`

5. **Environment Variables:**
   ```
   REACT_APP_API_URL=https://YOUR_BACKEND_URL.onrender.com/api
   ```

---

## Step 5: Update Frontend API Configuration

Edit [frontend/src/services/api.js](frontend/src/services/api.js):

```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export default api;
```

---

## Step 6: Database Initialization

### First Deployment Only:
When the backend starts for the first time:
1. Hibernate connects to Railway MySQL
2. Scans all `@Entity` classes
3. Creates tables automatically (ddl-auto: update)
4. Tables created:
   - `user`
   - `event`
   - `speaker`
   - `event_registration`
   - `event_attendance`
   - `event_speaker`
5. ✅ Database is ready

### Subsequent Deployments:
- `ddl-auto: update` preserves existing tables
- New columns/tables are added if schema changes
- No data loss on redeployment

---

## Common Issues & Solutions

### Issue 1: "Communications link failure"
**Cause:** Cannot connect to Railway MySQL

**Solution:**
```
1. Check DATABASE_URL is correct
2. Verify Railway MySQL is running
3. Check network connectivity between Render and Railway
4. Add these to DATABASE_URL:
   - connectTimeout=10000
   - socketTimeout=30000
```

### Issue 2: "Access denied for user 'root'"
**Cause:** Wrong credentials

**Solution:**
```
- Verify DB_USERNAME = root
- Verify DB_PASSWORD = UOUrchBLOLddRwwRybIrVuHvgCAqwTDZ
- Test locally: mysql -h turntable.proxy.rlwy.net -u root -p
```

### Issue 3: "No open ports detected"
**Cause:** PORT environment variable not set or server didn't start

**Solution:**
```
- Add PORT=8080 to environment variables
- Check application logs for startup errors
- Ensure pom.xml has spring-boot-starter-web dependency
```

### Issue 4: Frontend "Cannot reach API"
**Cause:** Wrong REACT_APP_API_URL

**Solution:**
```
- Set REACT_APP_API_URL in frontend environment variables
- Format: https://YOUR_BACKEND_SERVICE.onrender.com/api
- Verify backend is deployed and running first
```

---

## Architecture Overview

```
┌─────────────────┐
│    Netlify      │
│   Frontend      │
│   (React)       │
└────────┬────────┘
         │ HTTPS
         │ REACT_APP_API_URL
         │
         ▼
┌─────────────────────┐
│      Render         │
│  Backend Service    │
│   (Spring Boot)     │
└────────┬────────────┘
         │ JDBC
         │ DATABASE_URL
         │
         ▼
┌─────────────────────┐
│      Railway        │
│   MySQL Database    │
│     (railway db)    │
└─────────────────────┘
```

---

## Environment Variable Summary

| Variable | Value | Purpose |
|----------|-------|---------|
| `DATABASE_URL` | Railway JDBC URL | MySQL connection |
| `DB_USERNAME` | root | Database user |
| `DB_PASSWORD` | UOUrchBLOLddRwwRybIrVuHvgCAqwTDZ | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | update | Auto schema management |
| `JWT_SECRET` | Your secret key | JWT token signing |
| `MAIL_USERNAME` | Your Gmail | Email sender |
| `MAIL_PASSWORD` | App password | Gmail authentication |
| `PORT` | 8080 | Server port |
| `REACT_APP_API_URL` | Backend URL | Frontend API endpoint |

---

## Monitoring & Troubleshooting

### View Logs
```bash
# Render Dashboard
1. Go to your service
2. Click "Logs" tab
3. Filter by time or error level
```

### Check Database Status
```bash
# Using MySQL client
mysql -h turntable.proxy.rlwy.net -u root -p UOUrchBLOLddRwwRybIrVuHvgCAqwTDZ

# View tables
USE railway;
SHOW TABLES;
DESC user;
```

### Restart Service
1. Go to Render Dashboard
2. Click your service
3. Click "Manual Deploy" or restart from menu

---

## File Changes Made

### Configuration Files Updated:
- ✅ `backend/src/main/resources/application.yml`
  - Changed `ddl-auto: create-drop` → `ddl-auto: update`
  - Ready for local development

- ✅ `backend/src/main/resources/application-production.yml`
  - All values configurable via environment variables
  - Ready for Render deployment

### No Code Changes Required:
- All entity classes remain unchanged
- All service and controller logic remains unchanged
- JPA automatically handles table creation

---

## Summary

✅ **Backend Configuration:** Railway MySQL + Render
✅ **Frontend Configuration:** Netlify + React
✅ **Database:** Auto-created on first run
✅ **Deployment:** Step-by-step guide provided
✅ **Monitoring:** Logs and troubleshooting included

**Next Steps:**
1. Push code to GitHub
2. Deploy backend on Render (Steps 2-3)
3. Deploy frontend on Netlify (Step 4)
4. Test application end-to-end
5. Monitor logs for any issues

---

## Testing Checklist

- [ ] Backend deployed on Render
- [ ] Frontend deployed on Netlify
- [ ] API health check passes
- [ ] Database tables created
- [ ] Frontend loads successfully
- [ ] Can login/signup
- [ ] Can create events
- [ ] Can register for events
- [ ] Email notifications working (if configured)

---

**Deployment Date:** January 31, 2026
**Status:** Ready for Production
