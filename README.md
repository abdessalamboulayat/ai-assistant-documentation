## RAG pipeline with Java

> Intelligent documentation Q&A powered by RAG, semantic caching, and persistent memory.
### RAG + Semantic Cache + Memory

An intelligent AI assistant that ingests your documents, answers natural language questions using Retrieval-Augmented Generation, caches semantically similar queries to cut LLM costs, and remembers context across conversation turns.

---
## Table of Contents
 
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)

---
## Features

| Feature | Description |
|---|---|
| **Document Ingestion** | Ingestion Pipeline: Extract, Chunk, Embed, Load |
| **RAG Q&A** | Retrieves relevant chunks from a vector store (context) before each LLM call |
| **Semantic Cache** | Skips the LLM entirely when a near-identical question was already answered |
| **Session Memory** | Short-term memory keeps conversation context across multiple turns |
| **Long-term Memory** | Extracts and persists key facts such as preferences about the user across sessions |
| **Preference Learning** | Follow up questions to understand more user preferences |

---

## Architecture 
### 1. Document Ingestion (ETL Pipeline)
<img width="1114" height="806" alt="_home_my-pc_Downloads_rag_ingestion_pipeline html" src="https://github.com/user-attachments/assets/e75d0f40-a340-44f1-a038-5d1968c2a51f" />


#### Key Steps

- Load your documents *(Markdown, HTML, TXT)*
- Split into ~500-token chunks *(with overlap)*
- Generate embeddings (convert each chunk into vectors)
- Store in Redis with metadata

### 2. Chat Flow
<img width="1099" height="1722" alt="_home_my-pc_Downloads_rag_architecture_image_style html" src="https://github.com/user-attachments/assets/f653dab9-4a07-4b99-ae28-c4081f12406d" />



---

## Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 21, Spring Boot 3.5, Spring AI |
| **LLM** | Ollama (`llama3.2`) — Gemini available via config |
| **Embeddings** | Ollama (`qwen3-embedding:0.6b`) |
| **Vector Store** | Redis Stack (Vector Search) |
| **Cache & Memory** | Redis (semantic cache index + session memory) |
| **Document Parsing** | Apache Tika (via Spring AI Tika Document Reader) |
| **Build** | Maven |

## Getting Started
 
### Prerequisites
 
- Java 21+
- Maven 3.9+
- [Ollama](https://ollama.com/) running locally
- [Redis Stack](https://redis.io/docs/stack/) (includes Vector Search)
### 1. Pull the required models
 
```bash
ollama pull llama3.2
ollama pull qwen3-embedding:0.6b
```
 
### 2. Start Redis Stack
 
```bash
docker run -d --name redis-stack \
  -p 6379:6379 \
  redis/redis-stack-server:latest
```
 
### 3. Clone and run
 
```bash
git clone https://github.com/abdessalamboulayat/ai-assistant-documentation.git
cd ai-assistant-documentation
mvn spring-boot:run
```
 
The application starts on `http://localhost:8080`.

---

## Configuration
 
All settings live in `src/main/resources/application.properties`.
 
```properties
# LLM — Ollama (default)
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3.2
spring.ai.ollama.embedding.options.model=qwen3-embedding:0.6b
 
# LLM — Gemini (alternative, uncomment to switch)
# spring.ai.model.chat=google-genai
# spring.ai.google.genai.api-key=YOUR_KEY
# spring.ai.google.genai.project-id=YOUR_PROJECT
 
# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.username=default
spring.data.redis.password=password

rag.similarity.treshold=0.95
 
# Session memory TTL (hours)
redis.session.chat.ttl=24
 
# File upload
spring.servlet.multipart.max-file-size=3MB
```
---

### Switching to Gemini
 
1. Uncomment the Gemini lines above.
2. Choose a gemini model and add your API KEY.
3. Comment out or remove the `spring.ai.ollama.chat.*` properties.
4. Restart the application — no code changes required.

---

## Notes

- Semantic caching helps reduce both **latency** and **LLM costs**
- Memory is currently **session-scoped only**
- System performance improves with usage due to cache reuse

---
## API Reference
 
### Ingest a document
 
```
POST /documents
Content-Type: multipart/form-data
 
document    File to ingest (MD, HTML, PDF, DOCX, TXT — max 3 MB)
```
 
**Response**
 
```
200 Ok
```
 
### Send a message
 
```
POST /chat
Content-Type: application/x-www-form-urlencoded
 
question    Natural language question
```
 
**Response**
 
```json
{
  "answer": "Authentication is handled by...",
  "memories": [
    { "type": "SEMANTIC_MEMORY", "content": "User works with Spring Security" }
  ],
  "followUpQuestion": "Should I always include code examples in my answers?"
}
```
 
`followUpQuestion` is `null` when no preference signal was detected. When present, sending the user's reply (`"yes"`, `"sure"`, etc.) to the same endpoint saves the preference permanently.
