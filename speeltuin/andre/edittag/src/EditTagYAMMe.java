package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The EditTag can create an url to an editor with nodenrs, fields and paths.
 * FieldTags register these with the EditTag. This way the linked editor
 * 'knows' which nodes and fields are available in the page.
 *
 * @author Andre van Toly
 * @version $Id: EditTagYAMMe.java,v 1.1 2005-02-21 22:16:38 andre Exp $
 */

public class EditTagYAMMe extends TagSupport implements EditTag {

    private static final Logger log = Logging.getLoggerInstance(EditTag.class);

    private static String editor;
    private static String icon;
    private static ArrayList startList = new ArrayList();       // startnodes: 346
    private static ArrayList pathList  = new ArrayList();       // paths: 346.news,posrel,urls
    private static ArrayList nodeList  = new ArrayList();       // nodes: 602 (should be 346.602)
    private static ArrayList fieldList = new ArrayList();       // fields: 602_news.title
    
    // Map to accommadate the fields and their startnodes
    Map fld2snMap = new HashMap();
    
    // new statics
    private static Query query;
    private static int nodenr;
    private static String fieldName;
    
    public void setEditor(String editor) {      // editor : link to the editor
        this.editor = editor;
    }
    
    public void setIcon(String icon) {          // icon : link to the edit icon
        this.icon = icon;
    }
    
