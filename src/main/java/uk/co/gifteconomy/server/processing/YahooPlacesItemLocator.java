package uk.co.gifteconomy.server.processing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.co.gifteconomy.model.ItemLocatorResult;
import uk.co.gifteconomy.model.yahoo.geocoder.ErrorCodes;
import uk.co.gifteconomy.model.yahoo.geocoder.Result;
import uk.co.gifteconomy.model.yahoo.geocoder.ResultSet;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class YahooPlacesItemLocator extends AbstractItemLocator {
	
	private static final Logger logger = 
			Logger.getLogger(YahooPlacesItemLocator.class.getName());
	
	// TODO - move this to the config.xml	
	public static final String APP_ID = "a5FCTj36";
	public static final String YAHOO_URL = 
			"http://where.yahooapis.com/geocode?";
	public static final int CONNECT_TIMEOUT = 10000;
	public static final int REQUEST_TIMEOUT = 30000;

	@Override
	public ItemLocatorResult locate(
			String locale,
			String country,
			String county,
			String city,
			String neighbourhood,
			LatLng center,
			Double maxDistance) {
		
		// Map of parameters to build a query string for the geocoding
		// service.
		HashMap<String, String> parametersMap = new HashMap<String, String>();
		// App Id
		parametersMap.put("appid", APP_ID);
		// Locale
		parametersMap.put("locale", locale);
		// Specific flags to narrow down the results
		parametersMap.put("gflags", "L");
		// Country
		parametersMap.put("country", country);
		// County (level 1 is US state)
		parametersMap.put("county", county);
		// Administrative name (City/Town/Locality)
		parametersMap.put("city", city);
		// Neighbourhood - only for cities
		if(neighbourhood != null && !neighbourhood.equals("")) {
			parametersMap.put("neighbourhood", neighbourhood);		
		}
		
		// Setup default return values
		ItemLocatorResult result = null;
		
		// Try to get results from the GeoCoding service
		List<Result> results = getResultsFromYahoo(parametersMap);
		if(!results.isEmpty()) {
			Result bestResult = findBestLocation(results, center, maxDistance);
			if(bestResult != null) {
				List<String> nameParts = new ArrayList<String>();
				if(bestResult.getNeighborhood() != "") {
					nameParts.add(bestResult.getNeighborhood());
				}
				if(bestResult.getCity() != "") {
					nameParts.add(bestResult.getCity());
				}
				if(bestResult.getCounty() != "") {
					nameParts.add(bestResult.getCounty());
				}
				result = new ItemLocatorResult(
						bestResult.getLatitude(),
						bestResult.getLongitude(),
						getLocationName(bestResult),
						(String[]) nameParts.toArray());
			}
		}
		return result;
	}
	
	/**
	 * Get the actual Geocoded results from the Yahoo geocoder service
	 * 	
	 * @param parametersMap A Map of parameters to pass to Yahoo
	 * @return
	 */
	private List<Result> getResultsFromYahoo(Map<String, String> parametersMap) {
		HttpURLConnection yahooPlacesUrlCon = null;
		List<Result> results = new ArrayList<Result>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ResultSet.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			// Connect to the URL and set the connect timeout to 10 seconds
			// and the response timeout to 30 seconds - we give Yahoo! ages!
			// because this is parallelised anyway and sometimes Yahoo! is slow.
			URL yahooPlacesUrl = new URL(YAHOO_URL + "?" 
					+ createYahooParameterString(parametersMap));
			yahooPlacesUrlCon = 
					(HttpURLConnection) yahooPlacesUrl.openConnection();
			yahooPlacesUrlCon.setConnectTimeout(CONNECT_TIMEOUT);
			yahooPlacesUrlCon.setReadTimeout(REQUEST_TIMEOUT);
			
			// Unmarshall the result (hopefully!)
			ResultSet resultSet = (ResultSet) jaxbUnmarshaller.unmarshal(
					yahooPlacesUrlCon.getInputStream());
			// If there's something in it's results, we can return them
			if(resultSet != null &&
					resultSet.getError().equals(ErrorCodes.NO_ERROR.getCode()) &&
					resultSet.getFound() > 0) {
				results = resultSet.getResults();
			}
		} catch (JAXBException e) {
			// An error occurred in creating the unmarshaller
			// do nothing, we just return the unknown location
			logger.warning(
				"Error occured in unmarshalling item: " + e.getMessage());
		} catch (IOException e) {
			// An error occurred opening the url - again do nothing and just
			// return the unknown location
			logger.warning(
				"Error occured with geocoder url: " + e.getMessage());
		}
		finally {
			// Make sure we close the URL connection
			if(yahooPlacesUrlCon != null) {
				yahooPlacesUrlCon.disconnect();
			}
		}
		
		return results;		
	}
	
	/**
	 * Get the name of the location from a result - could be the neighbourhood
	 * or the 'city' name.
	 * 
	 * @param result
	 */
	private String getLocationName(Result result) {	
		String locationName = "Unknown";
		if(!result.getNeighborhood().equals("")) {
			locationName = result.getNeighborhood(); 
		}
		else if(!result.getCity().equals("")) {
			locationName = result.getCity();
		}
		return locationName;
	}
	
	/**
	 * Find the best location from a set of results
	 * In practice, this means the first one that is within the reasonable
	 * limit we've setup for the current item source.
	 * 
	 * @param results
	 */
	private Result findBestLocation(List<Result> results, LatLng center,
			Double maxDistance) {
		Result bestResult = null;
		for(Result result: results) {
			LatLng location = new LatLng(result.getLatitude(),
					result.getLongitude());
			if(resultIsNearEnough(location,
					maxDistance,
					center,
					LengthUnit.MILE)) {
				bestResult = result;
				break;
			}
		}
		return bestResult;
	}	

	/**
	 * Create a parameter string for the Yahoo! Places geocoding API
	 * @param parametersMap - map of parameter names and values to use
	 * @return a valid URL parameter string
	 */
	private String createYahooParameterString(
			Map<String,	String> parametersMap) {
		
		StringBuilder sb = new StringBuilder();
		for(String key: parametersMap.keySet()){
			if(!sb.toString().equals("")) {
				sb.append("&");
			}
			sb.append(buildParameter(key, parametersMap.get(key)));
		}
		return sb.toString();
	}
	
	/**
	 * Build a URL parameter from a name and a value. This UTF-8 encodes the
	 * parameters, as is required by some RFC or other.
	 * 
	 * @param key - the name of the parameter
	 * @param value - the value of the parameter
	 */
	private String buildParameter(String key, String value) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(key);
			sb.append("=");
			sb.append(URLEncoder.encode(value, "UTF-8"));
			return sb.toString();
		}
		catch (UnsupportedEncodingException ex) {
			logger.severe("UTF-8 is unsupported: " + ex.getMessage());
			return "";
		}
	}

}
