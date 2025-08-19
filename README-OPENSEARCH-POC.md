# Patient Search PoC (OpenSearch)

This PoC demonstrates **patient** search with nested **organization**, **provider**, and **portal** data in a single index using OpenSearch. It implements:

- Case-insensitive (lowercase normalizers + folded analyzers)
- Autocomplete (edge n-grams + match_phrase_prefix)
- Fuzzy (fuzziness AUTO with prefix_length)
- Multi-field search (multi_match across name/email/phone/org/providers/portals/address)
- Highlighting (per-field highlighter output)
- Pagination + Sorting (from/size + .keyword sort mapping)

## Run OpenSearch locally

```bash
docker compose up -d
# OpenSearch: http://localhost:9200  (security disabled for PoC)
# Dashboards:  http://localhost:5601
```

## Configure Spring Boot

Edit `src/main/resources/application.yml` (already present) to point to `localhost:9200`. Security is disabled in docker-compose (PoC only).

## Start the app

```bash
./mvnw spring-boot:run
```

## Prepare index & sample data

The app will create the `patients_v1` index on startup via `IndexManager`. Seed sample docs:

```bash
curl -X POST 'http://localhost:8080/api/patients/index-sample'
```

## Search examples

```bash
# Basic multi-field search with fuzzy and highlights
curl 'http://localhost:8080/api/patients/search?q=jon do&fuzzy=true&highlightFields=fullName&highlightFields=organization.name'

# Autocomplete (uses edge n-grams)
curl 'http://localhost:8080/api/patients/search?q=ja&autocomplete=true&fuzzy=false'

# Pagination + sorting (case-insensitive due to .keyword with lowercase normalizer)
curl 'http://localhost:8080/api/patients/search?q=doe&page=0&size=5&sort=lastName:asc'

# Restrict fields
curl 'http://localhost:8080/api/patients/search?q=acme&fields=organization.name'
```

## Mapping

See `src/main/resources/opensearch/patients_v1.json` – includes analyzers, normalizers, nested `providers` and `portals`, and an `all` catch-all field with synonyms.
