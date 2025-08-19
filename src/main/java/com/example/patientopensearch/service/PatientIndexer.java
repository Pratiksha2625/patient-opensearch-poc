package com.example.patientopensearch.service;

import com.example.patientopensearch.entity.*;
import com.example.patientopensearch.impl.PatientOpenSearchService;
import com.example.patientopensearch.search.doc.PatientDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientIndexer {
    private final PatientOpenSearchService openSearchService;

    public void index(Patient patient) {
        PatientDocument doc = toDocument(patient);
        openSearchService.index(doc);
    }

    private PatientDocument toDocument(Patient p) {
        PatientDocument.Address addr = null;

        List<PatientDocument.ProviderLite> providers = new ArrayList<>();
        if (p.getPatientProviders() != null) {
            p.getPatientProviders().forEach(pp -> providers.add(
                    PatientDocument.ProviderLite.builder()
                            .id(pp.getProvider().getId())
                            .name(pp.getProvider().getName())
                            .build()
            ));
        }

        List<PatientDocument.PortalLite> portals = new ArrayList<>();
        if (p.getPatientPortals() != null) {
            p.getPatientPortals().forEach(pp -> portals.add(
                    PatientDocument.PortalLite.builder()
                            .id(pp.getPortal().getId())
                            .name(pp.getPortal().getName())
                            .url(null)
                            .build()
            ));
        }

        PatientDocument.OrganizationLite orgLite = null;
        if (p.getPatientOrganizations() != null && !p.getPatientOrganizations().isEmpty()) {
            PatientOrganization po = p.getPatientOrganizations().iterator().next();
            if (po != null && po.getOrganization() != null) {
                orgLite = PatientDocument.OrganizationLite.builder()
                        .id(po.getOrganization().getId())
                        .name(po.getOrganization().getName())
                        .build();
            }
        }

        String fullName = (p.getFirstName() + " " + p.getLastName()).trim();

        return PatientDocument.builder()
                .id(UUID.randomUUID()) // external doc id; could also use patient id if UUID
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .fullName(fullName)
                .gender(p.getGender())
                .dob(p.getDob() != null ? p.getDob().toString() : null)
                .email(p.getEmail())
                .phone(p.getPhone())
                .isActive(p.getIsActive())
                .address(addr)
                .organization(orgLite)
                .providers(providers)
                .portals(portals)
                .tags(Collections.emptyList())
                .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : OffsetDateTime.now().toString())
                .updatedAt(p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : OffsetDateTime.now().toString())
                .build();
    }
}
