# Levee 🌊
> Keeps your services from flooding.

Levee is a Java-based rate limiter built as a standalone REST API. It controls and restricts usage of external APIs, third-party services, and LLM (Large Language Model) APIs — making it suitable for any backend service that needs to enforce usage limits, regardless of language or framework.

Levee is uniquely suited for **LLM token management** — Token Bucket algorithm tracks variable token costs per request and refills continuously over time, making it directly applicable to controlling costs when integrating with providers such as OpenAI and Anthropic.

---

## Background

Rate limiting is a critical concern for any service that consumes external APIs or LLMs. Without it:
- A single misconfigured service can exhaust an entire API quota
- LLM costs can spike unpredictably from a single runaway agent
- No visibility into which app or user is consuming the most usage

Levee was built to solve this — a dedicated, language-agnostic rate limiting service that any upstream application can call before making an external API or LLM request.

### Algorithms

Levee implements rate limiting using the **Strategy design pattern** — algorithms are pluggable and selected at initialisation. Adding a new algorithm requires only a new `@Component` class with no changes to existing code (Open/Closed Principle).

| Algorithm | Cost per request | Best for | Status |
|---|---|---|---|
| Fixed Window | 1 | Simple API request counting | ✅ Available |
| Token Bucket | Variable (actual token count) | LLM token budget management | ✅ Available |
| Sliding Window | 1 | More accurate request counting | 🔜 Coming soon |
| Leaky Bucket | 1 | Smooth, constant throughput | 🔜 Coming soon |

