package dev.aftermath.collector.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "replay_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplayJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jobId;

    @Column(nullable = false)
    private String incidentId;

    private String targetBaseUrl;
    private boolean reproduced;
    private int originalStatusCode;
    private int replayedStatusCode;
    private boolean statusMatch;
    private long executionTimeMs;

    @Column(length = 10000)
    private String replayedResponseBody;

    @Column(length = 10000)
    private String resultJson;

    private String createdAt;
}
