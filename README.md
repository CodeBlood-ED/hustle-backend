# Hustle Web - Monolithic Backend

A production-ready Spring Boot monolith backend built for the **Hustle Web** e-commerce ecosystem (customer store and admin portal).

---

## 🏗️ Architecture Overview

The backend follows a **layered & modular monolithic architecture**, providing clear separation of concerns, high maintainability, and domain encapsulation:

```text
hustle-backend/
├── pom.xml                                   # Maven dependencies & build setup
├── mvnw / mvnw.cmd                           # Portable Maven wrapper scripts
├── uploads/products/                         # File storage directory for product images
└── src/
    ├── main/
    │   ├── java/com/hustle/backend/
    │   │   ├── HustleBackendApplication.java # Spring Boot entry point
    │   │   ├── common/                       # Shared responses, errors, and exception handlers
    │   │   │   ├── ApiResponse.java          # Standard API envelope
    │   │   │   ├── ErrorResponse.java        # Structured error model
    │   │   │   └── exception/                # GlobalExceptionHandler & custom exceptions
    │   │   ├── config/                       # Web, Security, CORS, OpenAPI, and Seeders
    │   │   │   ├── WebConfig.java            # CORS mappings & static upload resource handler
    │   │   │   ├── SecurityConfig.java       # Spring Security 6.x filter chain & rules
    │   │   │   ├── OpenApiConfig.java        # Swagger 3.0 documentation configuration
    │   │   │   └── DataInitializer.java      # Automatic seeding of users & catalog products
    │   │   ├── security/                     # JWT authentication layer
    │   │   │   ├── JwtTokenProvider.java     # JJWT 0.12.x token generator & validator
    │   │   │   ├── JwtAuthenticationFilter.java
    │   │   │   ├── JwtAuthenticationEntryPoint.java
    │   │   │   └── CustomUserDetailsService.java
    │   │   └── modules/                      # Business domains
    │   │       ├── auth/                     # User signup, login, roles (USER, ADMIN)
    │   │       ├── product/                  # Catalog, multipart uploads, legacy admin APIs
    │   │       ├── cart/                     # Cart operations, quantity updates, calculations
    │   │       └── order/                    # Order placement, checkout, tracking, admin status
    │   └── resources/
    │       ├── application.properties        # Default configuration (H2, JWT, CORS)
    │       └── application-mysql.properties  # MySQL configuration profile
    └── test/                                 # Unit & Integration tests
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17 LTS** or newer
- No separate Maven installation required (the project includes `mvnw` / `mvnw.cmd`).

### Run the Application

#### Default (In-Memory H2 Database)
```powershell
.\mvnw.cmd spring-boot:run
```
*(On Linux/macOS: `./mvnw spring-boot:run`)*

The server will start on **`http://localhost:8080`**.

#### Optional: Run with MySQL
1. Ensure MySQL is running on port `3306` with database `hustle_db` (or create database `CREATE DATABASE hustle_db;`).
2. Run with the MySQL profile:
```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 🔑 Pre-Seeded Accounts

When the application boots for the first time, `DataInitializer` automatically provisions default accounts and catalog items matching the Hustle Web storefront:

| Role | Email | Password |
|---|---|---|
| **Admin** | `admin@hustle.com` | `admin123` |
| **Customer** | `user@hustle.com` | `password123` |

---

## 📖 API Documentation & Tools

- **Swagger UI / Interactive API Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **H2 Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **JDBC URL**: `jdbc:h2:mem:hustledb`
  - **User**: `sa`
  - **Password**: *(leave empty)*

---

## 📡 API Endpoints Reference

### 1. Hustle Admin Compatible Endpoints
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/upload_product` | Multipart form upload matching Hustle Admin (`product_title`, `product_category`, `product_mrp`, `product_netPrice`, `product_image`) |
| `GET` | `/api/v1/get_products` | Retrieves all products formatted for Hustle Admin (`product_id`, `product_title`, `product_category`, `product_mrp`, `product_net_price`, `product_image`) |

### 2. Authentication (`/api/v1/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/signup` | Public | Register customer (`name`, `email`, `contact`, `password`) |
| `POST` | `/api/v1/auth/login` | Public | Authenticate user & retrieve JWT token |
| `GET` | `/api/v1/auth/me` | Bearer Token | Get current authenticated user profile |

### 3. Products & Catalog (`/api/v1/products`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/products` | Public | List products (supports query params: `?category=iphone-15&search=case&tag=Popular`) |
| `GET` | `/api/v1/products/{id}` | Public | Get product details |
| `POST` | `/api/v1/products` | Public / Admin | Create product via JSON |
| `PUT` | `/api/v1/products/{id}` | Public / Admin | Update product details |
| `DELETE` | `/api/v1/products/{id}` | Public / Admin | Soft delete product |
| `GET` | `/api/v1/products/categories` | Public | Category summaries with item counts |
| `GET` | `/api/v1/products/images/{filename}` | Public | Stream / download uploaded product image |

### 4. Shopping Cart (`/api/v1/cart`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/v1/cart` | Public / Auth | Get cart items & calculated total (supports `X-Session-Id` header for guests) |
| `POST` | `/api/v1/cart/items` | Public / Auth | Add product to cart |
| `PUT` | `/api/v1/cart/items/{itemId}` | Public / Auth | Update quantity of a cart item |
| `DELETE` | `/api/v1/cart/items/{itemId}` | Public / Auth | Remove cart item |
| `DELETE` | `/api/v1/cart/clear` | Public / Auth | Clear all items from cart |

### 5. Orders & Checkout (`/api/v1/orders`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/orders` | Public / Auth | Place order (direct or from cart with shipping calculation and 18% tax) |
| `GET` | `/api/v1/orders/my-orders` | Bearer Token | User's order history |
| `GET` | `/api/v1/orders/{id}` | User / Admin | View order by ID |
| `GET` | `/api/v1/orders/track/{orderNumber}` | Public | Track order by order number (e.g. `HST-20261005-1234`) |
| `GET` | `/api/v1/orders` | Admin | List all orders |
| `PATCH`| `/api/v1/orders/{id}/status` | Admin | Update status (`CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`) |

---

## 🛡️ Security & CORS
- **CORS Allowed Origins**: `http://localhost:4200` (Hustle Web), `http://localhost:4201` (Hustle Admin), `http://localhost:3000`.
- **JWT**: Stateless HMAC-SHA256 tokens valid for 24 hours.
- Pass token as: `Authorization: Bearer <token>`.
