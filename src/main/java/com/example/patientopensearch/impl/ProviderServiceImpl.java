package com.example.patientopensearch.impl;

import com.example.patientopensearch.entity.Provider;
import com.example.patientopensearch.repository.ProviderRepository;
import com.example.patientopensearch.service.ProviderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderServiceImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    @Override
    public Provider getProviderById(UUID id) {
        return providerRepository.findById(id).orElseThrow(() -> new RuntimeException("Provider not found"));
    }

    @Override
    public Provider createProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    @Override
    public Provider updateProvider(UUID id, Provider provider) {
        Provider existing = providerRepository.findById(id).orElseThrow(() -> new RuntimeException("Provider not found"));
        existing.setName(provider.getName());
        return providerRepository.save(existing);
    }

    @Override
    public void deleteProvider(UUID id) {
        providerRepository.deleteById(id);
    }
}
