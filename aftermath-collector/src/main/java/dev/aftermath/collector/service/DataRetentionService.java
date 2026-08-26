package dev.aftermath.collector.service;

import dev.aftermath.collector.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataRetentionService {

    private final IncidentRepository incidentRepository;

    @Value("${aftermath.retention.days:30}")
    private int retentionDays;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public int purgeExpiredIncidents() {
        if (retentionDays <= 0) {
            log.info("Aftermath Data Retention: Auto-pruning disabled (retentionDays={})", retentionDays);
            return 0;
        }

        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        int purgedCount = incidentRepository.deleteByCreatedAtBefore(cutoff);

        log.info("Aftermath Data Retention: Purged {} raw incident capsules older than {} days (Cutoff: {}). Generated test artifacts preserved permanently.",
                purgedCount, retentionDays, cutoff);

        return purgedCount;
    }
}
