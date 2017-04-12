package com.amit.springtweet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PointOfInterest implements Serializable {
	
	private String name;
	private String street;
	private double latitude;
	private double longitude;
	private int pincode;
	private long distance;

	public PointOfInterest(String name, String street, double latitude, double longitude, int pincode,long distance) {
		super();
		this.name = name;
		this.street = street;
		this.latitude = latitude;
		this.longitude = longitude;
		this.pincode = pincode;
		this.distance = distance;
	}

	public static List<PointOfInterest> parse(String jsonstring) throws JSONException {
		List<PointOfInterest> pois = new ArrayList<PointOfInterest>();
		JSONObject json = new JSONObject(jsonstring);
		JSONArray arr = json.getJSONArray("results");
		for (int i = 0; i < arr.length(); i++) {
			JSONObject poi = arr.getJSONObject(i);
			pois.add(new PointOfInterest(poi.optString("poi"), poi.optString("street"), poi.optDouble("latitude"),
					poi.optDouble("longitude"), poi.optInt("pincode"),poi.optLong("poi_dist")));
		}
		return pois;
	}


	public String getName() {
		return name;
	}

	public String getStreet() {
		return street;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getPincode() {
		return pincode;
	}
	
	public long getDistance() {
		return distance;
	}	
	
	@Override
	public String toString() {
		
		JSONObject ret = new JSONObject();
		try {
			ret.put("name", name);		
			ret.put("street", street);
			ret.put("latitude", latitude);
			ret.put("longitude", longitude);
			ret.put("pincode", pincode);
			ret.put("poidist", distance);
		} catch (Exception e) {
			try {
				ret.put("error", e.getMessage());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
		}
		return ret.toString();
	}
}
