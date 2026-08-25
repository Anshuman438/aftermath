package dev.aftermath.collector.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String incidentId;

    private String traceId;
    private String serviceName;
    private String serviceVersion;
    private String environment;
    private String commitHash;

    private String httpMethod;
    private String requestUri;
    private Integer statusCode;

    private String exceptionClass;

    @Column(length = 2000)
    private String exceptionMessage;

    @Column(length = 10000)
    private String stackTrace;

    @Column(length = 50000)
    private String rawJson;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
