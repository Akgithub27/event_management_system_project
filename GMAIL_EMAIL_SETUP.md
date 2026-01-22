# Gmail SMTP Setup - Step by Step

Your email authentication is failing because the environment variables are not set. Follow these steps to fix it.

## Step 1: Generate a Gmail App Password

⚠️ **IMPORTANT: You must have 2-Factor Authentication enabled on your Google Account first!**

1. Go to Google Account: https://myaccount.google.com
2. Click on **Security** (left sidebar)
3. Look for **App passwords** (if you don't see it, enable 2-Factor Authentication first)
4. Select:
   - App: **Mail**
   - Device: **Windows Computer**
5. Click **Generate**
6. Google will show you a **16-character password** like: `abcd efgh ijkl mnop`
   - Copy this password (remove the spaces when you use it)
   - Example: `abcdefghijklmnop`

---

## Step 2: Set Environment Variables on Windows

### Option A: Permanent Setup (Recommended)

1. **Open Environment Variables:**
   - Press `Windows + R`
   - Type `sysdm.cpl`
   - Press Enter
   - Click **Environment Variables** button
   - Click **New** under User Variables

2. **Add Environment Variables:**

   | Variable Name | Variable Value |
   |---|---|
   | `MAIL_USERNAME` | `your-email@gmail.com` |
   | `MAIL_PASSWORD` | `abcdefghijklmnop` (16-char app password) |
   | `JWT_SECRET` | `my-super-secret-key-that-is-at-least-256-bits-long-please-change-in-production` |

3. **Example:**
   - **Variable Name:** `MAIL_USERNAME`
   - **Variable Value:** `aakashprakash27@gmail.com`
   - Click **OK**

4. **Repeat for:**
   - `MAIL_PASSWORD` → Your 16-character app password
   - `JWT_SECRET` → Keep the default or create your own

5. **Restart your IDE/Terminal** after adding environment variables

### Option B: Temporary Setup (for quick testing)

Run these commands in PowerShell (before starting your backend):

```powershell
$env:MAIL_USERNAME = "aakashprakash27@gmail.com"
$env:MAIL_PASSWORD = "abcdefghijklmnop"
$env:JWT_SECRET = "my-super-secret-key-that-is-at-least-256-bits-long-please-change-in-production"
```

---

## Step 3: Restart Your Backend

After setting the environment variables, restart your Spring Boot application:

```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

---

## Step 4: Test Email

1. **Sign up** with a new account on your app
2. You should see:
   - ✅ Success message on screen (immediate)
   - ✅ Welcome email arrives in 5-10 seconds (async)
3. **Register for an event**
   - ✅ Registration succeeds (immediate)
   - ✅ Confirmation email arrives (async)

---

## Troubleshooting

### Error: "Authentication failed"
- ❌ **Problem:** Wrong app password or not using app password
- ✅ **Solution:** 
  - Make sure you generated a 16-character **App Password** from Google
  - Not your regular Gmail password
  - Remove spaces from the password when copying

### Error: "Environment variables not found"
- ❌ **Problem:** Environment variables not set or terminal not restarted
- ✅ **Solution:**
  - Set them in Windows Environment Variables (Option A)
  - Restart your IDE completely
  - Or run Option B commands in new PowerShell window

### Error: "Less secure apps"
- ❌ **Problem:** Old Gmail security setting
- ✅ **Solution:**
  - Use App Password instead (step 1)
  - This is the correct method for 2FA accounts

### No email appears after 30 seconds
- ❌ **Problem:** Email might be in spam folder
- ✅ **Solution:**
  - Check Gmail **Spam** folder
  - Check if Gmail blocked it (check Blocked Addresses)
  - Review backend logs for errors

---

## Verification Checklist

- [ ] Google Account has 2-Factor Authentication enabled
- [ ] Generated 16-character App Password from Google
- [ ] Set `MAIL_USERNAME` environment variable
- [ ] Set `MAIL_PASSWORD` environment variable (app password, not regular password)
- [ ] Set `JWT_SECRET` environment variable
- [ ] Restarted IDE/Terminal
- [ ] Rebuilt backend with `mvn clean compile`
- [ ] Server shows "Started EventManagementApplication" message
- [ ] Signed up and received welcome email in 5-10 seconds
- [ ] Registered for event and received confirmation email

---

## What Happens After Setup

**Before fix:**
```
WARN [...] Failed to send welcome email: Authentication failed
```

**After fix:**
```
[server logs show successful email sending in background]
```

Emails are sent **asynchronously** (in background), so:
- Your signup/registration **completes immediately** ✓
- Email arrives in **5-10 seconds** (you'll see it in inbox)

This is intentional - email failures won't crash your registration process!

---

## Quick Reference

**Email Configuration in application.yml:**
```yaml
mail:
  host: smtp.gmail.com
  port: 587
  username: ${MAIL_USERNAME:your-email@gmail.com}
  password: ${MAIL_PASSWORD:your-app-password}
```

The values after `:` are defaults if environment variables aren't set. Your environment variables **override** these defaults.

---

## Still Having Issues?

Check these files to confirm the setup:
- [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml) - Email config
- [backend/src/main/java/com/eventmanagement/service/EmailService.java](backend/src/main/java/com/eventmanagement/service/EmailService.java) - Email sending logic
- [backend/pom.xml](backend/pom.xml) - Spring Mail dependency

All are correctly configured for Gmail SMTP. Only the environment variables need to be set on your Windows system.
