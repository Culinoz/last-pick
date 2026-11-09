# Last Pick

A full-stack Magic: The Gathering booster draft simulator built with Spring Boot and React.

## Prerequisites

- **[Java 25](https://www.oracle.com/java/technologies/downloads/#java25)** (JDK)
- **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (for PostgreSQL)

---

## Quick Start

### Windows (Automated)

1. Make sure **Docker Desktop** is running.
2. Double-click `run.bat` (or run it from PowerShell / CMD):
   ```bat
   .\run.bat
   ```
   *This automatically starts PostgreSQL, builds the frontend/backend on first run, and launches the app.*
3. Open [http://localhost:8080](http://localhost:8080) in your browser.

---

### Manual / macOS / Linux

1. **Start PostgreSQL:**
   ```bash
   docker compose up -d
   ```

2. **Build and package:**
   ```bash
   ./mvnw clean package -DskipTests
   ```
   *(Node.js, npm, and frontend dependencies are downloaded and built automatically by Maven).*

3. **Launch the application:**
   ```bash
   java -jar target/mtgDraft-0.0.1-SNAPSHOT.jar
   ```
   *(Alternatively: `./mvnw spring-boot:run`)*

4. Open [http://localhost:8080](http://localhost:8080) in your browser.
