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
import org.mmbase.applications.media.builders.MediaFragments;
import java.util.*;
import java.net.*;


/**
 * Produces links to mediahtml servlet. mediahtml must be used with
 * the html object tag.
 *
 * Depends on a 'template' to be linked to the fragment.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MarkupURLComposer.java,v 1.2 2003-02-18 14:07:46 michiel Exp $
 * @since MMBase-1.7
 */
public class MarkupURLComposer extends FragmentURLComposer { 
    private static Logger log = Logging.getLoggerInstance(MarkupURLComposer.class.getName());
    
    private List templates = null;


    public MarkupURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
     
    }

    protected MMObjectNode getTemplate() {
        return (MMObjectNode) getInfo().get("template");
    }
    
    public boolean canCompose() {
        MMObjectNode template = getTemplate();
        if (template == null) return false;
        Format sourceFormat = Format.get(source.getIntValue("format")); 
        if (getFormat() == Format.SMIL && !( sourceFormat == Format.RM || sourceFormat == Format.RA)) return false;
        return true;
    }

    protected StringBuffer  getURLBuffer() {
        MMObjectNode template = getTemplate();
        if (template != null) { 
            String url = template.getStringValue("url");
            StringBuffer buf = new StringBuffer(url + "fragment=" + fragment.getNumber() + "&format=" +  Format.get(source.getIntValue("format")));            
            if (url.indexOf("://") < 0) {
                if (! url.startsWith("/")) {
                    buf.insert(0, Config.templatesDir);
                }
                buf.insert(0, "http://" + Config.host);
            }
            return buf;
        } else {
            return new StringBuffer("[Could not compose]"); // should not happen
        }
        
    }
    public String getGUIIndicator(Map options) {
        Locale locale = (Locale) options.get("locale");
        Format sourceFormat = Format.get(source.getIntValue("format")); 
        return super.getGUIIndicator(options) + " (" + sourceFormat.getGUIIndicator(locale) + ")";
    }


    public String getDescription(Map options) { 
        Locale locale = (Locale) options.get("locale");
        String url = getURL() + "&amp;language=" + locale.getLanguage();
        MMObjectNode template = getTemplate();
        if (template.getStringValue("mimetype").equals("text/html")) {
            return template.getStringValue("name") + "<br />" + template.getStringValue("description") + ":<br /><nobr>&lt;object data='" + url + "' type='text/html'&gt;&lt/object&gt;</nobr>";
        } else {
            return template.getStringValue("name") + "<br />" + template.getStringValue("description");
        }
    }


    public Format  getFormat()   { 
        MMObjectNode template = getTemplate();
        if (template == null) return Format.HTML;
        String mimetype = template.getStringValue("mimetype");
        log.service("found mimetype '" + mimetype +"'");
        if (mimetype.equals("application/smil")) {
            return Format.SMIL;
        } else {
            return Format.HTML; 
        }
    } 

}
