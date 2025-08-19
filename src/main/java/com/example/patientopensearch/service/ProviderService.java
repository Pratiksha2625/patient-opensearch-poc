package com.example.patientopensearch.service;

import com.example.patientopensearch.entity.Provider;

import java.util.List;
import java.util.UUID;

public interface ProviderService {
    List<Provider> getAllProviders();
    Provider getProviderById(UUID id);
    Provider createProvider(Provider provider);
    Provider updateProvider(UUID id, Provider provider);
    void deleteProvider(UUID id);
}
