package dev.aftermath.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "aftermath")
public class AftermathProperties {

    private boolean enabled = true;
    private String collectorUrl = "http://localhost:8090";
    private int maxBodySizeKb = 64;

    private Capture capture = new Capture();
    private Redaction redaction = new Redaction();

    public static class Capture {
        private String statusCodes = "400-599";
        private List<String> excludePaths = new ArrayList<>();

        public String getStatusCodes() {
            return statusCodes;
        }

        public void setStatusCodes(String statusCodes) {
            this.statusCodes = statusCodes;
        }

        public List<String> getExcludePaths() {
            return excludePaths;
        }

        public void setExcludePaths(List<String> excludePaths) {
            this.excludePaths = excludePaths;
        }
    }

    public static class Redaction {
        private boolean enabled = true;
        private List<String> additionalHeaders = new ArrayList<>();
        private List<String> additionalBodyPatterns = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getAdditionalHeaders() {
            return additionalHeaders;
        }

        public void setAdditionalHeaders(List<String> additionalHeaders) {
            this.additionalHeaders = additionalHeaders;
        }

        public List<String> getAdditionalBodyPatterns() {
            return additionalBodyPatterns;
        }

        public void setAdditionalBodyPatterns(List<String> additionalBodyPatterns) {
            this.additionalBodyPatterns = additionalBodyPatterns;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCollectorUrl() {
        return collectorUrl;
    }

    public void setCollectorUrl(String collectorUrl) {
        this.collectorUrl = collectorUrl;
    }

    public int getMaxBodySizeKb() {
        return maxBodySizeKb;
    }

    public void setMaxBodySizeKb(int maxBodySizeKb) {
        this.maxBodySizeKb = maxBodySizeKb;
    }

    public Capture getCapture() {
        return capture;
    }

    public void setCapture(Capture capture) {
        this.capture = capture;
    }

    public Redaction getRedaction() {
        return redaction;
    }

    public void setRedaction(Redaction redaction) {
        this.redaction = redaction;
    }
}
