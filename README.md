# CafeManagement
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
2. cafe-employee - TODO
3. cafe-admin - TODO


