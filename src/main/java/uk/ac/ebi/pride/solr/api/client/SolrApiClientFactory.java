package uk.ac.ebi.pride.solr.api.client;

public class SolrApiClientFactory {

    private final SolrApiRestClient solrApiRestClient;
    private SolrProjectClient solrProjectClient = null;

    /**
     * @param apiBaseUrl  API base url of Repo-WS
     * @param apiKeyName  Name of API key
     * @param apiKeyValue Value of API key
     * @param appName     The name of APP that is initiating this. For Logging & Debug purposes.
     */
    public SolrApiClientFactory(String apiBaseUrl, String apiKeyName, String apiKeyValue, String appName) {
        this.solrApiRestClient = new SolrApiRestClient(apiBaseUrl, apiKeyName, apiKeyValue, appName);
    }

    public SolrProjectClient getSolrProjectClient() {
        if (solrProjectClient == null) {
            this.solrProjectClient = new SolrProjectClient(solrApiRestClient);
        }
        return solrProjectClient;
    }
}
