package com.example.patientopensearch.controller;

import com.example.patientopensearch.impl.PortalOpenSearchService;
import com.example.patientopensearch.search.doc.PortalDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/portals")
public class PortalController {

    private final PortalOpenSearchService service;

    public PortalController(PortalOpenSearchService service) {
        this.service = service;
    }

    @PostMapping
    public String indexPortal(@RequestBody PortalDocument portal) {
        if (portal.getId() == null) {
            portal.setId(UUID.randomUUID());
        }
        service.index(portal);
        return "Indexed portal with ID: " + portal.getId();
    }

    @PostMapping("/bulk")
    public String bulkIndex(@RequestBody List<PortalDocument> portals) {
        portals.forEach(p -> {
            if (p.getId() == null) p.setId(UUID.randomUUID());
        });
        service.bulkIndex(portals);
        return "Bulk indexed " + portals.size() + " portals.";
    }

    @GetMapping("/{id}")
    public PortalDocument getPortal(@PathVariable UUID id) {
        return service.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deletePortal(@PathVariable UUID id) {
        service.delete(id);
        return "Deleted portal with ID: " + id;
    }

    @GetMapping("/search")
    public Page<PortalDocument> search(@RequestParam String query,
                                       @RequestParam(defaultValue = "false") boolean autocomplete,
                                       @RequestParam(defaultValue = "false") boolean fuzzy,
                                       @RequestParam(required = false) List<String> fields,
                                       @RequestParam(required = false) List<String> highlightFields,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {

        return service.search(
                query,
                autocomplete,
                fuzzy,
                fields != null ? fields : List.of("name", "description"),
                highlightFields != null ? highlightFields : List.of(),
                PageRequest.of(page, size),
                Map.of("name", "asc") // example sorting
        );
    }

    @GetMapping("/fuzzy")
    public Page<PortalDocument> fuzzySearch(@RequestParam String query,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return service.search(
                query,
                false, // autocomplete
                true,  // fuzzy
                List.of("name", "description"), // search fields
                List.of(), // highlight fields
                PageRequest.of(page, size),
                Map.of("name", "asc") // sort by name ascending
        );
    }

    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam String prefix) {
        return service.suggest(prefix);
    }
}
