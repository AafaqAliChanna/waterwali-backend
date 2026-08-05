# WaterWali Backend — Phase 1 + Phase 2

Java + Spring Boot backend. Implements: user registration/login/JWT (Phase 1), order placement with server-side pricing and PostGIS location (Phase 2).

## Requirements
- Java 17+
- Maven
- PostgreSQL running locally with a database named `waterwali`

## Setup
1. Create the database and enable PostGIS (one-time):
   ```sql
   CREATE DATABASE waterwali;
   \c waterwali
   CREATE EXTENSION IF NOT EXISTS postgis;
   ```
2. Set environment variables (never commit real secrets — see security note below):
   ```bash
   export DB_USERNAME=postgres
   export DB_PASSWORD=your_local_password
   export JWT_SECRET=$(openssl rand -base64 32)
   ```
3. Run the app:
   ```bash
   mvn spring-boot:run
   ```
   Server starts at `http://localhost:8080`.

## ⚠️ Security note (important)
An earlier commit had a real database password and JWT secret hardcoded in
`application.properties`, pushed to this public repo. Both are now read from
environment variables instead. You should still:
1. Change your actual Postgres password to a new one.
2. Never reuse `waterwali_super_secret_jwt_key_1234567890` as a JWT secret again.
3. Consider using `git filter-repo` or GitHub's secret-scanning guidance to scrub the
   old values from your Git history, since old commits still contain them.

## Endpoints — Phase 1 (public)
| Method | URL | Body | Notes |
|---|---|---|---|
| POST | `/api/auth/register` | `{ "name", "phone", "password", "role" }` | role = CUSTOMER or DRIVER |
| POST | `/api/auth/login` | `{ "phone", "password" }` | Returns a JWT token |

## Endpoints — require `Authorization: Bearer <token>`
| Method | URL | Body | Notes |
|---|---|---|---|
| GET | `/api/users/me` | — | Current logged-in user's profile |
| POST | `/api/orders` | `{ "latitude", "longitude", "tankerSize" }` | tankerSize = SIZE_1000L/2000L/3000L/5000L. Price is server-calculated. |
| GET | `/api/orders/mine` | — | Logged-in customer's own orders |
| GET | `/api/orders/{id}` | — | One order (only if it belongs to you) |

Test all endpoints with Postman before connecting Flutter.

## Folder Structure
See the WaterWali Master Handbook §6 for what each folder means.
