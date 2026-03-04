# BiteSpeed Backend Task: Identity Reconciliation

A Spring Boot REST service that identifies and reconciles customer identities across multiple purchases using different contact information.

## 🚀 Live Endpoint

**Base URL:** `https://bitespeed-identity-reconciliation-k58n.onrender.com`

**API Endpoint:** `https://bitespeed-identity-reconciliation-k58n.onrender.com/identify`

**Swagger UI:** `https://bitespeed-identity-reconciliation-k58n.onrender.com/swagger-ui/index.html`

---

## 📸 Screenshots

### Swagger UI
<img width="1899" height="994" alt="swagger" src="https://github.com/user-attachments/assets/68def57a-00a1-42af-82aa-32f60e807d86" />
<img width="1901" height="990" alt="swagger2" src="https://github.com/user-attachments/assets/d88c47ca-f33b-43d0-9446-4a37b77a5302" />


### Deployed on Render
<img width="1903" height="973" alt="deployed" src="https://github.com/user-attachments/assets/19f26279-5744-47a7-b680-c8ef422321a2" />


### API Response
<img width="1897" height="1052" alt="test-respose" src="https://github.com/user-attachments/assets/1220097a-1918-4c28-8881-045b5da3334e" />


---

## 📋 Problem Statement

FluxKart.com customers sometimes use different emails and phone numbers for each purchase. This service links all those contacts to a single customer identity using a primary/secondary contact model.

---

## 🛠️ Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.5
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Containerization:** Docker & Docker Compose
- **Documentation:** Swagger UI (SpringDoc OpenAPI)
- **Deployment:** Render.com

---

## 📡 API Reference

### POST /identify

Identifies and consolidates customer contact information.

**Request Body:**
```json
{
  "email": "string (optional)",
  "phoneNumber": "string (optional)"
}
```

**Response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["primary@email.com", "secondary@email.com"],
    "phoneNumbers": ["9876543210", "8765432109"],
    "secondaryContactIds": [2, 3]
  }
}
```

---

## 🧪 Test Scenarios

### Scenario 1: New Contact (creates primary)
```bash
curl -X POST https://bitespeed-identity-reconciliation-k58n.onrender.com/identify \
  -H "Content-Type: application/json" \
  -d '{"email":"rahul.sharma@gmail.com","phoneNumber":"9876543210"}'
```

**Response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["rahul.sharma@gmail.com"],
    "phoneNumbers": ["9876543210"],
    "secondaryContactIds": []
  }
}
```

### Scenario 2: Existing phone, new email (creates secondary)
```bash
curl -X POST https://bitespeed-identity-reconciliation-k58n.onrender.com/identify \
  -H "Content-Type: application/json" \
  -d '{"email":"rahul.s@outlook.com","phoneNumber":"9876543210"}'
```

**Response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["rahul.sharma@gmail.com", "rahul.s@outlook.com"],
    "phoneNumbers": ["9876543210"],
    "secondaryContactIds": [2]
  }
}
```

### Scenario 3: Two primaries merging (older stays primary)
```bash
curl -X POST https://bitespeed-identity-reconciliation-k58n.onrender.com/identify \
  -H "Content-Type: application/json" \
  -d '{"email":"priya.mehta@gmail.com","phoneNumber":"8765432109"}'
```

**Response:**
```json
{
  "contact": {
    "primaryContatctId": 3,
    "emails": ["priya.mehta@gmail.com", "priya.m@outlook.com"],
    "phoneNumbers": ["7654321098", "8765432109"],
    "secondaryContactIds": [4]
  }
}
```

---

## 🏃 Run Locally

### Prerequisites
- Docker & Docker Compose
- Java 21

### Steps
```bash
# Clone the repository
git clone https://github.com/SamadhanGh/bitespeed-identity-reconciliation-Assignment.git
cd bitespeed-identity-reconciliation-Assignment

# Start the app with Docker
docker compose up --build
```

App will be running at `http://localhost:8080`

Swagger UI at `http://localhost:8080/swagger-ui/index.html`

---

## 🗄️ Database Schema
```sql
CREATE TABLE contact (
    id SERIAL PRIMARY KEY,
    phone_number VARCHAR(255),
    email VARCHAR(255),
    linked_id INTEGER,
    link_precedence VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
```

---

## 🔄 Identity Reconciliation Logic

1. **No match** → Create new primary contact
2. **Match found, no new info** → Return consolidated response
3. **Match found, new info** → Create secondary contact linked to primary
4. **Two separate primaries linked** → Older one stays primary, newer gets demoted to secondary along with all its secondaries

---

## 👨‍💻 Author

**Samadhan Ghorpade**
- GitHub: [@SamadhanGh](https://github.com/SamadhanGh)
- Email: sdghorpade2003@gmail.com
- LinkedIn: [LinkedIn Profile](https://www.linkedin.com/in/samadhan-gh/)
