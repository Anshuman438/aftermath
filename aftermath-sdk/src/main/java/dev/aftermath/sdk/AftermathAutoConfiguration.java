package dev.aftermath.sdk;

import dev.aftermath.sdk.capture.AftermathAspectInterceptor;
import dev.aftermath.sdk.capture.CaptureFilter;
import dev.aftermath.sdk.capture.FailureDetector;
import dev.aftermath.sdk.model.DeploymentInfo;
import dev.aftermath.sdk.redaction.RedactionEngine;
import dev.aftermath.sdk.transport.AsyncEventDispatcher;
import dev.aftermath.sdk.transport.EventTransport;
import dev.aftermath.sdk.transport.HttpEventTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "aftermath.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AftermathProperties.class)
public class AftermathAutoConfiguration {

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    @Value("${aftermath.version:0.1.0-SNAPSHOT}")
    private String serviceVersion;

    @Bean
    public DeploymentInfo aftermathDeploymentInfo() {
        return new DeploymentInfo(serviceName, serviceVersion, "local", "dev-commit");
    }

    @Bean
    public RedactionEngine aftermathRedactionEngine(AftermathProperties properties) {
        return new RedactionEngine(
                properties.getRedaction().getAdditionalHeaders(),
                properties.getRedaction().getAdditionalBodyPatterns()
        );
    }

    @Bean
    public FailureDetector aftermathFailureDetector() {
        return new FailureDetector(400, 599);
    }

    @Bean
    public EventTransport aftermathEventTransport(AftermathProperties properties) {
        HttpEventTransport httpTransport = new HttpEventTransport(properties.getCollectorUrl());
        return new AsyncEventDispatcher(httpTransport);
    }

    @Bean
    @ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
    public AftermathAspectInterceptor aftermathAspectInterceptor(EventTransport transport, DeploymentInfo deploymentInfo) {
        return new AftermathAspectInterceptor(transport, deploymentInfo);
    }

    @Bean
    public FilterRegistrationBean<CaptureFilter> aftermathCaptureFilterRegistration(
            EventTransport transport,
            RedactionEngine redactionEngine,
            FailureDetector failureDetector,
            DeploymentInfo deploymentInfo,
            AftermathProperties properties) {

        int maxBodyBytes = properties.getMaxBodySizeKb() * 1024;
        CaptureFilter filter = new CaptureFilter(
                transport,
                redactionEngine,
                failureDetector,
                deploymentInfo,
                maxBodyBytes,
                properties.isEnabled()
        );

        FilterRegistrationBean<CaptureFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
