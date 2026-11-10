# BudgetBee Backend 🐝

REST API for a personal finance app: track expenses, set **monthly category budgets**, create **savings goals**, get **automatic alerts** when spending nears or crosses a limit, and chat with **Bee**, an AI finance coach.

## Tech stack
Java 21 · Spring Boot 3 · Spring Security (JWT) · Spring Data JPA / Hibernate · MySQL 8 · Maven · Ollama (local LLM) · JUnit 5 + Mockito · Docker Compose

## Features
- JWT authentication (BCrypt password hashing); every query is scoped to the logged-in user
- Expense tracking with default + custom categories
- Monthly budget per category with alerts at **80%** and **100%+** (de-duplicated per category per month)
- Savings goals with progress tracking and goal-reached notifications
- Dashboard endpoint: month total, budget usage %, goals
- AI coach: builds a snapshot of the user's finances and sends it to a local LLM (Ollama); rule-based fallback if the model is offline
- Centralised validation and error handling

## Architecture
```
Controller (REST)  →  Service (business rules)  →  Repository (Spring Data JPA)  →  MySQL
                              │
                              └→ AiService → Ollama (HTTP)
```

## Run locally
Prerequisites: JDK 21, Maven 3.9+, Docker (optional: Ollama).

```bash
docker compose up -d          # starts MySQL
mvn spring-boot:run           # API on http://localhost:8080
mvn test                      # unit tests
```
Optional AI: `ollama pull llama3.2:3b`

### Configuration (environment variables)
| Variable | Default (local only) |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | local MySQL, root/root |
| `JWT_SECRET` | dev placeholder — **set a long random value in real use** |
| `CORS_ORIGIN` | `http://localhost:5173` |
| `OLLAMA_URL`, `OLLAMA_MODEL` | `http://localhost:11434`, `llama3.2:3b` |

## API
All endpoints except `/api/auth/**` need `Authorization: Bearer <token>`.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Sign up / log in, returns JWT |
| GET, POST | `/api/categories` | List / create categories |
| GET, POST, DELETE | `/api/expenses[/{id}]` | Expenses (POST runs the budget check) |
| GET, POST, DELETE | `/api/budgets[/{id}]` | Monthly limit per category (POST = create or update) |
| GET, POST, DELETE | `/api/goals[/{id}]` | Savings goals |
| POST | `/api/goals/{id}/contribute` | Add savings to a goal |
| GET, POST | `/api/notifications`, `/api/notifications/read-all` | Alerts |
| GET | `/api/dashboard` | Monthly summary |
| POST, GET | `/api/ai/chat`, `/api/ai/insight` | AI coach |


