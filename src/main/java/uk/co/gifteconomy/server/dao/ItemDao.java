package uk.co.gifteconomy.server.dao;

import java.util.Date;
import java.util.List;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.model.ItemSource;
import uk.co.gifteconomy.model.ItemType;

/**
 * Dao interface for Items, so that we can abstract away from any persistence
 * specifics like libraries, or hopefully, data storage technology.
 * 
 * This implies that queries will be explicitly enshrined in methods, which
 * suits the current NoSql/BigTable implementation just fine, but doesn't 
 * transfer so well if we switch to plain Sql - this might have to be revisited
 * then!
 * 
 * @author steve
 *
 */
public interface ItemDao {
	
	/**
	 * Method to get the latest item date for a list of sources.
	 * Useful to check whether to bother sending any updates to a client,
	 * whether to bother processing an item or list of items supplied, or
	 * to use in generating last-modified-since headers when contacting other
	 * servers to obtain lists of items. 
	 * 
	 * @param sources - the list of ItemSource objects to check
	 * @return the latest item date in any of the given sources
	 */
	public Date getLatestItemDate(List<ItemSource> sources) 
			throws IllegalArgumentException;
	
	/**
	 * Persist a list of items.
	 * 
	 * @param items - the List of items to persist
	 * @return a boolean indicating whether the operation was a success or not.
	 * It's implied that a success is if and only if ALL the items get 
	 * persisted.
	 * @throws IllegalArgumentException if items is empty
	 */
	public boolean persistItems(List<Item> items) 
			throws IllegalArgumentException;
	
	/**
	 * Persist a single item.
	 * 
	 * @param item - the item to persist
	 * @return a boolean indicating whether or not the item was successfully 
	 * persisted.
	 */
	public boolean persistItem(Item item);
	
	/**
	 * Retrieve a single item by its id
	 * 
	 * @param id - the Long id number which identifies this Item
	 * @return - the Item if a matching Item is found, or Item.UNKNOWN_ITEM 
	 * otherwise.
	 */
	public Item getItem(Long id);
	
	/**
	 * Retrieve all the items of the supplied type for the supplied list of 
	 * sources. Items are retrieved in a newest first order.
	 * 
	 * @param sources - the list of ItemSource objects to return items from
	 * @param type - the type of items to return
	 * @return - a List<Item> of matching items if any are found, or an empty 
	 * list otherwise.
	 */
	public List<Item> getAllItemsForSources(
			List<ItemSource> sources,
			ItemType type);
	
}
