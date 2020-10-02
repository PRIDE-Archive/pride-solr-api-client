package uk.ac.ebi.pride.solr.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Value("${solr.api.baseUrl}")
    private String baseUrl;

    @Value("${solr.api.key.name}")
    private String keyName;

    @Value("${solr.api.key.value}")
    private String keyValue;

    @Value("${solr.api.appName}")
    private String appName;

    public SolrApiRestClient solrApiRestClient() {
        return new SolrApiRestClient(baseUrl, keyName, keyValue, appName);
    }

    @Bean
    public SolrProjectClient solrProjectClient() {
        return new SolrProjectClient(solrApiRestClient());
    }

}
