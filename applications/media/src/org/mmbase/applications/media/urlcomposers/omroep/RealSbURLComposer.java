/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.urlcomposers.RealURLComposer;

import java.util.Map;

/**
 * An example. URL's from these kind of URLComposers can contain 'start' and 'end' arguments and so on.
 *
 * @author Michiel Meeuwissen
 * @version $Id: RealSbURLComposer.java,v 1.7 2003-07-15 12:36:13 michiel Exp $
 * @since MMBase-1.7
 */
public class RealSbURLComposer extends RealURLComposer {

    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl");
    }
    
    protected String getBandPrefix() {
        return "sb.";
    }
    protected String getBand() {
        return "smalband";
    }
    public String getGUIIndicator(Map options) {
        return super.getGUIIndicator(options) + " (" + getBand() +")";
    }

    protected StringBuffer getURLBuffer() {
        StringBuffer buff = new StringBuffer("rtsp://streams.omroep.nl");
        int lastSlash = CgiURLComposer.addURL(buff, source.getStringValue("url"));
        buff.insert(lastSlash + 1, getBandPrefix());
        RealURLComposer.getRMArgs(buff, fragment, info); // append time, title, als
        return buff;
    }
}


