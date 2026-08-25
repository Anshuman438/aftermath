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
public class ReplayResult {
    private String jobId;
    private String incidentId;
    private String targetBaseUrl;
    private boolean reproduced;
    private int originalStatusCode;
    private int replayedStatusCode;
    private boolean statusMatch;
    private String originalResponseBody;
    private String replayedResponseBody;
    private Map<String, String> replayedResponseHeaders;
    private long executionTimeMs;
    private String errorMessage;
    private String timestamp;
}
