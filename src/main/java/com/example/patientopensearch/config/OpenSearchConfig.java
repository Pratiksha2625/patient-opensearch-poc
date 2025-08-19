package com.example.patientopensearch.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.hosts:localhost:9200}")
    private String hostsCsv;

    @Value("${opensearch.scheme:http}")
    private String scheme;

    @Value("${opensearch.username:}")
    private String username;

    @Value("${opensearch.password:}")
    private String password;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OpenSearchClient openSearchClient(ObjectMapper mapper) {
        List<HttpHost> hosts = Arrays.stream(hostsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(h -> {
                    String[] parts = h.split(":");
                    String host = parts[0];
                    int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9200;
                    return new HttpHost(host, port, scheme);
                })
                .toList();

        RestClientBuilder builder = RestClient.builder(hosts.toArray(new HttpHost[0]))
                .setRequestConfigCallback(req -> req
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000))
                .setHttpClientConfigCallback(http -> {
                    http.setMaxConnTotal(100).setMaxConnPerRoute(100);
                    if (username != null && !username.isBlank()) {
                        BasicCredentialsProvider creds = new BasicCredentialsProvider();
                        creds.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                        http.setDefaultCredentialsProvider(creds);
                    }
                    return http;
                });

        RestClient lowLevel = builder.build();

        RestClientTransport transport = new RestClientTransport(lowLevel, new JacksonJsonpMapper(mapper));

        return new OpenSearchClient(transport);
    }
}
