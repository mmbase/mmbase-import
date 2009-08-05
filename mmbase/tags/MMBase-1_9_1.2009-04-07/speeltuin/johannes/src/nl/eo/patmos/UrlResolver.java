package nl.eo.patmos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.ContextProvider;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NotFoundException;

import org.mmbase.bridge.jsp.taglib.util.ContextContainer;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.Entry;

public class UrlResolver {
    /** This is the list of 'map' id's, all the ids on the URL that are known within the Patmos framework */
    private static String[] mapids = new String[]{"portal", "season", "baseBlock", "map", "event", "program", "eshop", "course", "mag", "theme", "weblog", "socialsite"};
    
    public static String getReferids(List excluded) {
        HashMap h = new HashMap();
        for (int i=0; i<excluded.size(); i++) {
            Entry e = (Entry)excluded.get(i);
            h.put(e.getKey(), e.getValue());
        }
        
        StringBuffer result = new StringBuffer();
        for (int i=0; i<mapids.length; i++) {
            if (!h.containsKey(mapids[i])) {
                if (result.length() > 0) {
                    result.append(',');
                }
                result.append(mapids[i]);
                result.append('?');
            }
        }
        
        return result.toString();
    }
    
    /**
     * Will return a map containing the following parameters:
     * <ul>
     *   <li> portal </li>
     *   <li> map </li>
     *   <li> season (if specified) </li>
     *   <li> one of the following: program, event, eshop, ... </li>
     * </ul>
     * The following order of importance is used:
     * <ul>
     *   <li> Those parameters passed as &lt;mm:param /&gt; override everything </li>
     *   <li> Objects in the current context are third </li>
     *   <li> When all else fails, the parameters are retrieved from the current HTTP Request </li>
     * </ul>
     * @param pageContext
     * @param context
     * @return
     * @throws JspTagException 
     */
    public static Map extractParams(PageContext pageContext, List extraParameters) throws JspTagException {
        HashMap map = new HashMap();
        Map parameterMap = pageContext.getRequest().getParameterMap();
        
        // Extract all relevant parameters, first from the parameterMap
        for (int i=0; i<mapids.length; i++) {
            boolean found = false;
            
            if (!found && parameterMap.containsKey(mapids[i])) {
                Object o = parameterMap.get(mapids[i]);
                if (o instanceof String) {
                    map.put(mapids[i], o);
                } else if (o instanceof String[]) {
                    map.put(mapids[i], ((String[])o)[0]);
                } else {   
                    map.put(mapids[i], o.toString());
                }
            }
        }
        
        // Maybe some explicit <mm:param> tags were given, these override everything else
        if (extraParameters != null && extraParameters.size() > 0) {
            for (int i=0; i<extraParameters.size(); i++) {
                Entry entry = (Entry)extraParameters.get(i);
                map.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Find the 'map' and put it in the map.
        // Skip 'portal', 'season' and 'baseblock'
        for (int i=4; i<mapids.length; i++) {
            if (map.containsKey(mapids[i]) && map.get(mapids[i]) != null && !"".equals(map.get(mapids[i]))) {
                map.put("map", map.get(mapids[i]));
                break;
            }
        }
        
        return map;
    }
    
    /**
     * Find the url for 1 page deep. This has a bit of extra code that will
     * try to find a parent portal if it exists.
     */
    public static String findUrl(String page, Node portalNode) {
        List l1 = getPaths(portalNode);

        Node portal = portalNode;
        int counter = 0;

        // Before: 'ronduitthema', 'portals', 'maps', 'object'
        while (true) {
            NodeList parents = portal.getRelatedNodes("portals", "related", "source");
            if (parents != null && parents.size() == 1) {
                portal = parents.getNode(0);
                String treepath = getTreePath(portal);
                if (treepath != null && !"".equals(treepath)) {
                    counter++;
                    l1.add(counter, treepath);
                }
            } else {
                break;
            }
        }
        // After: 'ronduitthema', 'ronduit', 'eoportal', 'portals', 'maps', 'object'

        for (int i=0; i<l1.size(); i++) {
            String path = "" + l1.get(i) + File.separator + page;
            if (pathExists(path)) {
                return path;
            }
        }
        
        return null;
    }
     
    /**
     * Find the url on 2 levels deep.
     */
    public static String findUrl(String page, Node portalNode, Node mapNode) {
        List l1 = getPaths(portalNode);
        List l2 = getPaths(mapNode);

        for (int i=0; i<l1.size(); i++) {
            for (int j=0; j<l2.size(); j++) {
                String path = "" + l1.get(i) + File.separator + l2.get(j) + File.separator + page;
                if (pathExists(path)) {
                    return path;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Find the url on 3 levels deep.
     */
    public static String findUrl(String page, Node portalNode, Node mapNode, Node seasonNode) {
        List l1 = getPaths(portalNode);
        List l2 = getPaths(mapNode);
        List l3 = getPaths(seasonNode);

        for (int i=0; i<l1.size(); i++) {
            for (int j=0; j<l2.size(); j++) {
                for (int k=0; k<l3.size(); k++) {
                    String path = "" + l1.get(i) + File.separator + l2.get(j) + File.separator + l3.get(k) + File.separator + page;
                    
                    if (pathExists(path)) {
                        return path;
                    }
                }
            }
        }
        
        return null;
    }
    
    public static String findUrl(String page, Cloud cloud, PageContext pageContext, List extraParameters) throws JspTagException {
        Map params = extractParams(pageContext, extraParameters);
        
        for (Iterator i = params.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            extraParameters.add(new Entry(entry.getKey(), entry.getValue()));
        }

        if (params.get("portal") != null) {
            Node portalNode = cloud.getNode((String)params.get("portal"));
            
            if (!params.containsKey("map")) {
                String finalpage = findUrl(page, portalNode);
                if (finalpage != null) {
                    return File.separator + finalpage;
                }
            } else {
                Node mapNode = cloud.getNode((String)params.get("map"));
                if (params.containsKey("season")) {
                    Node seasonNode = cloud.getNode((String)params.get("season"));
                    String finalpage = findUrl(page, portalNode, mapNode, seasonNode);
                    if (finalpage != null) {
                        return File.separator + finalpage;
                    }
                }
        
                String finalpage = findUrl(page, portalNode, mapNode);
                
                if (finalpage != null) {
                    return File.separator + finalpage;
                }
            }
        }

        return File.separator + "components" + File.separator + page;
    }
    
    private static List getPaths(Node n) {
        ArrayList al = new ArrayList();
        NodeManager nm = n.getNodeManager();
        while (nm != null) {
            al.add(al.size(), nm.getName());
            try {
                nm = nm.getParent();
            } catch (NotFoundException e) {
                break;
            }
            
        }
        
        String treepath = getTreePath(n);
        if (treepath != null && !"".equals(treepath)) {
            al.add(0, treepath);
        }
        
        return al;
    }
    
    private static boolean pathExists(String path) {
        String filename = MMBaseContext.getHtmlRoot() + File.separator + path;
        return new File(MMBaseContext.getHtmlRoot() + File.separator + path).exists();
    }
    
    private static String getTreePath(Node node) {
        NodeManager nm = node.getNodeManager();
        String field = null;
        while (field == null && nm != null) {
            field = nm.getProperty("smartpathfield");
            try {
                nm = nm.getParent();
            } catch (NotFoundException e) {
                break;
            }
        }
        return node.getStringValue(field);
    }
    
    public static void main(String[] args) {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        Node n1 = cloud.getNode(args[0]);
        Node n2 = cloud.getNode(args[1]);
        String page = args[2];
        System.out.println(findUrl(page, n1, n2));
    }
    
    // This code is copied from the MMBase 'Referids.java' file
    // copyrighted MMBase
    public static Map getReferids(String referids, ContextContainer context) throws JspTagException {
        Map result = new HashMap(); 
        StringTokenizer st = new StringTokenizer(referids, ",");
        
        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            if (key.equals("")) continue;
            int at = key.indexOf('@');
            String urlKey;
            if (at > -1) {
                urlKey = key.substring(at + 1, key.length());
                key = key.substring(0, at);
            } else {
                urlKey = key;
            }

            boolean mayBeMissing;
            if (key.endsWith("?")) {
                mayBeMissing = true;
                boolean keyIsUrlKey = key.equals(urlKey);
                key = key.substring(0, key.length() - 1);
                if (keyIsUrlKey) urlKey = key;
            } else {
                mayBeMissing = false;
            }
            if (key.equals("_")) {
                if (urlKey.equals("_")) throw new JspTagException("Should use '@' when using '_' in referids");
                //TODO: Hmm, tricky this is
            } else if ((! mayBeMissing) || context.isPresent(key)) {
                Object value = context.getObject(key);
                if (value != null) {
                    result.put(urlKey, value);
                }
            }
        }
        return result;
    }

}
