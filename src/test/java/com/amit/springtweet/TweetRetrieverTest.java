package com.amit.springtweet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TweetRetrieverTest extends TestCase {
	
	public void testTemp() throws ParseException {
		String temp = "RT Bibek @Amvit #Debroy #demonetisation Thrashes http://t.co/asdge324 Karan Thapar for https://t.co/5cT23TRMjc Making Irresponsible Comment on Demonetization! ";
		Pattern p1 = Pattern.compile("http[s]{0,1}://[/\\w\\.]*|@\\w*|^RT|#\\w*");
		Matcher m = p1.matcher(temp);
		while(m.find()) {
			System.out.println(m.group());
		}
		
		
		String str = temp.replaceAll("http[s]{0,1}://[/\\w\\.]*|@\\w*|^RT|#\\w*", "");
		System.out.println(str);
		
		//Date d = new Date("Wed Mar 22 10:15:12 EDT 2017");
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = inputDateFormat.parse("2016-11-26 09:05:38");
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		System.out.println(format.format(d));
	}
	
	public void testMassageTweets() throws IOException {
		TweetRetriever retriever = new TweetRetriever();
		String inputDateFormat = "yyyy-MM-dd HH:mm:ss";
		Map<String,Integer> columns = new HashMap<String,Integer>();
		
		columns.put("id", 0);
		columns.put("text", 1);
		columns.put("created", 2);
		retriever.massageTweets("tweetsNov2016.csv", "tweetsNov2016-massaged.json", columns,inputDateFormat);
		
		/*
		columns.put("id", 0);
		columns.put("created", 1);
		columns.put("text", 2);
		retriever.massageTweets("tweets-Mar212017-Mar272017.csv", "tweets-Mar212017-Mar272017-massaged.json", columns,null);
	    */
	}
	
    public static Test suite() {
        return new TestSuite( TweetRetrieverTest.class );
    }
}
