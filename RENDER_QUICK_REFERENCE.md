# Render Deployment - Quick Reference

## Files Created for Render Deployment

1. **`render.yaml`** - Render service configuration
2. **`RENDER_DEPLOYMENT.md`** - Complete setup guide
3. **`.env.example`** - Environment variables template
4. **`application-production.yml`** - Production Spring Boot config
5. **`setup-render.sh`** - Secret generation script

## Quick Start (5 Steps)

### Step 1: Push to GitHub
```bash
git add .
git commit -m "Add Render deployment config"
git push origin main
```

### Step 2: Generate Secrets
```bash
# JWT Secret
openssl rand -base64 32

# Gmail App Password
# Go to: https://myaccount.google.com/apppasswords
```

### Step 3: Create Render MySQL Database
- Go to https://render.com/dashboard
- Click "New" → "MySQL"
- Name: `event-management-db`
- Save connection details

### Step 4: Create Web Service
- Click "New" → "Web Service"
- Select your GitHub repo
- Build: `mvn clean install -DskipTests`
- Start: `java -jar target/event-management-system-1.0.0.jar`

### Step 5: Add Environment Variables
Copy all values from `.env.example` and set in Render Dashboard:
```
SPRING_DATASOURCE_URL=<from MySQL service>
SPRING_DATASOURCE_USERNAME=event_user
SPRING_DATASOURCE_PASSWORD=<from MySQL service>
JWT_SECRET=<generated above>
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<gmail-app-password>
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
```

## Database Connection String Format

```
# Render MySQL provides:
mysql://user:password@host:port/database

# Convert to JDBC format for Spring Boot:
jdbc:mysql://host:port/database?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

## Test Deployment

```bash
# Once deployed, test with:
curl https://your-service-name.onrender.com/api/events

# Expected response:
# {"success":true,"message":"Events retrieved successfully","data":[]}
```

## Environment Variables Reference

| Variable | Example Value | Required |
|----------|---------------|----------|
| SPRING_PROFILES_ACTIVE | production | Yes |
| SPRING_DATASOURCE_URL | jdbc:mysql://... | Yes |
| SPRING_DATASOURCE_USERNAME | event_user | Yes |
| SPRING_DATASOURCE_PASSWORD | your_password | Yes |
| JWT_SECRET | generated_base64_string | Yes |
| JWT_EXPIRATION | 86400000 | Optional |
| MAIL_HOST | smtp.gmail.com | Yes |
| MAIL_PORT | 587 | Yes |
| MAIL_USERNAME | email@gmail.com | Yes |
| MAIL_PASSWORD | app_password | Yes |
| MAIL_FROM_ADDRESS | noreply@eventmanagement.com | Optional |

## Common Commands

### Generate JWT Secret
```bash
# macOS/Linux
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { [byte](Get-Random -Maximum 256) }))
```

### Monitor Deployment
1. Go to Render Dashboard
2. Select Web Service
3. Click "Logs" tab
4. Look for "Started EventManagementApplication"

### View Database Logs
1. Go to Render Dashboard
2. Select MySQL Database
3. Click "Logs" tab

### Force Redeploy
1. Go to Web Service
2. Click "Manual Deploy"
3. Select branch and deploy

## Render URLs

- Dashboard: https://render.com/dashboard
- Docs: https://render.com/docs
- Support: support@render.com

## Next Steps

After backend is deployed:

### 1. Deploy Frontend
- Create Render Web Service for frontend
- Or use Netlify/Vercel
- Update API URL in `api.js`

### 2. Connect Services
- Frontend calls backend API
- Test full flow: signup → login → create event → register

### 3. Monitor Production
- Watch logs regularly
- Set up error monitoring
- Track performance

### 4. Plan for Scale
- Upgrade from free tier if needed
- Enable automatic backups
- Configure alerts

## Troubleshooting

### No database connection?
1. Copy correct SPRING_DATASOURCE_URL from MySQL service
2. Include `?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
3. Verify credentials match

### Build failed?
1. Check pom.xml syntax
2. Run `mvn clean install` locally first
3. Check Java version (17+)

### Email not working?
1. Use Gmail App Password (not account password)
2. Enable 2FA on Google Account
3. Verify credentials in environment variables

### Service too slow?
1. Free tier spins down after 15 minutes
2. First request takes 30-60 seconds
3. Upgrade to Starter plan ($7/month) for always-on

## Cost

### Free Tier
- 0.5GB RAM per service
- Spins down after 15 minutes inactivity
- Perfect for development/testing

### Starter Plan ($7/month)
- 2GB RAM
- Always-on service
- Perfect for small production sites

### Standard Plan ($25/month)
- 4GB RAM
- Multiple replicas
- Auto-scaling
- Priority support

## Database Backups

### Free Tier
- No automatic backups
- Manual backups possible
- Data persists

### Starter+ Tier
- Daily automatic backups
- 7-day retention
- Easy restore

## Important Notes

1. **First Deploy**: Takes 10-15 minutes (Maven downloads everything)
2. **Free Tier**: Service stops after 15 minutes inactivity
3. **Sensitive Variables**: Uncheck "Sync" for passwords/secrets
4. **Auto-Deploy**: Enabled by default from GitHub
5. **HTTPS**: Automatic, included with Render

## Support Resources

- [Render Docs](https://render.com/docs)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [MySQL Docs](https://dev.mysql.com/doc/)
- [Gmail App Password Setup](https://support.google.com/accounts/answer/185833)

## Final Checklist

Before going live:
- [ ] Backend deployed and accessible
- [ ] Database connected
- [ ] Email notifications working
- [ ] Frontend updated with backend URL
- [ ] User signup/login tested
- [ ] Event creation tested
- [ ] Registration working
- [ ] All logs clean (no errors)

---

**For detailed instructions, see:**
- [RENDER_DEPLOYMENT.md](./backend/RENDER_DEPLOYMENT.md)
- [RENDER_DEPLOYMENT_CHECKLIST.md](./RENDER_DEPLOYMENT_CHECKLIST.md)
