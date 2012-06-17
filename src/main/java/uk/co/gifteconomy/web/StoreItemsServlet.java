package uk.co.gifteconomy.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.gifteconomy.model.Item;
import uk.co.gifteconomy.server.dao.ItemDao;

@SuppressWarnings("serial")
public class StoreItemsServlet extends HttpServlet {
	
	public static final Logger logger = Logger.getLogger(FetchFreecycleFeedServlet.class.getName());
	
	// DAO which should be injected into this class to provide persistence
	// access
	private ItemDao itemDao;
	
	/**
	 * Respond to get requests by persisting the items into the datastore. 
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		List<Item> items = getItemsFromRequest(req);
		itemDao.persistItems(items);
	}
	
	/**
	 * Parse the items from the request
	 * TODO Actually do this!
	 * @param req
	 * @return
	 */
	private List<Item> getItemsFromRequest(HttpServletRequest req) {
		// String serialisedItems = req.getParameter("items");
		// TODO how do we unserialise this string?
		List<Item> items = new ArrayList<Item>();
		return items;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}
}
