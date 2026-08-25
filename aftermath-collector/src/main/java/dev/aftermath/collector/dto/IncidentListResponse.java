package dev.aftermath.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentListResponse {
    private List<IncidentResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
