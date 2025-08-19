package com.example.patientopensearch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient_portals")
public class PatientPortal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "portal_id", nullable = false)
    private Portal portal;

    @Column(name = "portal_name")
    private String portalName;

    @Column(name = "date_added", updatable = false)
    private OffsetDateTime dateAdded;

    @PrePersist
    public void prePersist() {
        this.dateAdded = OffsetDateTime.now();
    }
}
