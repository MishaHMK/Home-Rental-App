## 🏚️: **Accommodation Rental Project**

This is **Accommodation Rental API**, a spring-boot backend built for accommodation rental

## :mag_right: **Project Overview**

Home Rental provides different operations:
- Role-based access/authorization (Admin, Customer) 
- User management: Allows to find/edit data about users in system
- Accommodation management: Allows to find/create/remove/edit data about accommodations in system
- Booking management: Allows to find/create/remove/edit data about bookings of avaliable accommodations
- Payment management: Allows to create/confirm/cancel/renew payments with support of Stripe payment system
- Telegram notifications: Admin users can be notified through Telegram bot about key operations in system

## ⏬ You can find overview of all api endpoints in the end of this document

## :hammer_and_wrench: **Technology Stack**
**Core:**
| Tool    | Description                                         |
|---------|-----------------------------------------------------|
| Java 17 | Core programming language of the backend            |
| Maven   | Project management and build tool                   |

**Spring:**
| Tool                     | Description                                                |
|--------------------------|------------------------------------------------------------|
| Spring Boot 3.4.2     | Advanced architecture framework for building applications |
| Spring Boot Web      | Enables embedded web server and REST API development       |
| Spring Data JPA     | Simplifies database access operations using JPA and ORM    |
| Spring Boot Security   | Provides authentication and authorization capabilities    |
| Spring Boot Validation | Ready-to-use collection of data constraints/checks         |

**Data storage and access:**
| Tool        | Description                                                |
|-------------|------------------------------------------------------------|
| MySQL 8.0.33 | Database management system                                 |
| Hibernate   | Bidirectional mapping tool between Java code and SQL database |
| Liquibase   | Tool for database creation and version control             |

**External functional API:**
| Tool        | Description                                                |
|-------------|------------------------------------------------------------|
| Telegram Bots | HTTP-based interface to build bots for Telegram          |
| Stripe API | Library to integrate Stripe payment functions to API project |


**Additional libs and tools:**
 | Tool      | Description                                               |
|-----------|-----------------------------------------------------------|
| Lombok    | Library for Java code simplification                      |
| MapStruct | Tool for simple data mapping                              |
| JWT       | Authorization standard                                     |
| Swagger   | Tools to create API documentation                         |
| Docker    | Platform for project packaging and deployment             |
| Test Containers    | Library to use lightweighted databases for tests |

## :computer: **How to run the project on Windows**
1. Download [Java](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) and [Maven](https://maven.apache.org/install.html).
2. Open your terminal (cmd) and check Java installation by `java -version` and Maven `mvn -version`
3. Clone repository: Open your terminal (cmd) and use `https://github.com/MishaHMK/Home-Rental-App.git`.
4. Download and install [MySQL](https://dev.mysql.com/downloads/installer/).
5. Open your terminal (cmd) and create MySQL user `mysql -u USER -p`.
6. Create a database `CREATE DATABASE DB_NAME;`
7. Add .env file in root folder of cloned project and write down a configuration:
![image](https://github.com/user-attachments/assets/3c40722e-94c5-47bb-9731-cafbdfd2004a)
9. Run project `mvn clean install` and then `java -jar target/HomeRentalApp-0.0.1-SNAPSHOT.jar`
10. Proceed to [Interactive Swagger Documentation](http://localhost:8080/api/swagger-ui/index.html)

🔸 The project is also testable in deployed [Documentation](SOON) - IN PROGRESS

## :movie_camera: **Video Preview**
https://www.loom.com/share/2aa05782c1404ab8a40fd4744d493328

## 🧪: **Testing results**
More than 80% lines of code are covered with JUnit tests
![image](https://github.com/user-attachments/assets/98cf2fdd-b8f1-405f-8c66-c0659460ea5d)

## :page_facing_up: **Endpoints explanation**

Some endpoints require a [role] for access, use JWT token (Bearer) or Basic authentication.

**AuthController:** Handles registration and login requests.
- POST: `/api/auth/registration` - register new user.
- POST: `/api/auth/login` - login user and receive JWT token.

**UserController:** Handles registration and login requests.
- PUT: `/api/users/update` - update currently logged in user profile data [Admin].
- PATCH: `/users/{userId}/role` - update role of the selected user by his id.
- GET: `/api/users/me` - receive currently logged in user info.

**AccommodationController:** Handles requests for accommodations operations (Authorization is required).
- GET: `/api/accommodations` - Receive all accommodations.
- GET: `/api/accommodations/{id}` - Receive a specific accommodation data by its ID.
- POST: `/api/accommodations` - Create a new accommodation. [Admin]
- PUT: `/api/accommodations/{id}` - Update accommodation data by its ID. [Admin]
- DELETE: `/api/accommodations/{id}` - Soft delete accommodation. [Admin]
🔸 Allowed accomodation type values are: HOUSE, APARTMENT, CONDO, VACATION_HOME

**BookingController:** Handles requests for bookings operations (Authorization is required). 
- GET: `/api/bookings/my` - Receive all bookings of logged in user.
- GET: `/api/bookings/{id}` - Search a specific booking by ID. [Admin/Customer owner]
- GET: `/api/bookings/search` - Fiter booking by statuses or user id with optional pagination. [Admin]
- POST: `/api/bookings` - Create new booking. [Admin]
- PUT: `/api/bookings/{id}` - Update booking data. [Admin]
- PUT: `/api/bookings/update-status/{id}` - Change booking status. [Admin]
- DELETE: `/api/bookings/{id}` - Soft delete booking. [Admin]
🔸 Allowed booking type values are: PENDING, CONFIRMED, CANCELED, EXPIRED

**PaymentController:** Handles requests for order operations.
- GET: `/api/payments` - Receive all user orders.
- GET: `/api/payments/success` - Receive payment confirmation.
- GET: `/api/payments/cancel` - Receive payment cancellation.
- POST: `/api/payments` - Create new payment.
- PATCH: `/api/payments/{id}` - Renew expired payment. [Admin]
🔸 Allowed payment type values are: PENDING, PAID, CANCELED, EXPIRED
