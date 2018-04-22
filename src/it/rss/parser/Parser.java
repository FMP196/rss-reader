package it.rss.parser;

import it.rss.utils.Tag;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Parser {
	
	// Default constructor
	public Parser() {
		// do nothing
	}

	/**
	 * Get a single Feed from a rss link
	 */
	public Feed getFeed(String url, boolean searchOnWebsite)
	{
		Feed feed = null;
		String rsslink = null;
		
		if (searchOnWebsite == true) {
			rsslink = getRssLink(url);
			if (rsslink == null)
				return null;
		}
		else
			rsslink = url;
		
		String xml = getXmlFromUrl(rsslink);
		
		if (xml != null)
		{
			try
			{
				Document doc = this.getDomElement(xml);
				
				// get feed informations
				NodeList nodeList = doc.getElementsByTagName(Tag.CHANNEL);				
				Element e = (Element) nodeList.item(0);
								
				String title = this.getValue(e, Tag.TITLE);
				String link = this.getValue(e, Tag.LINK);	
				String lastdate = this.getValue(e, Tag.LASTDATE);
								
				// get feed items
				List<Item> items = getFeedItems(rsslink);
				
				// create feed
				feed = new Feed(title, link, rsslink, lastdate);
				feed.setItems(items);				
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return feed;
	}

	/**
	 * Get Feed Items
	 * @param rsslink - RSS Link
	 * @return
	 */
	private List<Item> getFeedItems(String rsslink)
	{
		List<Item> itemList = new ArrayList<Item>();
		String xml = getXmlFromUrl(rsslink);
		
		if(xml != null) {
			try {
				Document doc = this.getDomElement(xml);
				NodeList nodeList = doc.getElementsByTagName(Tag.CHANNEL);
				Element e = (Element) nodeList.item(0);
				NodeList items = e.getElementsByTagName(Tag.ITEM);
								
				for(int i=0; i<items.getLength(); i++)
				{
					Element e1 = (Element) items.item(i);
					String title = this.getValue(e1, Tag.TITLE);
					String description = this.getValue(e1, Tag.DESCRIPTION);
					String link = this.getValue(e1, Tag.LINK);
					String pubdate = this.getValue(e1, Tag.PUBDATE);	
					itemList.add(new Item(title,  description, link, pubdate));
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return itemList;
	}

	/**
	 * Getting RSS feed link from HTML source code
	 */
	public String getRssLink(String url)
	{
		String rsslink = null;
		try {
			org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
			org.jsoup.select.Elements links = doc.select("link[type=application/rss+xml]");
			
			if (links.size() > 0) {
				rsslink = links.get(0).attr("href").toString();
			}
			else {
				org.jsoup.select.Elements links1 = doc.select("link[type=application/atom+xml]");
				if(links1.size() > 0) {
					rsslink = links1.get(0).attr("href").toString();	
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rsslink;
	}

	/**
	 * Extract the XML content from a web page
	 */
	private String getXmlFromUrl(String URL)
	{
		String XML = null;
		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			XML = EntityUtils.toString(httpEntity);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return XML;
	}

	/**
	 * Get XML dom element
	 */
	private Document getDomElement(String XML)
	{
		Document document = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(XML));
			document = (Document) db.parse(is);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return document;
	}

	/**
	 * Get element value
	 */
	private String getElementValue(Node elem)
	{
		Node child = null;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
						return ""+child.getNodeValue();
					}
					else if (child.getNodeType() == Node.TEXT_NODE) {
						return ""+child.getNodeValue();
					}
				}
			}
		}
		
		return "";
	}

	private String getValue(Element item, String str) {
		NodeList nodeList = item.getElementsByTagName(str);
		return "" + this.getElementValue(nodeList.item(0));
	}

	/**
	 * Check the URL validity
	 * @return - True if the URL is valid, False otherwise;
	 */
	public boolean isValid(String url) {
		String pattern = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
		boolean result = (url.trim().length() > 0) && (url.matches(pattern));
		return result;
	}
}