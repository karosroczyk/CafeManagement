## ☕ Cafe Management System
This project is a microservices-based Cafe Management System orchestrated with Docker Compose.
OPIS JAK DO CV

## 🛠️ Tech Stack

Backend:
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

- ![Docker](https://img.shields.io/badge/-Docker-2496ED?logo=docker&logoColor=white)
  ![Docker Compose](https://img.shields.io/badge/-Docker%20Compose-2496ED?logo=docker&logoColor=white)

Frontend:
- ![Angular](https://img.shields.io/badge/-Angular-DD0031?logo=angular&logoColor=white)
  ![TypeScript](https://img.shields.io/badge/-TypeScript-3178C6?logo=typescript&logoColor=white)
- ![Bootstrap](https://img.shields.io/badge/-Bootstrap-7952B3?logo=bootstrap&logoColor=white)
  ![CSS3](https://img.shields.io/badge/-CSS3-1572B6?logo=css3&logoColor=white)
- ![HTML5](https://img.shields.io/badge/-HTML5-E34F26?logo=html5&logoColor=white)
- ![NPM](https://img.shields.io/badge/-npm-CB3837?logo=npm&logoColor=white)

## 🧩 Architecture Overview
Each microservice connects to its own MySQL database.  
The system uses **Eureka Discovery Server** to enable dynamic communication between services.

 🧠 Backend Overview

Each management module  follows a **layered architecture**:
- **Controller** → handles incoming requests.
- **Service** → implements business logic.
- **DAOJPA** → extends `JpaRepository` for database operations.

## 🧩 Services Overview
- Provide a REST API with full CRUD, pagination, sorting
- Include input validation, custom exceptions, and transactional integrity.
- Can be accessed directly or through the API Gateway.

---
🍽️ **Menu Management Service**

The Menu Management Service is responsible for managing menu items and categories.

Example endpoints:
- GET /api/menuitems?page=0&size=5 – fetch all menu items
- GET /api/menuitems/filter/category-name?categoryName=Coffee – filter by category

📂 **Folder**: [`MenuManagement`](./backend/MenuManagement)

---
🥫 **Inventory Management Service**

The Inventory Management Service is responsible for tracking stock levels and availability of menu items. 
Items are updated automatically when orders are placed or stock is replenished.

Example endpoints:

- GET /api/inventory/availability?menuItemIds=1,2&quantitiesOfMenuItems=3,1 – check item availability
- PUT /api/inventory/reduce?menuItemIds=1,2&quantitiesOfMenuItems=3,1 – reduce stock when

📂 **Folder**: [`InventoryManagement`](./backend/InventoryManagement)

---
🥫 **Order Management Service**

The Order Management Service handles customer orders, integrates with other microservices, 
and ensures consistent order processing and stock validation.

The **Order Service** uses `WebClient` to communicate with:
- **Menu Service** — to fetch item details and prices.
- **Inventory Service** — to validate and reduce stock availability.

Example endpoints:

- GET /orders/customer/{customerId} – fetch all orders by customer
- POST /orders/placeOrder?menuItemIds=1,2&quantitiesOfMenuItems=3,1 – place new order with stock

📂 **Folder**: [`OrderManagement`](./backend/OrderManagement)

## ⚙ How To Run in dev mode

Run backend:
1. Run DiscoveryServer
2. Run Auth Service
3. Run Menu Service
3. Run Inventory Service
4. Run Order Service

Run frontend:
1. Run ng serve
2. Go to http://localhost:4200/login

## 🔐 Eureka Discovery Server

The Discovery Server is a Spring Boot application using Eureka, acting as a service registry
for all microservices. It allows services to register themselves and discover other services dynamically.
API Gateway runs on port 8088. The Menu, Inventory, and Order Management services are also connected 
through this port, requiring Bearer Token authentication when accessed via the gateway. 
These services can still be accessed directly on their individual ports without authentication.


How is this Discovery Server (Eureka Server) working:
2. do we need config directory in Order?
3. It works because of the annotations @EnableEurekaServer?
   EurekaClient is used in OrderService to communicate with Menu and Inventory services

## 🔐 Security and Roles
It provides secure user registration, login, email-based account activation, 
and JWT-based request authorization across all microservices.

Role	Accessible Modules / Actions
CLIENT	HOME, CAFE-MENU (read only), CREATE ORDER, MY ORDERS, MY PROFILE (no role change)
EMPLOYEE	HOME, CAFE-MENU (CRUD), ALL ORDERS, MY PROFILE (no role change)
ADMIN	HOME, ALL PROFILES (can change roles)

MODULES:
1. cafe-client
   - main(home), menu-cafe, order, order-history, profile
2. cafe-employee - home, profile TODO: menu, orders
3. cafe-admin - home, profile-list, profile

role based security in backend:
.requestMatchers("/api/menuitems/**").hasAnyRole("CLIENT", "EMPLOYEE")
.requestMatchers("/api/categories/**").hasAnyRole("CLIENT", "EMPLOYEE")
.requestMatchers("/api/orders/**").hasAnyRole("CLIENT", "EMPLOYEE")
.requestMatchers("/api/inventory/**").hasAnyRole("CLIENT", "EMPLOYEE")
.anyRequest()
.authenticated()

role based security in frontend:
-based on jwt token decoding

## 🧩 Tests
UT, Integration, E2E
Integration tests:
- Order -MockMVC - not real service running
- Inventory, Menu - real service running

## 🧩 Docker
This project uses Docker Compose to orchestrate multiple microservices for the Cafe Management System.
Each service runs in its own container with an attached MySQL database and connects through a shared network.
The setup also includes service discovery (Eureka), a frontend UI, and a test mail server (MailDev).

🧩 Microservices Overview

Service	Description	Port (Host → Container)
menumanagement	Manages menu items and categories	8081:8081
db_menu	MySQL database for Menu service	3307:3306
inventorymanagement	Handles inventory stock and updates	8082:8082
db_inventory	MySQL database for Inventory service	3308:3306
ordermanagement	Manages customer orders	8083:8083
db_order	MySQL database for Order service	3309:3306
usermanagement	Handles user registration, login, and authentication	8088:8088
db_user	MySQL database for User service	3310:3306
eureka (discoveryserver)	Service registry for dynamic discovery	8762:8761
maildev	Fake SMTP server for email testing (UI + SMTP)	1080:1080 (UI), 1025:1025 (SMTP)
frontend	React/Angular/Vue frontend (depending on your build)	8080:80

🧩 How It Works
Each microservice (Menu, Inventory, Order, User) connects to its own dedicated MySQL container.
depends_on and health checks ensure that a service starts only after its database is ready.
Eureka enables service discovery between microservices.
The frontend communicates with backend services (through the API Gateway or directly) via Docker’s internal network.🚀 Running the System

1️⃣ Build the project
1. Make sure your project is built (so Docker can use the JARs). In each service directory:
   ./mvnw clean package -DskipTests
2. Create an image for each service:
   docker build -t <your_image_name>:v.1.0 .
3. Start Docker Compose from the project root (where docker-compose.yml is located):
   docker compose up --build

Example of accessing DB from console:
- winpty docker exec -it <container_name> mysql -u usermanagement -p
- SHOW DATABASES;
- USE usermanagement;
- SHOW TABLES;