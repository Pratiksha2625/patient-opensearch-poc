package com.example.patientopensearch.search.mapper;

import com.example.patientopensearch.entity.Organization;
import com.example.patientopensearch.entity.Portal;
import com.example.patientopensearch.entity.PatientProvider;
import com.example.patientopensearch.search.doc.OrganizationDocument;
import com.example.patientopensearch.search.doc.PortalDocument;

public final class SearchMappers {
    private SearchMappers(){}

    public static PortalDocument toDoc(Portal p) {
        return PortalDocument.builder()
                .id(p.getId())
                .name(p.getName())
                .description(null)
                .build();
    }

    public static OrganizationDocument toDoc(Organization o) {
        return OrganizationDocument.builder()
                .id(o.getId())
                .name(o.getName())
                .build();
    }
}
