package dev.aftermath.sdk.model;

public class DeploymentInfo {
    private String serviceName;
    private String serviceVersion;
    private String environment;
    private String commitHash;

    public DeploymentInfo() {
    }

    public DeploymentInfo(String serviceName, String serviceVersion, String environment, String commitHash) {
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
        this.environment = environment;
        this.commitHash = commitHash;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }
}
