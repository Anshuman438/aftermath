package dev.aftermath.collector.service;

import dev.aftermath.collector.entity.EvidenceEntity;
import dev.aftermath.collector.entity.IncidentEntity;
import dev.aftermath.collector.entity.TestArtifactEntity;
import dev.aftermath.collector.repository.EvidenceRepository;
import dev.aftermath.collector.repository.IncidentRepository;
import dev.aftermath.collector.repository.TestArtifactRepository;
import dev.aftermath.sdk.model.ErrorSnapshot;
import dev.aftermath.sdk.model.IncidentEvent;
import dev.aftermath.sdk.model.RequestSnapshot;
import dev.aftermath.testgen.generator.JUnitTestGenerator;
import dev.aftermath.testgen.model.GeneratedTestArtifact;
import dev.aftermath.testgen.model.TestGeneratorOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestGenService {

    private final IncidentRepository incidentRepository;
    private final EvidenceRepository evidenceRepository;
    private final TestArtifactRepository testArtifactRepository;
    private final JUnitTestGenerator jUnitTestGenerator = new JUnitTestGenerator();

    @Transactional
    public GeneratedTestArtifact generateTestForIncident(String incidentId, TestGeneratorOptions options) {
        IncidentEntity entity = incidentRepository.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        List<EvidenceEntity> headers = evidenceRepository.findByIncidentIdAndType(incidentId, "HEADER");
        Map<String, String> headerMap = new HashMap<>();
        for (EvidenceEntity h : headers) {
            headerMap.put(h.getKeyName(), h.getValueData());
        }

        List<EvidenceEntity> bodies = evidenceRepository.findByIncidentIdAndType(incidentId, "BODY");
        String bodyContent = (bodies != null && !bodies.isEmpty()) ? bodies.get(0).getValueData() : null;

        RequestSnapshot reqSnapshot = new RequestSnapshot();
        reqSnapshot.setMethod(entity.getHttpMethod());
        reqSnapshot.setUri(entity.getRequestUri());
        reqSnapshot.setHeaders(headerMap);
        reqSnapshot.setBody(bodyContent);

        ErrorSnapshot errSnapshot = new ErrorSnapshot();
        errSnapshot.setStatusCode(entity.getStatusCode() != null ? entity.getStatusCode() : 500);
        errSnapshot.setExceptionClass(entity.getExceptionClass());
        errSnapshot.setMessage(entity.getExceptionMessage());
        errSnapshot.setStackTrace(entity.getStackTrace());

        IncidentEvent incidentEvent = new IncidentEvent();
        incidentEvent.setIncidentId(entity.getIncidentId());
        incidentEvent.setTraceId(entity.getTraceId());
        incidentEvent.setRequest(reqSnapshot);
        incidentEvent.setError(errSnapshot);

        GeneratedTestArtifact artifact = jUnitTestGenerator.generateTest(incidentEvent, options);

        try {
            TestArtifactEntity dbEntity = TestArtifactEntity.builder()
                    .artifactId(artifact.getArtifactId())
                    .incidentId(incidentId)
                    .framework(artifact.getFramework())
                    .packageName(artifact.getPackageName())
                    .className(artifact.getClassName())
                    .fileName(artifact.getFileName())
                    .codeContent(artifact.getCodeContent())
                    .createdAt(artifact.getCreatedAt())
                    .build();

            testArtifactRepository.save(dbEntity);
            log.info("Saved TestArtifactEntity {} for incident {}", artifact.getArtifactId(), incidentId);
        } catch (Exception e) {
            log.warn("Failed to persist TestArtifactEntity to DB: {}", e.getMessage());
        }

        return artifact;
    }

    public List<TestArtifactEntity> getArtifactsByIncidentId(String incidentId) {
        return testArtifactRepository.findByIncidentIdOrderByCreatedAtDesc(incidentId);
    }
}
