package com.amit.springtweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.web.client.RestTemplate;

public class TweetRetriever {
	static String consumerKey = "<<your consumerKey here>>";
	static String consumerSecret = "<<your consumerSecret here>>";
	static String accessToken = "<<your accessToken here>>";
	static String accessTokenSecret = "<<your accessTokenSecret here>>";

	private final static String API_PATH = "https://api.twitter.com/1.1/search/tweets.json";

	private static void getTweetsUsingREST(TwitterTemplate twitter) {
		String query = "?q=demonetisation&count=100&lang=en" + "&tweet_mode=extended&geocode=19.076,72.8777,50km";
		String url = API_PATH + query;

		int counter = 0;

		do {
			String response = execute(url);
			MySearchResults results = processResponse(response);
			processTweets(results.getTweets(), true);

			if (results.hasNextUrl()) {
				String nextPageUrl = results.getNextPageUrl();
				System.out.println("NextPageURL:" + nextPageUrl);
				int start = nextPageUrl.indexOf("max_id");
				int end = nextPageUrl.indexOf("&", start);
				String maxId = nextPageUrl.substring(start, end);
				url = API_PATH + query + "&" + maxId;
			} else {
				System.out.println("Results does not have Next Page URL {nextPageUrl:" + results.getNextPageUrl()
						+ ", maxId:" + results.getMax_id() + ", sinceId:" + results.getSince_id() + "}");
				System.out.println("Response received : " + response);
				break;
			}
			counter++;
			if (counter % 3 == 0) {
				try {
					System.out.println("Sleeping for a minute");
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} while (counter <= 10);
	}

	private static String execute(String url) {
		System.out.println("Twitter Endpoint:" + url);
		TwitterTemplate twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		RestTemplate restTemplate = twitter.getRestTemplate();
		String response = restTemplate.getForObject(url, String.class);
		return response;
	}

	private static MySearchResults processResponse(String response) {
		MySearchResults results = new MySearchResults();
		List<Tweet> tweets = new ArrayList<Tweet>();

		try {
			JSONObject responseJson = new JSONObject(response);
			JSONArray statuses = responseJson.getJSONArray("statuses");
			// Metadata
			JSONObject searchMetadata = responseJson.getJSONObject("search_metadata");
			long maxId = searchMetadata.optLong("max_id");
			long sinceId = searchMetadata.optLong("since_id");
			String nextUrl = searchMetadata.optString("next_results");

			// Tweets
			for (int i = 0; i < statuses.length(); i++) {
				JSONObject status = statuses.getJSONObject(i);
				long id = status.optLong("id");
				String text = status.optString("full_text");
				Date createdAt = new Date(status.optString("created_at"));
				JSONObject user = status.getJSONObject("user");
				String userName = user.optString("name");
				boolean isRetweeted = status.optBoolean("retweeted");
				int retweetCount = status.optInt("retweet_count");
				boolean isFavorited = status.optBoolean("favorited");
				int favoriteCount = status.optInt("favorite_count");
				Tweet t = new Tweet(id, text, createdAt, userName, null, null, 0, null, null);
				t.setRetweeted(isRetweeted);
				t.setRetweetCount(retweetCount);
				t.setFavorited(isFavorited);
				t.setFavoriteCount(favoriteCount);
				tweets.add(t);
			}
			results.setTweets(tweets);
			results.setMax_id(maxId);
			results.setSince_id(sinceId);
			results.setNextPageUrl(nextUrl);
		} catch (Exception e) {
			System.out.println("Could not load JSON");
		}
		return results;
	}

	private static void processTweets(List<Tweet> tweets, boolean append) {
		System.out.println("Processing " + tweets.size() + " tweets.");
		PrintWriter out = null;
		FileWriter writer = null;
		try {
			writer = new FileWriter("tweets.csv", append);
			out = new PrintWriter(writer);
			for (Tweet t : tweets) {
				String text = t.getUnmodifiedText().replaceAll("\r", "").replaceAll("\n", "");
				out.print(t.getId());
				out.print("$SEP$");
				out.print(t.getCreatedAt());
				out.print("$SEP$");
				out.print(text);
				out.println();
			}
		} catch (IOException ioe) {
			System.out.println("IOException occurred," + ioe.getMessage());
		} finally {
			try {
				out.flush();
				out.close();
				writer.close();
			} catch (Exception e) {
				System.out.println("Exception closing stream-" + e.getMessage());
			}
		}
	}

	private static void printTweet(Tweet tweet) {
		System.out.println(tweet.getText());
		System.out.println(tweet.getId());
		System.out.println(tweet.getCreatedAt());
	}

	private static void getTweetsForUser(Twitter twitter) {
		List<Tweet> tweets = twitter.timelineOperations().getUserTimeline("amvit");
		for (Tweet tweet : tweets) {
			printTweet(tweet);
		}
	}

	private static void readTweetsFromFiles(String rootdir) throws Exception {
		File rootdirFile = new File(rootdir);
		String[] fileNames = rootdirFile.list();
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("Good", 0);
		map.put("Bad", 0);
		for (String fileName : fileNames) {
			System.out.println("fileName:" + fileName);
			Map<String, Integer> result = readTweetsFromFile(rootdir + "/" + fileName);
			map.put("Good", map.get("Good") + result.get("Good"));
			map.put("Bad", map.get("Bad") + result.get("Bad"));
		}
		System.out.println("Total Good records processed:" + map.get("Good"));
		System.out.println("Total Bad records:" + map.get("Bad"));
	}

	private static Map<String, Integer> readTweetsFromFile(String fileName) throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		Pattern p = Pattern.compile("[^\\x00-\\x7F]");
		BufferedReader reader = null;
		PrintWriter out = null;
		FileWriter writer = null;
		int counter = 0;
		int badrecords = 0;
		try {

			writer = new FileWriter("tweetsNov2016.csv", true);
			out = new PrintWriter(writer);

			reader = new BufferedReader(new FileReader(fileName));
			String record = null;
			while ((record = reader.readLine()) != null) {

				String parts[] = record.split("\t");
				if (parts.length > 8) {
					String text = parts[0];
					boolean isRT = text.startsWith("\"RT ");
					boolean isNonEnglish = p.matcher(text).find();
					if (isRT || isNonEnglish) {
						continue;
					}
					text = text.replaceAll("\r", "").replaceAll("\n", "");
					String createdAt = parts[4];
					String id = parts[7];

					out.print(id + "$SEP$" + text + "$SEP$" + createdAt);
					out.println();
					counter++;
				} else {
					badrecords++;
					// System.out.println("record does not have number of
					// parts");
				}
			}
			System.out.println("Processed " + counter + "records" + " and " + badrecords + "bad records.");
		} finally {
			reader.close();
			out.close();
			map.put("Good", counter);
			map.put("Bad", badrecords);
		}
		return map;
	}
	
	
	public void massageTweets(String inputFileName,String outputFileName,Map<String,Integer> columns,String inDateFormat) throws IOException {
		BufferedReader reader = null;
		PrintWriter writer = null;
		int index = 0;
		String record = null;
		SimpleDateFormat inputDateFormat = null;
		if(inDateFormat != null) {
			inputDateFormat = new SimpleDateFormat(inDateFormat);
		}
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Set<String> ids = new HashSet<String>();
		try {
			reader = new BufferedReader(new FileReader(inputFileName));
			writer = new PrintWriter(new PrintWriter(outputFileName));
			int idIndex = columns.get("id");
			int textIndex = columns.get("text");
			int createdDateIndex = columns.get("created");
			
			while((record = reader.readLine()) != null) {
				if(record.isEmpty()) {
					continue;
				}
				String parts[] = record.split("\\$SEP\\$");
				String text = parts[textIndex];
				if(ids.contains(parts[idIndex])) {
					continue;
				}
				if(text.startsWith("RT")) {
					continue;
				}
				text = text.replaceAll("http[s]{0,1}://[/\\w\\.]*|@\\w*|#\\w*", "");
				
				if(!text.trim().isEmpty()) {
					Date d = null;
					if(inputDateFormat != null) {
						d = inputDateFormat.parse(parts[createdDateIndex]);
					} else {
						d = new Date(parts[createdDateIndex]);
					}
					String createdDate = outputDateFormat.format(d);
					JSONObject obj = new JSONObject();
					obj.put("id",parts[idIndex]);
					obj.put("text",text);
					obj.put("created",createdDate);
					writer.println(obj.toString());
					if(index%100 == 0) {
						System.out.println("Processed " + index + " records.");
					}
					ids.add(parts[idIndex]);
					index = index + 1;
				}
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
