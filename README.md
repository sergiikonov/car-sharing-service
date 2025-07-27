# Car Sharing Service API

## ✨ Project Overview

Welcome to **Car Sharing Service**, a REST API designed to modernize and digitize the car rental process.  
This project was created to replace the outdated paper-based system of managing cars, rentals, users, and payments with a fully automated online platform.

The API provides functionality for managing cars, handling user authentication via JWT, renting vehicles, processing payments through **Stripe API**, and sending real-time notifications to administrators via **Telegram Bot**.  

Additionally, the system includes a **daily overdue rental check** that automatically detects overdue rentals and sends detailed notifications every morning at **09:00 AM**.

## 🛠️ Technological Foundations

- [Java 17](https://www.oracle.com/java/): Modern, stable, and LTS version of Java.
- [Spring Boot 3.5.3](https://spring.io/projects/spring-boot): For building production-ready REST APIs quickly and efficiently.
- [Spring Security (JWT)](https://spring.io/projects/spring-security): Secure authentication and authorization using JWT tokens.
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa): For working with relational data and ORM.
- [MySQL 8.0.33](https://www.mysql.com/): Relational database for data persistence.
- [Liquibase](https://www.liquibase.org/): Database migrations and version control.
- [MapStruct](https://mapstruct.org/): Automatic mapping between DTOs and entities.
- [Stripe API](https://stripe.com/docs/api): Payment integration for online rentals.
- [Telegram Bot API](https://core.telegram.org/bots/api): Real-time notifications for system events.
- [Docker & Docker Compose](https://www.docker.com/): Containerized deployment for consistent environments.
- [Swagger / OpenAPI](https://swagger.io/): Interactive API documentation.
- [JUnit 5](https://junit.org/): Unit and integration testing.

## 🔧 Key Functionalities

#### 1. User Authentication & Authorization:
- `POST /auth/registration` — Register a new user with email and password.
- `POST /auth/login` — Authenticate user and receive JWT token for secure access.
- Role-based access control (`CUSTOMER`, `MANAGER`) via Spring Security.

#### 2. User Management:
- `GET /users/me` — Get current user profile.
- `PUT /users/me` — Update user profile data.
- `PUT /users/{id}/role` — **(Manager only)** Update user's role.

#### 3. Car Management:
- `POST /cars` — **(Manager only)** Create a new car with specified parameters.
- `GET /cars` — Get a paginated list of all available cars.
- `GET /cars/{id}` — Get details for a specific car.
- `PUT /cars/{id}` — **(Manager only)** Update car details.
- `DELETE /cars/{id}` — **(Manager only)** Delete a car from the fleet.

#### 4. Rental Management:
- `POST /rentals` — Create a new rental for a selected car.
- `GET /rentals` — Get rentals for a specific user; managers can view all rentals.
- `GET /rentals/{id}` — Get details of a specific rental.
- `POST /rentals/{id}/return` — Return a rented car and update its availability.
- **Overdue Check:** A scheduled job runs every day at **09:00 AM** to check overdue rentals and automatically sends Telegram notifications with details about each overdue rental.

#### 5. Payment Management:
- `POST /payments` — Initiate a Stripe payment session for a rental.
- `GET /payments` — View payments (user-specific or all for managers).
- `GET /payments/success` — Handle successful Stripe payment.
- `GET /payments/cancel` — Handle failed or canceled payment sessions.

#### 6. Telegram Notifications:
- Automatic notifications via Telegram Bot for all critical actions:
  - New rental creation.
  - Payment initiation and completion.
  - Car return status.
  - **Daily overdue check results at 09:00 AM**.

## ⚙️ Setting Up the Project Locally

### ✅ Requirements:
- Java 17
- Maven
- Docker
- MySQL

### 🚀 Steps:

1. Clone the repository:
```sh
git clone https://github.com/sergiikonov/car-sharing-service
cd car-sharing-service
```
2. Configure Environment Variables:
```sh
Create a .env file (a template is included as .env.origin).
```
3. Build the project:
```sh
mvn clean install
```
4. Run with Docker:
```sh
docker-compose build
docker-compose up
```
5. Access Swagger UI:
```sh
http://localhost:8080/swagger-ui.html
```
## 📭 Postman Collection

I’ve prepared a Postman collection to quickly test all endpoints:
👉 [Download Postman Collection](https://sergii-838248.postman.co/workspace/Sergii's-Workspace~cc6177e6-e3a2-4ef1-8b2a-04bc61c70757/collection/43376300-fc32ed2c-df61-45dc-a413-a686dba4f32a?action=share&creator=43376300)

## 🤝 Challenges and How I Overcame Them

### Stripe API Integration:
[]()
> Integrating Stripe API for payments was the most challenging part of the project. At first, it seemed straightforward, but I encountered several issues with session creation, webhooks, and payment validation. After carefully analyzing Stripe’s documentation and debugging step-by-step, the integration was completed successfully. This process provided valuable real-world experience with payment systems.
### Telegram Bot Notifications:
[]()
> Setting up the Telegram Bot and ensuring it works reliably with long polling and secure environment configuration required several iterations. Eventually, the bot became a critical part of the system for real-time alerts, including daily overdue rental checks.
## 🏁 Conclusion

The Car Sharing Service API project integrates modern Java backend technologies to solve real-world challenges such as payment processing, rental management, and live notifications.
It provides a scalable and secure foundation for a full-fledged car rental platform.

## 🌐 Repository

🔗 [GitHub Repository](https://github.com/sergiikonov/car-sharing-service)

## 📧 Contact

Project Lead: Sergii Konovalov
Email: sergii2konovalov@gmail.com