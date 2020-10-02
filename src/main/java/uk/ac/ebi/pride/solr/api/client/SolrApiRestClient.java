package uk.ac.ebi.pride.solr.api.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

/**
 * This class handles all the GET, POST, PUT, DELETE requests to the Solr API
 */
@Slf4j
class SolrApiRestClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKeyName;
    private final String apiKeyValue;
    private final String appName;

    /**
     * Constructor
     *
     * @param baseUrl     Solr REST API base URL
     * @param apiKeyName  API key
     * @param apiKeyValue API secret
     * @param appName     The name of APP that is calling these REST APIs. For Logging & Debug purposes.
     */
    SolrApiRestClient(String baseUrl, String apiKeyName, String apiKeyValue, String appName) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKeyName = apiKeyName;
        this.apiKeyValue = apiKeyValue;
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        this.appName = appName;

    }

    public String sendPostRequest(String url, String payload) {
        url = baseUrl + url;
        ResponseEntity<String> response;
        try {
            //  create headers
            HttpHeaders headers = createHeaders();

            // build the request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(payload, headers);
            log.info("POST Request : " + url);
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            HttpStatus statusCode = response.getStatusCode();
            if (statusCode != HttpStatus.OK && statusCode != HttpStatus.CREATED && statusCode != HttpStatus.ACCEPTED) {
                String errorMessage = "[POST] Received invalid response for : " + url + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (RestClientException e) {
            log.info("POST Request payload : " + payload);
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }

    public String sendDeleteRequest(String url, MultiValueMap<String, String> queryParams) {
        ResponseEntity<String> response;
        try {
            //  create headers
            HttpHeaders headers = createHeaders();

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + url);
            if (queryParams != null) {
                uriBuilder.queryParams(queryParams);
            }
            URI completeUrl = uriBuilder.build().toUri();

            // build the request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(headers);

            log.info("DELETE Request : " + completeUrl);
            response = restTemplate.exchange(completeUrl, HttpMethod.DELETE, requestEntity, String.class);

            HttpStatus statusCode = response.getStatusCode();
            if (statusCode != HttpStatus.NO_CONTENT) {
                String errorMessage = "[DELETE] Received invalid response for : " + completeUrl + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }

    /**
     * This method construct the URL with URI parameters and Query parameters and
     * perform a get call
     * //TODO retry logics
     *
     * @param url         Path after the base URL
     * @param uriParams   URI parameters
     * @param queryParams Query parameters
     * @return JSON object in String format
     */
    public String sendGetRequestWithRetry(String url, Map<String, String> uriParams, MultiValueMap<String, String> queryParams) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + url);
        if (queryParams != null) {
            uriBuilder.queryParams(queryParams);
        }
        URI completeUrl = (uriParams != null) ? uriBuilder.buildAndExpand(uriParams).toUri() : uriBuilder.build().toUri();

        return makeGetRequest(completeUrl);
    }


    /**
     * This method sets HTTP headers, perform the rest call and returns results in String format
     *
     * @param uri constructed URL with URI and query parameters
     * @return
     */
    private String makeGetRequest(URI uri) {
        ResponseEntity<String> response;
        try {
            //  create headers
            HttpHeaders headers = createHeaders();

            // build the request
            HttpEntity entity = new HttpEntity(headers);

            log.info("GET Request : " + uri);
            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            HttpStatus statusCode = response.getStatusCode();
            if (statusCode != HttpStatus.OK && statusCode != HttpStatus.CREATED && statusCode != HttpStatus.ACCEPTED) {
                String errorMessage = "[GET] Received invalid response for : " + uri + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }

    public String sendPostRequestWithJwtAuthorization(String url, String payload, String jwtToken) {
        url = baseUrl + url;
        ResponseEntity<String> response;
        try {
            //  create headers
            HttpHeaders headers = createHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);

            // build the request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(payload, headers);

            log.info("Post Request With Jwt: " + url);
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            HttpStatus statusCode = response.getStatusCode();
            if (statusCode != HttpStatus.OK && statusCode != HttpStatus.CREATED && statusCode != HttpStatus.ACCEPTED) {
                String errorMessage = "[POST] Received invalid response for : " + url + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }

    public String sendPostRequestForFindByKeyword(String url, String payload,
                                                  MultiValueMap<String, String> queryParams) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + url);
        if (queryParams != null) {
            uriBuilder.queryParams(queryParams);
        }
        URI completeUrl = uriBuilder.build().toUri();
        ResponseEntity<String> response;

        try {
            // build the request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(payload,
                    createHeaders());
            response = restTemplate.exchange(completeUrl, HttpMethod.POST, requestEntity, String.class);

            HttpStatus statusCode = response.getStatusCode();
            if (statusCode != HttpStatus.OK && statusCode != HttpStatus.CREATED && statusCode != HttpStatus.ACCEPTED) {
                String errorMessage = "[POST] Received invalid response for : " + url + " : " + response;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        } catch (RestClientException e) {
            log.info("POST Request payload : " + payload);
            log.error(e.getMessage(), e);
            throw e;
        }
        return response.getBody();
    }


    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.set(apiKeyName, apiKeyValue);
        headers.set("app", appName);
        return headers;
    }
}
