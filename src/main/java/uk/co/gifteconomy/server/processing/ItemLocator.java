package uk.co.gifteconomy.server.processing;

import com.javadocmd.simplelatlng.LatLng;

import uk.co.gifteconomy.model.ItemLocatorResult;

/**
 * Interface which defines how ItemLocators operate. Generally speaking they
 * take a string which (hopefully) contains some location information like a
 * region, town or neighbourhood and they turn that into an actual location
 * consisting of three pieces of information: latitude, longitude and a nice
 * name.
 * 
 * @author steve
 *
 */
public interface ItemLocator {
	
	/**
	 * Locate an item given the parameters supplied.
	 * 
	 * Generally the locale, country, county, center and max distance will be 
	 * fixed and come from some configuration setting for whatever 
	 * {@link ItemSource} the item comes from. 
	 * 
	 * You then supply the item title or whatever other string
	 * contains the exact location and put it into the city or neighbourhood
	 * fields, supplying an empty String for the other if you don't know it, or
	 * don't want to narrow it down that far.
	 *  
	 * @param locale
	 * @param country
	 * @param county
	 * @param city
	 * @param neighbourhood
	 * @param center
	 * @param maxDistance
	 * @return
	 */
	public ItemLocatorResult locate(
			String locale,
			String country,
			String county,
			String city,
			String neighbourhood,
			LatLng center,
			Double maxDistance);

}
