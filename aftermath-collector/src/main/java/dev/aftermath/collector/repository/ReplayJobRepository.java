package dev.aftermath.collector.repository;

import dev.aftermath.collector.entity.ReplayJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplayJobRepository extends JpaRepository<ReplayJobEntity, Long> {
    Optional<ReplayJobEntity> findByJobId(String jobId);
    List<ReplayJobEntity> findByIncidentId(String incidentId);
}
