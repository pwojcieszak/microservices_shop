# microservices_shop
Project used to familarize myself with microservices. It consists of 3 main services which are: product-service, inventory-service, order-service. 
Application's main functionality is an option to add products to inventory, query them and make an order, which first checks whether the product is in stock.

# How to run the application using Docker
1. Run `mvn clean package -DskipTests` to build the applications and create the docker image locally.
2. Run `docker-compose up -d` to start the applications.

# Getting Client's Secret
First go to `localhost:8080` which is keycloak service, go to administration console, log-in using "admin : admin", then choose 
realm `microservices-realm` -> `clients` -> `spring-cloud-client` -> `credentials`. Here, copy sercret. 
<br><br>
Having client's secret you can use API. Using for example Postman use OAuth 2.0 and use:
1. Access token URL: http://keycloak:8080/realms/spring-boot-microservices-realm/protocol/openid-connect/token
2. Client ID: spring-cloud-client
3. Client Secret: {your secret}
4. Scope: openid offline_access

# Using Endpoints
Add new product on `http://localhost:8181/api/product` using POST.
Get list of products on `http://localhost:8181/api/product` using GET.
Attempt to place an order on `http://localhost:8181/api/order` using POST.

# Using Grafana
Grafana is available on `http://localhost:3000`.
