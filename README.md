# Cartly

Cartly is a full-stack e-commerce application built with a React frontend and Spring Boot microservices. It supports authentication, products and categories, carts and wishlists, checkout, payments, inventory, orders, and notifications.

## Architecture

```text
React + Vite (http://localhost:5173)
              |
              v
API Gateway (http://localhost:8080)
  ├── Auth service          :8081
  ├── User service          :8082
  ├── Product service       :8083
  ├── Category service      :8084
  ├── Cart service          :8085
  ├── Order service         :8086
  ├── Payment service       :8087
  ├── Inventory service     :8088
  └── Notification service  :8089
```

The frontend proxies `/api` requests to the API gateway during local development.

## Prerequisites

- Java 17 or later
- Maven 3.9 or later
- MySQL 8 or later
- Node.js 18 or later

Each backend service uses MySQL. By default, the services connect as `root` with password `root` and create their databases if needed. Override these values with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` before starting a service. Set `JWT_SECRET` to a strong, private value outside local development.

## Run locally

1. Start MySQL and make sure the configured database user can create databases.
2. In `backend`, build and run the services. Start the dependent services first, then `order-service`, then `api-gateway`.

```powershell
cd backend
mvn clean package

# Run each service in a separate terminal, for example:
mvn -pl auth-service spring-boot:run
mvn -pl user-service spring-boot:run
mvn -pl product-service spring-boot:run
mvn -pl category-service spring-boot:run
mvn -pl cart-service spring-boot:run
mvn -pl inventory-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl api-gateway spring-boot:run
```

3. Start the frontend in another terminal.

```powershell
cd frontend
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173). The gateway health endpoint is available at [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

## Main API routes

All API requests are sent through the gateway at `http://localhost:8080`.

| Area | Route prefix |
| --- | --- |
| Authentication | `/api/auth` |
| Users and addresses | `/api/users` |
| Products | `/api/products` |
| Categories | `/api/categories` |
| Cart and wishlist | `/api/cart` |
| Orders | `/api/orders` |
| Payments | `/api/payments` |
| Inventory | `/api/inventory` |
| Notifications | `/api/notifications` |

Authenticated requests use a bearer token. The frontend automatically attaches it from local storage after sign-in.

## Verification

```powershell
# Backend tests
cd backend
mvn test

# Frontend production build
cd frontend
npm run build
```

## Project layout

```text
backend/   Spring Boot parent project and ten microservices
frontend/  React + Vite + Tailwind application
```
