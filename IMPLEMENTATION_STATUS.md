# Implementation status

This checklist tracks the requirements in the original Cartly implementation plan. Checked items are present in the repository; remaining items are deliberately visible for future work.

## Platform and infrastructure

- [x] Maven multi-module backend with ten Spring Boot services
- [x] React, Vite, Tailwind, React Router, Axios, and Lucide frontend
- [x] API gateway routing for all downstream services
- [x] MySQL configuration via environment variables
- [x] Seeded admin/customer accounts, categories, products, and inventory
- [x] Docker Compose stack with MySQL, all services, gateway, and frontend
- [x] Multi-stage backend and frontend Docker builds
- [x] `.env.example` for Docker configuration
- [ ] Fresh Docker Compose build and end-to-end smoke test

## Backend services

- [x] Authentication, JWT validation, registration, login, and current-user endpoints
- [x] User profiles and address CRUD
- [ ] User password-update endpoint
- [x] Product catalog, filtering, pagination, featured/new/best-seller routes, CRUD, and statistics
- [x] Category CRUD
- [x] Cart totals, cart item CRUD, wishlist, and move-to-cart support
- [x] Order placement, history, cancellation, status updates, and statistics
- [x] Payment processing and refunds for card, UPI, and cash on delivery
- [x] Inventory reserve, deduct, release, updates, and low-stock queries
- [x] Notifications and read status
- [ ] Enforced admin authorization for all administrative APIs
- [ ] Validated order-status state transitions
- [ ] Transactional compensation for failed checkout steps

## Frontend

- [x] Storefront home, product catalog, product details, login, registration, cart, checkout, confirmation, and order history
- [x] JWT attachment and auth/cart/wishlist/toast contexts
- [x] Checkout aligned with current cart, address, and order-service API contracts
- [ ] Wishlist page
- [ ] Profile and address-management page
- [ ] Order-details tracking page
- [ ] Admin dashboard and management pages
- [ ] Reusable component/hook structure described in the plan

## Verification and documentation

- [x] Backend test classes for every service (latest stored reports: 26 passing tests)
- [x] Frontend production build passes
- [x] README with architecture, native setup, routes, and verification commands
- [ ] Fresh backend Maven test run in the current environment (Maven executable is unavailable)
- [ ] Complete API cURL examples and ER/schema documentation
