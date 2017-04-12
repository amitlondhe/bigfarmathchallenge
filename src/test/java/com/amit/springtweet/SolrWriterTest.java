package com.amit.springtweet;

import junit.framework.TestCase;

public class SolrWriterTest extends TestCase {

	public void pushTweetsToSolr() throws Exception {
		SolrWriter solrWriter = new SolrWriter();
		solrWriter.addDocumentsToSolr("tweets","tweetsNov2016-sentiments.json");
		solrWriter.addDocumentsToSolr("tweets","tweets-Mar212017-Mar272017-sentiments.json");
	}
	
	public void pushNewsToSolr() throws Exception {
		SolrWriter solrWriter = new SolrWriter();
		solrWriter.addDocumentsToSolr("news","C:/casnc/mystuff/data/news/newsbydates-hindu-sentiments.json");
		solrWriter.addDocumentsToSolr("news","C:/casnc/mystuff/data/news/newsbydates-news18-sentiments.json");
		solrWriter.addDocumentsToSolr("news","C:/casnc/mystuff/data/news/newsbydates-indiatoday-sentiments.json");
	}

}
