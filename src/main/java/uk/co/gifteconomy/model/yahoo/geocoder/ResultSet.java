package uk.co.gifteconomy.model.yahoo.geocoder;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A JAXB Annotated class describing a result set from Yahoo's Places
 * Geo-coding API.
 * 
 * An example element is given below:
 * 
 * <ResultSet version="1.0">
 * 	<Error>0</Error>
 * 	<ErrorMessage>No error</ErrorMessage>
 * 	<Locale>en_GB</Locale>
 * 	<Quality>87</Quality>
 * 	<Found>1</Found>
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
 * </ResultSet>
 */
@XmlRootElement(name="ResultSet", namespace="")
public class ResultSet {
	
	private List<Result> results;
	private int found;
	private String error;
	
	@XmlElement(name="Result")
	public List<Result> getResults(){
		return this.results;
	}
	
	@XmlElement(name="Found")
	public int getFound() {
		return found;
	}

	@XmlElement(name="Error")
	public String getError() {
		return error;
	}

	public void setFound(int found) {
		this.found = found;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public void setResults(List<Result> results) {
		this.results = results;
	}
	
}
