package com.example.patientopensearch.repository;

import com.example.patientopensearch.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
}
