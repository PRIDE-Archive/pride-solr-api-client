package uk.ac.ebi.pride.solr.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.ac.ebi.pride.solr.api.client.utils.Utils;
import uk.ac.ebi.pride.solr.commons.PrideSolrProject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class handles all the methods related to Project
 */
@Slf4j
public class SolrProjectClient {

    private final ObjectMapper objectMapper;
    private final SolrApiRestClient solrApiRestClient;

    private static final String PROJECT_URL_PATH = "/project";

    SolrProjectClient(SolrApiRestClient solrApiRestClient) {
        this.objectMapper = Utils.getJacksonObjectMapper();
        this.solrApiRestClient = solrApiRestClient;
    }

    public Optional<PrideSolrProject> findByAccession(String accession) throws IOException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/findByAccession";
        // set query parameters
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("accession", accession);

        String response = solrApiRestClient.sendGetRequestWithRetry(url, null, queryParams);
        if (response == null || response.equalsIgnoreCase("null") || response.trim().isEmpty()) {
            return Optional.empty();
        }
        PrideSolrProject prideSolrProject = objectMapper.readValue(response, PrideSolrProject.class);
        return Optional.ofNullable(prideSolrProject);
    }

    public void saveAll(List<PrideSolrProject> projects) throws JsonProcessingException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/saveAll";
        String payload = objectMapper.writeValueAsString(projects);
        solrApiRestClient.sendPostRequest(url, payload, 0);
    }

    public PrideSolrProject save(PrideSolrProject project) throws JsonProcessingException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/save";
        return postObjectWithUrl(project, url);
    }

    public PrideSolrProject update(PrideSolrProject project) throws JsonProcessingException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/update";
        return postObjectWithUrl(project, url);
    }

    public PrideSolrProject upsert(PrideSolrProject project) throws JsonProcessingException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/upsert";
        return postObjectWithUrl(project, url);
    }

    private PrideSolrProject postObjectWithUrl(PrideSolrProject project, String url) throws JsonProcessingException, InterruptedException {
        String payload = objectMapper.writeValueAsString(project);
        String response = solrApiRestClient.sendPostRequest(url, payload, 0);

        return objectMapper.readValue(response, PrideSolrProject.class);
    }

    public void deleteProjectById(String id) throws JsonProcessingException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/deleteProjectById";

        String payload = objectMapper.writeValueAsString(id);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("id", id);
        solrApiRestClient.sendDeleteRequest(url, queryParams, 0);
    }

    public void deleteAll() throws JsonProcessingException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/deleteAll";
        solrApiRestClient.sendDeleteRequest(url, null, 0);
    }

    public Optional<Set<String>> findAllAccessions() throws IOException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/findAllAccessions";
        return getRequestWithUrl(url);
    }

    public Optional<Set<String>> findAllIds() throws IOException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/findAllIds";
        return getRequestWithUrl(url);
    }

    public Optional<Set<String>> findProjectAccessionsWithEmptyFileNames() throws IOException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/findProjectAccessionsWithEmptyFileNames";
        return getRequestWithUrl(url);
    }

    public Optional<Set<String>> findProjectAccessionsWithEmptyPeptideSequencesOrProteinIdentifications() throws IOException, InterruptedException {
        final String url = PROJECT_URL_PATH + "/findProjectAccessionsWithEmptyPeptideSequencesOrProteinIdentifications";
        return getRequestWithUrl(url);

    }

    public Optional<Set<String>> getRequestWithUrl(String url) throws JsonProcessingException, InterruptedException {
        String response = solrApiRestClient.sendGetRequestWithRetry(url, null, null);
        if (response == null || response.equalsIgnoreCase("null") || response.trim().isEmpty()) {
            return Optional.empty();
        }
        Set<String> projectAccessionsOrIds = objectMapper.readValue(response, new TypeReference<Set<String>>() {
        });
        return Optional.ofNullable(projectAccessionsOrIds);
    }
}

