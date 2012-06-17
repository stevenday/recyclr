package uk.co.gifteconomy.model.yahoo.geocoder;

import javax.xml.bind.annotation.XmlElement;

/**
 * A JAXB Annotated class describing a single result from Yahoo's Places
 * Geo-coding API. Note, the fields available are not exhaustive, they're only
 * what I need to locate an item to the nearest neighborhood at best.
 * 
 * An example element is given below:
 * 
 * 	<Result>
 * 		<quality>39</quality>
 * 		<latitude>50.545116</latitude>
 * 		<longitude>-4.202175</longitude>
 * 		<offsetlat>50.543880</offsetlat>
 * 		<offsetlon>-4.145140</offsetlon>
 * 		<radius>8700</radius>
 * 		<name></name>
 * 		<line1></line1>
 * 		<line2>Tavistock</line2>
 * 		<line3></line3>
 * 		<line4>United Kingdom</line4>
 * 		<house></house>
 * 		<street></street>
 * 		<xstreet></xstreet>	
 * 		<unittype></unittype>
 * 		<unit></unit>
 * 		<postal></postal>
 * 		<neighborhood></neighborhood>
 * 		<city>Tavistock</city>
 * 		<county>Devon</county>
 * 		<state>England</state>
 * 		<country>United Kingdom</country>
 * 		<countrycode>GB</countrycode>
 * 		<statecode>ENG</statecode>
 * 		<countycode>DEV</countycode>
 * 		<uzip></uzip>
 * 		<hash></hash>
 * 		<woeid>37027</woeid>
 * 		<woetype>7</woetype>
 * 	</Result>
 */
public class Result {
	private String neighborhood;
	private String city;
	private String county;
	private Float latitude;
	private Float longitude;
	private int radius;
	private int quality;
	
	@XmlElement
	public String getNeighborhood() {
		return neighborhood;
	}
	
	@XmlElement
	public String getCity() {
		return city;
	}
	
	@XmlElement
	public String getCounty() {
		return county;
	}
	
	@XmlElement
	public Float getLatitude() {
		return latitude;
	}
	
	@XmlElement
	public Float getLongitude() {
		return longitude;
	}
	
	@XmlElement
	public int getRadius() {
		return radius;
	}
	
	@XmlElement
	public int getQuality() {
		return quality;
	}
	
	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setCounty(String county) {
		this.county = county;
	}
	
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public void setQuality(int quality) {
		this.quality = quality;
	}
	
}
