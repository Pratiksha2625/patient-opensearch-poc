package com.example.patientopensearch.controller;

import com.example.patientopensearch.search.doc.PatientDocument;
import com.example.patientopensearch.impl.PatientOpenSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientSearchController {
    private final PatientOpenSearchService patientService;

    @GetMapping("/search")
    public ResponseEntity<Page<PatientDocument>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "false") boolean autocomplete,
            @RequestParam(defaultValue = "true") boolean fuzzy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> fields,
            @RequestParam(required = false) List<String> highlightFields,
            @RequestParam(required = false) Map<String, String> sort
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientDocument> result = patientService.search(q, autocomplete, fuzzy, fields, highlightFields, pageable, sort);
        return ResponseEntity.ok(result);
    }
}