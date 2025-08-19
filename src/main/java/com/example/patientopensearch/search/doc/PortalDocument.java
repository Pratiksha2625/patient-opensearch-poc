package com.example.patientopensearch.search.doc;

import lombok.*;
import java.util.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PortalDocument {
    private UUID id;
    private String name;
    private String description;
    private Map<String, List<String>> highlights;
}
