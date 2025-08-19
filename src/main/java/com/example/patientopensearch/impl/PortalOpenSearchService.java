package com.example.patientopensearch.impl;

import com.example.patientopensearch.search.IndexNames;
import com.example.patientopensearch.search.doc.PortalDocument;
import com.example.patientopensearch.service.OpenSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.search.HighlightField;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortalOpenSearchService implements OpenSearchService<PortalDocument, UUID> {

    private final OpenSearchClient client;
    private final ObjectMapper objectMapper;
    private static final String INDEX = IndexNames.PORTALS;

    @Override
    public void index(PortalDocument document) {
        try {
            client.index(i -> i.index(INDEX)
                    .id(document.getId().toString())
                    .document(document));
        } catch (Exception e) {
            throw new RuntimeException("Error indexing portal document", e);
        }
    }

    @Override
    public void bulkIndex(List<PortalDocument> documents) {
        try {
            List<BulkOperation> ops = documents.stream()
                    .map(doc -> BulkOperation.of(b -> b
                            .index(ix -> ix.index(INDEX)
                                    .id(doc.getId().toString())
                                    .document(doc))
                    ))
                    .toList();

            BulkRequest br = new BulkRequest.Builder().operations(ops).build();
            BulkResponse resp = client.bulk(br);
            if (resp.errors()) {
                throw new RuntimeException("Bulk indexing had errors");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during bulk indexing", e);
        }
    }

    @Override
    public Optional<PortalDocument> findById(UUID id) {
        try {
            GetResponse<PortalDocument> resp = client.get(
                    g -> g.index(INDEX).id(id.toString()),
                    PortalDocument.class
            );
            return resp.found() ? Optional.ofNullable(resp.source()) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching by id", e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            client.delete(d -> d.index(INDEX).id(id.toString()));
        } catch (Exception e) {
            throw new RuntimeException("Error deleting portal doc", e);
        }
    }
    @Override
    public List<String> suggest(String prefix) {
        try {
            SearchRequest req = new SearchRequest.Builder()
                    .index(INDEX)
                    .size(5)
                    .query(q -> q.matchPhrasePrefix(mp -> mp.field("name.autocomplete").query(prefix)))
                    .build();

            SearchResponse<PortalDocument> resp = client.search(req, PortalDocument.class);
            return resp.hits().hits().stream()
                    .map(h -> h.source() != null ? h.source().getName() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .limit(5)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting suggestions", e);
        }
    }


    @Override
    public Page<PortalDocument> search(
            String query,
            boolean autocomplete,
            boolean fuzzy,
            List<String> fields,
            List<String> highlightFields,
            Pageable pageable,
            Map<String, String> sort
    ) {
        Query q = fuzzy ? multiMatchQuery(query, true) : multiMatchQuery(query, false);

        // If autocomplete requested, adjust the query to use autocomplete fields
        if (autocomplete && fields != null && !fields.isEmpty()) {
            q = new Query.Builder().multiMatch(mm -> mm
                    .query(query)
                    .fields(fields.stream()
                            .map(f -> f + ".autocomplete")
                            .toList())
            ).build();
        }

        // Prepare highlighting
        Map<String, HighlightField> highlightMap = null;
        if (highlightFields != null && !highlightFields.isEmpty()) {
            highlightMap = highlightFields.stream()
                    .collect(Collectors.toMap(
                            f -> f,
                            f -> new HighlightField.Builder().build()
                    ));
        }

        return doSearch(q, pageable, highlightMap, sort);
    }

    private Query multiMatchQuery(String text, boolean fuzzy) {
        return new Query.Builder().multiMatch(mm -> {
            mm.query(text)
                    .fields("name^1.0", "description^0.5", "name.autocomplete^2.0");
            if (fuzzy) {
                mm.fuzziness("AUTO");
            }
            return mm;
        }).build();
    }

    private Page<PortalDocument> doSearch(
            Query query,
            Pageable pageable,
            Map<String, HighlightField> highlightFields,
            Map<String, String> sort
    ) {
        try {
            SearchRequest.Builder sb = new SearchRequest.Builder()
                    .index(INDEX)
                    .from(pageable.getPageNumber() * pageable.getPageSize())
                    .size(pageable.getPageSize())
                    .query(query);

            // Apply sorting
            if (sort != null && !sort.isEmpty()) {
                sort.forEach((field, dir) -> {
                    sb.sort(SortOptions.of(s -> s.field(f -> f.field(mapSortField(field))
                            .order("asc".equalsIgnoreCase(dir) ? SortOrder.Asc : SortOrder.Desc))));
                });
            }

            // Apply highlighting
            if (highlightFields != null && !highlightFields.isEmpty()) {
                sb.highlight(h -> {
                    highlightFields.forEach(h::fields);
                    return h;
                });
            }

            SearchResponse<PortalDocument> resp = client.search(sb.build(), PortalDocument.class);

            List<PortalDocument> data = new ArrayList<>();
            for (Hit<PortalDocument> hit : resp.hits().hits()) {
                PortalDocument doc = hit.source();
                if (doc == null) continue;

                if (hit.highlight() != null && !hit.highlight().isEmpty()) {
                    doc.setHighlights(new HashMap<>(hit.highlight()));
                }
                data.add(doc);
            }

            long total = resp.hits().total() != null ? resp.hits().total().value() : data.size();
            return new PageImpl<>(data, pageable, total);
        } catch (Exception e) {
            throw new RuntimeException("Search failed", e);
        }
    }

    private String mapSortField(String property) {
        if ("name".equals(property)) return "name.keyword";
        if ("description".equals(property)) return "description";
        return property;
    }
}
