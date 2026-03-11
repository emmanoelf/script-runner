# Script Execution Platform

A backend service built with **Spring Boot** designed to execute Docker-based scripts and stream logs to a web interface in **real time**.

The system allows authenticated users to submit a **target** and an **instruction**, which will trigger a Docker container execution. The output is streamed live to the frontend via **WebSocket** and stored as log files on the server.

---

# Tech Stack

| Layer              | Technology                         |
|--------------------|------------------------------------|
| Framework          | Spring Boot 3.5                    |
| Language           | Java 21                            |
| Persistence        | Spring Data JPA + Hibernate        |
| Database           | PostgreSQL                         |
| Authentication     | JWT (JSON Web Token)               |
| Password Hashing   | Argon2                             |
| Realtime Streaming | Spring WebSocket + STOMP           |
| Script Execution   | Docker CLI                         |
| Containerization   | Docker                             |
| API Format         | REST + WebSocket                   |
| Testing (planned)  | JUnit 5 + Mockito + Spring MockMvc |

---

# Project Architecture

The application will follow a layered architecture:

```
Controller -> Service (business logic) ->  Repository (database access) -> Database (PostgreSQL)
```

Additional components:

```
WebSocket Layer
Docker Execution Runner
Log File Storage
JWT Security Filter
```

---

# Roadmap

## Database Layer

### Entities

* [x] User entity
* [x] Execution entity
* [x] ExecutionStatus enum

---

### Database

* [ ] Configure PostgreSQL connection

---
# Security

Authentication will use **JWT + Argon2**.

### Security Features

* [ ] JWT token generation
* [ ] JWT authentication filter
* [ ] Secure endpoints
* [ ] User authentication context
* [ ] Password hashing with Argon2
* [ ] Secure WebSocket authentication

Security flow:

```
Client → Login
       → JWT issued
       → JWT sent in Authorization header
       → SecurityFilter validates token
```

---

# Repository Layer

Repositories will use **Spring Data JPA**.

* [ ] Create `UserRepository`
* [ ] Create `ExecutionRepository`
* [ ] Implement custom queries if needed
* [ ] Add query to list executions by user
* [ ] Add query to fetch execution metadata

---

# Service Layer

Services will contain all **business logic**.

### User Service

* [ ] User registration
* [ ] Password validation

### Execution Service

Responsible for managing script executions.

* [ ] Create execution record
* [ ] Generate unique log file
* [ ] Start Docker container
* [ ] Capture stdout/stderr
* [ ] Stream logs via WebSocket
* [ ] Write logs to file
* [ ] Update execution status
* [ ] Handle execution failures

---

# Controller Layer

Controllers will expose REST endpoints.

### Authentication Controller

* [ ] Login endpoint
* [ ] JWT token generation
* [ ] JWT token response

### Execution Controller

* [ ] Submit execution request
* [ ] Fetch execution logs
* [ ] List user executions
---


# WebSocket (Real-Time Logs)

WebSocket will be used to stream execution logs to the frontend.

### WebSocket Features

* [ ] WebSocket configuration
* [ ] STOMP messaging
* [ ] Topic-based messaging
* [ ] Execution-specific channels

Example channel structure:

```
/topic/execution/{executionId}
```

Flow:

```
Docker stdout → Spring Boot → Write to log file → Publish to WebSocket topic → Frontend receives live logs
```

---

# Docker Script Execution

Scripts will run inside Docker containers.

### Features

* [ ] Docker execution service
* [ ] ProcessBuilder integration
* [ ] Capture stdout and stderr
* [ ] Error handling
* [ ] Execution timeout protection


---

# Log Storage

Logs will be stored as **files**, not in the database.

### Log Strategy

* [ ] Generate unique log file per execution
* [ ] Use UUID-based file names
* [ ] Store logs in `/logs` directory
* [ ] Provide log download endpoint

Example structure:

```
logs/
 ├── 9f3a1d2.log
 ├── 3e91c2f.log
 └── b12a9ff.log
```

---

# Frontend Communication

The frontend will interact with the backend using:

### REST

* authentication
* execution requests
* log retrieval

### WebSocket

* real-time log streaming

Example workflow:

```
User submits execution
      ↓
REST API creates execution
      ↓
WebSocket subscribed to execution channel
      ↓
Logs streamed in real time
```

# License

MIT License
