/**
 * Returns the HTTP base URL for the backend API.
 *
 * Priority:
 * 1. VITE_API_URL env variable (explicit override)
 * 2. In production (bundled in the JAR), uses empty string (same origin)
 * 3. In dev mode (Vite dev server), points to hostname:8080 (Spring Boot)
 */
export function getApiBaseUrl(): string {
    if (import.meta.env.VITE_API_URL) {
        return import.meta.env.VITE_API_URL as string;
    }
    if (import.meta.env.PROD) {
        // Bundled inside Spring Boot — API is on the same origin
        return "";
    }
    // Dev mode: Vite runs on :5173, Spring Boot on :8080
    return `${window.location.protocol}//${window.location.hostname}:8080`;
}

/**
 * Returns the WebSocket URL for the STOMP broker.
 *
 * Priority:
 * 1. VITE_WS_URL env variable (explicit override)
 * 2. In production (bundled in the JAR), derives from current origin
 * 3. In dev mode (Vite dev server), points to hostname:8080
 */
export function getWsUrl(): string {
    if (import.meta.env.VITE_WS_URL) {
        return import.meta.env.VITE_WS_URL as string;
    }
    const wsProtocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    if (import.meta.env.PROD) {
        // Bundled inside Spring Boot — same host and port
        return `${wsProtocol}//${window.location.host}/ws`;
    }
    // Dev mode: Spring Boot on :8080
    return `${wsProtocol}//${window.location.hostname}:8080/ws`;
}
