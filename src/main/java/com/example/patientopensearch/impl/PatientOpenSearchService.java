package com.example.patientopensearch.impl;

import com.example.patientopensearch.service.OpenSearchService;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TextQueryType;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;

import com.example.patientopensearch.search.IndexNames;
import com.example.patientopensearch.search.doc.PatientDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientOpenSearchService implements OpenSearchService<PatientDocument, UUID> {
    private final OpenSearchClient client;

    @Override
    public void index(PatientDocument doc) {
        try {
            client.index(i -> i.index(IndexNames.PATIENTS).id(String.valueOf(doc.getId())).document(doc));
        } catch (Exception e) {
            throw new RuntimeException("Indexing failed", e);
        }
    }

    @Override
    public void bulkIndex(List<PatientDocument> docs) {
        try {
            List<BulkOperation> ops = docs.stream().map(d -> BulkOperation.of(b -> b
                    .index(idx -> idx.index(IndexNames.PATIENTS).id(String.valueOf(d.getId())).document(d)))).toList();
            client.bulk(b -> b.index(IndexNames.PATIENTS).operations(ops));
        } catch (Exception e) {
            throw new RuntimeException("Bulk indexing failed", e);
        }
    }

    @Override
    public Optional<PatientDocument> findById(UUID id) {
        try {
            GetResponse<PatientDocument> r = client.get(g -> g.index(IndexNames.PATIENTS).id(id.toString()), PatientDocument.class);
            return r.found() ? Optional.ofNullable(r.source()) : Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Get failed", e);
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            client.delete(d -> d.index(IndexNames.PATIENTS).id(id.toString()));
        } catch (Exception e) {
            throw new RuntimeException("Delete failed", e);
        }
    }
    public Page<PatientDocument> search(
            String query,
            boolean autocomplete,
            boolean fuzzy,
            List<String> fields,
            List<String> highlightFields,
            Pageable pageable,
            Map<String, String> sort
    ) {
        try {
            Query q = buildQuery(query, autocomplete, fuzzy, fields);

            List<SortOptions> sortOpts = new ArrayList<>();
            if (sort != null) {
                for (Map.Entry<String, String> e : sort.entrySet()) {
                    String field = mapSortField(e.getKey());
                    SortOrder order = "desc".equalsIgnoreCase(e.getValue()) ? SortOrder.Desc : SortOrder.Asc;
                    sortOpts.add(SortOptions.of(s -> s.field(f -> f.field(field).order(order))));
                }
            }

            SearchResponse<PatientDocument> sr = client.search(s -> {
                s.index(IndexNames.PATIENTS)
                        .query(q)
                        .from((int) pageable.getOffset())
                        .size(pageable.getPageSize());
                if (!sortOpts.isEmpty()) s.sort(sortOpts);
                return s;
            }, PatientDocument.class);

            long total = sr.hits().total() != null ? sr.hits().total().value() : sr.hits().hits().size();
            List<PatientDocument> out = sr.hits().hits().stream()
                    .map(h -> h.source())
                    .filter(Objects::nonNull)
                    .toList();

            return new PageImpl<>(out, pageable, total);
        } catch (Exception e) {
            throw new RuntimeException("Search failed", e);
        }
    }

    private Query buildQuery(String q, boolean autocomplete, boolean fuzzy, List<String> fields) {
        if (q == null || q.isBlank()) {
            return QueryBuilders.matchAll().build()._toQuery();
        }

        List<String> defaultFields = Arrays.asList(
                "fullName", "firstName", "lastName",
                "email", "phone",
                "organization.name",
                "providers.name",
                "portals.name",
                "address.street", "address.city", "address.state", "address.zip",
                "all"
        );

        List<String> f = (fields == null || fields.isEmpty()) ? defaultFields : fields;

        MultiMatchQuery.Builder mm = new MultiMatchQuery.Builder()
                .query(q)
                .fields(f)
                .type(TextQueryType.BestFields);

        if (fuzzy) {
            mm.fuzziness("AUTO").prefixLength(1);
        }

        return mm.build()._toQuery();
    }

    @Override
    public List<String> suggest(String prefix) {
        return Collections.emptyList();
    }


    private String mapSortField(String field) {
        Set<String> textish = new HashSet<>(Arrays.asList(
                "firstName", "lastName", "fullName",
                "organization.name", "address.city"
        ));
        if (textish.contains(field)) return field + ".keyword";
        return field;
    }

    @PostConstruct
    void ensureIndexWithDateMappings() {
        try {
            var exists = client.indices().exists(e -> e.index(IndexNames.PATIENTS));
            if (!exists.value()) {
                client.indices().create(c -> c
                    .index(IndexNames.PATIENTS)
                    .mappings(m -> m
                        .properties("dob", p -> p.date(d -> d.format("strict_date||epoch_millis||epoch_second")))
                        // If you also store these as dates, keep the same format; otherwise remove them.
                        .properties("createdAt", p -> p.date(d -> d.format("strict_date||epoch_millis||epoch_second")))
                        .properties("updatedAt", p -> p.date(d -> d.format("strict_date||epoch_millis||epoch_second")))
                    )
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure index and date mappings for patients", e);
        }
    }
}