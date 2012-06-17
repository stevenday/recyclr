package uk.co.gifteconomy.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.model.ItemSource;
import uk.co.gifteconomy.model.ItemType;
import uk.co.gifteconomy.server.LocalDatastoreTest;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class ObjectifyItemDaoTest extends LocalDatastoreTest {
	
	private ObjectifyItemDao uut;
	
	// Center of Crediton
	private ItemSource sourceOne;
	
	// Center of Bristol
	private ItemSource sourceTwo;
	
	private Date latestDate;
	
	/**
	 * Set up the DAO under test and test datastore service
	 */
	@Before
	@Override
	public void setUp() {
		super.setUp();
		this.uut = new ObjectifyItemDao();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2012, 4, 7, 10, 0, 0);
		latestDate = cal.getTime();
		
		sourceOne = new ItemSource();
		sourceOne.setName("Test Item Source One");
		sourceOne.setLat(50.85776d);
		sourceOne.setLon(-3.800097d);
		Point p = new Point(sourceOne.getLat(), sourceOne.getLon());
		sourceOne.setGeoCells(GeocellManager.generateGeoCell(p));
		
		// Center of Bristol
		sourceTwo = new ItemSource();
		sourceTwo.setName("Test Item Source Two");
		sourceTwo.setLat(53.800651d);
		sourceTwo.setLon(-4.064941d);
		p = new Point(sourceTwo.getLat(), sourceTwo.getLon());
		sourceTwo.setGeoCells(GeocellManager.generateGeoCell(p));
	}
	
	/**
	 * Tear down test datastore service so we have a fresh one for each test
	 */
	@After
	@Override
	public void tearDown() {
		super.tearDown();
		sourceOne = null;
		sourceTwo = null;
	}
	
	/**
	 * Test the getLatestItemDate method returns the right result when we have
	 * just one source in the db and supply just that source to the method.
	 */
	@Test
	public void testGetLatestItemDateSingleSource() {
		generateTestDataOneSource();
		List<ItemSource> sources = Arrays.asList(new ItemSource[] {sourceOne});
		Date latestItemDate = uut.getLatestItemDate(sources);
		assertEquals(latestDate, latestItemDate);
	}
	
	/**
	 * Test the get latest item method returns the right result when we have
	 * multiple sources in the db.
	 */
	@Test
	public void testGetLatestItemDateMultipleSources() {
		generateTestDataTwoSources();
		List<ItemSource> sources = Arrays.asList(
				new ItemSource[] {sourceOne, sourceTwo}
			);
		Date latestItemDate = uut.getLatestItemDate(sources);
		assertEquals(latestDate, latestItemDate);
	}
	
	/**
	 * Test the get latest item method returns the right result when we have
	 * multiple sources in the db and supply one of them to that method.
	 */
	@Test
	public void testGetLatestItemDateOneOfMultipleSources() {
		generateTestDataTwoSources();
		List<ItemSource> sources = Arrays.asList(new ItemSource[] {sourceTwo});
		Date latestItemDate = uut.getLatestItemDate(sources);
		Date latestDateSiteTwo = new Date(latestDate.getTime() - 3600000L);
		assertEquals(latestDateSiteTwo, latestItemDate);
	}
	
	/**
	 * Make some dummy data for the db with everything from one source
	 */
	private void generateTestDataOneSource() {
		Objectify ofy = ObjectifyService.begin();
		
		// Put the item source in first
		Key<ItemSource> sourceKey = ofy.put(sourceOne);
		assertNotNull(sourceKey);
		
		// Now put in some items for it
		for(int i: new int[]{0,1,2,3,4,5,6,7,8,9}) {
			// increment the date backwards so that item 0 is the newest in the
			// datastore
			Date startDate = new Date(latestDate.getTime() - (i * 3600));
			Item testItem = new Item(
					ItemType.OFFERED,
					"Test item " + i,
					"http://www.example.com/" + i,
					startDate,
					"Test location " + i,
					sourceKey);
			Key<Item> itemKey = ofy.put(testItem);
			assertNotNull(itemKey);
		}
	}
	
	/**
	 * Generate test data and divide the locations between two sites
	 */
	private void generateTestDataTwoSources() {
		Objectify ofy = ObjectifyService.begin();
		
		// Put the item sources in first
		Key<ItemSource> sourceKey = ofy.put(sourceOne);
		assertNotNull(sourceKey);
		
		Key<ItemSource> sourceKeyTwo = ofy.put(sourceTwo);
		assertNotNull(sourceKeyTwo);
		
		// Now put in some items for it
		for(int i: new int[]{0,1,2,3,4,5,6,7,8,9}) {
			// increment the date backwards so that item 0 is the newest in the
			// datastore
			Date startDate = new Date(latestDate.getTime() - (i * 3600000L));
			// Divide items between the sources - evens (inc 0) get source 1,
			// odds get 2, so the latest item for source 1 is LATEST_DATE
			// and the latest item for source 2 is LATEST_DATE - 1h
			Long itemSourceId = sourceKey.getId();
			if(i > 0) {
				itemSourceId = sourceKeyTwo.getId();
			}
			
			Item testItem = new Item(
					ItemType.OFFERED,
					"Test item " + i,
					"http://www.example.com/" + i,
					startDate,
					"Test location " + i,
					new Key<ItemSource>(ItemSource.class, itemSourceId));
			
			Key<Item> itemKey = ofy.put(testItem);
			assertNotNull(itemKey);
		}
	}
}
