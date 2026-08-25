package dev.aftermath.sdk.redaction;

import dev.aftermath.sdk.model.RequestSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RedactionEngineTest {

    private RedactionEngine redactionEngine;

    @BeforeEach
    void setUp() {
        redactionEngine = new RedactionEngine();
    }

    @Test
    void testHeaderRedaction() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer secret-token-123");
        headers.put("Cookie", "sessionid=abc; secret=xyz");
        headers.put("Content-Type", "application/json");

        RequestSnapshot snapshot = new RequestSnapshot("POST", "/api/test", null, headers, "{}", System.currentTimeMillis());
        RequestSnapshot redacted = redactionEngine.redactRequest(snapshot);

        assertEquals("[REDACTED]", redacted.getHeaders().get("Authorization"));
        assertEquals("[REDACTED]", redacted.getHeaders().get("Cookie"));
        assertEquals("application/json", redacted.getHeaders().get("Content-Type"));
    }

    @Test
    void testBodyPIIRedaction() {
        String body = "{\"email\": \"john.doe@example.com\", \"phone\": \"+1 555-123-4567\", \"password\": \"secret123\"}";
        String redacted = redactionEngine.redactBody(body);

        assertFalse(redacted.contains("john.doe@example.com"));
        assertTrue(redacted.contains("[EMAIL_REDACTED]"));
        assertFalse(redacted.contains("secret123"));
    }
}