    public int doStartTag() throws JspException {
        try {
            // Start by printing some bogus information
            pageContext.getOut().print("<div class=\"et\">");
            return EVAL_BODY_INCLUDE;
        } catch (IOException ioe) {
            throw new JspException(ioe.getMessage());
        }
    }
        
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(makeHTML(editor, icon) +
                "<br />gevonden startnodes : " + makeList4Url(startList) + 
                "<br />gevonden pad: " + makeList4Url(pathList) +
                "<br />gevonden nodes: " + makeList4Url(nodeList) +
                "<br />gevonden velden : " + makeList4Url(fieldList) +
                " ");
            fieldList.clear();  // clear the lists!
            startList.clear();
            pathList.clear();
            nodeList.clear();
            fld2snMap.clear();
            return EVAL_PAGE;
        } catch(IOException ioe) {
            throw new JspException(ioe.getMessage());
        }
    }
    
    /**
     * Here is were the FieldTag registers its fields and some associated 
     * and maybe usefull information with the EditTag.
     *
     * @param query     SearchQuery that delivered the field
     * @param nodenr    Nodenumber of the node the field belongs to
     * @param field     Name of the field
     */ 
    public void registerField(Query query, int nodenr, String field) {
        this.query = query;
        this.nodenr = nodenr;
        this.fieldName = field;		// field
        
        String path = getPathFromQuery(query);
        if (path != null && !path.equals("") && !pathList.contains(path)) {
            pathList.add(path);
            log.info("Added path : " + path);
        }
        
        ArrayList nl = getNodesFromQuery(query, nodenr);
        Iterator e = nl.iterator();			// iterate over the startnodes
        while (e.hasNext()) {
            String nr = (String)e.next();
            boolean startnode = false;
            
            // fills fld2snMap (only used to keep track of startnodes)
            if (!fld2snMap.containsValue(nr) ) {
                fld2snMap.put(String.valueOf(nodenr), nr);
                log.info("@ Added nodenr : " + nodenr + " sn : " + nr);
            } else {					// a node is a startnode when there was
           		startnode = true;		//   no previous field with this nodenr as startnodenr
            }
            
            // fill startList (startnodes)
            if (!startList.contains(nr) && startnode) {
                startList.add(nr);
                log.info("Added startnode : " + nr);
            }
            
            // fill nodeList (just the nodes in a page)
            // nodes 507.234 (= startnode.node)
            String str = nr + "_" + String.valueOf(nodenr);
			if (!nodeList.contains(str)) {
				nodeList.add(str);
				log.info("Added nodenr : " + str);
			}
			
			// fill fieldList (all the used fields in a page)
            String fieldstr = nr + "_" + fieldName;
            if (!fieldList.contains(fieldstr)) {
                fieldList.add(fieldstr);
                log.info("Added field : " + fieldstr);
            }
        }
        
        
    }
    
    /**
     * Extract the startnodes from a query
     *
     * @param   query The SearchQuery
     * @param   int   Nodenumber
     * @return  ArrayList with the startnodes
     */
    public ArrayList getNodesFromQuery(Query query, int nr) {
        ArrayList snl = new ArrayList();
        java.util.List steps = query.getSteps();
        String number = String.valueOf(nr);
        
        if (steps.size() == 1) {    //
            snl.add(number);
            // log.info("Found startnode (just 1 step) : " + number);
        } 
        
        Iterator si = steps.iterator();
        while (si.hasNext()) {
            Step step = (Step) si.next();
            
            // Get the nodes from this step
            SortedSet nodeSet = step.getNodes();
            for (Iterator nsi = nodeSet.iterator(); nsi.hasNext();) {
                Integer n = (Integer)nsi.next();
                number = String.valueOf(n);
                
                if (!snl.contains(number)) {
                    snl.add(number);
                    // log.info("Found startnode : " + number);
                }
            }
            
        }
        return snl;
    }
    
    /**
    * Just get the path from this query
    *
    * @param query  The query
    * @return       A path like 345.news,posrel,urls
    */  
    public String getPathFromQuery(Query query) {
        String path = null;     
        
        java.util.List steps = query.getSteps();
        if (steps.size() > 1) {
            Iterator si = steps.iterator();
            while (si.hasNext()) {
                Step step = (Step) si.next();
                            
                String nodenrs = "";
                SortedSet nodeSet = step.getNodes();	// Get the (start?)nodes from this step
                for (Iterator nsi = nodeSet.iterator(); nsi.hasNext();) {
                    Integer number = (Integer)nsi.next();
                    if (nodenrs.equals("")) {
                        nodenrs = String.valueOf(number);
                    } else {
                        nodenrs = nodenrs + "," + String.valueOf(number);
                    }
                    
                }
                
                // path: Get one nodetype at the time (the steps)
                if (step.getAlias() != null) {
                    if (path == null || path.equals("")) {
                        path = nodenrs + "_" + step.getAlias();
                    } else {
                        path = path + "," + step.getAlias();
                    }
                }
            }
        }
        return path;
    }
    
    /**
    * Creates a ; seperated string for the url with paths, fields or startnodes.
    * 
    * @param al     One of the lists
    * @return       A ; seperated string
    *
    */
    public static String makeList4Url(ArrayList al) {
        String str = "";
        if (al.size() > 0) {
            Iterator e = al.iterator();
            while(e.hasNext()) {
                if (str.equals("")) { 
                    str = (String)e.next();
                } else { 
                    str = str + ";" + e.next();
                }
            }
        }
        return str;
    }
    
    /**
    * Creates a string with the link (and icon) to the editor
    *
    * @param editor     An url to an editor
    * @param icon       An url to a graphic file
    * @return           A HTML string with a link suitable for an editor like yammeditor.
    * 
    */
    public static String makeHTML(String editor, String icon) {
        String url = editor + "?nrs=" + makeList4Url(startList) + 
            "&amp;fields=" + makeList4Url(fieldList) +
            "&amp;paths=" + makeList4Url(pathList) +
            "&amp;nodes=" + makeList4Url(nodeList);
        String html = "<div class=\"et\"><a title=\"click to edit\" href=\"" + url + "\" target=\"_blank\">edit</a></div>";
        if (!icon.equals("")) {
            html = "<div class=\"et\"><a title=\"click me to edit\" href=\"" + url + "\" target=\"_blank\"><img src=\"" + icon + "\"></a></div>";
        }
        return html;
    }
    
}
