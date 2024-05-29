package injest;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Scraper {

    public void Scrape() throws Exception {
     List<URI> list = new ArrayList<URI>();
        for ( String site : sites) {
            list.add(new URI(site));
        }
        HttpClient client = HttpClient.newHttpClient();
        List<CompletableFuture<String>> futures = list.stream()
                .map(target -> client
                        .sendAsync(
                                HttpRequest.newBuilder(target).GET().build(),
                                HttpResponse.BodyHandlers.ofString())
                        .thenApply(response -> response.body()))
                .collect(Collectors.toList());
        Thread.sleep(3000);
        int i = 0;
        for (CompletableFuture<String> news : futures) {
            System.out.println("Saving for:" + sites[i]);
            saveFile(sites[i], location, news.get());
            i++;
        }

    }


    public static void main(String[] args) throws Exception{
    String location = "D:/site/upload/data/current/";
        String[] sites = new String[]{
          "https://news.google.com/home?hl=en-IN&gl=IN&ceid=IN:en"
        };
        Scraper scraper = new Scraper();
        scraper.Scrape(sites, location);
        Thread.sleep(5000);
        Map data = scraper.read(location);
        SolrManager.getInstance().uploadAll(data);
    }



    private void saveFile(String siteName, String location,String content)
    {
        try {
            siteName = siteName.substring(0,20);
            siteName = (siteName.replace("https://", "")).replace("www.", "").replace(".", "_").replace("/", "_").replace("https://","").replace("?","");
            SimpleDateFormat format = new SimpleDateFormat("_dd-MM-yy-hh-mm");
            String DateToStr = format.format(new Date());
            String fileName =  location + siteName + DateToStr + ".xml";
            System.out.println("CreatingFile:" + fileName);
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        }catch ( Exception e2) {
            e2.printStackTrace();
        }
    }


    
     private Map read(String location) throws IOException {
        File directory = new File(location);
        File[] files = directory.listFiles();
        Map allSitesSet = new HashMap();
        if (files != null) {
            for (File file : files) {
                System.out.println("READING FILE FROM:" + file.getAbsolutePath());
                String shortFileName = file.getName().split("_")[0];
                BufferedReader buff = null;
                try {
                    buff = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
                    String str = null;
                    StringBuffer strbuff = new StringBuffer();
                    while( (str = buff.readLine()) != null ) {
                        strbuff.append(str);
                    }
                    str = strbuff.toString();
                    buff.close();
                    System.out.println("String size:" + str.length());
                    Set setFromSite = HTMLParsers.moveForward(str);
                    allSitesSet.put(shortFileName,setFromSite);
                    System.out.println("Size of news loaded from File:"+setFromSite.size());
                } catch (Exception e) {
                    System.out.println(e.toString());
                    e.printStackTrace();
                    if( buff !=null) buff.close();

                }
                finally {
                    try {
                        if ( buff != null) buff.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return allSitesSet;
    }


}
