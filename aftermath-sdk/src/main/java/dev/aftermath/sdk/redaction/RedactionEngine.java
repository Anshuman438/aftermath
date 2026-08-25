package dev.aftermath.sdk.redaction;

import dev.aftermath.sdk.model.RequestSnapshot;

import java.util.List;

public class RedactionEngine {

    private final HeaderRedactor headerRedactor;
    private final BodyRedactor bodyRedactor;

    public RedactionEngine() {
        this.headerRedactor = new HeaderRedactor();
        this.bodyRedactor = new BodyRedactor();
    }

    public RedactionEngine(List<String> extraHeaders, List<String> extraBodyPatterns) {
        this.headerRedactor = new HeaderRedactor(extraHeaders);
        this.bodyRedactor = new BodyRedactor(extraBodyPatterns);
    }

    public RequestSnapshot redactRequest(RequestSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        snapshot.setHeaders(headerRedactor.redactHeaders(snapshot.getHeaders()));
        snapshot.setBody(bodyRedactor.redact(snapshot.getBody()));
        return snapshot;
    }

    public String redactBody(String body) {
        return bodyRedactor.redact(body);
    }
}
