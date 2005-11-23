/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.editor;

import java.io.IOException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.Entry;

/**
 * This is an example implementation of the EditTag. It extends the implementation
 * class Editor of EditTag. To create your own editor with the edittag you could
 * or rather should also extend Editor.<br />
 * EditTagYAMMe works together with the editor yammeditor.jsp. It creates an URL 
 * that looks something like this:<br />
 * yammeditor.jsp?nrs=76&fields=76_number;76_title;76_subtitle;76_intro;80_gui();
 *
 * @author Andr&eacute; van Toly
 * @version $Id: YAMMEditor.java,v 1.3 2005-11-23 13:07:53 andre Exp $
 * @see org.mmbase.bridge.jsp.taglib.editor.EditTag
 * @see org.mmbase.bridge.jsp.taglib.editor.Editor
 */

public class YAMMEditor extends Editor {

    private static final Logger log = Logging.getLoggerInstance(YAMMEditor.class);

    private Query query;
    private int nodenr;
    private String fieldName;
    
    private ArrayList queryList = new ArrayList();
    private ArrayList nodenrList = new ArrayList();
    private ArrayList fldList = new ArrayList();
    
    protected List parameters;
    private String editor;
    private String icon;
    
    private ArrayList startList = new ArrayList();       // startnodes: 346
    private ArrayList pathList  = new ArrayList();       // paths: 346_news,posrel,urls
    private ArrayList nodeList  = new ArrayList();       // nodes: 602 (should be 346.602)
    private ArrayList fieldList = new ArrayList();       // fields: 602_news.title    
    // Map to accommadate the fields and their startnodes
    Map fld2snMap = new HashMap();
    
    /**
     * @param params	List with the parameters of the edittag.
     */
    public void setParameters(List params) {
       this.parameters = params;
       log.debug("parameters: " + parameters);
    }
    
    public void setQueryList(ArrayList qlist) {
        this.queryList = qlist;
    }
    public void setNodenrList(ArrayList nrlist) {
        this.nodenrList = nrlist;
    }
    public void setFieldList(ArrayList flist) {
        this.fldList = flist;
    }

    /**
     * Create a string containing all the needed html to find the editor. Or rather
     * it calls one of the older methods i wrote, that was easier then rewriting
     * the old method.
     *
     * @return  A String with a link and an icon to access yammeditor.jsp
     */
    public String getEditorHTML() {
        String html = "Sorry. You should see an icon and a link to yammeditor here.";
        
        // Parameters 
        String url = "";
        String icon = "";
        
        Iterator pi = parameters.iterator();
        while (pi.hasNext()) {
            Entry entry = (Entry) pi.next();
            String key = (String) entry.getKey();
            
            if (key.equals("url")) url = (String) entry.getValue();
            if (key.equals("icon")) icon = (String) entry.getValue();
        } 
        
        html = makeHTML(url, icon);
        // log.debug("returning: " + html);
        return html;
    }
    
    /**
     * Values passed by the EditTag from the FieldTags.
     *
     * @param queryList     ArrayList with SearchQuery objects from fields
     * @param nodenrList    ArrayList with nodenumbers
     * @param fieldList     ArrayList with fieldnames 
     */ 
    public void registerFields(ArrayList queryList, ArrayList nodenrList, ArrayList fieldList) {
        log.debug("processing fields");
        for (int i = 0; i < nodenrList.size(); i++) {
            String fldName = (String) fieldList.get(i);
            log.debug("processing field '" + fldName);
            Query query = (Query) queryList.get(i);
            int nodenr = Integer.parseInt( (String) nodenrList.get(i) );
            
            processField(query, nodenr, fldName);
        }
        
        // make the lists null
        queryList = null;
        nodenrList = null;
        fieldList = null;
    }

