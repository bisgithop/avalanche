package injest;



import java.io.BufferedReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTMLParsers {

	public static String StripDash(String single)
	{
		if( single.contains("-") && (single.contains("/") || single.contains("\\"))) {
    		System.out.println("Not appropriate String:" + single);
    		return null;
    	}    		
    	else{
    		single.replace("<", "");
        	single.replace(">", "");
    		return single;
    	}				
	}

	
	/**
	 * @param str
	 * @return
	 */
	public static Set moveForward( String str)
	{
		Set list = new HashSet<String>();
		int hash =0;
		int progress =0;
		
		progress = getText(str.substring(hash + progress,str.length()),list);
		while( progress > 0 )
		{			
			progress = getText(str.substring(hash,str.length()), list);
		//	System.out.println("Progress:" + progress + " Hash:" + hash + " Lenght:" + str.length());
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hash = hash + progress;						
		}
		return list;
	}


	private static AtomicLong idCounter = new AtomicLong();
	public static String createID()
	{
		return String.valueOf(idCounter.getAndIncrement());
	}

	public static int getText( String single2, Set list)
	{
		
		Pattern pattern1 = Pattern.compile(">");
		Pattern pattern2 =  Pattern.compile("<");	
		Matcher matcher1 =  pattern1.matcher(single2);
		Matcher matcher2 =  pattern2.matcher(single2);
		int progress = 0; 
		if(matcher1.find() && matcher2.find())
		{					
			if (matcher2.end() - matcher1.end() > 15 )
			{
				String sentence = single2.substring(matcher1.end(), matcher2.end()-1);
				if( sentence.contains(" ") && 
						!(sentence.contains("{") || sentence.contains("loadAds(")
								|| sentence.contains("||")
								|| sentence.contains("document.")
								|| sentence.contains("Object()")
								|| sentence.contains("window.")
								|| sentence.contains("_")
								|| sentence.contains("~")
								||sentence.trim().length() < 10
								|| sentence.contains("=\"\";")))
				{	
					
					if ( Pattern.matches("See all.*.sources.*", sentence)) {
						System.out.println("skipping:" + sentence);
					}
					else if(sentence.split(" ") != null && sentence.split(" ").length < 4)
					{
						System.out.println("skipping:" + sentence);
					}					
					else if(Pattern.matches("^[0-9][0-9]\\s[a-zA-Z0-9,]+\\s[0-9]+,\\s[0-9]+\\shrs\\sIST", sentence))
					{
						System.out.println("skipping:" + sentence);
					}
					else if(sentence.contains("| Last Updated:"))
					{
						System.out.println("skipping:" + sentence);
					}
					else if(Pattern.matches("all.*.news.*.articles.*", sentence))
					{
						System.out.println("skipping:" + sentence);
					}
					else if(Pattern.matches("Updated.*.mins.*.ago.*", sentence))
					{
						System.out.println("skipping:" + sentence);
					}
					else if(Pattern.matches("Updated.*.minutes.*.ago.*", sentence))
					{
						System.out.println("skipping:" + sentence);
					}
					else
					{
					
					
					long sentenceId = -1l;
					try {

						//sentenceId = SiteIndex.getIndexLong(sentence);
						sentenceId =  idCounter.getAndIncrement();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					list.add(sentenceId+":"+sentence);		
					
					}
				}			
			}
		progress = matcher2.end();	
		}	
		return progress;	
	}
	

	static String single;
	public static void main(String[] args) {
		BufferedReader buff;

		
	}
	

	
}
