package uk.co.gifteconomy.model;


/**
 * Class to encapsulate where an item is physically located.
 * 
 * @author steve
 *
 */
public class ItemLocatorResult {

	private double latitude;
	private double longitude;
	private String name;
	private String[] nameParts;
	
	public ItemLocatorResult(double latitude, double longitude, String name, String[] nameParts) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.nameParts = nameParts;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getNameParts() {
		return nameParts;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNameParts(String[] nameParts) {
		this.nameParts = nameParts;
	}
	
}