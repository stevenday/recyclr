package uk.co.gifteconomy.model;

import java.util.List;

import javax.persistence.Id;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;

/**
 * Class to encapsulate where an item comes from - eg: an RSS feed or
 * an email inbox.
 * 
 * @author steve
 *
 */
public class ItemSource {
	// Id is used by the datastore - we don't manually add this
	@Id private Long id;	
	private double lat;	
	private double lon;
	private List<String> geoCells;
	private String name;
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public List<String> getGeoCells() {
		if(geoCells == null) {
			generateGeoCells();
		}
		return geoCells;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public void setGeoCells(List<String> geoCells) {
		this.geoCells = geoCells;
	}
	
	private void generateGeoCells() {
		Point p = new Point(lat, lon);
		this.geoCells = GeocellManager.generateGeoCell(p);
	}
	
}
