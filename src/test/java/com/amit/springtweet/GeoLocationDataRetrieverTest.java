package com.amit.springtweet;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GeoLocationDataRetrieverTest extends TestCase {
	
	public void testGetAtmsNearHopspital() throws Exception {
		Map<String,String> hospitals = new HashMap<String,String>();
		hospitals.put("Apollo Hospitals - Navi Mumbai", "19.020782,73.028693" );		
		hospitals.put("Fortis Hiranandani Hospital","19.0834899,72.9958137");
		hospitals.put("Fortis Hospital","19.2375093,73.1233483999999");
		hospitals.put("Kokilaben Dhirubhai Ambani Hospital & Medical Research Institute","19.131221,72.8247739999999");
		hospitals.put("Global Hospitals","18.9995429,72.8402034000001");
		hospitals.put("Fortis Heart Failure Program Mulund","19.1619809,72.9418247");
		hospitals.put("SevenHills Hospital","19.1177246,72.8785998999999");
		hospitals.put("Kohinoor Hospital", "19.076212,72.8862086");
		hospitals.put("Wockhardt Hospitals, The Umrao IMSR","19.284424,72.8624170000001");
		testGetNearbyATMS(hospitals,"hospital");
	}
	
	public void testGetAtmsNearSchool() throws Exception {
		Map<String,String> schools = new HashMap<String,String>();
		schools.put("American School of Bombay", "19.0677569,72.8707673");
		schools.put("Dhirubhai Ambani International School", "19.06678,72.870619");
		schools.put("NES International School Mulund", "19.1798706,72.9396434");
		schools.put("Vibgyor High - Goregaon", "19.159477,72.835884");
		schools.put("Oberoi International School", "19.1692229,72.8665960000001");
		schools.put("Smt. Sulochanadevi Singhania School", "19.2078039,72.9661702");
		schools.put("Udayachal High School", "19.1021576,72.9280907");
		schools.put("Orchids The International School", "18.9563262,72.8366518");
		schools.put("Podar International School ICSE", "19.0135811,73.0104804");
		schools.put("Podar International School", "19.1207121,72.9161397");
		testGetNearbyATMS(schools,"school");
	}

	public void testGetNearbyATMS(Map<String,String> places,String poitype) throws Exception {
		GeoLocationDataRetriever retriever = new GeoLocationDataRetriever();
		Map<String,List<PointOfInterest>> pois = retriever.getNearByATMs(places,2);
		System.out.println(pois);		
		storeResults(places, pois,poitype);
	}

	private void storeResults(Map<String, String> places, Map<String, List<PointOfInterest>> pois,String poitype) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter("places.txt",true));
			for(Map.Entry<String,List<PointOfInterest>> poi: pois.entrySet()) {
				JSONObject obj = new JSONObject();
				obj.put(poitype, poi.getKey());
				// Lookup latlong for hospital from input map.
				String latlongHospital = places.get(poi.getKey());
				if(!StringUtils.isEmpty(latlongHospital)) {
					String[] latlong = latlongHospital.split(",");
					obj.put("latitude", latlong[0]);
					obj.put("longitude", latlong[1]);
				}
				JSONArray atms = new JSONArray(poi.getValue());
				obj.put("atms", atms);
				writer.println(obj.toString());
			}
		} catch (Exception e) {
			System.out.println("exception writing file " + e.getMessage());
		} finally {
			writer.close();
		}
	}
	
    public static Test suite() {
        return new TestSuite( GeoLocationDataRetrieverTest.class );
    }
}
