/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.urlcomposers.RealURLComposer;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.net.*;
import java.text.*;


/**
 * An example. URL's from these kind of URLComposers can contain 'start' and 'end' arguments and so on.
 *
 * @author Michiel Meeuwissen
 * @version $Id: RealSbURLComposer.java,v 1.2 2003-02-17 09:11:29 michiel Exp $
 * @since MMBase-1.7
 */
public class RealSbURLComposer extends RealURLComposer {
    
    private static Logger log = Logging.getLoggerInstance(RealSbURLComposer.class.getName());

    public RealSbURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
    }

    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl");
    }
    
    protected String getBandPrefix() {
        return "sb.";
    }
    public String getGUIIndicator(Locale locale) {
        return super.getGUIIndicator(locale) + " (smalband)";
    }

    protected StringBuffer getURLBuffer() {
        StringBuffer buff = new StringBuffer("rtsp://streams.omroep.nl");
        int lastSlash = CgiURLComposer.addURL(buff, source.getStringValue("url"));
        buff.insert(lastSlash + 1, getBandPrefix());
        RealURLComposer.getRMArgs(buff, fragment); // append time, title, als
        return buff;
    }
}


