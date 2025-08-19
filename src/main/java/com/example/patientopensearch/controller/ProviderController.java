package com.example.patientopensearch.controller;

import com.example.patientopensearch.entity.Provider;
import com.example.patientopensearch.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
        return ResponseEntity.ok(providerService.createProvider(provider));
    }

    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable UUID id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(@PathVariable UUID id, @RequestBody Provider provider) {
        return ResponseEntity.ok(providerService.updateProvider(id, provider));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProvider(@PathVariable UUID id) {
        providerService.deleteProvider(id);
        return ResponseEntity.ok("Provider deleted successfully");
    }
}
