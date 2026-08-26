package dev.aftermath.sdk.model;

import java.util.UUID;

public class IncidentEvent {
    private String incidentId;
    private String traceId;
    private long timestamp;
    private RequestSnapshot request;
    private ErrorSnapshot error;
    private DeploymentInfo deployment;
    private SystemSnapshot system;

    public IncidentEvent() {
        this.incidentId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.system = new SystemSnapshot();
    }

    public IncidentEvent(String traceId, RequestSnapshot request, ErrorSnapshot error, DeploymentInfo deployment) {
        this.incidentId = UUID.randomUUID().toString();
        this.traceId = traceId;
        this.timestamp = System.currentTimeMillis();
        this.request = request;
        this.error = error;
        this.deployment = deployment;
        this.system = new SystemSnapshot();
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public RequestSnapshot getRequest() {
        return request;
    }

    public void setRequest(RequestSnapshot request) {
        this.request = request;
    }

    public ErrorSnapshot getError() {
        return error;
    }

    public void setError(ErrorSnapshot error) {
        this.error = error;
    }

    public DeploymentInfo getDeployment() {
        return deployment;
    }

    public void setDeployment(DeploymentInfo deployment) {
        this.deployment = deployment;
    }

    public SystemSnapshot getSystem() {
        return system;
    }

    public void setSystem(SystemSnapshot system) {
        this.system = system;
    }
}
