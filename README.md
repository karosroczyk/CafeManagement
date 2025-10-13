☕ Cafe Management System — Docker Setup
This project is a microservices-based Cafe Management System built with Spring Boot, Spring Cloud Gateway, Eureka Discovery, and MySQL, all orchestrated via Docker Compose.

http://localhost:8761/ - Discovery Server
http://localhost:8088/ - Backend DB/

Run backend:
1. Run DiscoveryServer
2. Run BookNetworkApi
3. Run MenuApp
3. Run InventoryApp
4. Run OrderApp

Run frontend:
1. Navigate to frontend page
2. ng serve
3. Go to http://localhost:4200/login

BookNetwork is working on 8088 port. Menu, Inventory and Order Management are also connected to that port and accessing them via this port require authentication (Bearer Token). They can also be accessed without authentication via its individual ports. Accordingly:

1. MenuManagement: http://localhost:8081/
2. InventoryManagement: http://localhost:8082/
3. OrderManagement: http://localhost:8083/

NOTE: Individual ports may be used for testing purposes so as not to enter token everytime, but for production purposes we should use 8088

NOTE: What to do if sometimes can't login/register because JWT expired:
- Open in incognito page

Backend in SpringBoot:
Each Management module has Controller which contains Server.
Each Server implements ServerInterface. Contains DAOJPA as connection to DB. Contains EurekaClient.
Each DAOJPA extends JpaRepository.
OrderManagement Service contain WebClient.Builder used for comunicating with other modules e.g. Menu, Inventory.
Modular approach.

ROLES:
1. CLIENT can access:
   - HOME, CAFE-MENU (read only), CREATE AN ORDER, MY ORDERS, MY PROFILE (BEZ OPCJI ZMIANY ROLE)
2. EMPLOYEE can access:
   - HOME, CAFE-MENU (crud), ALL ORDERS, MY PROFILE (BEZ OPCJI ZMIANY ROLE)
3. ADMIN can access:
   - HOME, (ALL PROFILES z opcja zmiany ROLE)

NOTE: cafe-client/components/menu mozna przeniesc do app/components
NOTE: Change name from OrderDialogComponent to DialogComponent
MODULES:
1. cafe-client
   - main(home), menu-cafe, order, order-history, profile
2. cafe-employee - DONE: home, profile TODO: menu, orders
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

We probabkly want dynamic approach with EurekaClient as it is in Order, need to update Inventory and 

How is this Discovery Server (Eureka Server) working:
1. Do we need private EurekaClient discoveryClient; in Menu and Inventory?
2. do we need config directory in Order
3. It works because of the annotations @EnableEurekaServer

Integration tests:
- Order -MockMVC - not real service running
- Inventory, Menu - real service running

DOCKER:
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