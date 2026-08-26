package dev.aftermath.sdk.capture;

import dev.aftermath.sdk.model.DeploymentInfo;
import dev.aftermath.sdk.model.ErrorSnapshot;
import dev.aftermath.sdk.model.IncidentEvent;
import dev.aftermath.sdk.model.RequestSnapshot;
import dev.aftermath.sdk.transport.EventTransport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

@Aspect
public class AftermathAspectInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AftermathAspectInterceptor.class);

    private final EventTransport transport;
    private final DeploymentInfo deploymentInfo;

    public AftermathAspectInterceptor(EventTransport transport, DeploymentInfo deploymentInfo) {
        this.transport = transport;
        this.deploymentInfo = deploymentInfo;
    }

    @AfterThrowing(
            pointcut = "@annotation(org.springframework.scheduling.annotation.Scheduled) || " +
                       "@annotation(org.springframework.scheduling.annotation.Async)",
            throwing = "exception"
    )
    public void captureBackgroundFailure(JoinPoint joinPoint, Throwable exception) {
        try {
            String methodName = joinPoint.getSignature().toShortString();
            log.info("Aftermath: Capturing background/async failure in {}", methodName);

            RequestSnapshot req = new RequestSnapshot(
                    "TASK",
                    methodName,
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    "Background Async Execution",
                    System.currentTimeMillis()
            );

            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));

            ErrorSnapshot error = new ErrorSnapshot(
                    exception.getClass().getName(),
                    exception.getMessage(),
                    sw.toString(),
                    500
            );

            IncidentEvent event = new IncidentEvent("BACKGROUND-TASK", req, error, deploymentInfo);
            transport.send(event);
        } catch (Exception e) {
            log.warn("Aftermath: Fail-open aspect interceptor exception", e);
        }
    }
}
