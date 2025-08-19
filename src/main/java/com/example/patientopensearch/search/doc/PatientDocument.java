package com.example.patientopensearch.search.doc;

import lombok.*;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDocument {
    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String gender;
    private String dob;
    private String email;
    private String phone;
    private Boolean isActive;
    private Address address;
    private OrganizationLite organization;
    private List<ProviderLite> providers;
    private List<PortalLite> portals;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
    private Map<String, List<String>> highlights;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Address {
        private String street; private String city; private String state; private String zip;
    }
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OrganizationLite { private UUID id; private String name; }
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ProviderLite { private UUID id; private String name; }
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PortalLite { private UUID id; private String name; private String url; }
}