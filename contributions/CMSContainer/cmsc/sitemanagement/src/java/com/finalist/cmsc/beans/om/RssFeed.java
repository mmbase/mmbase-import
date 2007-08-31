package com.finalist.cmsc.beans.om;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeQuery;

import com.finalist.cmsc.mmbase.ResourcesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.util.version.VersionUtil;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

@SuppressWarnings("serial")
public class RssFeed extends NavigationItem {
	
    private Log log = LogFactory.getLog(RssFeed.class);
	
	private final static DateFormat formatRFC822Date = new SimpleDateFormat("EE d MMM yyyy HH:mm:ss zzzzz"); 
	public void service(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
		
		response.setHeader("Content-Type", "application/xml+rss; charset=UTF-8");
		  
		StringBuffer output = new StringBuffer();
		output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		output.append("<rss version=\"2.0\">\n");
		output.append("<channel>");
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		Node node = cloud.getNode(this.getId());
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
		output.append("CMS Container RssFeed module "+VersionUtil.getCmscVersion(context));
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
	    		String description = resultNode.getStringValue("intro");
	    		if(description.length() == 0) {
	    			description = resultNode.getStringValue("body");
	    			if(description.indexOf("<br/>") != -1) {
	    				description = description.substring(0, description.indexOf("<br/>"));
	    			}
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
	        	output.append("</item>");
	        	
	        	Date change = resultNode.getDateValue("lastmodifieddate");
	        	if(lastChange == null || change.getTime() > lastChange.getTime()) {
	        		lastChange = change;
	        	}
	        }
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
    
    public static String getServerDocRoot(HttpServletRequest request) {
        StringBuffer s = new StringBuffer();
        s.append(request.getScheme()).append("://").append(request.getServerName());
        
        int serverPort = request.getServerPort();
        if (serverPort != 80 && serverPort != 443 ) {
            s.append(':').append(Integer.toString(serverPort));
        }
        s.append('/');
        return s.toString();
     }
}
