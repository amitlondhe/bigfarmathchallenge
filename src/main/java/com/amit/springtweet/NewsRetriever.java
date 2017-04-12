package com.amit.springtweet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Retrieves the news from various news portals.
 * @author acl29
 *
 */
public class NewsRetriever {

	static Pattern p = Pattern.compile("demonetisation|demonetization|noteban|"
			+ "(replace.*note)|(.*note.*replace.*)|(note.*exchange)|(.*exchange.*note*)|"
			+ "(.*end of.*(500|1000).* note[s])");


	public Map<String, List<String>> getAndStoreNewsFeedForNews18(String fileName) throws IOException {
		Map<String, List<String>> news = new HashMap<String, List<String>>();
		List<String> failures = new ArrayList<String>();
		// There are 96 pages of search results
		for (int i = 96; i >= 1; i--) {
			String url = "http://www.news18.com/newstopics/demonetisation/news/page-" + i + "/";
			try {
				Document doc = Jsoup.connect(url).get();
				Elements parent = doc.select("div.search-listing > ul > li");
				Iterator<Element> children = parent.iterator();
				while (children.hasNext()) {
					Element child = children.next();
					Elements newsHeadline = child.select("h2 > a");
					Iterator<Element> iter = newsHeadline.iterator();
					if (iter.hasNext()) {
						List<String> al = new ArrayList<String>();
						Element newsItem = iter.next();
						String newsText = newsItem.text();
						Elements newsDate = child.select("span.post-date");
						String newsDateText = "Not Found";
						if (newsDate.size() > 0) {
							newsDateText = newsDate.get(0).text();
						}
						al.add(newsText);
						news.put(newsDateText, al);
					}
				}
				System.out.println("Found " + news.size() + " news @ " + url + ".");
				Thread.sleep(30000);
			} catch (Exception ioe) {
				System.out.println("Exception occured for url:" + url + "," + ioe.getMessage());
				// ioe.printStackTrace();
				failures.add(url);
			}
		}
		news.put("failures", failures);
		storeNewsByDates(news, fileName);
		return news;
	}

	public void getAndStoreNewsFeedsForHindu(String fileName) throws ParseException, IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = dateFormat.parse("2016/11/09");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		System.out.println(cal.getTime());
		Map<String, List<String>> newsByDates = new HashMap<String, List<String>>();
		for (int i = 0; i < 60; i++) {
			String newsDate = dateFormat.format(cal.getTime());
			List<String> news = getNewsFeed("http://www.thehindu.com/archive/web/" + newsDate, "ul.archive-list > li");
			System.out.println("Retrieved " + news.size() + " news for " + newsDate);
			newsByDates.put(newsDate, news);
			cal.add(Calendar.DATE, 1);
		}

		storeNewsByDates(newsByDates, fileName);
	}

	// http://indiatoday.intoday.in/calendar/9-11-2016/online.html
	public void getAndStoreNewsFeedsForIndiaToday(String fileName) throws ParseException, IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
		Date date = dateFormat.parse("9-11-2016");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		System.out.println(cal.getTime());
		Map<String, List<String>> newsByDates = new HashMap<String, List<String>>();
		for (int i = 0; i < 60; i++) {
			String newsDate = dateFormat.format(cal.getTime());
			List<String> news = getNewsFeed("http://indiatoday.intoday.in/calendar/" + newsDate + "/online.html",
					"div.secheadtext > a");
			System.out.println("Retrieved " + news.size() + " news for " + newsDate);
			newsByDates.put(newsDate, news);
			cal.add(Calendar.DATE, 1);
		}
		storeNewsByDates(newsByDates, fileName);
	}

	public void storeNewsByDates(Map<String, List<String>> newsByDates, String fileName) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(fileName, true));
		try {
			for (Map.Entry<String, List<String>> entry : newsByDates.entrySet()) {
				for (String news : entry.getValue()) {
					out.print(entry.getKey());
					out.print("$SEP$");
					out.print(news);
					out.println();
				}
			}
		} catch (Exception e) {
			System.out.println("Error saving the news to " + fileName + " due to " + e.getMessage());
		} finally {
			out.close();
		}
	}

	public List<String> getNewsFeed(String url, String xpath) {
		List<String> news = new ArrayList<String>();
		try {
			Thread.sleep(30000);
			Document doc = Jsoup.connect(url).get();
			Elements newsHeadlines = doc.select(xpath);

			System.out.println(newsHeadlines.size());
			Iterator<Element> iterator = newsHeadlines.iterator();
			while (iterator.hasNext()) {
				Element headline = iterator.next();
				String newsHeadline = headline.text();
				if (p.matcher(newsHeadline).find()) {
					news.add(newsHeadline);
				}
			}
			System.out.println("Found " + news.size() + " news @ " + url + ".");
		} catch (Exception ioe) {
			System.out.println("Exception occured for url:" + url + "," + ioe.getMessage());
		}
		return news;
	}
	
	/**
	 * Used when we encounter some failures .
	 * @param dates
	 * @throws IOException
	 */
	public void getNewsFeedByDates(List<String> dates) throws IOException {
		Map<String, List<String>> newsByDates = new HashMap<String, List<String>>();
		for (String date : dates) {
			List<String> news = getNewsFeed("http://www.thehindu.com/archive/web/" + date, "ul.archive-list > li");
			newsByDates.put(date, news);
		}
		storeNewsByDates(newsByDates, "newsbydates.csv");
	}
	
	public void changeDateFormat(String inFileName,String outFileName,String inputDateFormat) throws Exception {
		BufferedReader reader = null;
		PrintWriter writer = null;
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {
			reader = new BufferedReader(new FileReader(inFileName));
			writer = new PrintWriter(new PrintWriter(outFileName));
			String record = null;
			while((record = reader.readLine()) != null) {
				JSONObject obj = new JSONObject(record);
				String date = obj.getString("date");
				SimpleDateFormat dateFormat = new SimpleDateFormat(inputDateFormat);								
				String outputDate = outputFormat.format(dateFormat.parse(date));
				obj.put("date", outputDate);
				writer.println(obj.toString());				
			}
		} catch(Exception e) {
			System.out.println("Error " + e.getMessage());
		} finally {
			reader.close();
			writer.close();
		}
	}
}
