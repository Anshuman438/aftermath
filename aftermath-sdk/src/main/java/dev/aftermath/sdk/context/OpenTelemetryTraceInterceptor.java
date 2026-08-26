package dev.aftermath.sdk.context;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

public class OpenTelemetryTraceInterceptor {

    public static String extractTraceId(HttpServletRequest request) {
        // 1. Check W3C Trace Context standard header (traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01)
        String traceparent = request.getHeader("traceparent");
        if (traceparent != null && !traceparent.isBlank()) {
            String[] parts = traceparent.split("-");
            if (parts.length >= 2 && !parts[1].isBlank()) {
                return parts[1];
            }
        }

        // 2. Check B3 Single Header (b3: 80f1988a56347198-05e3ac9a4f6e3b90-1)
        String b3 = request.getHeader("b3");
        if (b3 != null && !b3.isBlank()) {
            String[] parts = b3.split("-");
            if (parts.length > 0 && !parts[0].isBlank()) {
                return parts[0];
            }
        }

        // 3. Check Zipkin / B3 Multi-headers
        String b3TraceId = request.getHeader("X-B3-TraceId");
        if (b3TraceId != null && !b3TraceId.isBlank()) {
            return b3TraceId;
        }

        // 4. Check Datadog Trace ID
        String ddTraceId = request.getHeader("x-datadog-trace-id");
        if (ddTraceId != null && !ddTraceId.isBlank()) {
            return ddTraceId;
        }

        // 5. Check SLF4J MDC traceId
        String mdcTraceId = MDC.get("traceId");
        if (mdcTraceId != null && !mdcTraceId.isBlank()) {
            return mdcTraceId;
        }

        // 6. Check custom header X-Trace-Id
        String customTraceId = request.getHeader("X-Trace-Id");
        if (customTraceId != null && !customTraceId.isBlank()) {
            return customTraceId;
        }

        return null;
    }
}
