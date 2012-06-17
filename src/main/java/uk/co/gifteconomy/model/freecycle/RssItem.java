package uk.co.gifteconomy.model.freecycle;

import javax.xml.bind.annotation.XmlElement;

public class RssItem {
	
	private String title;
	private String link;
	private String date;
	
	@XmlElement
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@XmlElement(name="link")
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	@XmlElement(name="pubDate")
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "Title: " + title + " Link: " + " Date: " + date;
	}	
	
}
