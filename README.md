# Cafe Management System ☕
This project is a complete microservices-based system to manage a café’s daily operations. 
Users can browse the menu, place orders, track orders, and manage private account, while 
staff can manage menu items, track and manage inventory stock, and process orders.

## Tech Stack 🛠️

### Backend:
- ![Java](https://img.shields.io/badge/-Java%2021-007396?logo=openjdk&logoColor=white)
  ![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-6DB33F?logo=springboot&logoColor=white)
  ![Spring Cloud](https://img.shields.io/badge/-Spring%20Cloud-6DB33F?logo=spring&logoColor=white)
  ![Spring Security](https://img.shields.io/badge/-Spring%20Security%20(JWT)-6DB33F?logo=springsecurity&logoColor=white)
 
- ![Spring Data JPA](https://img.shields.io/badge/-Spring%20Data%20JPA-6DB33F?logo=hibernate&logoColor=white)
  ![Hibernate](https://img.shields.io/badge/-Hibernate-59666C?logo=hibernate&logoColor=white)
  ![MySQL](https://img.shields.io/badge/-MySQL-4479A1?logo=mysql&logoColor=white)
  
- ![Eureka Discovery](https://img.shields.io/badge/-Eureka%20Discovery-6DB33F?logo=spring&logoColor=white)
  ![API Gateway](https://img.shields.io/badge/-API%20Gateway-000000?logo=apachesuperset&logoColor=white)
  ![WebClient](https://img.shields.io/badge/-WebClient-2496ED?logo=spring&logoColor=white)

- ![Maven](https://img.shields.io/badge/-Maven-C71A36?logo=apachemaven&logoColor=white)

- ![JUnit 5](https://img.shields.io/badge/-JUnit5-25A162?logo=JUnit5&logoColor=white)
  ![Mockito](https://img.shields.io/badge/-Mockito-4D32A8?logo=Mockito&logoColor=white).
  ![Selenium](https://img.shields.io/badge/-Selenium-43B02A?logo=selenium&logoColor=white)

- ![Docker](https://img.shields.io/badge/-Docker-2496ED?logo=docker&logoColor=white)
  ![Docker Compose](https://img.shields.io/badge/-Docker%20Compose-2496ED?logo=docker&logoColor=white)

### Frontend:
- ![Angular](https://img.shields.io/badge/-Angular-DD0031?logo=angular&logoColor=white)
  ![TypeScript](https://img.shields.io/badge/-TypeScript-3178C6?logo=typescript&logoColor=white)
- ![Bootstrap](https://img.shields.io/badge/-Bootstrap-7952B3?logo=bootstrap&logoColor=white)
  ![CSS3](https://img.shields.io/badge/-CSS3-1572B6?logo=css3&logoColor=white)
- ![HTML5](https://img.shields.io/badge/-HTML5-E34F26?logo=html5&logoColor=white)
- ![NPM](https://img.shields.io/badge/-npm-CB3837?logo=npm&logoColor=white)

## Architecture Overview 🧩
The application uses a microservices architecture, where each service is independent and communicates via REST APIs.

Internally, each service follows a layered architecture:
- **Controller** – handles HTTP requests and responses.
- **Service** – contains business logic and orchestrates calls to other services.
- **DAO / Repository** – manages database access using JPA/Hibernate.

Services register with Eureka Discovery Server for dynamic discovery, and the API Gateway centralizes routing and authentication.

### Services Overview 🌐
- Provide a REST API with full CRUD, pagination, sorting
- Include input validation, custom exceptions, and transactional integrity.
- Can be accessed directly or through the API Gateway.

---
#### 1. Menu Management Service 📋

The Menu Management Service is responsible for managing menu items and categories.

Example endpoints:
- GET /api/menuitems?page=0&size=5 – fetch all menu items
- GET /api/menuitems/filter/category-name?categoryName=Coffee – filter by category

📂 **Folder**: [`MenuManagement`](./backend/MenuManagement)

---
#### 2. Inventory Management Service 📦

The Inventory Management Service is responsible for tracking stock levels and availability of menu items. 
Items are updated automatically when orders are placed or stock is replenished.

Example endpoints:
- GET /api/inventory/availability?menuItemIds=1,2&quantitiesOfMenuItems=3,1 – check item availability
- PUT /api/inventory/reduce?menuItemIds=1,2&quantitiesOfMenuItems=3,1 – reduce stock when

📂 **Folder**: [`InventoryManagement`](./backend/InventoryManagement)

---
#### 3. Order Management Service 🛒

The Order Management Service handles customer orders, integrates with other microservices, 
and ensures consistent order processing and stock validation.

The **Order Service** uses `WebClient` to communicate with:
- **Menu Service** — to fetch item details and prices.
- **Inventory Service** — to validate and reduce stock availability.

Example endpoints:
- GET /orders/customer/{customerId} – fetch all orders by customer
- POST /orders/placeOrder?menuItemIds=1,2&quantitiesOfMenuItems=3,1 – place new order with stock

📂 **Folder**: [`OrderManagement`](./backend/OrderManagement)

---
#### 4. Auth Management Service 👤

The Auth Service handles user management, authentication, and role-based access control.
It ensures secure registration, login with email activation, and proper role enforcement for all users.

The User Service uses Spring Security and JWT to provide:
- Authentication — validate credentials and issue JWT tokens
- Authorization — restrict access based on user roles (e.g., admin, client)
- Email Service — send account activation emails with secure tokens

Example endpoints:
- GET /api/user/{id} – fetch user by ID
- POST /api/user/email – fetch user details by email

📂 **Folder**: [`AuthManagement`](./backend/AuthManagement)

---
### Eureka Discovery Server 🧭

The Discovery Server is a Spring Boot application using Eureka, acting as a service registry
for all microservices. It allows services to register themselves and discover other services dynamically.
API Gateway runs on port **8088**. The Menu, Inventory, and Order Management services are also connected
through this port, requiring Bearer Token authentication when accessed via the gateway.
These services can still be accessed directly on their individual ports without authentication.

---
### Security and Roles 🛡️
The application provides secure user registration, login, and email-based account activation.
Authentication and authorization are implemented using JWT, ensuring secure and stateless communication between the client and the server.

When a user logs in, the server validates their credentials and generates a JWT token containing essential user information (such as email and role).
This token is then sent to the client and must be included in the Authorization header as a Bearer Token for any subsequent requests to protected endpoints.

The server verifies the validity of each token on every request, ensuring that only authenticated users can access specific resources.
Additionally, role-based access control is enforced — meaning that users can access different pages and functionalities depending on their assigned roles.

## Tests 🧪
The application includes comprehensive testing across multiple layers:
- **Unit Tests** – for controllers and services using JUnit 5 and Mockito.
- **Integration Tests** – verifying interactions between services and database.
- **End-to-End (E2E) Tests** – using Selenium to simulate real user interactions in the frontend.
- **API Testing** – performed with Postman to ensure all endpoints behave correctly.

## Docker 🐳
This project uses Docker Compose to orchestrate multiple microservices for the Cafe Management System.
Each service runs in its own container with an attached MySQL database and connects through a shared network.
The setup also includes service discovery (Eureka), a frontend UI, and a mail server (MailDev).

### Microservices Overview

| Service                  | Description                                  | Port (Host → Container)             |
|--------------------------|----------------------------------------------|------------------------------------|
| menumanagement           | Manages menu items and categories           | 8081:8081                          |
| db_menu                  | MySQL database for Menu service              | 3307:3306                          |
| inventorymanagement      | Handles inventory stock and updates         | 8082:8082                          |
| db_inventory             | MySQL database for Inventory service        | 3308:3306                          |
| ordermanagement          | Manages customer orders                      | 8083:8083                          |
| db_order                 | MySQL database for Order service             | 3309:3306                          |
| usermanagement           | Handles user registration, login, and authentication | 8088:8088                        |
| db_user                  | MySQL database for User service              | 3310:3306                          |
| eureka (discoveryserver) | Service registry for dynamic discovery      | 8762:8761                          |
| maildev                  | Fake SMTP server for email testing (UI + SMTP) | 1080:1080 (UI), 1025:1025 (SMTP)  |
| frontend                 |Angular frontend (depending on build)   | 8080:80                             |

