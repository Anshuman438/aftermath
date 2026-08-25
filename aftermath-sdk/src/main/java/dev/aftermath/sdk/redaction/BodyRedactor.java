package dev.aftermath.sdk.redaction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BodyRedactor implements RedactionRule {

    private final List<Pattern> customPatterns = new ArrayList<>();

    public BodyRedactor() {
    }

    public BodyRedactor(List<String> extraPatterns) {
        if (extraPatterns != null) {
            for (String p : extraPatterns) {
                this.customPatterns.add(Pattern.compile("(?i)\"" + Pattern.quote(p) + "\"\\s*:\\s*\"[^\"]*\""));
            }
        }
    }

    @Override
    public String redact(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }

        String result = body;

        // Apply built-in PII patterns
        result = PatternRegistry.JWT_PATTERN.matcher(result).replaceAll("[TOKEN_REDACTED]");
        result = PatternRegistry.CREDIT_CARD_PATTERN.matcher(result).replaceAll("[CC_REDACTED]");
        result = PatternRegistry.EMAIL_PATTERN.matcher(result).replaceAll("[EMAIL_REDACTED]");
        result = PatternRegistry.PHONE_PATTERN.matcher(result).replaceAll("[PHONE_REDACTED]");
        result = PatternRegistry.SENSITIVE_JSON_KEY_PATTERN.matcher(result).replaceAll("\"[REDACTED_FIELD]\": \"[REDACTED]\"");

        // Apply custom field patterns
        for (Pattern p : customPatterns) {
            result = p.matcher(result).replaceAll("\"[REDACTED_FIELD]\": \"[REDACTED]\"");
        }

        return result;
    }
}
