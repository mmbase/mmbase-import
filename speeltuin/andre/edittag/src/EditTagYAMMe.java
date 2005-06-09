package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.Entry;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.mmbase.util.XMLBasicReader;

/**
 * This is an example implementation of the EditTag. EditTagYAMMe works together
 * with the editor yammeditor.jsp.
 *
 * @see EditTag
 * @author Andre van Toly
 * @version $Id: EditTagYAMMe.java,v 1.4 2005-06-09 08:42:59 andre Exp $
 */

public class EditTagYAMMe extends TagSupport implements EditTag, ParamHandler {

    private static final Logger log = Logging.getLoggerInstance(EditTagYAMMe.class);

    private static String editor;
    private static String icon;
    private static ArrayList startList = new ArrayList();       // startnodes: 346
    private static ArrayList pathList  = new ArrayList();       // paths: 346_news,posrel,urls
    private static ArrayList nodeList  = new ArrayList();       // nodes: 602 (should be 346.602)
    private static ArrayList fieldList = new ArrayList();       // fields: 602_news.title    
    // Map to accommadate the fields and their startnodes
    Map fld2snMap = new HashMap();
    
    private static Query query;
    private static int nodenr;
    private static String fieldName;
    
    // Variables belonging to parametization (?) of edittag
    protected List parameters;
    
    public void setEditor(String editor) throws JspTagException {   // editor : link to the editor
        this.editor = editor;
    }
    
    public void setIcon(String icon) throws JspTagException {		// icon : link to the edit icon
        this.icon = icon;
    }
    
    public void addParameter(String key, Object value) throws JspTagException {
        // parameters.set(key, value);
        parameters.add(new Entry(key, value));
    }
    
    public int doStartTag() throws JspException {
        Class thisClass = EditTag.class;
        InputSource edittypes = new InputSource(thisClass.getResourceAsStream("resources/edittagtypes.xml"));
        XMLBasicReader reader  = new XMLBasicReader(edittypes, thisClass);
        
        Element edittypesElement = reader.getElementByPath("edittagtypes");
        Enumeration e = reader.getChildElements(edittypesElement, "editor");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
			String typeString = element.getAttribute("type");
			String claz = reader.getElementValue(reader.getElementByPath(element, "editor.class"));
			log.info("Resources - type: " + typeString + "  claz: " + claz);
		}
        
        parameters = new ArrayList();
        return EVAL_BODY_INCLUDE;
    }
        
    public int doEndTag() throws JspException {
        try {
//            if (log.isDebugEnabled()) {
                log.info("Parameters " + parameters);
//            }
			Iterator i = parameters.iterator();
			while (i.hasNext()) {
				Entry param  = (Entry) i.next();
				if (param.getValue() == null) continue;
				String key = (String) param.getKey();
				String val = param.getValue().toString();
                log.info("Parameters/stuk: " + key + " + " + val);
			}
            
            pageContext.getOut().print( makeHTML(editor, icon) );
            log.info("startnodes : " + makeList4Url(startList));
            log.info("paths : " + makeList4Url(pathList));
            log.info("nodes : " + makeList4Url(nodeList));
            log.info("fields : " + makeList4Url(fieldList));
            
            fieldList.clear();  // clear the lists!
            startList.clear();
            pathList.clear();
            nodeList.clear();
            fld2snMap.clear();
        	parameters = null;
            
            return EVAL_PAGE;
        } catch(IOException ioe) {
            throw new JspException(ioe.getMessage());
        }
    }
    
    /**
     * Here is were the FieldTag registers its fields and some associated 
     * and maybe usefull information with EditTagYAMMe.
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
            log.debug("Added path : " + path);
        }
        
        ArrayList nl = getNodesFromQuery(query, nodenr);
        Iterator e = nl.iterator();			// iterate over the startnodes
        while (e.hasNext()) {
            String nr = (String)e.next();
            boolean startnode = false;
            
            // fills fld2snMap (only used to keep track of startnodes)
            if (!fld2snMap.containsValue(nr) ) {
                fld2snMap.put(String.valueOf(nodenr), nr);
                log.debug("Added nodenr : " + nodenr + " sn : " + nr);
            } else {					// a node is a startnode when there was
           		startnode = true;		//   no previous field with this nodenr as startnodenr
            }
            
            // fill startList (startnodes)
            if (!startList.contains(nr) && startnode) {
                startList.add(nr);
                log.debug("Added startnode : " + nr);
            }
            
            // fill nodeList (just the nodes in a page)
            // nodes 507.234 (= startnode.node)
            String str = nr + "_" + String.valueOf(nodenr);
			if (!nodeList.contains(str)) {
				nodeList.add(str);
				log.debug("Added nodenr : " + str);
			}
			
			// fill fieldList (all the used fields in a page)
            String fieldstr = nr + "_" + fieldName;
            if (!fieldList.contains(fieldstr)) {
                fieldList.add(fieldstr);
                log.debug("Added field : " + fieldstr);
            }
        }
        
        
    }
    
    /**
     * Extract the startnodes from a query
     *
     * @param   query The SearchQuery
     * @param   nr    Nodenumber
     * @return  ArrayList with the startnodes
     */
    public ArrayList getNodesFromQuery(Query query, int nr) {
        ArrayList snl = new ArrayList();
        java.util.List steps = query.getSteps();
        String number = String.valueOf(nr);
        
        if (steps.size() == 1) {    //
            snl.add(number);
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
                }
            }
            
        }
        return snl;
    }
    
    /**
    * Just get the path from this query
    *
    * @param query  The query
    * @return       A path like 345_news,posrel,urls which is the nodenumber of
    *               the node this field belongs to and the path that leads to it.
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
    * @param al     One of the ArrayLists
    * @return       A ; seperated string with the elements from the ArrayList
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
    * @return           An HTML string with a link suitable for the editor yammeditor.jsp
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
