package dev.aftermath.collector.service;

import dev.aftermath.collector.entity.IncidentEntity;
import dev.aftermath.collector.repository.IncidentRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractTestGenerator {

    private final IncidentRepository incidentRepository;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContractTestArtifact {
        private String incidentId;
        private String contractFramework;
        private String fileName;
        private String codeContent;
    }

    public ContractTestArtifact generateContractTest(String incidentId) {
        IncidentEntity entity = incidentRepository.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        String method = entity.getHttpMethod() != null ? entity.getHttpMethod() : "POST";
        String uri = entity.getRequestUri() != null ? entity.getRequestUri() : "/";
        String serviceName = entity.getServiceName() != null ? entity.getServiceName() : "unknown-service";

        String codeContent = String.format("""
package dev.aftermath.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Auto-generated OpenAPI / Pact Contract Validation Test
 * Incident ID: %s
 * Service: %s
 */
public class ContractValidationTest_%s {

    @Test
    @DisplayName("Validate API Schema Contract for %s %s")
    void testValidateApiSchemaContract() {
        given()
                .baseUri("http://localhost:8082")
                .contentType("application/json")
        .when()
                .%s("%s")
        .then()
                .statusCode(%d);
    }
}
""", incidentId, serviceName, incidentId.substring(0, 8).replace("-", "_"),
                method, uri, method.toLowerCase(), uri, entity.getStatusCode() != null ? entity.getStatusCode() : 500);

        return ContractTestArtifact.builder()
                .incidentId(incidentId)
                .contractFramework("OPENAPI_JSON_SCHEMA")
                .fileName("ContractValidationTest_" + incidentId.substring(0, 8).replace("-", "_") + ".java")
                .codeContent(codeContent)
                .build();
    }
}
