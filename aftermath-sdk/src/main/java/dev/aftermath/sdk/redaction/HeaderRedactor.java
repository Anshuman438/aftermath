package dev.aftermath.sdk.redaction;

import java.util.*;

public class HeaderRedactor {

    private final Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList(
            "authorization", "cookie", "set-cookie", "x-api-key", "api-key", "proxy-authorization", "x-auth-token"
    ));

    public HeaderRedactor() {
    }

    public HeaderRedactor(Collection<String> extraSensitiveHeaders) {
        if (extraSensitiveHeaders != null) {
            for (String h : extraSensitiveHeaders) {
                this.sensitiveHeaders.add(h.toLowerCase(Locale.ROOT));
            }
        }
    }

    public Map<String, String> redactHeaders(Map<String, String> headers) {
        if (headers == null) {
            return Collections.emptyMap();
        }
        Map<String, String> redacted = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (key != null && sensitiveHeaders.contains(key.toLowerCase(Locale.ROOT))) {
                redacted.put(key, "[REDACTED]");
            } else {
                redacted.put(key, entry.getValue());
            }
        }
        return redacted;
    }
}