    /**
     * Here is were the FieldTag registers its fields and some associated 
     * and maybe usefull information with EditTagYAMMe.
     *
     * @param query     SearchQuery that delivered the field
     * @param nodenr    Nodenumber of the node the field belongs to
     * @param field     Name of the field
     */ 
    public void processField(Query query, int nodenr, String field) {
        this.query = query;
        this.nodenr = nodenr;
        this.fieldName = field;     // field
        
        //log.debug("Query : " + query);
        
        String path = getPathFromQuery(query);
        if (path != null && !path.equals("") && !pathList.contains(path)) {
            pathList.add(path);
            log.debug("Added path : " + path);
        }
        
        ArrayList nl = getNodesFromQuery(query, nodenr);
        Iterator e = nl.iterator();         // iterate over the startnodes
        while (e.hasNext()) {
            String nr = (String)e.next();
            boolean startnode = false;

            
            /* fills fld2snMap (only used to keep track of startnodes, 
            it contains nodenr's
            when a nr is found in this map ...
            */
            
            if (!fld2snMap.containsValue(nr) ) {
                fld2snMap.put(String.valueOf(nodenr), nr);
                log.debug("Added nodenr : " + nodenr + " sn : " + nr);
            } else if (fld2snMap.isEmpty()) {
                fld2snMap.put(String.valueOf(nodenr), nr);
                startnode = true;
                log.debug("Added nodenr (startnode): " + nodenr + " sn : " + nr);
            } else {                    // a node is a startnode when there was
                startnode = true;       //   no previous field with this nodenr as startnodenr
            }
            
            // fill startList (startnodes)
            if (!startList.contains(nr) && startnode) {         // 507 (= just startnodenr)
                startList.add(nr);
                log.debug("Added startnode : " + nr);
            }
            
            // fill nodeList (just the nodes in a page)
            String str = nr + "_" + String.valueOf(nodenr);     // 507_234 (= startnodenr_nodenr)
            if (!nodeList.contains(str)) {
                nodeList.add(str);
                log.debug("Added nodenr : " + str);
            }
            
            // fill fieldList (all the used fields in a page)   // 507_title (= startnodenr_fieldname)
            String fieldstr = nr + "_" + fieldName;
            if (!fieldList.contains(fieldstr)) {
                fieldList.add(fieldstr);
                log.debug("Added field : " + fieldstr);
            }
        }

    }
        
    public ArrayList getNodesFromQuery(Query query, int nr) {
        ArrayList nl = new ArrayList();
        java.util.List steps = query.getSteps();
        String number = String.valueOf(nr);
        
        if (steps.size() == 1) {    // why ?
            nl.add(number);
            log.debug("1. added nr to list of all the nodes in query: " + number);
        } 
        
        Iterator si = steps.iterator();
        while (si.hasNext()) {
            Step step = (Step) si.next();
            
            // Get the nodes from this step 
            //   (haalt alle nodes uit step, itereert erover en stopt ze in nl )
            SortedSet nodeSet = step.getNodes();
            for (Iterator nsi = nodeSet.iterator(); nsi.hasNext();) {
                Integer n = (Integer)nsi.next();
                number = String.valueOf(n);
                
                if (!nl.contains(number)) {
                    nl.add(number);
                    log.debug("2. added nr to list of all the nodes in query: " + number);
                }
            }
            
        }
        return nl;
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
                SortedSet nodeSet = step.getNodes();    // Get the (start?)nodes from this step
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
    * Creates a ; seperated string for the url with paths, fields or startnodes
    * from an ArrayList.
    * 
    * @param	al One of the ArrayLists to use
    * @return   A ; seperated string with the elements from the ArrayList
    *
    */
    public String makeList4Url(ArrayList al) {
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
    public String makeHTML(String editor, String icon) {
        String url = editor + "?nrs=" + makeList4Url(startList) + 
            "&amp;fields=" + makeList4Url(fieldList) +
            "&amp;paths=" + makeList4Url(pathList) +
            "&amp;nodes=" + makeList4Url(nodeList);
        String html = "<div class=\"et\"><a title=\"click to edit\" href=\"" + url + "\" onclick=\"window.open(this.href); return false;\">edit</a></div>";
        if (!icon.equals("")) {
            html = "<div class=\"et\"><a title=\"click me to edit\" href=\"" + url + "\" onclick=\"window.open(this.href); return false;\"><img src=\"" + icon + "\" alt=\"edit\"></a></div>";
        }
        return html;
    }
    
}
