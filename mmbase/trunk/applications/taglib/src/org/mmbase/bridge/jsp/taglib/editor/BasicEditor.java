/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.editor;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.PageContext;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.Entry;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is an example implementation of the EditTag. It extends Editor.
 * To create your own editor with the edittag you could or rather should also 
 * extend Editor.<br />
 * BasicEditor accepts two parameters url and icon and returns a link with a nodenr
 * of the very first field the edittag encounters, with an icon to click on.
 * 
 * @author Andr&eacute; van Toly
 * @version $Id: BasicEditor.java,v 1.1 2005-12-30 22:28:38 andre Exp $
 * @see EditTag
 * @see Editor
 * @since MMBase-1.8
 */

public class BasicEditor extends Editor {

    private static final Logger log = Logging.getLoggerInstance(BasicEditor.class);

    /**
     * Makes html with a link to a node in an editor using the parameters url and icon.
     * 
     * @param   context The PageContext
     */
    public void getEditorHTML(PageContext context) {
        
        String nodenr = "";
        if (!nodenrList.isEmpty()) {    // get the first node from this list to edit
            nodenr = (String) nodenrList.get(0);
        }
        
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
        
        String str = makeHTML(context, url, icon, nodenr);
        log.debug("returning: " + str);
        try { 
            context.getOut().write(str);
        } catch (IOException ioe){
            log.error("Error writing to PageContext: " + ioe);
        }
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
        
        // do something with the lists
        
        // clear the lists in case of caching or something
        queryList.clear();
        nodenrList.clear();
        fieldList.clear();
    }
    
    /**
    * Creates a string with the link (and icon) to the editor
    *
    * @param url        An url to an editor
    * @param icon       An url to a graphic file
    * @return           An HTML string with a link suitable for the editor yammeditor.jsp
    * 
    */
    protected String makeHTML(PageContext pc, String url, String icon, String nodenr) {
        // TODO, Doesn't work in context. Perhaps some functionality of mm:url must be made accessible in static methods.
        
        StringBuffer html = new StringBuffer();
        
        html.append("<div class=\"et\"><a title=\"click to edit\" href=\"");
        url = makeRelative(url, pc);
        html.append(url);
        html.append(nodenr);
        html.append("\" onclick=\"window.open(this.href); return false;\"><img src=\"");
        html.append(icon); 
        html.append("\" alt=\"edit\"></a></div>");
        
        return html.toString();
    }
    
    /**
     * Copied this method from the UrlTag.
     * If it would be nice that an URL starting with '/' would be generated relatively to the current request URL, then this method can do it.
     * If the URL is not used to write to (this) page, then you probably don't want that.
     *
     * The behaviour can be overruled by starting the URL with two '/'s.
	 *
     */
    protected String makeRelative(String url, PageContext pageContext) {
    	StringBuffer show = new StringBuffer(url);
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
        if (show.charAt(0) == '/') { // absolute on servletcontex
            if (show.length() > 1 && show.charAt(1) == '/') {
                log.debug("'absolute' url, not making relative");
                /*
                if (addContext()) {
                    show.deleteCharAt(0);
                    show.insert(0, req.getContextPath());
                }
                */
            } else {
                log.debug("'absolute' url");
                String thisDir = new java.io.File(req.getServletPath()).getParent();
                show.insert(0,  org.mmbase.util.UriParser.makeRelative(thisDir, "/")); // makes a relative path to root.
            }
        }
        return show.toString();
    }

}
