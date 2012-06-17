package uk.co.gifteconomy.model;

import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.Key;

/**
 * Encapsulation of an item. 
 * 
 * @author steve
 * 
 * TODO - use @Cached to cache this in the memcache? Need to think about how the
 * long to keep it - this is tied to the feed refresh time.
 */
public class Item {

	@Id	private Long id;
	private ItemType type;
	private String title;
	private String link;
	private Date posted;
	private String location;
	private Key<ItemSource> source;
	
	/**
	 * Representation of an empty or unknown item - static so we can use it in
	 * things like query results when we get nothing back. Better than returning
	 * null, so Joshua Bloch says.
	 */
	public static final Item UNKNOWN_ITEM = new Item();
	
	/**
	 * Private constructor for unknown items.
	 */
	private Item() {
		this.type = ItemType.UNKNOWN;
		this.title = "";
		this.location = "Unknown";
	}
	
	/**
	 * Constructor for items that are clones of another item
	 */
	public Item(Item item) {
		this(
				item.getType(),
				item.getTitle(),
				item.getLink(),
				item.getPosted(),
				item.getLocation(),
				item.getSource()
			);
	}
	
	/**
	 * Constructor for Items with all the details specified
	 * @param type
	 * @param title
	 * @param link
	 * @param posted
	 * @param location
	 * @param sourceKey
	 */
	public Item(ItemType type,
			String title,
			String link,
			Date posted,
			String location,
			Key<ItemSource>sourceKey) {
		
		super();
		this.type = type;
		this.title = title;
		this.link = link;
		this.posted = new Date(posted.getTime());
		this.location = location;
		this.source = sourceKey;
	}

	public Long getId() {
		return id;
	}

	public ItemType getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public Date getPosted() {
		return new Date(posted.getTime());
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public Key<ItemSource> getSource() {
		return source;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setType(ItemType type) {
		this.type = type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setPosted(Date posted) {
		this.posted = new Date(posted.getTime());
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setSource(Key<ItemSource> sourceKey) {
		this.source = sourceKey;
	}

	@Override
	public String toString() {
		return "Type: " + type 
				+ " Title: " + title 
				+ " Link: " + link 
				+ " Date: " + posted 
				+ " Location: " + location 
				+ " Source: " + source.getName();
	}
	
}
