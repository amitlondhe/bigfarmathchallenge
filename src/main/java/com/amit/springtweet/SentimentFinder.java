package com.amit.springtweet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SentimentFinder {

	public String getSentiments(JSONArray tweets) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<String> request = new HttpEntity<String>(tweets.toString(), headers);
		String response = restTemplate.postForObject("http://sentiment.vivekn.com/api/batch/", request, String.class);
		//System.out.println("Sentiment Response:" + response);
		return response;
	}

	public String getSentiment(String tweet) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("txt", tweet);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		String response = restTemplate.postForObject("http://sentiment.vivekn.com/api/text/", request, String.class);
		//System.out.println("Sentiment Response:" + response);
		return response;
	}
	
	public void rateAndStoreSentiment(String inputFileName,String outputFileName,String textFieldName) throws IOException {
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(inputFileName));
			String record = null;
			JSONArray inputArr = new JSONArray();
			JSONArray newsForSentiments = new JSONArray();
			int index = 0;
			while((record = reader.readLine()) != null) {
				JSONObject obj = new JSONObject(record);
				inputArr.put(index, obj);
				String news = obj.getString(textFieldName);
				newsForSentiments.put(index,news);
				index = index + 1;				
			}
			String temp = newsForSentiments.toString();
			System.out.println(temp);
			System.out.println("Sending " + newsForSentiments.length() + " news to find sentiments.");
			
			String response = getSentiments(newsForSentiments);
			if(response.contains("Bad JSON request")) {
				throw new RuntimeException("Error: "+ response);
			}
			System.out.println("Sentiment API response " + response);
			JSONArray newsWithSentiments = new JSONArray(response);
			System.out.println("Received " + newsWithSentiments.length() + " news sentiments.");
			if(newsForSentiments.length() != newsWithSentiments.length()) {
				throw new IllegalStateException("News sent for sentiment does not match with results returned.");
			}
			
			writer = new PrintWriter(new PrintWriter(outputFileName));
			for(int i=0;i < newsWithSentiments.length();i++) {
				JSONObject sentimentScore = (JSONObject) newsWithSentiments.get(i);
				JSONObject news = (JSONObject)inputArr.get(i);
				news.put("confidence",sentimentScore.getString("confidence"));
				news.put("result",sentimentScore.getString("result"));
				
				writer.println(news.toString());
			}

		} catch(Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		} finally {
			reader.close();
			writer.close();
			System.out.println("Done writing " + outputFileName + ".");
		}
		
	}

	public void rateAndStoreOneByOneSentiment(String inputFileName,String outputFileName,String textFieldName) throws IOException {
		BufferedReader reader = null;
		PrintWriter writer = null;
		int index = 0;
		String record = null;
		try {
			reader = new BufferedReader(new FileReader(inputFileName));
			writer = new PrintWriter(new FileWriter(outputFileName,true));
			while((record = reader.readLine()) != null) {
				JSONObject obj = new JSONObject(record);
				String news = obj.getString(textFieldName);
				String response = getSentiment(news);
				JSONObject responseObj = new JSONObject(response);
				String sentiment = responseObj.getJSONObject("result").getString("sentiment");
				String confidence = responseObj.getJSONObject("result").getString("confidence");
				obj.put("sentiment", sentiment);
				obj.put("confidence", confidence);
				writer.println(obj.toString());
				
				if(index%100 == 0) {
					System.out.println("Processed " + index + " records.");
				}
				index = index + 1;
			}
		} catch(Exception e) {
			System.out.println("Error processing record '" + record + "' due to " + e.getMessage());
			e.printStackTrace();
		} finally {
			reader.close();
			writer.close();
			System.out.println("Done writing " + outputFileName + ".");
		}
		
	}

}
