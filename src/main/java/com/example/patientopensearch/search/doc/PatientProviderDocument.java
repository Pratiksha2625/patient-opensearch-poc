package com.example.patientopensearch.search.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProviderDocument {
    private UUID id;
    private String patientId;
    private String providerId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
