package com.example.patientopensearch.search.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDocument {
    private UUID id;
    private String name;
    private List<String> suggestTerms; // not used by index now, but you can keep for UI
    private Set<String> relatedPatientNames;
}
