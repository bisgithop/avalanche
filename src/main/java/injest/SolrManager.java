package injest;


import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SolrManager {

    static Logger  logger = Logger.getLogger("SolrManager");
    final static String urlString = "http://localhost:8983/solr/new_core";
    static SolrClient solrClient;
    private SolrManager(){
        solrClient =  new Http2SolrClient.Builder(urlString).build();
    }

    private static SolrManager solrManager;

    public static SolrManager getInstance(){
        if ( solrManager == null) {
            solrManager = new SolrManager();
        }
        return solrManager;
    }

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

    public static boolean upload(Long siteId, String rowId, String rowData, Long id, String site, String label) throws SolrServerException, IOException, NoSuchAlgorithmException
    {

        // check duplicates before uploading
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFilterQuery("rowid_s:" + rowId);
        QueryResponse response =  solrClient.query(query);
        SolrDocumentList results = response.getResults();
        if ( rowData == null) {
            System.out.println("UPLOADING NULLL...");
            return false;
        }
        rowData = rowData.trim();
        if(results !=null && results.size() > 0) {
            String str1 = rowData.replace(":", "*");
            String str2 = null;
            if( Pattern.compile("\\s").matcher(str1).find()) {
                str2 = str1.replace(" ", "*");
            }
            SolrQuery query2 = new SolrQuery();
            query2.setQuery("*:*");
            query2.addFilterQuery("row_s:*" + str2 + "*");
            query2.addFilterQuery("rowid_s:" + rowId);
            SolrDocumentList results2 = null;
            try {
                QueryResponse response2 =  solrClient.query(query2);
                results2 = response2.getResults();
            }
            catch(Exception e) {
                System.out.println("Exception while checking:" + str2);
                //don't index
                return false;
            }
            if(results2 !=null && results2.size() > 0) {
                System.out.println("RowData/Site already exist:" + rowData  );
                return false;
            }
        }
        SolrInputDocument doc1 = new SolrInputDocument();
        doc1.addField("id", id);
        doc1.addField("siteid_l", siteId);
        doc1.addField("rowid_s", rowId);
        doc1.addField("row_s", rowData);
        doc1.addField("junk_i", 0);
        doc1.addField("created_dt", new Date());
        doc1.addField("site_s", site);
        doc1.addField("labels_ss", label);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        doc1.addField( "date_s",date );
        doc1.addField( "time_l",System.currentTimeMillis());
        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        docs.add(doc1);
        solrClient.add(docs);
        solrClient.commit();
        System.out.println("Indexed:"+rowData);
        return true;
    }


    public void uploadAll(Map data) {
        Set keys = data.keySet();
        Iterator it = keys.iterator();
        int i = 0;
        while (it.hasNext()) {
            String site = (String) it.next();
            Set dataSet = (Set) data.get(site);
            Iterator dataRows = dataSet.iterator();
            while (dataRows.hasNext()) {
                String dataRow = (String) dataRows.next();
                System.out.println("Indexing-Count:" + (i++));
                String[] row = dataRow.split(":");
                try {
                    upload(SiteIndex.getIndexLong(site), row[0], row[1], System.currentTimeMillis(), site, "web");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("Exception in uploading");
                }
            }

        }
    }



}

