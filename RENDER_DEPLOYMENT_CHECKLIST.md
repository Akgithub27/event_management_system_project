# Render Deployment Checklist

## Pre-Deployment Setup

### 1. Prepare Repository
- [ ] Ensure code is committed and pushed to GitHub
- [ ] Verify `pom.xml` exists in backend directory
- [ ] Check `Dockerfile` exists for container deployment
- [ ] Verify `.gitignore` excludes sensitive files

```bash
git status
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### 2. Generate Required Secrets

#### JWT Secret (256+ bits)
```bash
# macOS/Linux
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { [byte](Get-Random -Maximum 256) }))
```
Save this value - you'll need it in Render.

#### Gmail App Password
1. Go to https://myaccount.google.com/apppasswords
2. Select "Mail" and your device type
3. Google generates 16-character password
4. Save this value - you'll need it in Render.

### 3. Create Render Account
- [ ] Go to https://render.com
- [ ] Sign up with GitHub account
- [ ] Authorize Render to access repositories
- [ ] Verify email address

---

## Render Dashboard Setup

### 4. Create MySQL Database

**In Render Dashboard:**

1. Click "New" → "MySQL"
2. Configure:
   - [ ] Name: `event-management-db`
   - [ ] Database: `event_management_db`
   - [ ] User: `event_user`
   - [ ] Plan: Free (development) or Standard (production)
3. Click "Create Database"
4. Save connection details:
   - [ ] External Connection String (SPRING_DATASOURCE_URL)
   - [ ] Database Name: `event_management_db`
   - [ ] User: `event_user`
   - [ ] Password (SPRING_DATASOURCE_PASSWORD)

### 5. Create Web Service

**In Render Dashboard:**

1. Click "New" → "Web Service"
2. Select GitHub repository
3. Configure:
   - [ ] Name: `event-management-backend`
   - [ ] Environment: `Java` (or `Docker`)
   - [ ] Region: Select closest region
   - [ ] Branch: `main`

4. Build & Start Commands:
   - [ ] Build Command: `mvn clean install -DskipTests`
   - [ ] Start Command: `java -jar target/event-management-system-1.0.0.jar`

5. Plan:
   - [ ] Free (development) or Starter (production)

6. Click "Create Web Service"

---

## Environment Variables Configuration

### 6. Add Environment Variables

**In Web Service Settings → Environment:**

| Variable | Value | Notes |
|----------|-------|-------|
| SPRING_PROFILES_ACTIVE | `production` | Keep as is |
| SPRING_DATASOURCE_URL | From database service | Format: `mysql://user:pass@host/db` |
| SPRING_DATASOURCE_USERNAME | From database service | Usually: `event_user` |
| SPRING_DATASOURCE_PASSWORD | From database service | Sensitive - uncheck "Sync" |
| SPRING_DATASOURCE_DRIVER_CLASS_NAME | `com.mysql.cj.jdbc.Driver` | Keep as is |
| SPRING_JPA_HIBERNATE_DDL_AUTO | `update` | Auto-create tables |
| SPRING_JPA_SHOW_SQL | `false` | Don't log SQL |
| JWT_SECRET | Your generated secret | Sensitive - uncheck "Sync" |
| JWT_EXPIRATION | `86400000` | 24 hours in milliseconds |
| MAIL_HOST | `smtp.gmail.com` | Keep as is |
| MAIL_PORT | `587` | Keep as is |
| MAIL_USERNAME | Your Gmail address | Sensitive - uncheck "Sync" |
| MAIL_PASSWORD | Gmail App Password | Sensitive - uncheck "Sync" |
| MAIL_FROM_ADDRESS | `noreply@eventmanagement.com` | Sender address |
| SERVER_PORT | `8080` | Keep as is |
| SERVER_SERVLET_CONTEXT_PATH | `/api` | API base path |
| LOGGING_LEVEL_ROOT | `INFO` | Reduce log spam |

**Important:** For sensitive variables (passwords, secrets), uncheck "Sync" checkbox.

```bash
# Save to remember your values
cat > /tmp/render-env.txt << EOF
JWT_SECRET=<your-generated-secret>
SPRING_DATASOURCE_PASSWORD=<render-generated>
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<gmail-app-password>
EOF
```

---

## Deployment & Testing

### 7. Deploy

1. [ ] In Render Dashboard, service status shows "Building"
2. [ ] Wait for build to complete (5-15 minutes)
3. [ ] Check deployment status (should show "Live")
4. [ ] View logs for any errors

### 8. Test Backend

```bash
# Test API endpoint
curl https://<your-service-name>.onrender.com/api/events

# Expected response:
# {"success":true,"message":"Events retrieved successfully","data":[]}
```

### 9. Monitor Logs

In Web Service → "Logs" tab:
- [ ] No build errors
- [ ] No database connection errors
- [ ] Application started successfully
- [ ] Port 8080 is listening

Look for messages like:
```
Started EventManagementApplication in X.XXX seconds
```

### 10. Verify Database Connection

