package uk.co.gifteconomy.model.freecycle;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="rss", namespace="")
public class RssFeed {
	
	private List<RssItem> items;

	@XmlElementWrapper(name="channel")
	@XmlElement(name="item")
	public List<RssItem> getItems() {
		return items;
	}
	
	public void setItems(List<RssItem> items) {
		this.items = items;
	}		
	
}
