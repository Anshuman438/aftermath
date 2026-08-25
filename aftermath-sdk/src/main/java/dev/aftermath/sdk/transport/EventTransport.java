package dev.aftermath.sdk.transport;

import dev.aftermath.sdk.model.IncidentEvent;

public interface EventTransport {
    void send(IncidentEvent event);
}
