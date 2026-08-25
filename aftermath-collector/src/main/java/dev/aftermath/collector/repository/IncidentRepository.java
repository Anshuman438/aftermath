package dev.aftermath.collector.repository;

import dev.aftermath.collector.entity.IncidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {
    Optional<IncidentEntity> findByIncidentId(String incidentId);
    Page<IncidentEntity> findByServiceNameContainingIgnoreCaseOrExceptionClassContainingIgnoreCase(
            String serviceName, String exceptionClass, Pageable pageable);
}
