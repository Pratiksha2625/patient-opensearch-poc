package com.example.patientopensearch.service;

import com.example.patientopensearch.entity.Patient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Service
public class PatientSearchService {

    private final OpenSearchClient client;
    private static final String INDEX_NAME = "patients";

    public PatientSearchService(OpenSearchClient client) {
        this.client = client;
    }

    public void indexPatient(Patient patient) {
        try {
            client.index(IndexRequest.of(i -> i
                    .index(INDEX_NAME)
                    .id(patient.getId().toString())
                    .document(patient)
            ));
        } catch (IOException e) {
            throw new RuntimeException("Error indexing patient to OpenSearch", e);
        }
    }
}
