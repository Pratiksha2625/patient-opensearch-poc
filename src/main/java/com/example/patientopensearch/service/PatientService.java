package com.example.patientopensearch.service;

import com.example.patientopensearch.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    Patient savePatient(Patient patient);
    List<Patient> getAllPatients();
    Patient getPatientById(Long id); // ID type matches repository
    void deletePatient(Long id);
}

