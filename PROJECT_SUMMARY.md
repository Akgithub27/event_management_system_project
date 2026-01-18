# PROJECT SUMMARY & ARCHITECTURE

## Overview
This is a comprehensive Event Management System built with modern web technologies. It demonstrates professional enterprise-level development practices for a 4+ years experienced developer.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend (React)                         │
│                    Port: 3000 (Development)                     │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Pages: Login, Signup, Dashboard, EventDetail, CreateEvent │  │
│  │ Context: AuthContext (Authentication State)              │  │
│  │ API Service: Axios Interceptors with JWT                 │  │
│  │ Styling: Custom CSS with Gradient Design                 │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬──────────────────────────────────┘
                             │
                    REST API (Axios/Fetch)
                             │
┌────────────────────────────┴──────────────────────────────────┐
│                       Backend (Spring Boot)                    │
│                    Port: 8080 (Development)                    │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Controllers: AuthController, EventController, etc.        │  │
│  │ Services: AuthService, EventService, RegistrationService │  │
│  │ Repositories: UserRepository, EventRepository, etc. (JPA) │  │
│  │ Security: Spring Security + JWT Authentication           │  │
│  │ Email: JavaMailSender for Notifications                  │  │
│  │ Entities: User, Event, Registration, Speaker, etc.       │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬──────────────────────────────────┘
                             │
                      JDBC (MySQL Driver)
                             │
┌────────────────────────────┴──────────────────────────────────┐
│                     Database (MySQL 8.0)                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Tables: users, events, registrations, speakers, etc.     │  │
│  │ Relationships: Many-to-Many, One-to-Many                 │  │
│  │ Indexes: Email, User-Event Composite, etc.               │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Tech Stack Details

### Backend (Spring Boot 3.2)
- **Framework**: Spring Boot 3.2.0
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + JWT (JJWT 0.12.3)
- **Database Driver**: MySQL Connector/J
- **Build Tool**: Maven 3.8+
- **Java Version**: 17+
- **Email**: JavaMailSender
- **Logging**: SLF4J with Logback
- **Testing**: JUnit 5, Mockito

### Frontend (React 18)
- **Library**: React 18.2.0
- **Router**: React Router 6.20.0
- **HTTP Client**: Axios 1.6.0
- **Build Tool**: Create React App (react-scripts 5.0.1)
- **Node Version**: 16+
- **Package Manager**: npm/yarn

### Database (MySQL 8.0)
- **Engine**: InnoDB
- **Encoding**: UTF-8
- **Timezone**: UTC
- **Connection Pooling**: HikariCP
- **Max Pool Size**: 10

## File Structure

### Backend
```
backend/
├── src/main/java/com/eventmanagement/
│   ├── EventManagementApplication.java      (Main Entry Point)
│   ├── entity/                              (JPA Entities)
│   │   ├── User.java
│   │   ├── Event.java
│   │   ├── Speaker.java
│   │   ├── EventSpeaker.java
│   │   ├── EventRegistration.java
│   │   ├── EventAttendance.java
│   │   ├── UserRole.java
│   │   └── RegistrationStatus.java
│   ├── repository/                          (Spring Data JPA)
│   │   ├── UserRepository.java
│   │   ├── EventRepository.java
│   │   ├── SpeakerRepository.java
│   │   ├── EventSpeakerRepository.java
│   │   ├── EventRegistrationRepository.java
│   │   └── EventAttendanceRepository.java
│   ├── service/                             (Business Logic)
│   │   ├── AuthService.java
│   │   ├── EventService.java
│   │   ├── RegistrationService.java
│   │   └── EmailService.java
│   ├── controller/                          (REST Endpoints)
│   │   ├── AuthController.java
│   │   ├── EventController.java
│   │   └── RegistrationController.java
│   ├── dto/                                 (Data Transfer Objects)
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── SignupRequest.java
│   │   ├── SignupResponse.java
│   │   ├── EventDTO.java
│   │   ├── CreateEventRequest.java
│   │   ├── SpeakerDTO.java
│   │   └── ApiResponse.java
│   ├── config/                              (Configuration)
│   │   ├── JwtProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── SecurityConfig.java
│   └── exception/                           (Exception Handling)
│       ├── ResourceNotFoundException.java
│       ├── BadRequestException.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   └── application.yml                      (Configuration)
├── src/test/java/com/eventmanagement/
│   ├── AuthServiceTest.java
│   └── EventServiceTest.java
├── pom.xml                                  (Maven Dependencies)
└── Dockerfile                               (Docker Configuration)
```

