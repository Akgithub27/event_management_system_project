# Event Management System

A full-stack event management application built with Spring Boot, React, and MySQL.

## Tech Stack

- **Backend**: Spring Boot 3.2, Spring Security, JPA/Hibernate
- **Frontend**: React 18, React Router, Axios
- **Database**: MySQL 8.0
- **Authentication**: JWT Tokens
- **Email**: JavaMailSender
- **Deployment**: 
  - Frontend: Netlify
  - Backend: Render/Railway/AWS EC2

## Features

- ✅ User Registration & Authentication
- ✅ Event Browsing & Search
- ✅ Event Registration & Management
- ✅ Admin Event Creation & Deletion
- ✅ Event Categories & Filters
- ✅ Attendance Tracking
- ✅ Email Notifications
- ✅ Responsive Design

## Project Structure

```
event_management_system_project/
├── backend/                 # Spring Boot Application
│   ├── src/
│   │   ├── main/java/com/eventmanagement/
│   │   │   ├── entity/         # JPA Entities
│   │   │   ├── repository/     # Data Access Layer
│   │   │   ├── service/        # Business Logic
│   │   │   ├── controller/     # REST Controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── config/         # Configuration
│   │   │   └── exception/      # Exception Handling
│   │   └── resources/
│   │       └── application.yml # Configuration
│   └── pom.xml
│
└── frontend/                # React Application
    ├── src/
    │   ├── pages/          # Page Components
    │   ├── components/     # Reusable Components
    │   ├── services/       # API Services
    │   ├── context/        # React Context
    │   ├── styles/         # CSS Styles
    │   ├── App.js          # Root Component
    │   └── index.js        # Entry Point
    ├── public/
    │   └── index.html      # HTML Template
    └── package.json
```

## Setup Instructions

### Backend Setup

1. **Prerequisites**:
   - Java 17+
   - Maven 3.8+
   - MySQL 8.0+

2. **Database Setup**:
```sql
CREATE DATABASE event_management;
```

3. **Configure Database** (backend/src/main/resources/application.yml):
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/event_management
    username: root
    password: your_password
```

4. **Run the Application**:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Prerequisites**:
   - Node.js 16+
   - npm or yarn

2. **Install Dependencies**:
```bash
cd frontend
npm install
```

3. **Configure API URL** (.env):
```
REACT_APP_API_URL=http://localhost:8080/api
```

4. **Start Development Server**:
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login user

### Events
- `GET /api/events` - Get all events
- `GET /api/events/upcoming` - Get upcoming events
- `GET /api/events/{id}` - Get event details
- `GET /api/events/search?q=term` - Search events
- `GET /api/events/category/{category}` - Get events by category
- `POST /api/events` - Create event (Admin only)
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

### Registrations
- `POST /api/registrations/events/{eventId}` - Register for event
- `DELETE /api/registrations/events/{eventId}` - Cancel registration
- `GET /api/registrations/my-registrations` - Get user registrations

## Deployment

### Frontend (Netlify)

1. Push your code to GitHub
2. Connect your GitHub repository to Netlify
3. Build Command: `npm run build`
4. Publish Directory: `build`
5. Set environment variable:
   - `REACT_APP_API_URL` = Your backend API URL

### Backend (Render/Railway/AWS EC2)

#### Option 1: Render
1. Connect GitHub repository to Render
2. Create Web Service
3. Build Command: `./mvnw clean install`
4. Start Command: `java -jar target/event-management-system-1.0.0.jar`
5. Add Environment Variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET`
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`

#### Option 2: Railway
1. Connect GitHub repository to Railway
2. Railway will automatically detect Spring Boot project
3. Configure environment variables in Railway dashboard

#### Option 3: AWS EC2
1. Launch an EC2 instance with Java installed
2. Install MySQL on the instance
3. Deploy JAR file and run it

## Email Configuration

Update `application.yml` with your Gmail credentials:
```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-password
```

Generate App Password from your Google Account:
- Visit https://myaccount.google.com/apppasswords
- Generate password for Mail
- Use this password in configuration

## Default Admin Account

To create an admin user, run this SQL:
```sql
INSERT INTO users (email, password, first_name, last_name, role, is_active, created_at, updated_at)
VALUES ('admin@example.com', '[BCRYPT_HASH]', 'Admin', 'User', 'ADMIN', true, NOW(), NOW());
```

Note: Use `BCryptPasswordEncoder` to generate password hash.

## Testing

### Backend Unit Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Performance Considerations

- JWT tokens expire after 24 hours
- Pagination implemented for large datasets
- Connection pooling configured
- CORS enabled for secure cross-origin requests
- Response caching for static assets

## Security Features

- Spring Security with JWT Authentication
- CORS Configuration
- Password Encryption with BCrypt
- Role-Based Access Control (RBAC)
- Input Validation
- Exception Handling

## Future Enhancements

- [ ] Payment Integration
- [ ] Ticket System
- [ ] User Ratings & Reviews
- [ ] Advanced Analytics Dashboard
- [ ] Mobile App (React Native)
- [ ] WebSocket Real-time Updates
- [ ] Social Media Integration
- [ ] Google Calendar Integration

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Support

For support, email support@eventmanagement.com or create an issue on GitHub.

---

**Made with ❤️ by a 4-year experienced developer**
