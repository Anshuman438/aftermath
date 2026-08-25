package dev.aftermath.sdk.capture;

import dev.aftermath.sdk.model.DeploymentInfo;
import dev.aftermath.sdk.model.ErrorSnapshot;
import dev.aftermath.sdk.model.IncidentEvent;
import dev.aftermath.sdk.model.RequestSnapshot;
import dev.aftermath.sdk.redaction.RedactionEngine;
import dev.aftermath.sdk.transport.EventTransport;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.util.*;

public class CaptureFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CaptureFilter.class);

    private final EventTransport transport;
    private final RedactionEngine redactionEngine;
    private final FailureDetector failureDetector;
    private final DeploymentInfo deploymentInfo;
    private final int maxBodySize;
    private final boolean enabled;

    public CaptureFilter(EventTransport transport, RedactionEngine redactionEngine,
                         FailureDetector failureDetector, DeploymentInfo deploymentInfo,
                         int maxBodySize, boolean enabled) {
        this.transport = transport;
        this.redactionEngine = redactionEngine;
        this.failureDetector = failureDetector;
        this.deploymentInfo = deploymentInfo;
        this.maxBodySize = maxBodySize;
        this.enabled = enabled;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!enabled || !(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        RequestWrapper requestWrapper = new RequestWrapper(httpRequest, maxBodySize);
        ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);

        Throwable uncaughtException = null;
        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } catch (Throwable t) {
            uncaughtException = t;
            throw t;
        } finally {
            try {
                int status = responseWrapper.getStatus();
                if (uncaughtException != null && status < 400) {
                    status = 500;
                }

                if (failureDetector.isFailure(status, uncaughtException)) {
                    captureIncident(requestWrapper, status, uncaughtException, startTime);
                }

                responseWrapper.copyBodyToResponse();
            } catch (Exception e) {
                // Fail-open protection: Filter must never crash host application
                log.warn("Aftermath: Exception inside capture filter cleanup", e);
            }
        }
    }

    private void captureIncident(RequestWrapper request, int statusCode, Throwable exception, long startTime) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = request.getHeader("X-Trace-Id");
        }
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.put(name, request.getHeader(name));
            }
        }

        Map<String, String> queryParams = new LinkedHashMap<>();
        request.getParameterMap().forEach((k, v) -> queryParams.put(k, String.join(",", v)));

        RequestSnapshot rawSnapshot = new RequestSnapshot(
                request.getMethod(),
                request.getRequestURI(),
                queryParams,
                headers,
                request.getBodyAsString(),
                startTime
        );

        RequestSnapshot redactedRequest = redactionEngine.redactRequest(rawSnapshot);

        String exClass = exception != null ? exception.getClass().getName() : "HTTP_" + statusCode;
        String exMsg = exception != null ? exception.getMessage() : "HTTP Status " + statusCode;
        String stackTrace = null;
        if (exception != null) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            stackTrace = sw.toString();
        }

        ErrorSnapshot error = new ErrorSnapshot(exClass, exMsg, stackTrace, statusCode);

        IncidentEvent event = new IncidentEvent(traceId, redactedRequest, error, deploymentInfo);
        transport.send(event);
    }
}
