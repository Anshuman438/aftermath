package dev.aftermath.collector.service;

import dev.aftermath.collector.entity.IncidentEntity;
import dev.aftermath.collector.repository.IncidentRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CascadingErrorAnalyzer {

    private final IncidentRepository incidentRepository;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CascadingChainReport {
        private String incidentId;
        private String primaryCulpritClass;
        private String primaryCulpritMethod;
        private Integer primaryLineNumber;
        private String primaryService;
        private List<String> errorChain;
        private String rootCauseSummary;
        private String recommendedAction;
    }

    public CascadingChainReport analyzeCascadingErrors(String incidentId) {
        IncidentEntity entity = incidentRepository.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        String stackTrace = entity.getStackTrace() != null ? entity.getStackTrace() : "";
        List<String> chain = new ArrayList<>();

        String primaryClass = "UnknownClass";
        String primaryMethod = "unknownMethod";
        int lineNumber = 1;

        if (stackTrace.contains("Caused by:")) {
            String[] parts = stackTrace.split("Caused by:");
            for (String part : parts) {
                String firstLine = part.trim().split("\n")[0];
                chain.add(firstLine);
            }

            // The deepest 'Caused by' exception is the root culprit!
            String rootPart = parts[parts.length - 1].trim();
            for (String line : rootPart.split("\n")) {
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
                                primaryClass = fullMethod.substring(0, lastDot);
                                primaryMethod = fullMethod.substring(lastDot + 1);
                            }
                            if (parenClose > parenOpen && clean.contains(":")) {
                                String lineNumStr = clean.substring(clean.indexOf(":") + 1, parenClose);
                                lineNumber = Integer.parseInt(lineNumStr);
                            }
                        }
                        break;
                    } catch (Exception ignored) {}
                }
            }
        } else {
            chain.add(entity.getExceptionClass() + ": " + entity.getExceptionMessage());
        }

        String summary = String.format("Primary Root Cause Culprit identified in %s.%s at line %d. This exception triggered a cascading failure chain across downstream HTTP calls.",
                primaryClass, primaryMethod, lineNumber);

        String action = String.format("Fix the root exception in %s.%s line %d to resolve all downstream cascading failures.",
                primaryClass, primaryMethod, lineNumber);

        return CascadingChainReport.builder()
                .incidentId(incidentId)
                .primaryCulpritClass(primaryClass)
                .primaryCulpritMethod(primaryMethod)
                .primaryLineNumber(lineNumber)
                .primaryService(entity.getServiceName() != null ? entity.getServiceName() : "unknown-service")
                .errorChain(chain)
                .rootCauseSummary(summary)
                .recommendedAction(action)
                .build();
    }
}
