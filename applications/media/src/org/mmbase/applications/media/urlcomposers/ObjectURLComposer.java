/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.applications.media.Format;
import java.util.*;
import java.net.*;


/**
 * Produces links to mediahtml servlet. mediahtml must be used with
 * the html object tag.
 *
 * Depends on a 'template' to be linked to the fragment.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ObjectURLComposer.java,v 1.1 2003-02-17 12:40:29 michiel Exp $
 * @since MMBase-1.7
 */
public class ObjectURLComposer extends RamURLComposer { // also for wmp/asx
    private static Logger log = Logging.getLoggerInstance(RamURLComposer.class.getName());
    
    //private final static String SERVLET_MAPPING = "/mediahtml"; // todo make configurable/ read from web.xml 

    private List templates = null;
    public ObjectURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
    }

    private List getTemplates() {
        if (templates == null) {
            if (fragment != null) {
                templates = fragment.getRelatedNodes("templates");     
            } else {
                templates = new Vector();
            }
        }
        return templates;
    }
    
    public boolean canCompose() {
        return getTemplates().size() > 0;
    }

    protected StringBuffer  getURLBuffer() {
        if (fragment != null) {
            List templates = getTemplates();
            if (templates == null || templates.size() == 0) {
                log.debug("not templates");
            } else if (templates.size() > 1) {       
                log.warn("More then one template linked");
            } else {
                MMObjectNode template = (MMObjectNode) templates.get(0);
                return new StringBuffer(template.getStringValue("url") + "fragment=" + (fragment == null ? "" : "" + fragment.getNumber()) + "&format=" + format);            
            }
        }
        return new StringBuffer("[could not compose]");
    }

    public String getGUIIndicator(Locale locale) {
        String url = getURL();
        if (url.startsWith("/")) {
            url = MMBaseContext.getHtmlRootUrlPath() + url.substring(1);
        }       
        return "&lt;object class='mediahtml' data='" + url + "' type='text/html'&gt;&lt/object&gt;";
    }


    public Format  getFormat()   { 
        return Format.HTML; 
    } 

}
