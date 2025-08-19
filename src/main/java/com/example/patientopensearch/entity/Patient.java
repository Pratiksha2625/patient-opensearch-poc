package com.example.patientopensearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patients")
public class Patient {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Column(name = "f_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    @Column(name = "l_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(?i)(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    @Column(name = "sex", nullable = false)
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @Email
    @Size(max = 50)
    private String email;

    @Size(max = 20)
    private String phone;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatientProvider> patientProviders;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatientOrganization> patientOrganizations;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatientPortal> patientPortals;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
