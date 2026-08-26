package dev.aftermath.collector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aftermath.collector.entity.IncidentEntity;
import dev.aftermath.collector.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ArchiveExporter {

    private final IncidentRepository incidentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] exportIncidentsToGzipArchive(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        List<IncidentEntity> incidents = incidentRepository.findByCreatedAtBefore(cutoff);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos);
             OutputStreamWriter writer = new OutputStreamWriter(gzos, StandardCharsets.UTF_8)) {

            objectMapper.writeValue(writer, incidents);
            gzos.finish();

            log.info("S3 Cold Compliance Archive: Exported {} raw incident capsules (Cutoff: {}) into compressed GZIP archive.",
                    incidents.size(), cutoff);

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate cold compliance GZIP archive: {}", e.getMessage(), e);
            throw new RuntimeException("GZIP Archive generation failed: " + e.getMessage());
        }
    }
}
