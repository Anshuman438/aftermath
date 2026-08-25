package dev.aftermath.collector.repository;

import dev.aftermath.collector.entity.EvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<EvidenceEntity, Long> {
    List<EvidenceEntity> findByIncidentId(String incidentId);
    List<EvidenceEntity> findByIncidentIdAndType(String incidentId, String type);
}
