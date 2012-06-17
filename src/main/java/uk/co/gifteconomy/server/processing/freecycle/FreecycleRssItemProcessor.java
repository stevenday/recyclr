package uk.co.gifteconomy.server.processing.freecycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.model.ItemLocatorResult;
import uk.co.gifteconomy.model.ItemType;
import uk.co.gifteconomy.model.freecycle.RssItem;
import uk.co.gifteconomy.server.processing.ItemLocator;
import uk.co.gifteconomy.server.processing.ItemProcessor;

import com.javadocmd.simplelatlng.LatLng;

public class FreecycleRssItemProcessor implements ItemProcessor {
	
	public void setItemLocator(ItemLocator itemLocator) {
		this.itemLocator = itemLocator;
	}

	private static final Logger logger = 
			Logger.getLogger(FreecycleRssItemProcessor.class.getName());
	
	// Date format, dates look like: Sun, 22 Apr 2012 16:41:46 +0000
	private static final SimpleDateFormat df = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	static {
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	// Timestamp format
	private static final Pattern TIMESTAMP_REGEX = 
			Pattern.compile(" @ \\d\\d:\\d\\d:\\d\\d");
	
	// Item type regular expressions, to classify as wanted/offered/taken
	private static final Pattern WANT_REGEX = Pattern.compile(
			"^((want|wantd|wanted)[,.-:\\s]+).*",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern OFFER_REGEX = Pattern.compile(
			"^((offer|ofer|ofered|oferd|offerd|offered)[,.-:\\s]+).*",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern TAKEN_REGEX = Pattern.compile(
			"^((taken|takn|received|recieved|gone)[,.-:\\s]+).*",
			Pattern.CASE_INSENSITIVE);
	
	// Configuration for locating items
	// TODO - look these up in the datastore for the feed id
	public static final String COUNTRY = "UK";
	public static final String COUNTY = "Devon";
	public static final String LOCALE = "en_GB";
	// Lat/Lon for Lapford (Center of mid devon feed)
	public static final LatLng FEED_CENTER = new LatLng(50.85776d,-3.800097d);
	// How close is close enough? (35 miles)
	public static final double NEAR_ENOUGH = 35.0d;
	
	private ItemLocator itemLocator;

	/**
	 * Process an RssItem into a datastore Item
	 * @param rssItem
	 * @return
	 */
	@Override
	public Item process(RssItem rssItem) {
		Date posted = parseDate(rssItem.getDate());
		String link = rssItem.getLink();
		String title = cleanTitle(rssItem.getTitle());
		Item item = new Item(
					ItemType.UNKNOWN,
					title,
					link,
					posted,
					"",
					null
				);
		item = classify(item);
		item = locate(item);
		return item;
	}
	
	/**
	 * Parse a date string, if we get a parseException we catch it and just
	 * return the current date.
	 * 
	 * @param date The date string to parse
	 * @return
	 */
	private Date parseDate(String date) {
		// Default is right now!
		Date parsedDate = new Date();
		try {
			 parsedDate = df.parse(date);
		}
		catch(ParseException e) {
			// Do nothing, just return the parsed date
			logger.warning("Exception parsing item date: " + e.getMessage());
		}
		return parsedDate;
	}

	private Item locate(Item item) {
		// Get the location from the itemLocator
		ItemLocatorResult location = itemLocator.locate(
				LOCALE,
				COUNTRY,
				COUNTY,
				item.getTitle(),
				"",
				FEED_CENTER,
				NEAR_ENOUGH
			);
		
		// Pull out the info we need
		Item locatedItem = item;
		locatedItem.setTitle(
				cleanTitleOfLocation(item.getTitle(), location.getNameParts()));
		locatedItem.setLocation(location.getName());
		// TODO - Get the lat/lon too 
		
		return locatedItem;
	}

	private Item classify(Item item) {
		Item returnItem = new Item(item);
		Matcher wantMatcher = WANT_REGEX.matcher(item.getTitle());
		Matcher offerMatcher = OFFER_REGEX.matcher(item.getTitle());
		Matcher takenMatcher = TAKEN_REGEX.matcher(item.getTitle());
		if(wantMatcher.matches()) {
			returnItem.setType(ItemType.WANTED);
			returnItem.setTitle(
					item.getTitle().replaceFirst(wantMatcher.group(1), ""));
		}
		else if(offerMatcher.matches()) {
			returnItem.setType(ItemType.OFFERED);
			returnItem.setTitle(
					item.getTitle().replaceFirst(offerMatcher.group(1), ""));
		}	
		else if(takenMatcher.matches()) {
			returnItem.setType(ItemType.TAKEN);
			returnItem.setTitle(
					item.getTitle().replaceFirst(takenMatcher.group(1), ""));
		}
		else {
			returnItem.setType(ItemType.UNKNOWN);
			returnItem.setTitle(item.getTitle());
		}
		
		return returnItem;
	}

	/**
	 * Clean an item title of the basic things we always want to get rid
	 * of.
	 * @param title The title String we want to clean
	 * @return
	 */
	private String cleanTitle(String title) {
		String trimmed = title.trim();
		Matcher timestampMatcher = TIMESTAMP_REGEX.matcher(trimmed);
		return timestampMatcher.replaceFirst("");
	}
	
	/**
	 * Clean the item title of any traces of the result location.
	 * Locations are often of one of the following forms:
	 * - Bristol
	 * - St. Werburghs, Bristol
	 * - (Bristol)
	 * - (St. Werburghs/Bristol)
	 * - Near St. Werburghs
	 * etc
	 * 
	 * So we have to try to remove as much as possible of it without being too
	 * greedy and removing all of the title!
	 * 
	 * Not least because some titles are like: (SOMETHING - Bristol)!
	 * 
	 * @param title - the String to 'clean' of any traces of the location
	 * @param result - the geo-coder result object which contains the location
	 */
	private String cleanTitleOfLocation(String title, String[] locationNames) {
		// Loop over each possible string in the result
		String cleanTitle = title;
		for(String locationName: locationNames) { 
			// Build a regular expression to match the name, plus any word
			// characters immediately touching it
			String locationNameRegex = ".*[\\s]([\\S]*" +
					locationName + "[\\S]*).*";
			Pattern locationNamePattern = Pattern.compile(locationNameRegex,
					Pattern.CASE_INSENSITIVE);
			Matcher locationNameMatcher = 
					locationNamePattern.matcher(cleanTitle);
			if(locationNameMatcher.matches()) {
				String matchedPattern = "(?i)" +
						Pattern.quote(locationNameMatcher.group(1));
				cleanTitle = cleanTitle
						// Remove the basic pattern 
						.replaceAll(matchedPattern, "")
						// Remove the word "near" explicitly
						.replaceAll("(?i)near", "");
			}
		}
		return cleanTitle(cleanTitle);
	}

}
