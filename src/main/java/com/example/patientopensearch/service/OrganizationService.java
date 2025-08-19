package com.example.patientopensearch.service;

import com.example.patientopensearch.entity.Organization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationService {
    List<Organization> getAllOrganizations();
    Optional<Organization> getOrganizationById(UUID id);
    Organization createOrganization(Organization organization);
    Organization updateOrganization(UUID id, Organization organization);
    void deleteOrganization(UUID id);
}
