package com.amit.springtweet;

public enum POIType {
	HOSPITAL("hospital"),
	SCHOOL("school");
	
	POIType(String poitype) {
		this.poitype = poitype;
	}
	String poitype;
}
