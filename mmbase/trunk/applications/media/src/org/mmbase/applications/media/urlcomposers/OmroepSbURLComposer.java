/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers;
import org.mmbase.applications.media.Format;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.net.*;
import java.text.*;


/**
 * An example. URL's from these kind of URLComposers can contain 'start' and 'end' arguments and so on.
 *
 * @author Michiel Meeuwissen
 * @version $Id: OmroepSbURLComposer.java,v 1.1 2003-02-11 23:16:11 michiel Exp $
 * @since MMBase-1.7
 */
public class OmroepSbURLComposer extends RealURLComposer {
    
    private static Logger log = Logging.getLoggerInstance(OmroepSbURLComposer.class.getName());

    public OmroepSbURLComposer(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map info) {
        super(provider, source, fragment, info);
    }

    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl");
    }
    
    protected String getBandPrefix() {
        return "sb.";
    }

    protected StringBuffer getURLBuffer() {
        String host     = getFormat() == Format.ASF ? "media.omroep.nl" : "streams.omroep.nl";
        StringBuffer buff = new StringBuffer(provider.getStringValue("protocol") + "://" + host  + provider.getStringValue("rootpath"));
        int lastSlash = OmroepcgiURLComposer.addURL(buff, source.getStringValue("url"));
        buff.insert(lastSlash + 1, getBandPrefix());
        RealURLComposer.getRMArgs(buff, fragment); // append time, title, als
        return buff;
    }
}


