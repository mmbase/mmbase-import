/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.urlcomposers.RamURLComposer;
import org.mmbase.applications.media.urlcomposers.RealURLComposer;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.net.*;
import java.text.*;


/**
 * An example. Produces an URL to the omroep cgi-scripts (for real and wm)
 *
 * @author Michiel Meeuwissen
 * @version $Id: CgiURLComposer.java,v 1.2 2003-02-19 20:50:25 michiel Exp $
 * @since MMBase-1.7
 */
public class CgiURLComposer extends RamURLComposer {
    
    private static Logger log = Logging.getLoggerInstance(CgiURLComposer.class.getName());

    public CgiURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
    }
    public Format getFormat() {
        Format format = super.getFormat();
        if (format == Format.RM)  return Format.RAM;
        if (format == Format.RA)  return Format.RAM;
        if (format == Format.ASF) return Format.WMP;                
        return format;
    }

    /**
     * Host must be cgi.omroep.nl
     */
    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl");
    }


    /**
     * Add the url to the buffer, but first remove the sb. or bb. prefix if it is in it already.
     */
    static int addURL(StringBuffer buf, String url) {
        int length    = buf.length();
        buf.append(url);
        int lastSlash = length + url.lastIndexOf('/');
        String existingPrefix = buf.substring(lastSlash + 1, lastSlash + 4);
        if (existingPrefix.equals("sb.") || existingPrefix.equals("bb.")) { // remove existing prefix.
            buf.delete(lastSlash, lastSlash + 3); 
        }
        return lastSlash;
    }

    protected StringBuffer getURLBuffer() {
        StringBuffer buff = new StringBuffer(provider.getStringValue("protocol") + "://cgi.omroep.nl" + provider.getStringValue("rootpath"));
        addURL(buff, source.getStringValue("url"));
        RealURLComposer.getRMArgs(buff, fragment, info); // append time, title, als
        return buff;            
    }

}