### Frontend
```
frontend/
├── src/
│   ├── pages/                               (Page Components)
│   │   ├── Login.js
│   │   ├── Signup.js
│   │   ├── Dashboard.js
│   │   ├── EventDetail.js
│   │   └── CreateEvent.js
│   ├── components/                          (Reusable Components)
│   │   └── EventCard.js
│   ├── services/                            (API Integration)
│   │   └── api.js
│   ├── context/                             (State Management)
│   │   └── AuthContext.js
│   ├── styles/                              (CSS Styling)
│   │   ├── index.css
│   │   ├── Auth.css
│   │   ├── Dashboard.css
│   │   ├── EventCard.css
│   │   ├── EventDetail.css
│   │   ├── CreateEvent.css
│   │   └── App.css
│   ├── App.js                               (Root Component)
│   └── index.js                             (Entry Point)
├── public/
│   └── index.html                           (HTML Template)
├── package.json                             (Dependencies)
├── netlify.toml                             (Netlify Config)
├── .env                                     (Development Env)
├── .env.production                          (Production Env)
└── Dockerfile                               (Docker Config)
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  role ENUM('ADMIN', 'USER') NOT NULL,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Events Table
```sql
CREATE TABLE events (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  description LONGTEXT,
  event_date TIMESTAMP NOT NULL,
  venue VARCHAR(150) NOT NULL,
  category VARCHAR(100) NOT NULL,
  capacity INT NOT NULL,
  registered_count INT DEFAULT 0,
  created_by BIGINT NOT NULL,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (created_by) REFERENCES users(id)
);
```

### Event Registrations Table
```sql
CREATE TABLE event_registrations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status ENUM('REGISTERED', 'CANCELLED', 'ATTENDED') DEFAULT 'REGISTERED',
  registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  confirmation_sent_at TIMESTAMP,
  UNIQUE KEY unique_registration (event_id, user_id),
  FOREIGN KEY (event_id) REFERENCES events(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/signup | Register new user |
| POST | /api/auth/login | Login user, return JWT |

### Events
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/events | Get all active events |
| GET | /api/events/upcoming | Get upcoming events |
| GET | /api/events/{id} | Get event details |
| GET | /api/events/search?q=term | Search events |
| GET | /api/events/category/{category} | Filter by category |
| POST | /api/events | Create event (Admin) |
| PUT | /api/events/{id} | Update event |
| DELETE | /api/events/{id} | Delete event |

### Registrations
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/registrations/events/{id} | Register for event |
| DELETE | /api/registrations/events/{id} | Cancel registration |

## Security Implementation

### JWT Authentication Flow
1. User logs in with email/password
2. Server validates credentials
3. Server generates JWT token containing:
   - User email
   - User ID
   - User role
   - Expiration time (24 hours)
4. Client stores token in localStorage
5. Client sends token in Authorization header for subsequent requests
6. Server validates token on each request via JwtAuthenticationFilter

### Spring Security Configuration
- CORS enabled for http://localhost:3000
- CSRF disabled (stateless API)
- Session management set to STATELESS
- Role-based access control (RBAC)
- Password encoding with BCrypt

## Deployment Strategy

### Development
```bash
Backend:  mvn spring-boot:run              (localhost:8080)
Frontend: npm start                        (localhost:3000)
Database: MySQL local instance
```

### Staging
```bash
Backend:  Deploy to Render/Railway
Frontend: Deploy to Netlify
Database: AWS RDS MySQL or Render Postgres
```

### Production
```bash
Backend:  AWS EC2 + RDS / Render / Railway
Frontend: Netlify CDN with custom domain
Database: AWS RDS MySQL (Multi-AZ)
CI/CD:    GitHub Actions
```

## Performance Optimizations

### Backend
- HikariCP connection pooling (max 10 connections)
- Lazy loading for JPA relationships
- Query optimization with proper indexes
- Response compression with gzip
- Caching strategy for repeated queries

### Frontend
- Code splitting with React Router
- Lazy loading of components
- Axios request caching
- Image optimization
- CSS/JS minification in production
- Service Worker for offline support

## Testing Strategy

### Backend (JUnit 5 + Mockito)
- Unit tests for Services
- Mock repositories and dependencies
- 80%+ code coverage target
- Run: `mvn test`

### Frontend (Jest + React Testing Library)
- Component unit tests
- Integration tests
- User interaction testing
- Run: `npm test`

## Error Handling

### Global Exception Handler
- ResourceNotFoundException → 404
- BadRequestException → 400
- All exceptions logged to console
- User-friendly error messages

### Frontend Error Handling
- Try-catch blocks around API calls
- Error state in components
- User notifications for errors
- Automatic logout on 401 response

## Monitoring & Logging

### Backend Logging
- SLF4J + Logback configuration
- Different log levels: ERROR, WARN, INFO, DEBUG
- Timestamp and thread information
- Structured logging for errors

### Frontend Logging
- Browser console logging
- Error tracking (can integrate Sentry)
- User action logging
- Performance monitoring

## Future Enhancements

1. **Payment Integration**
   - Stripe/PayPal for paid events
   - Invoice generation

2. **Advanced Features**
   - Event ratings and reviews
   - Waitlist management
   - Bulk email notifications
   - Event categories management

3. **Analytics**
   - Event attendance analytics
   - User engagement metrics
   - Revenue reports

4. **Mobile**
   - React Native mobile app
   - Native push notifications
   - QR code for check-in

5. **Integration**
   - Google Calendar sync
   - Slack notifications
   - Zoom meeting integration

## Development Standards

### Code Quality
- Clean code principles
- SOLID design patterns
- DRY (Don't Repeat Yourself)
- Meaningful variable/method names

### Version Control
- Git flow branching strategy
- Meaningful commit messages
- Pull request reviews
- Atomic commits

### Documentation
- Comprehensive README
- Code comments for complex logic
- API documentation
- Deployment guides

---

**This project demonstrates enterprise-level full-stack development with best practices for authentication, security, testing, and deployment.**
