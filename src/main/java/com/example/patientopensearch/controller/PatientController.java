package com.example.patientopensearch.controller;

import com.example.patientopensearch.entity.Patient;
import com.example.patientopensearch.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    @Autowired
    private RestTemplate restTemplate;
    private final PatientService patientService;
    private final String OPENSEARCH_URL = "http://localhost:9200/patients_v1/_search?size=100&pretty";



    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    @GetMapping
    public ResponseEntity<String> getAllPatients() {
        String body = "{ \"query\": { \"match_all\": {} } }";
        String result = restTemplate.postForObject(OPENSEARCH_URL, body, String.class);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id)  {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