1. Check logs for successful MySQL connection
2. Create test data (optional)
3. Query events endpoint again

---

## Frontend Configuration

### 11. Update Frontend

**In frontend `src/services/api.js`:**

```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://<your-service-name>.onrender.com/api';
```

### 12. Deploy Frontend

Choose your preferred platform:

**Option A: Render**
- Create new Web Service for frontend
- Build: `npm run build`
- Start: `npm start` or `serve -s build -l 3000`

**Option B: Netlify**
- Connect GitHub repo
- Build command: `npm run build`
- Publish directory: `build`

**Option C: Vercel**
- Import GitHub repo
- Auto-detected Next.js or React config
- Deploy with one click

---

## Post-Deployment

### 13. Enable Auto-Deploy (Optional)

1. [ ] Verify GitHub integration is active
2. [ ] Push code changes to test auto-deploy
3. [ ] Monitor Render Dashboard for deployment

```bash
# Make a test change
echo "# Update" >> README.md
git add .
git commit -m "Test auto-deploy"
git push origin main
# Watch Render Dashboard for automatic rebuild
```

### 14. Set Up Monitoring (Optional)

In Render Dashboard:
- [ ] View build logs
- [ ] View runtime logs
- [ ] Set up alerts (if premium)
- [ ] Monitor resource usage

### 15. SSL/TLS Certificate

- [ ] Render provides automatic HTTPS
- [ ] Certificate is auto-renewed
- [ ] Use HTTPS URLs only

---

## Troubleshooting

### Build Failed: Maven Issues
```
Solution:
1. Check pom.xml syntax
2. Verify Java 17+ compatibility
3. Check dependency versions
4. Try: mvn clean install locally first
```

### Database Connection Error
```
Solution:
1. Verify SPRING_DATASOURCE_URL format
2. Check username and password
3. Ensure database is created
4. Verify database allows external connections
```

### Email Not Sending
```
Solution:
1. Verify Gmail App Password (not account password)
2. Enable 2FA on Google Account
3. Check MAIL_USERNAME and MAIL_PASSWORD are correct
4. Verify SMTP settings in logs
```

### Service Keeps Restarting
```
Solution:
1. Check application logs
2. Verify all environment variables are set
3. Check memory allocation (free tier = 512MB)
4. Increase plan if needed
```

### Slow Startup (First Deploy)
```
Normal for first deploy:
- Maven downloads dependencies
- Java compilation takes time
- Initial build: 10-15 minutes
- Subsequent builds: 3-5 minutes

Free tier also has 15-minute inactivity spin-down.
```

---

## Optimization Tips

### For Development
- Use free tier plans
- Plan for 30-60 second startup times
- Service spins down after 15 minutes inactivity

### For Production
- Upgrade to Starter plan ($7+/month)
- Always-on service
- Better performance
- Auto-scaling available
- Daily backups for database

### Cost Estimation
| Component | Free | Starter | Standard |
|-----------|------|---------|----------|
| Web Service | $0 | $7 | $25 |
| MySQL DB | $0 | $15 | $50+ |
| **Total** | **$0** | **$22** | **$75+** |

---

## Backup & Recovery

### Enable Database Backups

In Render Dashboard → Database → Backups:
- Free tier: Manual backups only
- Starter+: Automatic daily backups

### Manual Database Backup
```bash
# Render doesn't provide direct export yet
# Use application to export data
# Or use MySQL client with connection string
```

### Restore from Backup

Contact Render support or:
1. Delete current database
2. Create new database
3. Restore from Render backup
4. Update connection string
5. Redeploy service

---

## Rollback Plan

If deployment fails:
1. Check logs for error
2. Fix issue locally
3. Push to GitHub
4. Render auto-redeploys
5. Or manually trigger redeploy

---

## Going Live Checklist

Before sharing application with users:

- [ ] All environment variables configured
- [ ] Database is running and healthy
- [ ] Email notifications working
- [ ] Backend API responding
- [ ] Frontend connected to backend
- [ ] User authentication working
- [ ] Event creation/registration working
- [ ] No errors in production logs
- [ ] Performance acceptable
- [ ] Backups enabled (if production)

---

## Support & Help

If you encounter issues:

1. **Check Render Logs:**
   Web Service → Logs tab

2. **Check Application Logs:**
   Spring Boot startup logs

3. **Test Database:**
   Verify connection string

4. **Verify Secrets:**
   Check all env variables set

5. **Contact Support:**
   - Render Support: support@render.com
   - Spring Boot Issues: Spring Docs
   - MySQL Issues: MySQL Docs

---

## Completion Status

- [ ] All steps completed
- [ ] Backend deployed and accessible
- [ ] Database connected and populated
- [ ] Email notifications configured
- [ ] Frontend updated with backend URL
- [ ] Application tested and working
- [ ] Monitoring configured
- [ ] Team notified of URLs

**Deployment Date:** _______________
**Backend URL:** _______________
**Frontend URL:** _______________
**Notes:** _______________
