package org.mmbase.mmsite;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import org.mmbase.util.transformers.Identifier;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.transformers.Identifier;


/**
 * Utility methods for url's and pagestructure.
 *
 * @author Andr&eacute; van Toly
 * @version $Rev$
 */
public final class UrlUtils {
	private static final Logger log = Logging.getLoggerInstance(UrlUtils.class);
 
	/**
	 * Nodes from this page to the root page (homepage).
	 *
	 * @param  node	A node of type 'page' with field 'path'
	 * @return list with all the nodes leading to the homepage, excluding this node.
	 */
	public static NodeList listPages2Root(Node node) {
		Cloud cloud = node.getCloud();
		NodeManager nm = cloud.getNodeManager("pages");
		return listNodes2Root(node, nm);
	}
	
	/**
	 * Nodes from here to root.
	 *
	 * @param  node	A node of soem type with a field 'path'
	 * @return list with all the nodes leading to the homepage excluding this node.
	 */
	public static NodeList listNodes2Root(Node node) {
		NodeManager nm = node.getNodeManager();
		return listNodes2Root(node, nm);
	}

	/**
	 * Retrieve a pages node by the content of its path field.
	 *
	 * @param   cloud   MMBase cloud
	 * @param   path    Value of field path, f.e. '/news/new'
	 * @return  null if not found
	 */
	protected static Node getPagebyPath(Cloud cloud, String path) {
		Node node = null;
		NodeManager nm = cloud.getNodeManager("pages");
		NodeList nl = nm.getList("LOWER(path) = '" + path + "'", null, null);
		nl.addAll(nm.getList("LOWER(path) = '" + path + "/'", null, null));
        
		if (nl.size() > 0)  node = nl.getNode(0);
		return node;
	}
	
	/**
	 * Nodes from here to the root. It examines the field 'path'.
	 * The parent of a node with '/news/article/some is the one 
	 * with path '/news/article' etc.
	 *
	 * @param  node	A node of some type with a field 'path'
	 * @return nodes leading to homepage/root of the site
	 */
	protected static NodeList listNodes2Root(Node node, NodeManager nm) {
        NodeList list = nm.createNodeList();
		
		String path = node.getStringValue("path");
		if (path.startsWith("/")) path = path.substring(1, path.length());
		if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
		if (log.isDebugEnabled()) log.debug("path from field: " + path);
		
		
		String[] pieces = path.split("/");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pieces.length - 1; i++) {
			if (i > 0) sb.append("/");
			sb.append(pieces[i]);
			String ppath = sb.toString();
			if (log.isDebugEnabled()) log.debug("testing: " + ppath);
			
			NodeList nl = nm.getList("LOWER(path) = '" + ppath + "'", null, null);
			nl.addAll(nm.getList("LOWER(path) = '/" + ppath + "'", null, null));

			// results
            if (nl.size() > 0) {
                Node n = nl.getNode(0);
				list.add(n);
			}
		}
		
		//if (log.isDebugEnabled()) log.debug("returning: " + list);
		return list;
	}

	/**
	 * Is the link to an external site or not.
	 *
	 * @param  req HttpServletRequest
	 * @param  url Some link
	 * @return true if external link
	 */
	public Boolean externalLink(HttpServletRequest req, String url) {
		String servername = req.getServerName();
		//log.debug("servername: " + servername + ", url: " + url);
		if (url.startsWith("http://") 
			&& url.indexOf(servername) < 0
			) {
			
			return true;
		}
		return false;
	}

	
}
