package com.finalist.cmsc.rssfeed.newnav;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeQuery;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.navigation.NavigationInformationProvider;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.portalImpl.NavigationItemRenderer;
import com.finalist.cmsc.portalImpl.registry.PortalRegistry;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.rssfeed.beans.om.RssFeed;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.version.VersionUtil;

public class RssFeedNavigationRenderer implements NavigationItemRenderer {

    private Log log = LogFactory.getLog(RssFeedNavigationRenderer.class);
	
	private final static DateFormat formatRFC822Date = new SimpleDateFormat("EE d MMM yyyy HH:mm:ss zzzzz"); 

	/**
	 * [FP] TODO: important! dangerous! use xml writer! and check on null in output... Looks less stupid when linked e.g.: a banner
	 */
	public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, ServletConfig sc, PortalRegistry registry) {
		if(item instanceof RssFeed) {
			RssFeed rssFeed = (RssFeed)item;
			
			response.setHeader("Content-Type", "application/xml+rss; charset=UTF-8");
			  
			StringBuffer output = new StringBuffer();
			output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			output.append("<rss version=\"2.0\">\n");
			output.append("<channel>");
			Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
			Node node = cloud.getNode(rssFeed.getId());
			output.append("<title>");
			output.append(node.getStringValue("title"));
			output.append("</title>");
			output.append("<link>");
			output.append(getServerDocRoot((HttpServletRequest) request));
			output.append("</link>");		
			output.append("<language>");
			output.append(node.getStringValue("language"));
			output.append("</language>");		
			output.append("<description>");
			output.append(node.getStringValue("description"));
			output.append("</description>");		
			output.append("<copyright>");
			output.append(node.getStringValue("copyright"));
			output.append("</copyright>");		
			output.append("<managingEditor>");
			output.append(node.getStringValue("email_managing_editor"));
			output.append("</managingEditor>");		
			output.append("<webMaster>");
			output.append(node.getStringValue("email_webmaster"));
			output.append("</webMaster>");		
			output.append("<generator>");
			output.append("CMS Container RssFeed module "+VersionUtil.getCmscVersion(servletContext));
			output.append("</generator>");		
			output.append("<docs>");
			output.append("http://blogs.law.harvard.edu/tech/rss");
			output.append("</docs>");

			List<String> contentTypesList = new ArrayList<String>();
			NodeList contentTypes = node.getRelatedNodes("typedef");
			for(NodeIterator ni = contentTypes.nodeIterator(); ni.hasNext();) {
				contentTypesList.add(ni.nextNode().getStringValue("name"));
			}
			
			boolean useLifecycle = true;
			String maximum = node.getStringValue("maximum");
			int maxNumber = (maximum != null && maximum != "")?Integer.parseInt(maximum):-1;

			Date lastChange = null;
			NodeList contentChannels = node.getRelatedNodes("contentchannel");
			StringBuffer imageOutput = null;
			boolean first = true;

			if(contentChannels.size() > 0) {
				Node contentChannel = contentChannels.getNode(0);
				
		        NodeQuery query = RepositoryUtil.createLinkedContentQuery(contentChannel, contentTypesList, ContentElementUtil.PUBLISHDATE_FIELD, "down", useLifecycle, null, 0, maxNumber, -1, -1, -1);
		        NodeList results = query.getNodeManager().getList(query);
		        for(NodeIterator ni = results.nodeIterator(); ni.hasNext();) {
		        	Node resultNode = ni.nextNode();
		        	output.append("<item>");
		    		output.append("<title>");
		    		output.append(resultNode.getStringValue("title"));
		    		output.append("</title>");
		    		
		    		String uniqueUrl = makeAbsolute(getContentUrl(resultNode), request);
		    		output.append("<link>");
		    		output.append(uniqueUrl);
		    		output.append("</link>");
		    		String description = null;
		    		if(resultNode.getNodeManager().hasField("intro")) {
		    			description = resultNode.getStringValue("intro");
		    		}
		    		if((description == null || description.length() == 0) && resultNode.getNodeManager().hasField("body")) {
		    			description = resultNode.getStringValue("body");
		    			if(description.indexOf("<br/>") != -1) {
		    				description = description.substring(0, description.indexOf("<br/>"));
		    			}
		    		} 
		    		if(description != null) {    			
		    			description = description.replaceAll("<.*?>", "");
		    		}
		    		output.append("<description>");    
		    		output.append(description); 
		    		output.append("</description>");
		    		output.append("<pubDate>");
		    		output.append(formatRFC822Date.format(resultNode.getDateValue("publishdate")));
		    		output.append("</pubDate>");
		    		output.append("<guid>");
		    		output.append(uniqueUrl);
		    		output.append("</guid>");
		    		
		    		NodeList images = resultNode.getRelatedNodes("images", "imagerel", null );
		    		if(first && images.size() > 0) {
		    			Node image = images.getNode(0);
		    			List<String> arguments = new ArrayList<String>();
		    			arguments.add("160x100");
		    			int iCacheNodeNumber = image.getFunctionValue("cache", arguments).toInt();
		    			String imageUrl = image.getFunctionValue("servletpath", null).toString() + iCacheNodeNumber;
		    			
		    			imageOutput = new StringBuffer();
		    			imageOutput.append("<image>");
		    			imageOutput.append("<url>");
		    			imageOutput.append(imageUrl); 
		    			imageOutput.append("</url>");
		    			imageOutput.append("<title/>");
		    			imageOutput.append("<link>");
		    			imageOutput.append(uniqueUrl); 
		    			imageOutput.append("</link>");
		    			imageOutput.append("</image>");
		    		}
		    		
		        	output.append("</item>");
		        	
		        	Date change = resultNode.getDateValue("lastmodifieddate");
		        	if(lastChange == null || change.getTime() > lastChange.getTime()) {
		        		lastChange = change;
		        	}
		        	
		        	first = false;
		        }
		    }
			
			if(imageOutput != null) {
				output.append(imageOutput);
			}

			if(lastChange != null) {
				output.append("<lastBuildDate>");
				output.append(formatRFC822Date.format(lastChange));
				output.append("</lastBuildDate>");
			}
			
			output.append("</channel>");
			output.append("</rss>");
			try {
				response.getOutputStream().write(output.toString().getBytes());
			} catch (IOException e) {
				log.error(e);
			}
		}
		else {
			throw new IllegalArgumentException("Got a wrong type in the RssFeedNavigationRenderer (only wants RssFeed), was"+item.getClass());
		}
	}

	private String getServerDocRoot(HttpServletRequest request) {
        StringBuffer s = new StringBuffer();
        s.append(request.getScheme()).append("://").append(request.getServerName());
        
        int serverPort = request.getServerPort();
        if (serverPort != 80 && serverPort != 443 ) {
            s.append(':').append(Integer.toString(serverPort));
        }
        s.append('/');
        return s.toString();
     }
	
    private String getContentUrl(Node node) {
        return ResourcesUtil.getServletPathWithAssociation("content", "/content/*", 
                node.getStringValue("number"), node.getStringValue("title"));
    }	
    
    private String makeAbsolute(String url, HttpServletRequest request) {
        String webapp = getServerDocRoot((HttpServletRequest) request);
        if (url.startsWith("/")) {
            url = webapp + url.substring(1);
        }
        else {
           url = webapp + url;
        }
        return url;
    }

	public TreeElement getTreeElement(NavigationInformationProvider renderer, Node parentNode, NavigationItem item, TreeModel model) {
		Node parentParentNode = NavigationUtil.getParent(parentNode);
    	UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentParentNode, false);
		TreeElement element = renderer.createElement(item, role, renderer.getOpenAction(parentNode, false));

		if (SecurityUtil.isEditor(role)) {
            element.addOption(renderer.createOption("edit_defaults.png", "site.rss.edit", "../rssfeed/RssFeedEdit.do?number=" + parentNode.getNumber()));
			element.addOption(renderer.createOption("delete.png", "site.rss.remove", "../rssfeed/RssFeedDelete.do?number=" + parentNode.getNumber()));
		}
		
		return element;
	}
}
