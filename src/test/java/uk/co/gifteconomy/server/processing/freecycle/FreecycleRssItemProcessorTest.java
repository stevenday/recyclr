package uk.co.gifteconomy.server.processing.freecycle;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.model.ItemLocatorResult;
import uk.co.gifteconomy.model.ItemType;
import uk.co.gifteconomy.model.freecycle.RssItem;
import uk.co.gifteconomy.server.processing.YahooPlacesItemLocator;

import com.javadocmd.simplelatlng.LatLng;

public class FreecycleRssItemProcessorTest {
	
	private FreecycleRssItemProcessor uut;
	
	@Mocked	public YahooPlacesItemLocator mockItemLocator;
	
	private static final SimpleDateFormat df = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	static {
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	private static final String DATE_STRING = "Sun, 22 Apr 2012 16:41:46 +0000";

	private static final String LINK = "http://www.example.com";
	
	@Before
	public void setUp() throws Exception {
		this.uut = new FreecycleRssItemProcessor();
		uut.setItemLocator(mockItemLocator);
		final ItemLocatorResult expectedLocation = new ItemLocatorResult(
				1.0d, 2.0d, "Crediton", new String[] {"Crediton", "Devon"});
		// Expectations on mockItemLocator
		new NonStrictExpectations() {{
			mockItemLocator.locate(
					anyString, 
					anyString, 
					anyString, 
					anyString, 
					anyString, 
					(LatLng) any, 
					anyDouble);
			returns(expectedLocation);
		}};
	}

	@Test
	public void testProcessesOfferedItem() throws ParseException {
		// Setup
		RssItem testRssItem = new RssItem();
		testRssItem.setDate(DATE_STRING);
		testRssItem.setLink(LINK);
		testRssItem.setTitle("OFFERED: Test Item Title (Crediton)");
		
		// Exercise
		Item item = uut.process(testRssItem);
		
		// Assert		
		assertEquals(item.getLink(), LINK);
		assertEquals(item.getPosted(), df.parse(DATE_STRING));
		assertEquals(item.getLocation(), "Crediton");
		assertEquals(item.getType(), ItemType.OFFERED);
	}
	
	@Test
	public void testProcessesWantedItem() throws ParseException {
		// Setup
		RssItem testRssItem = new RssItem();
		testRssItem.setDate(DATE_STRING);
		testRssItem.setLink(LINK);
		testRssItem.setTitle("WANTED: Test Item Title (Crediton)");
		
		// Exercise
		Item item = uut.process(testRssItem);
		
		// Assert		
		assertEquals(item.getLink(), LINK);
		assertEquals(item.getPosted(), df.parse(DATE_STRING));
		assertEquals(item.getLocation(), "Crediton");
		assertEquals(item.getType(), ItemType.WANTED);
	}
	
	@Test
	public void testProcessesTakenItem() throws ParseException {
		// Setup
		RssItem testRssItem = new RssItem();
		testRssItem.setDate(DATE_STRING);
		testRssItem.setLink(LINK);
		testRssItem.setTitle("TAKEN: Test Item Title (Crediton)");
		
		// Exercise
		Item item = uut.process(testRssItem);
		
		// Assert		
		assertEquals(item.getLink(), LINK);
		assertEquals(item.getPosted(), df.parse(DATE_STRING));
		assertEquals(item.getLocation(), "Crediton");
		assertEquals(item.getType(), ItemType.TAKEN);
	}

}
