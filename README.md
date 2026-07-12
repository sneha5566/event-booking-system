# 🎟️ Event Booking System

A production-ready Event Booking System built with Spring Boot that allows users to browse events, book seats, manage bookings, and enables organizers/admins to manage venues, shows, and events securely.

---

## 🚀 Features

### Authentication & Security
- JWT Authentication
- Role-Based Authorization (USER, ORGANIZER, ADMIN)
- Password Encryption with Spring Security
- Secure REST APIs

### Event Management
- Create Events
- Update Events
- Delete Events
- List Events
- Filter Active Events

### Venue Management
- Create Venue
- Update Venue
- Delete Venue
- View Venues

### Show Management
- Schedule Shows
- Prevent Overlapping Shows
- Dynamic Seat Pricing
- Show Status Management

### Booking System
- Book Seats
- Seat Locking
- Optimistic Locking to Prevent Double Booking
- Booking History
- Booking Cancellation

### Payment
- Payment Processing Flow
- Payment Status Tracking
- Booking Confirmation

### Scheduler
- Automatic Cancellation of Expired Bookings

### Dashboard APIs
- Admin Dashboard
- Organizer Dashboard
- Booking Statistics
- Revenue Statistics

### API Documentation
- Swagger / OpenAPI Integration

---

# 🛠 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Lombok
- Swagger (OpenAPI)
- Postman

---

# 📂 Project Structure

```
src
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── mapper
 ├── security
 ├── config
 ├── util
 └── exception
```

---

# 🔐 User Roles

### USER

- Browse Events
- Book Tickets
- Cancel Booking
- View Booking History

### ORGANIZER

- Manage Events
- Manage Shows
- View Organizer Dashboard

### ADMIN

- Manage Users
- Manage Venues
- Manage Events
- View Analytics Dashboard

---

# ⚙️ Running the Project

## Clone Repository

```bash
git clone <repository-url>
```

## Navigate

```bash
cd event-booking-system
```

## Configure Database

Update:

```
src/main/resources/application.properties
```

with your MySQL credentials.

Example:

```
spring.datasource.url=jdbc:mysql://localhost:3306/event_booking
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## Run

```bash
./mvnw spring-boot:run
```

or

Run `EventBookingApplication` directly from IntelliJ.

---

# 📖 API Documentation

After starting the application:

```
http://localhost:8080/swagger-ui/index.html
```

---

# 🔒 Authentication

Protected APIs require a JWT token.

Workflow:

1. Register
2. Login
3. Copy JWT Token
4. Authorize in Swagger
5. Access Protected APIs

---

# 📌 Future Improvements

- React Frontend
- Docker Deployment
- CI/CD Pipeline
- Email Notifications
- Payment Gateway Integration
- Cloud Deployment

---

# 👩‍💻 Author

**Sneha Neerukonda**

Computer Science Engineering Student

Built with ❤️ using Spring Boot.