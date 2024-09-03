package uk.ac.ebi.pride.solr.api.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ebi.pride.solr.commons.PrideSolrProject;

import java.io.IOException;
import java.util.Arrays;


@SpringBootTest(classes = TestConfig.class)
class SolrProjectClientTest {

    @Autowired
    private SolrProjectClient solrProjectClient;

    @Test
    public void contextLoad() throws IOException {
    }


    @Test
    void testAll() throws IOException, InterruptedException {

        //findAllAccessions
        Assertions.assertEquals(9816, solrProjectClient.findAllAccessions().get().size());

        //findAllIds
        Assertions.assertEquals(9816, solrProjectClient.findAllIds().get().size());

        //findByAccession
        PrideSolrProject prideSolrProject = solrProjectClient.findByAccession("PXD006197").get();
        prideSolrProject.setAccession("randomAccession");
        String originalId = (String) prideSolrProject.getId();
        prideSolrProject.setId("randomId");

        //Save
        solrProjectClient.save(prideSolrProject);
        PrideSolrProject randonmProject = solrProjectClient.findByAccession("randomAccession").get();
        Assertions.assertEquals("randomAccession", randonmProject.getAccession());


        //Update
        randonmProject.setAccession("updateAccession");
        PrideSolrProject updateProject = solrProjectClient.update(randonmProject);
        Assertions.assertEquals("updateAccession", updateProject.getAccession());

        //Upsert
        randonmProject.setAccession("upsertAccession");
        randonmProject.setId("upsertId");
        PrideSolrProject upsertProject = solrProjectClient.upsert(randonmProject);
        Assertions.assertEquals("upsertAccession", upsertProject.getAccession());

        //delete
        solrProjectClient.deleteProjectById("upsertId");
        solrProjectClient.deleteProjectById("randomId");
        solrProjectClient.saveAll(Arrays.asList(updateProject, randonmProject));
        solrProjectClient.deleteProjectById("upsertId");
        solrProjectClient.deleteProjectById("randomId");
        Assertions.assertFalse(solrProjectClient.findByAccession("randomAccession").isPresent());
        Assertions.assertFalse(solrProjectClient.findByAccession("updateAccession").isPresent());
        Assertions.assertFalse(solrProjectClient.findByAccession("upsertAccession").isPresent());
    }
}