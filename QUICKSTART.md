# Quick Start Guide

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Git

## Backend Setup (Spring Boot)

```bash
# Navigate to backend
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

**Backend URL**: `http://localhost:8080`

## Frontend Setup (React)

```bash
# Navigate to frontend
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

**Frontend URL**: `http://localhost:3000`

## Database Setup

```bash
# Create database
mysql -u root -p
CREATE DATABASE event_management;
exit;
```

Update `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/event_management
    username: root
    password: your_password
```

## Default Test Credentials

After database is created and Spring Boot has run once:

### Create Admin User
```sql
-- Use BCrypt to hash "admin123" -> $2a$10$...
INSERT INTO users (email, password, first_name, last_name, role, is_active, created_at, updated_at)
VALUES ('admin@example.com', '$2a$10$slYQmyNdGzin7olVN3p72OPST9/PgBkqquzi.Eid34qqjwNym3H5m', 'Admin', 'User', 'ADMIN', true, NOW(), NOW());
```

**Admin Login**:
- Email: `admin@example.com`
- Password: `admin123`

## Features to Test

### 1. User Authentication
- [ ] Sign up new user
- [ ] Login with credentials
- [ ] Logout

### 2. Event Management
- [ ] View all events
- [ ] Search events
- [ ] Filter by category
- [ ] Register for event
- [ ] Cancel registration

### 3. Admin Features
- [ ] Create new event
- [ ] Update event
- [ ] Delete event
- [ ] View registrations

### 4. Email Notifications
- [ ] Welcome email on signup
- [ ] Registration confirmation
- [ ] Event reminders

## Troubleshooting

**Port Already in Use**
```bash
# Kill process using port 8080 (Backend)
lsof -ti:8080 | xargs kill

# Kill process using port 3000 (Frontend)
lsof -ti:3000 | xargs kill
```

**Database Connection Error**
- Verify MySQL is running: `mysql -u root -p`
- Check credentials in application.yml
- Ensure event_management database exists

**Email Not Sending**
- Generate Gmail App Password
- Update MAIL_USERNAME and MAIL_PASSWORD
- Enable 2FA on Gmail account

## Project Structure

```
📁 event_management_system_project
├── 📁 backend
│   ├── src/main/java/com/eventmanagement/
│   ├── src/main/resources/
│   └── pom.xml
├── 📁 frontend
│   ├── src/
│   ├── public/
│   └── package.json
├── README.md
├── BACKEND_DEPLOYMENT.md
└── FRONTEND_DEPLOYMENT.md
```

## Next Steps

1. **Customize**
   - Update event categories
   - Modify email templates
   - Add custom CSS themes

2. **Deploy**
   - Follow BACKEND_DEPLOYMENT.md
   - Follow FRONTEND_DEPLOYMENT.md

3. **Scale**
   - Add more features
   - Implement pagination
   - Add reporting

## Documentation

- [Full README](README.md)
- [Backend Deployment](BACKEND_DEPLOYMENT.md)
- [Frontend Deployment](FRONTEND_DEPLOYMENT.md)

## Support

For issues:
1. Check logs (browser console and Spring Boot console)
2. Verify all prerequisites are installed
3. Check database connectivity
4. Review API endpoints

---

Happy coding! 🎉
