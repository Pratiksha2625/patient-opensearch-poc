package com.example.patientopensearch.impl;

import com.example.patientopensearch.entity.Patient;
import com.example.patientopensearch.repository.PatientRepository;
import com.example.patientopensearch.service.PatientIndexer;
import com.example.patientopensearch.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientIndexer patientIndexer;

    @Override
    public Patient savePatient(Patient patient) {
        Patient saved = patientRepository.save(patient);
        patientIndexer.index(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() { return patientRepository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    @Override
    public void deletePatient(Long id) { patientRepository.deleteById(id); }
}
