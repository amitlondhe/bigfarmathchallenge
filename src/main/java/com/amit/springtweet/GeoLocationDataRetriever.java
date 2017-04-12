package com.amit.springtweet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class GeoLocationDataRetriever {

	/*
	 * REST Key - l11k9zlpmev5al4e1r58aqlaqvw4np98
	 * https://apis.mapmyindia.com/advancedmaps/v1/l11k9zlpmev5al4e1r58aqlaqvw4np98/nearby_search?lat=19.020782&lng=73.028693&code=FINATM&page=1
	 */
	public List<PointOfInterest> getNearbyATMs(String latitude,String longitude,int pagenum) throws Exception {		
		String url = "http://apis.mapmyindia.com/advancedmaps/v1/<<your apikey here>>/"
				+ "nearby_search?lat=" + latitude +"&lng="+ longitude +"&code=FINATM&page="+pagenum; 
				
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		String response = restTemplate.getForObject(url, String.class);
		System.out.println("Nearby ATM Response:" + response);		
		return PointOfInterest.parse(response);
	}
	
	public Map<String,List<PointOfInterest>> getNearByATMs(Map<String,String> places,int howManyPages) throws Exception {
		Map<String,List<PointOfInterest>> atmMap = new HashMap<String,List<PointOfInterest>>();
		
		for(Map.Entry<String, String> entry:places.entrySet()) {
			List<PointOfInterest> atms = new ArrayList<PointOfInterest>();
			String place = entry.getKey();			
			String[] latlong = entry.getValue().split(",");
			for(int page=1;page <= howManyPages ; page++) {
				List<PointOfInterest> pois = getNearbyATMs(latlong[0], latlong[1],page);
				atms.addAll(pois);
			}
			atmMap.put(place, atms);
		}
		return atmMap;
	}

}
