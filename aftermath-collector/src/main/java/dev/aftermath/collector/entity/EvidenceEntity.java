package dev.aftermath.collector.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evidence")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String incidentId;

    private String type; // HEADER, PARAM, BODY, CONTEXT

    private String keyName;

    @Column(length = 10000)
    private String valueData;
}
