package injest;


import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
//import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;



import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class SolrManager {


    public static void main(String[] args) throws IOException, SolrServerException, NoSuchAlgorithmException {

        String indexString = "Test message with HttpClient http version 2";
        String urlString = "http://localhost:8983/solr/new_core";
        SolrClient solr2 = new Http2SolrClient.Builder(urlString).build();
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "629334545980436356");
        document.addField("c_s", indexString);
        document.addField("j_i", "0");
        document.addField("Date_dt", new Date());
        document.addField("n_l", 1234);
        UpdateResponse response = solr2.add(document);
        solr2.commit();

        /*
         SolrManager sm = new SolrManager(urlString);
         sm.delete();
         */


    }





}
