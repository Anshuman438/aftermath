package dev.aftermath.sdk.redaction;

import java.util.regex.Pattern;

public class PatternRegistry {

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b"
    );

    public static final Pattern PHONE_PATTERN = Pattern.compile(
            "\\b(?:\\+?\\d{1,3}[- .]?)?\\(?\\d{3}\\)?[- .]?\\d{3}[- .]?\\d{4}\\b"
    );

    public static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
            "\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12})\\b"
    );

    public static final Pattern JWT_PATTERN = Pattern.compile(
            "\\beyJ[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*\\b"
    );

    public static final Pattern SENSITIVE_JSON_KEY_PATTERN = Pattern.compile(
            "(?i)\"(?:password|pass|secret|token|api[_-]?key|ssn|credit[_-]?card)\"\\s*:\\s*\"[^\"]*\""
    );
}
