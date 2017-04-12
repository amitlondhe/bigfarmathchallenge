package com.amit.springtweet;

import java.io.IOException;
import java.text.ParseException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NewsRetrieverTest extends TestCase {

	public void testGetNewsFeedForNews18() throws IOException {
		NewsRetriever newsRetriever = new NewsRetriever();
		newsRetriever.getAndStoreNewsFeedForNews18("news18.csv");
	}
	
	public void testGetAndStoreNewsFeedsForHindu() throws IOException, ParseException {
		NewsRetriever newsRetriever = new NewsRetriever();
		newsRetriever.getAndStoreNewsFeedsForHindu("newsbydates.csv");
	}
	
	public void testGetAndStoreNewsFeedsForIndiaToday() throws ParseException, IOException {
		NewsRetriever newsRetriever = new NewsRetriever();
		newsRetriever.getAndStoreNewsFeedsForIndiaToday("newsbydates-indiatoday.csv");		
	}
	
	public void testChangeDateFormat() throws Exception {
		NewsRetriever newsRetriever = new NewsRetriever();
		//newsRetriever.changeDateFormat("newsbydates-indiatoday.csv", "newsbydates-indiatoday.json", "dd-MM-yyyy");
		//newsRetriever.changeDateFormat("newsbydates.csv", "newsbydates-hindu.json", "yyyy/MM/dd");
	}
	
    public static Test suite() {
        return new TestSuite( NewsRetrieverTest.class );
    }
	
}
