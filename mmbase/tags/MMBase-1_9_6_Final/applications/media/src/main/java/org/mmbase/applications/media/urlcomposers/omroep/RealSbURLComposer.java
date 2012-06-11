/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.urlcomposers.RealURLComposer;

import java.util.Locale;
import java.util.Map;

/**
 * An example. URL's from these kind of URLComposers can contain 'start' and 'end' arguments and so on.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class RealSbURLComposer extends RealURLComposer {

    @Override
    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl") && provider.getStringValue("rootpath").charAt(0) == '%';
    }


    /**
     * Add the url to the buffer, but first remove the sb. or bb. prefix if it is in it already.
     */
    static int addURL(StringBuilder buf, String url) {
        int length    = buf.length();
        buf.append(url);
        int lastSlash = length + url.lastIndexOf('/');
        String existingPrefix = buf.substring(lastSlash + 1, lastSlash + 4);
        if (existingPrefix.equals("sb.") || existingPrefix.equals("bb.")) { // remove existing prefix, if there is one.
            buf.delete(lastSlash + 1, lastSlash + 4);
        }
        return lastSlash;
    }
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

    @Override
    protected StringBuilder getURLBuffer() {
        StringBuilder buff = new StringBuilder("rtsp://streams.omroep.nl");
        int lastSlash = addURL(buff, source.getStringValue("url"));
        buff.insert(lastSlash + 1, getBandPrefix());
        RealURLComposer.getRMArgs(buff, fragment, info); // append time, title, als
        return buff;
    }
}


