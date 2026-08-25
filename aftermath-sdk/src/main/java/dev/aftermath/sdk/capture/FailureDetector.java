package dev.aftermath.sdk.capture;

public class FailureDetector {

    private final int minStatusCode;
    private final int maxStatusCode;

    public FailureDetector() {
        this(400, 599);
    }

    public FailureDetector(int minStatusCode, int maxStatusCode) {
        this.minStatusCode = minStatusCode;
        this.maxStatusCode = maxStatusCode;
    }

    public boolean isFailure(int statusCode, Throwable uncaughtException) {
        if (uncaughtException != null) {
            return true;
        }
        return statusCode >= minStatusCode && statusCode <= maxStatusCode;
    }
}
