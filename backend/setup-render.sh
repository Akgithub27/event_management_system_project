#!/bin/bash

# Render Deployment Setup Script
# This script helps generate required secrets and configure for Render deployment

echo "=========================================="
echo "Event Management System - Render Setup"
echo "=========================================="
echo ""

# Generate JWT Secret
echo "Generating JWT Secret..."
JWT_SECRET=$(openssl rand -base64 32)
echo "JWT_SECRET=$JWT_SECRET"
echo ""

# Create .env.render file with generated values
cat > .env.render << EOF
# Generated on $(date)
# Use these values in Render Dashboard

# Generated JWT Secret (256+ bits)
JWT_SECRET=$JWT_SECRET

# Email Configuration
# Get app password from: https://myaccount.google.com/apppasswords
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password

# Database Configuration (From Render MySQL Service)
# These are provided by Render when you create the database
SPRING_DATASOURCE_URL=mysql://user:password@host/database
SPRING_DATASOURCE_USERNAME=event_user
SPRING_DATASOURCE_PASSWORD=generated-by-render

# Application Configuration
SPRING_PROFILES_ACTIVE=production
SPRING_JPA_HIBERNATE_DDL_AUTO=update
JWT_EXPIRATION=86400000
EOF

echo "✓ Created .env.render file with configuration"
echo ""

# Next steps
echo "=========================================="
echo "NEXT STEPS:"
echo "=========================================="
echo ""
echo "1. Copy JWT_SECRET from above:"
echo "   JWT_SECRET=$JWT_SECRET"
echo ""
echo "2. Get Gmail App Password:"
echo "   - Go to https://myaccount.google.com/apppasswords"
echo "   - Enable 2-Factor Authentication first"
echo "   - Select Mail and your device"
echo "   - Copy the 16-character password"
echo ""
echo "3. Create Render Services:"
echo "   a) MySQL Database:"
echo "      - Name: event-management-db"
echo "      - Plan: Free or Standard"
echo ""
echo "   b) Web Service:"
echo "      - Repository: Your GitHub repo"
echo "      - Environment: Java (auto-detected)"
echo "      - Build: mvn clean install -DskipTests"
echo "      - Start: java -jar target/event-management-system-1.0.0.jar"
echo ""
echo "4. Add Environment Variables to Web Service:"
echo "   (Copy from .env.render file with your values)"
echo ""
echo "5. Deploy and test:"
echo "   curl https://your-service.onrender.com/api/events"
echo ""
echo "=========================================="
echo ""
echo "Configuration saved to: .env.render"
echo ""
