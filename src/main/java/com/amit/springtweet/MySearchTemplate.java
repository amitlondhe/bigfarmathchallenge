package com.amit.springtweet;


import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.social.support.URIBuilder;
import org.springframework.social.twitter.api.SavedSearch;
import org.springframework.social.twitter.api.SearchOperations;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Trends;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class MySearchTemplate implements SearchOperations {
	SearchOperations searchOperations;
	RestTemplate restTemplate;
	
	public MySearchTemplate(TwitterTemplate twitterTemplate) {
		this.searchOperations = twitterTemplate.searchOperations();
		this.restTemplate = twitterTemplate.getRestTemplate();
	}

	public SearchResults search(String query) {
		return searchOperations.search(query);
	}

	public SearchResults search(String query, int pageSize) {
		return searchOperations.search(query,pageSize);
	}

	public SearchResults search(String query, int pageSize, long sinceId, long maxId) {
		return  searchOperations.search(query,pageSize,sinceId,maxId);
	}

	public SearchResults search(SearchParameters searchParameters) {
		Assert.notNull(searchParameters);
		MultiValueMap<String, String> parameters = buildQueryParametersFromSearchParameters(searchParameters);
		parameters.set("tweet_mode","extended to the url");
		URI url = URIBuilder.fromUri("https://api.twitter.com/1.1/search/tweets.json").queryParams(parameters).build();
		return restTemplate.getForObject(url,SearchResults.class);
	}

	public List<SavedSearch> getSavedSearches() {
		// TODO Auto-generated method stub
		return null;
	}

	public SavedSearch getSavedSearch(long searchId) {
		// TODO Auto-generated method stub
		return null;
	}

	public SavedSearch createSavedSearch(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteSavedSearch(long searchId) {
		// TODO Auto-generated method stub
		
	}

	public Trends getLocalTrends(long whereOnEarthId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Trends getLocalTrends(long whereOnEarthId, boolean excludeHashtags) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static MultiValueMap<String, String> buildQueryParametersFromSearchParameters(SearchParameters searchParameters) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set("q", searchParameters.getQuery());
		if (searchParameters.getGeoCode() != null) {
			parameters.set("geocode", searchParameters.getGeoCode().toString());
		}
		if (searchParameters.getLang() != null) {
			parameters.set("lang", searchParameters.getLang());
		}
		if (searchParameters.getLocale() != null) {
			parameters.set("locale", searchParameters.getLocale());
		}
		if (searchParameters.getResultType() != null) {
			parameters.set("result_type", searchParameters.getResultType().toString());
		}
		parameters.set("count", searchParameters.getCount() != null ? String.valueOf(searchParameters.getCount()) : String.valueOf(DEFAULT_RESULTS_PER_PAGE));
		if (searchParameters.getUntil() != null) {
			parameters.set("until", new SimpleDateFormat("yyyy-MM-dd").format(searchParameters.getUntil()));
		}
		if (searchParameters.getSinceId() != null) {
			parameters.set("since_id", String.valueOf(searchParameters.getSinceId()));
		}
		if (searchParameters.getMaxId() != null) {
			parameters.set("max_id", String.valueOf(searchParameters.getMaxId()));
		}
		if (!searchParameters.isIncludeEntities()) {
			parameters.set("include_entities", "false");
		}
		return parameters;
	}

	public static final int DEFAULT_RESULTS_PER_PAGE = 50;

}
