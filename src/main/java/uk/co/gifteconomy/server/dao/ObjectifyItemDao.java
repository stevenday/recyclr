package uk.co.gifteconomy.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.model.ItemSource;
import uk.co.gifteconomy.model.ItemType;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

/**
 * Implementation of ItemDao for dealing with Items in the Google App Engine
 * High-Replication Datastore through the Objectify ORM library.
 * 
 * @see: http://code.google.com/p/objectify-appengine
 * 
 * @author steve
 *
 */
public class ObjectifyItemDao extends DAOBase implements ItemDao {
	
	// Register the entity classes we deal with
	static {
		ObjectifyService.register(Item.class);
		ObjectifyService.register(ItemSource.class);
	}
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(ObjectifyItemDao.class);
	
	// The maximum number of sources per query
	private static final int MAX_SOURCES_PER_QUERY = 5;
	
	public Date getLatestItemDate(List<ItemSource> sources) {
		List<ItemSource> validSourceList = new ArrayList<ItemSource>(
				checkAndTruncateSourcesParameter(sources));
		Query<Item> latestItemQuery = ofy().query(Item.class)
				.filter("source in", translateSourcesList(validSourceList))
				.order("-posted")
				.limit(1);
		Item latestItem = latestItemQuery.get();
		if(latestItem != null) {
			return latestItem.getPosted();
		}
		else {
			return null;
		}
	}
	
	public boolean persistItems(List<Item> items) {
		boolean success = false;
		Map<Key<Item>, Item> result = ofy().put(items);
		if(result != null && result.size() == items.size()); {
			success = true;
		}
		return success;
	}
	
	public boolean persistItem(Item item){
		boolean success = false;
		Key<Item> result = ofy().put(item);
		if(result != null); {
			success = true;
		}
		return success;
	}

	public Item getItem(Long id) {
		Item returnItem = Item.UNKNOWN_ITEM;
		try {
			returnItem = ofy().get(Item.class, id);
			if(returnItem == null) {
				returnItem = Item.UNKNOWN_ITEM;
			}
		}
		catch(NotFoundException nfe) {
			LOGGER.info("Not found exception when querying for item by id",
					nfe);
		}
		return returnItem;
	}

	public List<Item> getAllItemsForSources(List<ItemSource> sources,
			ItemType type) {

		List<ItemSource> validSourceList = new ArrayList<ItemSource>(
				checkAndTruncateSourcesParameter(sources));
		return ofy().query(Item.class)
				.filter("source in", translateSourcesList(validSourceList))
				.filter("type", type)
				.order("-posted")
				.list();
	}
	
	/**
	 * Helper method to check that a supplied sources list is valid.
	 * 
	 * In order to save our GAE datastore quota from greedy clients, we enforce
	 * a limit on the number of sources in a single query at the code level. 
	 * sources will be truncated if it is above MAX_SOURCES_PER_QUERY
	 * 
	 * @param sources - the sources list to check and truncate if necessary
	 */
	private List<ItemSource> checkAndTruncateSourcesParameter(
			List<ItemSource> sources) {
		
		List<ItemSource> returnSources = new ArrayList<ItemSource>(sources);
		if(sources.size() > MAX_SOURCES_PER_QUERY) {
			returnSources.subList(0, MAX_SOURCES_PER_QUERY - 1);
		}
		return returnSources;
	}
	
	/**
	 * Helper method to translate a list of ItemSource 's into a list of 
	 * Key<ItemSource> 's so that we can put them into Objectify queries
	 */
	private List<Key<ItemSource>> translateSourcesList(
			List<ItemSource> sources) {
		List<Key<ItemSource>> returnList = new ArrayList<Key<ItemSource>>();
		for(ItemSource source: sources) {
			returnList.add(
					new Key<ItemSource>(ItemSource.class, source.getId())
				);
		}
		return returnList;
	}
}
