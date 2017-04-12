package com.amit.springtweet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeoLocationDataAnalyzer {

	public List<JSONObject> analyzeGeoLocationData(String fileName,POIType poitype,int radius) throws Exception {
		List<JSONObject> list = new ArrayList<JSONObject>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			// When file has lot of records we may need to have multiple geoJson objects per file.
			// for now one geojson per file
			JSONObject geoJson = new JSONObject();
			JSONArray features = new JSONArray();
			
			while((line= reader.readLine())!=null) {
				JSONObject record = new JSONObject(line);
				
				// skip non poitypes
				if(!record.has(poitype.name().toLowerCase())) {
					continue;
				}
				
				JSONArray atms = record.getJSONArray("atms");
				int noOfAtmsWithinSpecifiedDistance = 0;
				for(int i=0;i<atms.length();i++) {
					Object obj = atms.get(i);
					JSONObject atm = null;
					if(obj instanceof String) {
						atm = new JSONObject((String)obj); 
					} else if(obj instanceof JSONObject) {
						atm  = atms.getJSONObject(i);
					}
					// We would like to find out the ATMs in 1 mile radius
					// However note that the distance is in meters.
					int distanceFromPoi = atm.optInt("poidist");
					if(distanceFromPoi <= radius) {
						noOfAtmsWithinSpecifiedDistance = noOfAtmsWithinSpecifiedDistance + 1; 
					}
				}
				
				if(noOfAtmsWithinSpecifiedDistance > 0) {					
					JSONObject properties = new JSONObject();
					properties.put("noofatms", noOfAtmsWithinSpecifiedDistance);
					properties.put("title",record.optString(poitype.name().toLowerCase()));
					properties.put("poitype",poitype.name().toLowerCase());
					JSONObject geometry = new JSONObject();
					geometry.put("type", "Point");
					JSONArray coordinates = new JSONArray();
					coordinates.put(record.optDouble("longitude"));
					coordinates.put(record.optDouble("latitude"));
					geometry.put("coordinates", coordinates);
					
					JSONObject feature = new JSONObject();
					feature.put("type", "Feature");
					feature.put("properties",properties);
					feature.put("geometry",geometry);
					
					features.put(feature);
				}
			}	
			geoJson.put("type", "FeatureCollection");
			geoJson.put("features", features);
			list.add(geoJson);
		} catch(IOException e) {
			System.out.println("IOException occurred while reading the file: " + e.getMessage());
		} finally {
			reader.close();
		}
		return list;
	}
	

	/**
	 * Generates the JS file which has variable assigned to a geo json string.
	 * example below: 
	 * var varname = 'json string'
	 * @param fileName
	 * @param geoJsons
	 * @param varname
	 */
	
	public void storeGeoJson(String fileName,List<JSONObject> geoJsons,String varname) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(fileName,true));
			for(JSONObject geoJson:geoJsons) {
				out.print("var "+ varname + "='"); 
				out.print(geoJson.toString());
				out.println("'");
			}
		} catch(Exception e) {
			System.out.println("Exception while writing geoJson:" + e.getMessage());
		} finally {
			out.close();
		}
		
	}
}
