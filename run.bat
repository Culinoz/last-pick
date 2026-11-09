@echo off
echo ===================================================
echo Starting MTG Draft application...
echo ===================================================

echo [1/3] Starting PostgreSQL container with Docker...
docker compose up -d

echo.
echo [2/3] Checking application build...
if not exist "target\mtgDraft-0.0.1-SNAPSHOT.jar" (
    echo Building application JAR first (this compiles backend and frontend)...
    call mvnw.cmd package -DskipTests
)

echo.
echo [3/3] Launching MTG Draft...
echo Open http://localhost:8080 in your browser when ready.
echo.
java -jar target\mtgDraft-0.0.1-SNAPSHOT.jar
pause
