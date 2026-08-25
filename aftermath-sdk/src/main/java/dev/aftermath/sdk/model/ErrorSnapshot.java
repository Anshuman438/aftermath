package dev.aftermath.sdk.model;

public class ErrorSnapshot {
    private String exceptionClass;
    private String message;
    private String stackTrace;
    private int statusCode;

    public ErrorSnapshot() {
    }

    public ErrorSnapshot(String exceptionClass, String message, String stackTrace, int statusCode) {
        this.exceptionClass = exceptionClass;
        this.message = message;
        this.stackTrace = stackTrace;
        this.statusCode = statusCode;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
