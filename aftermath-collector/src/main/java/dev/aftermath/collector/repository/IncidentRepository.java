package dev.aftermath.collector.repository;

import dev.aftermath.collector.entity.IncidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {
    Optional<IncidentEntity> findByIncidentId(String incidentId);
    Optional<IncidentEntity> findByFingerprint(String fingerprint);
    Page<IncidentEntity> findByServiceNameContainingIgnoreCaseOrExceptionClassContainingIgnoreCase(
            String serviceName, String exceptionClass, Pageable pageable);

    List<IncidentEntity> findByCreatedAtBefore(LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM IncidentEntity i WHERE i.createdAt < :cutoff")
    int deleteByCreatedAtBefore(LocalDateTime cutoff);
}
