# AI Documentation Assistant  
### RAG + Semantic Cache + Memory

An intelligent AI assistant that ingests documentation, answers questions using **Retrieval-Augmented Generation (RAG)**, caches semantically similar queries to reduce LLM costs, and maintains **conversation memory within a session** *(cross-session memory not yet implemented)*.

---

## Features

- Document Ingestion Pipeline (ETL)
- Vector Search with Redis
- RAG-based Question Answering
- Semantic Caching *(cost & latency optimization)*
- Session-based Chat Memory

---

## Architecture Overview

### 1. Document Ingestion (ETL Pipeline)
<img width="800" height="600" alt="image" src="https://github.com/user-attachments/assets/b4e50cd4-a158-4b01-b483-4b864c5ef5c5" />

#### Key Steps

- Load documentation files *(Markdown, HTML, TXT)*
- Split into ~500-token chunks *(with overlap)*
- Generate embeddings
- Store in Redis with metadata

### 2. Chat Flow
<img width="800" height="600" alt="Diagram Basics (Community) (1)" src="https://github.com/user-attachments/assets/bd25f43e-4084-46c1-ba30-ae542a88d8bf" />


---

## Tech Stack

| Component        | Technology                          |
|----------------|----------------------------------|
| LLM Provider | Ollama                           |
| Embeddings   | Ollama-compatible embedding model |
| Vector DB    | Redis Stack (Vector Search)       |
| Backend      | Java, Spring Boot, Spring AI      |

---

## Notes

- Semantic caching helps reduce both **latency** and **LLM costs**
- Memory is currently **session-scoped only**
- System performance improves with usage due to cache reuse

---

## Future Improvements

- Cross-session persistent memory
- Multi-document reasoning
- UI for document management

---
