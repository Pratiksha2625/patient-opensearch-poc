package com.example.patientopensearch.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Configuration
public class IndexManager {
    private final OpenSearchClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public IndexManager(OpenSearchClient client) {
        this.client = client;
    }

    @Bean
    ApplicationRunner createIndicesIfMissing() {
        return args -> {
            ensureIndex(IndexNames.PATIENTS, "opensearch/patients_v1.json");
            ensureIndex(IndexNames.PORTALS, "opensearch/portals_v1.json");
            ensureIndex(IndexNames.ORGANIZATIONS, "opensearch/organizations_v1.json");
            ensureIndex(IndexNames.PATIENT_PROVIDERS, "opensearch/patient_providers_v1.json");
        };
    }

    private void ensureIndex(String index, String classpathJson) throws Exception {
        BooleanResponse exists = client.indices().exists(ExistsRequest.of(e -> e.index(index)));
        if (exists.value()) {
            log.info("Index '{}' already exists", index);
            return;
        }

        String body = readResource(classpathJson);
        Map<String, Object> jsonMap = mapper.readValue(body, Map.class);

        CreateIndexRequest.Builder builder = new CreateIndexRequest.Builder().index(index);

        if (jsonMap.containsKey("settings")) {
            String settingsJson = mapper.writeValueAsString(jsonMap.get("settings"));
            JsonParser parser = JsonProvider.provider().createParser(new StringReader(settingsJson));
            builder.settings(IndexSettings._DESERIALIZER.deserialize(parser, new JacksonJsonpMapper()));
        }

        if (jsonMap.containsKey("mappings")) {
            String mappingsJson = mapper.writeValueAsString(jsonMap.get("mappings"));
            JsonParser parser = JsonProvider.provider().createParser(new StringReader(mappingsJson));
            builder.mappings(TypeMapping._DESERIALIZER.deserialize(parser, new JacksonJsonpMapper()));
        }

        client.indices().create(builder.build());
        log.info("Index '{}' created", index);
    }

    private String readResource(String path) throws Exception {
        ClassPathResource res = new ClassPathResource(path);
        try (InputStream is = res.getInputStream();
             Scanner sc = new Scanner(is, StandardCharsets.UTF_8)) {
            sc.useDelimiter("\\A");
            return sc.hasNext() ? sc.next() : "";
        }
    }
}
