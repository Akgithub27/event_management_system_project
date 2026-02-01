# Railway Deployment - Step-by-Step Guide

## FINAL SOLUTION FOR DEPLOYMENT

### Current Status:
✅ Root Dockerfile created and configured  
✅ MySQL database on Railway is ready  
✅ Environment variables prepared  

---

## STEP 1: Update Railway Dashboard Settings (CRITICAL)

**Go to Railway Dashboard → Your Project → Settings**

### Section: Build
1. Click "Build" on the left sidebar
2. Look for **Builder** dropdown - Select **"Dockerfile"**
3. Look for **Dockerfile Path** field - Clear it and enter: `Dockerfile`
4. Look for **Watch Paths** field - Clear everything
5. **SAVE**

### Section: Deploy  
1. Make sure **Region** is `us-west2` (or your preferred region)
2. Leave other settings as default
3. **SAVE**

---

## STEP 2: Ensure Environment Variables Are Set

**Go to Railway Dashboard → Your Project → Variables Tab**

Add these variables (or copy-paste the JSON values):

```
SPRING_PROFILES_ACTIVE=production
PORT=8080
DATABASE_URL=jdbc:mysql://turntable.proxy.rlwy.net:48482/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DATABASE_USER=root
DATABASE_PASSWORD=UOUrchBLOLddRwwRybIrVuHvgCAqwTDZ
JWT_SECRET=your_super_secret_key_that_is_at_least_32_characters_long
JWT_EXPIRATION=86400000
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=aakashprakash27@gmail.com
MAIL_PASSWORD=tbbb unit yygw rezj
MAIL_FROM_ADDRESS=noreply@eventmanagement.com
DB_HIKARI_MAX_POOL_SIZE=10
DB_HIKARI_MIN_IDLE=5
DB_HIKARI_CONNECTION_TIMEOUT=30000
DB_HIKARI_IDLE_TIMEOUT=600000
DB_HIKARI_MAX_LIFETIME=1800000
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_EVENTMANAGEMENT=DEBUG
```

---

## STEP 3: Git Push Your Changes

```bash
cd "c:\Users\aakas\OneDrive\Desktop\GUVI Projects\event_management_system_project"

# Check git status
git status

# Add all changes
git add .

# Commit
git commit -m "Fix Railway deployment - update Dockerfile and remove old config files"

# Push to GitHub
git push origin main
```

---

## STEP 4: Trigger Deployment in Railway

1. Go to Railway Dashboard
2. Click on your service (event_management_system_project)
3. Click **Deployments** tab
4. You should see the latest commit being deployed automatically
5. Watch the logs - it should build and deploy successfully

---

## Expected Build Process:

```
✓ Initialization (00:00)
✓ Build - Build Image (should succeed now!)
  - Pulls Maven image
  - Copies pom.xml and src/
  - Runs: mvn clean package -DskipTests
  - Builds JAR file
  - Creates Docker image from JAR
✓ Deploy (will start when build is complete)
✓ Post-deploy
```

---

## TROUBLESHOOTING

### If build still fails with "Dockerfile not found":
1. Go to Railway Settings → Build
2. Check the **Dockerfile Path** - it must be exactly: `Dockerfile`
3. Make sure there's no leading slash or "backend/" prefix
4. Click Save and trigger new deployment

### If build fails during Maven:
1. Check that pom.xml exists in `/backend/pom.xml`
2. Check that `backend/src/` directory exists with Java files
3. Verify no syntax errors in pom.xml

### If deployment succeeds but app won't start:
1. Check Environment Variables are all set
2. Go to Logs tab and look for error messages
3. Check DATABASE_URL is correct

---

## What Was Changed:

1. ✅ **Deleted**: `backend/Dockerfile` (old file)
2. ✅ **Deleted**: `backend/railway.json` (old config)
3. ✅ **Deleted**: `backend/railway.yaml` (old config)
4. ✅ **Deleted**: `backend/.env.railway` (old template)
5. ✅ **Created**: Root level `Dockerfile` with correct paths
6. ✅ **Kept**: `backend/railway-env-variables.json` (for reference)

---

## Next Steps After Successful Deployment:

1. Test the API endpoint from Railway URL
2. Update frontend API base URL to Railway URL
3. Test login and event creation
4. Set up custom domain if needed
5. Monitor logs and metrics

---

**Expected Deployment Time**: 3-5 minutes from push to live

Good luck! 🚀
