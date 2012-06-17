package uk.co.gifteconomy.server.processing;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.model.freecycle.RssItem;

/**
 * Abstract interface for any kind of Item 'processor'.
 * 
 * By making everything implement this, we can build a really flexible 
 * 'pipeline' of different processors for items from different sources
 * 
 * @author steve
 * 
 */
public interface ItemProcessor {
	
	/**
	 * Method to process an item from a feed/email/something else, and return a
	 * new processed item.
	 * 
	 * TODO We need to generalise this so that it doesn't rely on RssItem for
	 * when we'll want to process Emails, Craigslist, Gumtree, etc. In that case
	 * it'll have to be a message orientated parameter, which we'll have to come
	 * up with some format/scheme for. 
	 * Possibilities for the message formatting system are:
	 * JSON, ProtocolBuffers, BSON or that other one that was on HackerNews 
	 * recently. Probably want something that works like:
	 * ___________
	 * |  Header |
	 * |_________|
	 * | Payload |
	 * |_________|
	 * 
	 *  Where header identifies the type of message it is and maybe what class
	 *  should process it, and payload is an object of whatever type is 
	 *  appropriate for the message source.
	 *  
	 *  Spring Integration offers something like this, could we use that?
	 *  
	 * @return Item - processed item
	 */
	public Item process(RssItem item);
}
