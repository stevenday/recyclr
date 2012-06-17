package uk.co.gifteconomy.server.processing;

import uk.co.gifteconomy.model.ItemLocatorResult;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public abstract class AbstractItemLocator implements ItemLocator {

	@Override
	public abstract ItemLocatorResult locate(
			String locale,
			String country, 
			String county, 
			String city,
			String neighbourhood, 
			LatLng center, 
			Double maxDistance);
	
	/**
	 * Helper method to determine if a given location is "near enough" to 
	 * an item source's center. Used by ItemLocators to determine whether a
	 * geocoder's return value is valid for use. This is neccessary because
	 * (for example) - many people give away Belfast sinks, and they're not all
	 * in Belfast, as much as the geocoder would assure us they are!
	 *  
	 * Uses the SimpleLatLng project to actually calculate the distance using 
	 * the Haversine formula. 
	 * @see http://code.google.com/p/simplelatlng/
	 */
	public boolean resultIsNearEnough(
			LatLng location,
			double maxDistance,
			LatLng center,
			LengthUnit unit) {
		return LatLngTool.distance(center, location, unit) < maxDistance; 
	}

}
