package dev.aftermath.replay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplayRequest {
    private String incidentId;
    private String targetBaseUrl;
    private Map<String, String> overrideHeaders;
    private String overrideBody;
    private Integer timeoutMs;
}
