package com.example.patientopensearch.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OpenSearchService<T, ID> {

    void index(T doc);

    void bulkIndex(List<T> docs);

    Optional<T> findById(ID id);

    void delete(ID id);

    Page<T> search(
            String query,
            boolean autocomplete,
            boolean fuzzy,
            List<String> fields,
            List<String> highlightFields,
            Pageable pageable,
            java.util.Map<String, String> sort
    );
    List<String> suggest(String prefix);
}
