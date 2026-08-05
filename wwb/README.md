# WaterWali Backend — Phase 1 + Phase 2 (confirmed JDK 17)

## Requirements
- JDK 17 (a full JDK, not just a JRE — run `javac -version` to confirm you have it)
- Maven
- PostgreSQL with PostGIS extension

## Setup
```sql
CREATE DATABASE waterwali;
\c waterwali
CREATE EXTENSION IF NOT EXISTS postgis;
```

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_local_password
export JWT_SECRET=$(openssl rand -base64 32)
mvn clean install -U
mvn spring-boot:run
```

## Endpoints
| Method | URL | Auth | Body |
|---|---|---|---|
| POST | /api/auth/register | none | name, phone, password, role |
| POST | /api/auth/login | none | phone, password |
| GET | /api/users/me | Bearer token | — |
| POST | /api/orders | Bearer token | latitude, longitude, tankerSize |
| GET | /api/orders/mine | Bearer token | — |
| GET | /api/orders/{id} | Bearer token | — |