**Algorithm references:**
- Fixed Window: [Redis Fixed Window Rate Limiting](https://redis.io/learn/develop/java/spring/rate-limiting/fixed-window)
- Token Bucket: [Rate Limiting Algorithms — Upstash](https://upstash.com/docs/redis/sdks/ratelimit-ts/algorithms)

---

## Features

- **Multi-tenant** — rate limit by `appId:entityName` so multiple upstream apps share one Levee instance
- **Per-entity limiting** — restrict by IP address, user email, or service name
- **LLM token budgeting** — Token Bucket tracks variable token costs with continuous refill
- **Pluggable algorithms** — swap algorithms at init time via config, defaults to Fixed Window
- **Redis-backed** — TTL-based window management, atomic operations, thread safe
- **Minimal response disclosure** — rate limit responses return only `{ "allowed": true/false }` — no internal details exposed to potential attackers
- **Language agnostic** — REST API callable from Python, JavaScript, Java, or any HTTP client
- **Global exception handling** — 429 Too Many Requests, 400 Bad Request, 404 Not Found
- **Interactive API docs** — Swagger UI available at `/swagger-ui.html`

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Data Store | Redis (dev: Redis Cloud, prod: AWS ElastiCache) |
| API Docs | SpringDoc OpenAPI 3 + Swagger UI |
| Logging | SLF4J + Logback |
| Design Pattern | Strategy Pattern |

---

## Getting Started

### Prerequisites

- Java 21
- Maven
- Redis instance (local or Redis Cloud free tier)

### Clone and configure

```bash
git clone https://github.com/yourusername/levee.git
cd levee
```

Set the following environment variables (or add to `application-dev.properties`):

```bash
REDIS_HOST=your-redis-host
REDIS_PORT=your-redis-port
REDIS_USERNAME=default
REDIS_PASSWORD=your-redis-password
```

### Run

```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`.

---

## Documentation

Interactive API documentation is available via Swagger UI once the application is running:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI spec in JSON format:
```
http://localhost:8080/v3/api-docs
```

---

## API Endpoints

### `POST /init` — Initialise a rate limiter

Creates a rate limit config for an entity. Idempotent — returns 200 if already exists.

**Request — Fixed Window:**
```json
{
    "appId": "FAQtory",
    "entityName": "john@email.com",
    "algorithmType": "FIXED_WINDOW",
    "fixedSize": 100
}
```

**Request — Token Bucket:**
```json
{
    "appId": "FAQtory",
    "entityName": "openai-service",
    "algorithmType": "TOKEN_BUCKET",
    "fixedSize": 10000,
    "refillRate": 125
}
```

> All fields except `appId` and `entityName` are optional:
> - `algorithmType` defaults to `FIXED_WINDOW`
> - `fixedSize` defaults to `100`
> - `refillRate` defaults to `125` tokens/hour (Token Bucket only)

**Response — 200 OK:**
```json
{
    "key": "FAQtory:john@email.com"
}
```

---

### `POST /request` — Check and enforce rate limit

Call this before every external API or LLM call.

**Request — Fixed Window:**
```json
{
    "appId": "FAQtory",
    "entityName": "john@email.com"
}
```

**Request — Token Bucket:**
```json
{
    "appId": "FAQtory",
    "entityName": "openai-service",
    "cost": 4000
}
```

> `cost` is required for Token Bucket — must be a positive value representing token consumption.

**Response — 200 OK (allowed):**
```json
{
    "allowed": true
}
```

**Response — 429 Too Many Requests (denied):**
```json
{
    "allowed": false
}
```

> Levee intentionally returns minimal response details on rate limit decisions — no remaining count, reset time, or reason is exposed. Detailed usage is available via `/stats` (coming soon).

---

### `GET /stats` — Current usage for a key *(coming soon)*
### `POST /reset` — Reset usage for a key *(coming soon)*
### `DELETE /delete` — Remove a key entirely *(coming soon)*
### `GET /health` — Service and Redis health check *(coming soon)*

---

## Integration Example — Python Agentic App

Levee is language-agnostic. Here is how a Python agentic app integrates with Levee to manage LLM token budgets:

```python
import requests
import tiktoken

def check_rate_limit(app_id: str, entity: str, prompt: str):
    # estimate token cost before calling LLM
    enc = tiktoken.encoding_for_model("gpt-4")
    estimated_tokens = len(enc.encode(prompt))

    response = requests.post("http://localhost:8080/request", json={
        "appId": app_id,
        "entityName": entity,
        "cost": estimated_tokens
    })

    if response.status_code == 429:
        raise Exception("Token budget exceeded. Try again later.")

    return True
```

---

## Redis Data Model

Levee uses two namespaced Redis entries per entity:

| Namespace | Purpose | TTL |
|---|---|---|
| `levee:config:appId:entityName` | Algorithm, limit, refill rate — written once at init | 5 × window duration |
| `levee:usage:appId:entityName` | Available tokens, window start — updated on every allowed request | Fixed Window: 24hrs, Token Bucket: 30 days |

**Lazy evaluation** — Redis is only written on allowed requests. Denied requests result in zero writes, keeping Redis lean and preventing attackers from gaining information through denial patterns.

---

## Future Scope

- **API key security** — per `appId` API keys, `/register` endpoint, Spring Security role-based access
- **Per-key dynamic window** — `PER_MINUTE | PER_HOUR | PER_DAY` configurable per entity at init. Window boundary encoded in Redis key (e.g. `levee:usage:FAQtory:john@email.com:202603221422`)
- **Sliding Window algorithm** — more accurate request counting, no boundary exploitation
- **Leaky Bucket algorithm** — constant output rate, smooth traffic
- **`/stats`, `/reset`, `/delete`, `/health` endpoints**
- **Monitoring** — async SLF4J + Logback event logging, external tool integration (Splunk, Grafana)
- **AWS ECS deployment** — containerised on ECS Fargate behind API Gateway, AWS ElastiCache for Redis
- **Redis Cluster** — horizontal scaling for high traffic
- **Limit updates** — `/update` endpoint to change `fixedSize` without reinitialising

---

## Project Structure

```
com.levee
├── model
│   ├── LeveeConfig.java
│   └── LeveeUsage.java
├── contract
│   ├── LeveeInitRequest.java
│   ├── LeveeInitResponse.java
│   ├── LeveeUsageRequest.java
│   └── LeveeUsageResponse.java
├── enums
│   └── AlgorithmType.java
├── repository
│   ├── LeveeConfigRepository.java
│   └── LeveeUsageRepository.java
├── service
│   ├── LeveeService.java
│   └── LeveeUsageService.java
├── strategy
│   ├── RateLimitStrategy.java
│   ├── StrategyFactory.java
│   ├── FixedWindowStrategy.java
│   └── TokenBucketStrategy.java
├── exception
│   ├── RateLimitExceededException.java
│   └── LeveeExceptionHandler.java
└── config
    ├── SpringConfig.java
    ├── RedisConfig.java
    └── SecurityConfig.java
```

---

## Contributing

Levee is a portfolio project actively under development. Contributions, issues, and feedback are welcome.

---

*Levee — keeps your services from flooding.*