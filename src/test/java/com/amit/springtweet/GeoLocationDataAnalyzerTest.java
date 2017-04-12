package com.amit.springtweet;

import java.util.List;

import org.json.JSONObject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GeoLocationDataAnalyzerTest extends TestCase {

	public void testAnalyzeGeoLocationDataForHospitals() throws Exception {
		GeoLocationDataAnalyzer analyzer = new GeoLocationDataAnalyzer();		
		List<JSONObject> geojsons = analyzer.analyzeGeoLocationData("places.txt",POIType.HOSPITAL, 1600);
		analyzer.storeGeoJson("atms-in-mile.json", geojsons,"hospital_atm_locations");
	}

	public void testAnalyzeGeoLocationDataForSchools() throws Exception {
		GeoLocationDataAnalyzer analyzer = new GeoLocationDataAnalyzer();		
		List<JSONObject> geojsons = analyzer.analyzeGeoLocationData("places.txt",POIType.SCHOOL, 1600);
		analyzer.storeGeoJson("atms-in-mile.json", geojsons,"school_atm_locations");
	}
	
    public static Test suite() {
        return new TestSuite( GeoLocationDataAnalyzerTest.class );
    }

}
