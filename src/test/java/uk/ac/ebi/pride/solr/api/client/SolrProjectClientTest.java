package uk.ac.ebi.pride.solr.api.client;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.pride.solr.commons.PrideSolrProject;

import java.io.IOException;
import java.util.Arrays;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
class SolrProjectClientTest {

    @Autowired
    private SolrProjectClient solrProjectClient;

    @Test
    public void contextLoad() throws IOException {
    }


    //@Test
    void testAll() throws IOException {

        //findAllAccessions
        Assert.assertEquals(9816, solrProjectClient.findAllAccessions().get().size());

        //findAllIds
        Assert.assertEquals(9816, solrProjectClient.findAllIds().get().size());

        //findByAccession
        PrideSolrProject prideSolrProject = solrProjectClient.findByAccession("PXD006197").get();
        prideSolrProject.setAccession("randomAccession");
        String originalId = (String) prideSolrProject.getId();
        prideSolrProject.setId("randomId");

        //Save
        solrProjectClient.save(prideSolrProject);
        PrideSolrProject randonmProject = solrProjectClient.findByAccession("randomAccession").get();
        Assert.assertEquals("randomAccession", randonmProject.getAccession());


        //Update
        randonmProject.setAccession("updateAccession");
        PrideSolrProject updateProject = solrProjectClient.update(randonmProject);
        Assert.assertEquals("updateAccession", updateProject.getAccession());

        //Upsert
        randonmProject.setAccession("upsertAccession");
        randonmProject.setId("upsertId");
        PrideSolrProject upsertProject = solrProjectClient.upsert(randonmProject);
        Assert.assertEquals("upsertAccession", upsertProject.getAccession());

        //delete
        solrProjectClient.deleteProjectById("upsertId");
        solrProjectClient.deleteProjectById("randomId");
        solrProjectClient.saveAll(Arrays.asList(updateProject, randonmProject));
        solrProjectClient.deleteProjectById("upsertId");
        solrProjectClient.deleteProjectById("randomId");
        Assert.assertFalse(solrProjectClient.findByAccession("randomAccession").isPresent());
        Assert.assertFalse(solrProjectClient.findByAccession("updateAccession").isPresent());
        Assert.assertFalse(solrProjectClient.findByAccession("upsertAccession").isPresent());
    }
}