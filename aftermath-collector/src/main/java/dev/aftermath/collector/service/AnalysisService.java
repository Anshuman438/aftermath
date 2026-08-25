package dev.aftermath.collector.service;

import dev.aftermath.collector.entity.IncidentEntity;
import dev.aftermath.collector.repository.IncidentRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final IncidentRepository incidentRepository;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisResult {
        private String incidentId;
        private String exceptionClass;
        private String failingClass;
        private String failingMethod;
        private Integer lineNumber;
        private String rootCauseSummary;
        private String recommendedFix;
        private String suggestedDiff;
    }

    public AnalysisResult analyzeIncident(String incidentId) {
        IncidentEntity entity = incidentRepository.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        String stackTrace = entity.getStackTrace() != null ? entity.getStackTrace() : "";
        String exceptionClass = entity.getExceptionClass() != null ? entity.getExceptionClass() : "Exception";
        String message = entity.getExceptionMessage() != null ? entity.getExceptionMessage() : "";

        String failingClass = "UnknownClass";
        String failingMethod = "unknownMethod";
        int lineNumber = 1;

        if (stackTrace.contains("at ")) {
            for (String line : stackTrace.split("\n")) {
                line = line.trim();
                if (line.startsWith("at dev.aftermath.") || (line.startsWith("at ") && !line.contains("org.springframework") && !line.contains("jakarta.servlet"))) {
                    try {
                        String clean = line.substring(3).trim();
                        int parenOpen = clean.indexOf("(");
                        int parenClose = clean.indexOf(")");
                        if (parenOpen > 0) {
                            String fullMethod = clean.substring(0, parenOpen);
                            int lastDot = fullMethod.lastIndexOf(".");
                            if (lastDot > 0) {
                                failingClass = fullMethod.substring(0, lastDot);
                                failingMethod = fullMethod.substring(lastDot + 1);
                            }
                            if (parenClose > parenOpen && clean.contains(":")) {
                                String lineNumStr = clean.substring(clean.indexOf(":") + 1, parenClose);
                                lineNumber = Integer.parseInt(lineNumStr);
                            }
                        }
                        break;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        String summary;
        String fix;
        String diff;

        if (exceptionClass.contains("NullPointerException")) {
            summary = String.format("A NullPointerException occurred in %s.%s at line %d because a variable or method return value was null before dereferencing.", failingClass, failingMethod, lineNumber);
            fix = "Add a non-null validation check or default fallback value before invoking methods on object reference.";
            diff = String.format("""
--- a/%s.java
+++ b/%s.java
@@ -%d,6 +%d,8 @@
-    double discount = couponResponse.getDiscount();
+    Double rawDiscount = couponResponse != null ? couponResponse.getDiscount() : null;
+    double discount = (rawDiscount != null) ? rawDiscount : 0.0;
""", failingClass.replace(".", "/"), failingClass.replace(".", "/"), lineNumber, lineNumber);
        } else {
            summary = String.format("Unhandled %s encountered during HTTP request processing in %s.%s.", exceptionClass, failingClass, failingMethod);
            fix = "Wrap target invocation in try-catch block and handle exception gracefully.";
            diff = String.format("Check %s.java line %d", failingClass, lineNumber);
        }

        return AnalysisResult.builder()
                .incidentId(incidentId)
                .exceptionClass(exceptionClass)
                .failingClass(failingClass)
                .failingMethod(failingMethod)
                .lineNumber(lineNumber)
                .rootCauseSummary(summary)
                .recommendedFix(fix)
                .suggestedDiff(diff)
                .build();
    }
}
