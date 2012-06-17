package uk.co.gifteconomy.web;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.model.ItemSource;
import uk.co.gifteconomy.model.freecycle.RssFeed;
import uk.co.gifteconomy.model.freecycle.RssItem;
import uk.co.gifteconomy.server.dao.ItemDao;
import uk.co.gifteconomy.server.processing.ItemProcessor;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class FetchFreecycleFeedServlet extends HttpServlet {
	
	// TODO For testing only!
	private static final List<ItemSource> SOURCES = new ArrayList<ItemSource>();
	static {
		ItemSource sourceOne = new ItemSource();
		sourceOne.setName("Test Item Source One");
		sourceOne.setLat(50.85776d);
		sourceOne.setLon(-3.800097d);
		Point p = new Point(sourceOne.getLat(), sourceOne.getLon());
		sourceOne.setGeoCells(GeocellManager.generateGeoCell(p));
		
		Objectify ofy = ObjectifyService.begin();
		ofy.put(sourceOne);
		
		SOURCES.add(sourceOne);
	}

	private static final Logger logger = Logger.getLogger(
			FetchFreecycleFeedServlet.class.getName());
	
	// ItemProcessor to process the items, should be injected
	private ItemProcessor itemProcessor;
	
	// ItemDAO to get the latest item date, so we can see if there are new items
	private ItemDao itemDao;

	/**
	 * Respond to get requests by parsing the Freecycle feed URL and then adding
	 * a task to the task queue to persist them.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {		
		try {			
			JAXBContext jaxbContext = JAXBContext.newInstance(RssFeed.class); 
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			RssFeed feed = (RssFeed) jaxbUnmarshaller.unmarshal(
					new URL(getFeedUrlFromRequest(req)));
			if(feed != null) {
				List<RssItem> rssItems = feed.getItems();
				List<Item> items = new ArrayList<Item>();
				if(rssItems != null && rssItems.size() > 0) { 
					int i = 0;
					for(RssItem rssItem: rssItems) {
						Item item = itemProcessor.process(rssItem);
						// check if the feed has changed
						Date latestItem = itemDao.getLatestItemDate(SOURCES);
						if(i == 0 && latestItem.after(item.getPosted())) {
							// no point processing the rest
							break;
						}
						items.add(item);
						resp.getWriter().println(item);
					}
					// TODO whats the right way to serialise the list?			
					Queue itemsQueue = QueueFactory.getQueue("Items");
					TaskOptions task = TaskOptions.Builder.withUrl("items/add");
					task.param("items", items.toString());
					itemsQueue.add(task);					
				}
			}
			resp.setStatus(200);
		}
		catch (IOException e) {
			// Couldn't open the feed, the whole feed is a duffer
			logger.severe("Couldn't open feed: " + e.getMessage());
			// TODO - what HTTP Status code is right for this?
			// BadRequest, ServerError?
			resp.setStatus(500);
		} catch (JAXBException e) {
			// Couldn't unmarshall the feed, the whole feed is a duffer
			logger.severe("Couldn't unmarshall feed: " + e.getMessage());
			resp.setStatus(500);
		}
	}
	
	/**
	 * Parse and return the request url we're supposed to call from the request
	 * to load data for processing.
	 * @param req A {@link HttpServletRequest} that contains a parameter 
	 * "feed_url"for the url we should load data from.
	 * @return
	 */
	private String getFeedUrlFromRequest(HttpServletRequest req) {
		// TODO Take a source id instead and get the feed url from the datastore?
		String feedUrl = req.getParameter("feed_url");
		if(feedUrl == null) {
			feedUrl = "http://republisher.freegle.in/rss.php?group=MidDevonFreegle";
		}
		return feedUrl;
	}
	
	/**
	 * Setter for the ItemProcessor this class should use to process items.
	 * @param itemProcessor
	 */
	public void setItemProcessor(ItemProcessor itemProcessor) {
		this.itemProcessor = itemProcessor;
	}
	
	/**
	 * Setter for the ItemDao this class should use to load information from the
	 * datastore about items.
	 * @param itemDao
	 */
	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}
}
