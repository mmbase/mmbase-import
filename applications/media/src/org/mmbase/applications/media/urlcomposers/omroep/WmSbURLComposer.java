/*
  
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
  
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
  
*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.Map;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: WmSbURLComposer.java,v 1.7 2003-07-15 13:02:52 vpro Exp $
 * @since MMBase-1.7
 */
public class WmSbURLComposer extends URLComposer {

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
        return super.getGUIIndicator(options) + " (" + getBand() + ")";
    }


    protected StringBuffer getURLBuffer() {
        StringBuffer buff = new StringBuffer("mms://media.omroep.nl");
        int lastSlash = CgiURLComposer.addURL(buff, source.getStringValue("url"));
        buff.insert(lastSlash + 1, getBandPrefix());
        return buff;
    }
}


