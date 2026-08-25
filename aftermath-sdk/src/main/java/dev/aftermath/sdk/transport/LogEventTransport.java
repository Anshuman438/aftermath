package dev.aftermath.sdk.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aftermath.sdk.model.IncidentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEventTransport implements EventTransport {

    private static final Logger log = LoggerFactory.getLogger(LogEventTransport.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void send(IncidentEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.info("Aftermath Incident Capsule Captured [Log Transport]: {}", json);
        } catch (Exception e) {
            log.warn("Aftermath: Failed to serialize incident event", e);
        }
    }
}
