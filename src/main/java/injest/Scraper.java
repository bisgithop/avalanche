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
        String newsString =null;
        String siteName = "https://news.google.com/home?hl=en-IN&gl=IN&ceid=IN:en";
        List<URI> targets = Arrays.asList(
                new URI(siteName));
        HttpClient client = HttpClient.newHttpClient();
        List<CompletableFuture<String>> futures = targets.stream()
                .map(target -> client
                        .sendAsync(
                                HttpRequest.newBuilder(target).GET().build(),
                                HttpResponse.BodyHandlers.ofString())
                        .thenApply(response -> response.body()))
                .collect(Collectors.toList());

        Thread.sleep(1000);
        for (CompletableFuture<String> news : futures) {
            newsString  = news.get();
            System.out.println(newsString);
        }

        try {
             siteName = (siteName.replace("http://", "")).replace("www.", "").replace(".", "_").replace("/", "_").replace("https://","");

            SimpleDateFormat format = new SimpleDateFormat("_dd-MM-yyyy-hh-mm");
            String DateToStr = format.format(new Date());
            String fileName =  "D:/site/upload/data/current/" + siteName + DateToStr + ".xml";
            System.out.println("CreatingFile:" + fileName);


            File file = new File(fileName);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(newsString);
            bw.close();

        }catch ( Exception e2) {
            e2.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception{
        Scraper scraper = new Scraper();
        //scraper.Scrape();
        Set news = scraper.readFile("D:/site/upload/data/current/bbc_27-05-2024-12-01.xml");
        Iterator it = news.iterator();
        while( it.hasNext()){
            System.out.println("News:" + (String)it.next());
        }

    }



    private Set readFile(String fileName)
    {
        Set list = null;
        try {

            BufferedReader buff = new BufferedReader( new InputStreamReader(new FileInputStream(fileName)));
            String str = null;
            StringBuilder strbuff = new StringBuilder();

            while( (str = buff.readLine()) != null )
            {
                strbuff.append(str);
            }
            str = strbuff.toString();
            System.out.println("String size:" + str.length());
            list = HTMLParsers.moveForward(str);
            System.out.println("Size of news loaded from File:"+list.size());

        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();

        }
        return list;
    }

}
