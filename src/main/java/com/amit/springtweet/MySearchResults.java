package com.amit.springtweet;

import java.io.Serializable;
import java.util.List;

import org.springframework.social.twitter.api.Tweet;

public class MySearchResults implements Serializable {
	private static final long serialVersionUID = 1L;
	public List<Tweet> tweets;
	public String nextPageUrl;
	public long max_id;
	public long since_id;
	public boolean hasNextUrl;
	
	public boolean hasNextUrl() {
		return !this.nextPageUrl.trim().isEmpty();
	}

	public List<Tweet> getTweets() {
		return tweets;
	}
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	public String getNextPageUrl() {
		return nextPageUrl;
	}
	public void setNextPageUrl(String nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}
	public long getMax_id() {
		return max_id;
	}
	public void setMax_id(long max_id) {
		this.max_id = max_id;
	}
	public long getSince_id() {
		return since_id;
	}
	public void setSince_id(long since_id) {
		this.since_id = since_id;
	}
	

}
