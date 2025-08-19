package com.example.patientopensearch.impl;

import com.example.patientopensearch.entity.Organization;
import com.example.patientopensearch.repository.OrganizationRepository;
import com.example.patientopensearch.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    public Optional<Organization> getOrganizationById(UUID id) {
        return organizationRepository.findById(id);
    }

    @Override
    public Organization createOrganization(Organization organization) {
        // ensure JPA will generate id
        organization.setId(null);
        return organizationRepository.save(organization);
    }

    @Override
    public Organization updateOrganization(UUID id, Organization organization) {
        return organizationRepository.findById(id).map(existing -> {
            existing.setName(organization.getName());
            return organizationRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Organization not found with id " + id));
    }

    @Override
    public void deleteOrganization(UUID id) {
        organizationRepository.deleteById(id);
    }
}
