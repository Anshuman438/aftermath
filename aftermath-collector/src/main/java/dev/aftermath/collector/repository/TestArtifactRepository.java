package dev.aftermath.collector.repository;

import dev.aftermath.collector.entity.TestArtifactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestArtifactRepository extends JpaRepository<TestArtifactEntity, Long> {
    Optional<TestArtifactEntity> findByArtifactId(String artifactId);
    List<TestArtifactEntity> findByIncidentId(String incidentId);
    List<TestArtifactEntity> findByIncidentIdOrderByCreatedAtDesc(String incidentId);
}
