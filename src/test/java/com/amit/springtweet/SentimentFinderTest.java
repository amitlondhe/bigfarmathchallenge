package com.amit.springtweet;

import org.json.JSONArray;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SentimentFinderTest extends TestCase {

	public void testGetSentimentForTweets() throws Exception{
		JSONArray arr = new JSONArray();
		arr.put("This is a sample good text");
		arr.put("This is a bad text");
		
		SentimentFinder sentimentFinder = new SentimentFinder();
		String response = sentimentFinder.getSentiments(arr);
		System.out.println(response);
	}
	
	public void testGetSentimentForTweet() throws Exception{
		SentimentFinder sentimentFinder = new SentimentFinder();
		String response = sentimentFinder.getSentiment("This is a single tweet");
		System.out.println(response);
	}
	
	public void testRateAndStoreSentiment() throws Exception{
		SentimentFinder sentimentFinder = new SentimentFinder();
		//sentimentFinder.rateAndStoreSentiment("newsbydates-news18.json", "news18-sentiments.json","news");
		//sentimentFinder.rateAndStoreSentiment("newsbydates-indiatoday.json", "newsbydates-indiatoday-sentiments.json","news");
		//sentimentFinder.rateAndStoreSentiment("newsbydates-hindu.json", "newsbydates-hindu-sentiments.json","news");
	}
	
	public void testRateAndStoreOneByOneSentiment() throws Exception {
		SentimentFinder sentimentFinder = new SentimentFinder();
		//sentimentFinder.rateAndStoreOneByOneSentiment("newsbydates-news18.json", "newsbydates-news18-sentiments.json","news");
		//sentimentFinder.rateAndStoreOneByOneSentiment("tweets-Mar212017-Mar272017-massaged.json", "tweets-Mar212017-Mar272017-sentiments.json","text");
		sentimentFinder.rateAndStoreOneByOneSentiment("tweetsNov2016-massaged.json", "tweetsNov2016-sentiments.json","text");

	}
    public static Test suite() {
        return new TestSuite( SentimentFinderTest.class );
    }
}
