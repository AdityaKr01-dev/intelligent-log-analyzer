# Intelligent Log Analyzer

A Spring Boot application that ingests raw log files, categorizes events into structured records, and queries the OpenAI API to provide instant debugging suggestions for system errors.

## Features
- **Log Parsing**: Parses timestamps, log levels (`INFO`, `WARN`, `ERROR`), service identifiers, and stack traces.
- **AI Diagnostics**: Auto-queries OpenAI for remediation suggestions on detected `ERROR` entries.
- **REST APIs**: Ingest `.log` files via multipart upload, query error history, and search logs across services.
- **Persistence**: Relational schema managed by Spring Data JPA and PostgreSQL.

## Endpoints
- `POST /api/logs/upload` - Upload a `.log` file for parsing
- `GET /api/logs` - List all parsed logs
- `GET /api/logs/errors` - List errors with AI recommendations
- `GET /api/logs/search?query=...` - Filter logs by service, level, or keyword