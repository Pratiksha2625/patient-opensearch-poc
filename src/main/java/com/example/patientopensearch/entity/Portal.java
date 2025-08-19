package com.example.patientopensearch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "portals")
public class Portal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "portal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatientPortal> patientPortals;
}
