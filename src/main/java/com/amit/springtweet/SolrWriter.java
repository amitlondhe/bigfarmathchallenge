package com.amit.springtweet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SolrWriter {

	public void addDocumentsToSolr(String collectionName,String inputPath) throws Exception {

		String url = "http://localhost:8983/solr/"+ collectionName + "/update?wt=json";
		RestTemplate rest = new RestTemplate();
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", "application/json");
		BufferedReader reader = null;
		PrintWriter logoutput = null;
		Set<JSONObject> failedRecords = new HashSet<JSONObject>();
		try {
			reader = new BufferedReader(new FileReader(inputPath));
			String record = null;
			int index = 0;
			while ((record = reader.readLine()) != null) {
				JSONObject obj = new JSONObject(record);
				if(!obj.has("id")) {
					obj.put("id",index + obj.hashCode());
				}
				JSONObject solrDoc = new JSONObject();
				solrDoc.put("doc", obj);
				solrDoc.put("boost", 1.0);
				solrDoc.put("overwrite", true);
				solrDoc.put("commitWithin", 1000);
				JSONObject solrAdd = new JSONObject();
				solrAdd.put("add", solrDoc);
				HttpEntity<String> request = new HttpEntity<String>(solrAdd.toString(), headers);
				String response = rest.postForObject(url, request, String.class);
				//System.out.println("response: " + response);

				if (response.contains("Bad Request")) {
					failedRecords.add(obj);
					continue;
				}

				index = index + 1;

				if (index % 100 == 0) {
					System.out.println("Finished adding " + index + " Solr documents to " + collectionName);
				}
			}
			if (!failedRecords.isEmpty()) {
				logoutput = new PrintWriter(new FileWriter("failed-records.log"));
				logoutput.println(failedRecords);
			}

		} catch (Exception e) {
			System.out.println("Exception occurred: " + e.getMessage());
		} finally {
			reader.close();
			if (logoutput != null) {
				logoutput.close();
			}
		}
	}

}
