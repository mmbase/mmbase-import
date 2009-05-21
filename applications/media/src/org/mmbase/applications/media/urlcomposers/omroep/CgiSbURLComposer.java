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

import java.util.*;


/**
 * An example. Produces an URL to the omroep cgi-scripts (for real and wm)
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class CgiSbURLComposer extends RamURLComposer {


    protected String getBandPrefix() {
        return "sb.";
    }
    protected String getBand() {
        return "smalband";
    }
    @Override
    public String getGUIIndicator(Map<String,Locale> options) {
        return super.getGUIIndicator(options) + " (" + getBand() +")";
    }


    /**
     * These scripts actually wrap the source in a ram or wmp
     */
    @Override
    public Format getFormat() {
        Format format = super.getFormat();
        if (format == Format.RM)  return Format.RAM;
        if (format == Format.RA)  return Format.RAM;
        if (format == Format.ASF) return Format.WMP;
        return format;
    }

    /**
     * Host must be cgi.omroep.nl script.
     */
    @Override
    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl")  && provider.getStringValue("rootpath").charAt(0) == '%';
    }

    @Override
    protected StringBuffer getURLBuffer() {
        String rootPath = provider.getStringValue("rootpath").substring(1);
        StringBuffer buff = new StringBuffer(provider.getStringValue("protocol") + "://cgi.omroep.nl" + rootPath);
        int lastSlash = RealSbURLComposer.addURL(buff, source.getStringValue("url"));
        buff.insert(lastSlash + 1, getBandPrefix());
        RealURLComposer.getRMArgs(buff, fragment, info); // append time, title
        return buff;
    }

}


